package algorithms.imageoverlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.Option;
import api.UsageException;
import archive.ArchiveWriter;
import archive.CreationJobFileState;
import archive.ArchiveIOException;
import config.Settings;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;
import util.ByteConversion;
import util.algorithms.ImageUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the writing of an image overlay archive
 */
public class ImageOverlayWriter extends ImageOverlay implements ArchiveWriter
{
	private InputImageManager f_manager;
	private File f_imgFile;

	/**
	 * Constructs an image overlay writer
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to write archives
	 */
	public ImageOverlayWriter(Algorithm p_algo, Key p_key)
	{
		super(p_algo, p_key);
		File imageFolder = new File(p_algo.getParameterValue(Definition.IMAGE_FOLDER_PARAM));
		
		//prompt for the image folder if the one listed doesn't exist
		if (!imageFolder.exists())
		{
			try
			{
				p_algo.setParameter(Definition.IMAGE_FOLDER_PARAM, Option.PROMPT_OPTION.getValue());
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_fatal, e.getMessage());
			}
			
			imageFolder = new File(p_algo.getParameterValue(Definition.IMAGE_FOLDER_PARAM));
		}
		
		ConsumptionMode mode = ConsumptionMode.parseMode(
			p_algo.getParameterValue(Definition.IMAGE_CONSUMPTION_MODE_PARAM));
		f_manager = InputImageManager.getInstance(imageFolder, mode);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#newArchive()
	 */
	@Override
	public void newArchive() throws ArchiveIOException
	{
		loadCleanFile();
		reset();
	}

	/**
	 * Loads a new input image
	 * @throws ArchiveIOException If no input images are available
	 */
	private void loadCleanFile() throws ArchiveIOException
	{		
		//get the next image
		f_imgFile = f_manager.nextImageFile();
		
		//ran out of images
		if (f_imgFile == null)
		{
			throw new ArchiveIOException("No input images remain.");
		}
		else
		{
			//update status to show this new image file is about to be used
			if (Settings.trackFileStatus())
				JobStatus.setCreationJobFileStatus(f_imgFile, CreationJobFileState.WRITING);
		}
		
		boolean foundFile = false;
		while (f_imgFile != null && !foundFile)
		{
			try
			{
				f_img = ImageIO.read(f_imgFile);
				
				//using the faster rgb math operations requires
				//a standard color model
				if (f_img.getType() != BufferedImage.TYPE_INT_RGB)
				{
					reinterpretColorModel();
				}
				foundFile = true;
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not interpret input image file: " + f_imgFile.getName());
			}
		}
		
		//ran out of images after trying some unsuccessfully
		if (f_imgFile == null)
		{
			throw new ArchiveIOException("No input images remain.");
		}
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/26918675/removing-transparency-in-png-bufferedimage
	 * Changes the color model of the current image to rgb.
	 */
	private void reinterpretColorModel()
	{
		BufferedImage copy = new BufferedImage(f_img.getWidth(), f_img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(f_img, 0, 0, null);
		g2d.dispose();
		
		f_img = copy;
		
		Logger.log(LogLevel.k_debug, "Color model changed to rgb.");
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#write(byte)
	 */
	@Override
	public boolean write(byte p_byte)
	{
		try
		{
			int secured = ByteConversion
				.byteToInt(ByteConversion.intToByte(p_byte ^ f_random.nextByte()));
			
			if (f_density == InsertionDensity.k_25)
			{
				steps4(secured);
			}
			else //k_50
			{
				steps16(secured);
			}
		}
		catch (ArchiveIOException e)
		{
			//this happens naturally when the image file get's filled up
			return false;
		}

		return true;
	}
	
	/**
	 * Writes a byte of data with two bits per color
	 * @param p_val The byte of data to write stored as an int [0, 255]
	 * @throws ArchiveIOException If no more data is available
	 */
	private final void steps4(int p_val) throws ArchiveIOException
	{
		int div16 = p_val / 16;
		int mod16 = p_val % 16;

		f_split[0] = div16 / 4;
		f_split[1] = div16 % 4;
		f_split[2] = mod16 / 4;
		f_split[3] = mod16 % 4;

		for (int i = 0; i < 4; ++i)
		{
			nextColor();

			int color = ByteConversion.byteToInt(getColor());

			int step = (color / 4) * 4;

			setColor(ByteConversion.intToByte(step + f_split[i]));
		}
	}
	
	/**
	 * Writes a byte of data with four bits per color
	 * @param p_val The byte of data to write stored as an int [0, 255]
	 * @throws ArchiveIOException If no more data is available
	 */
	private final void steps16(int p_val) throws ArchiveIOException
	{
		f_split[0] = p_val / 16;
		f_split[1] = p_val % 16;

		for (int i = 0; i < 2; ++i)
		{
			nextColor();

			int color = ByteConversion.byteToInt(getColor());

			int step = (color / 16) * 16;

			setColor(ByteConversion.intToByte(step + f_split[i]));
		}
	}

	/**
	 * Sets the color at the current position to the specified value
	 * @param p_data The color value to set
	 */
	private void setColor(byte p_data)
	{
		if (f_colorIndex == 0)
		{
			f_img.setRGB(f_curPixelCoord[0], f_curPixelCoord[1], 
				ImageUtil.setRed(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]), p_data));

		}
		else if (f_colorIndex == 1)
		{
			f_img.setRGB(f_curPixelCoord[0], f_curPixelCoord[1], 
				ImageUtil.setGreen(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]), p_data));
		}
		else
		{
			f_img.setRGB(f_curPixelCoord[0], f_curPixelCoord[1], 
				ImageUtil.setBlue(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]), p_data));
		}
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#write(byte[], int, int)
	 */
	@Override
	public int write(byte[] p_bytes, int p_offset, int p_length)
	{
		for (int x = p_offset; x < p_offset + p_length; ++x)
		{
			if (!write(p_bytes[x]))
				return x - p_offset;
		}

		return p_length;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#saveFile(java.io.File, java.lang.String)
	 */
	@Override
	public void saveFile(File p_archiveStagingFolder, String p_fileName)
	{
		File archiveFile = new File(p_archiveStagingFolder.getAbsolutePath(), p_fileName + ".png");
		Logger.log(LogLevel.k_info, "Saving archive file: " + archiveFile.getAbsolutePath());
		
		try
		{
			if (!archiveFile.getParentFile().exists())
				archiveFile.getParentFile().mkdirs();
			
			ImageIO.write(f_img, "png", archiveFile);

			// update progress
			JobStatus.incrementArchivesCreated(1);

			try
			{
				//update manager
				f_manager.setFileUsed(f_imgFile);
				
				//update status to show previous image file was used
				if (Settings.trackFileStatus())
					JobStatus.setCreationJobFileStatus(f_imgFile, CreationJobFileState.FINISHED);
			}
			catch (IOException e)
			{
				//probably the file doesn't exist anymore, that's ok
				//otherwise it's a permissions problem
				Logger.log(LogLevel.k_debug, "The target image file could not be 'set used'. "
								+ "This may be ok. " + f_imgFile.getAbsolutePath());
				Logger.log(LogLevel.k_debug, e, false);
			}
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_fatal, "Cannot save archive file: " + archiveFile.getAbsolutePath());
		}
	}
}
