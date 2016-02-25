package algorithms.imageoverlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.Option;
import api.UsageException;
import config.Settings;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJobFileState;
import product.ProductIOException;
import product.ProductWriter;
import report.JobStatus;
import util.ByteConversion;
import util.algorithms.ImageUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageOverlayWriter extends ImageOverlay implements ProductWriter
{
	private InputImageManager f_manager;
	private File f_imgFile;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
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
	 * @see product.ProductWriter#newProduct()
	 */
	@Override
	public void newProduct() throws ProductIOException
	{
		loadCleanFile();
		reset();
	}

	/**
	 * @update_comment
	 * @throws ProductIOException
	 */
	private void loadCleanFile() throws ProductIOException
	{		
		//get the next image
		f_imgFile = f_manager.nextImageFile();
		
		//ran out of images
		if (f_imgFile == null)
		{
			throw new ProductIOException("No input images remain.");
		}
		else
		{
			//update status to show this new image file is about to be used
			if (Settings.trackFileStatus())
				JobStatus.setConversionJobFileStatus(f_imgFile, ConversionJobFileState.WRITING);
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
			throw new ProductIOException("No input images remain.");
		}
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/26918675/removing-transparency-in-png-bufferedimage
	 * @update_comment
	 */
	private void reinterpretColorModel()
	{
		Logger.log(LogLevel.k_debug, "Changing input image color model to rgb.");
		
		BufferedImage copy = new BufferedImage(f_img.getWidth(), f_img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(f_img, 0, 0, null);
		g2d.dispose();
		
		f_img = copy;
	}

	/* (non-Javadoc)
	 * @see product.ProductWriter#write(byte)
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
		catch (ProductIOException e)
		{
			//this happens naturally when the image file get's filled up
			return false;
		}

		return true;
	}
	
	/**
	 * @update_comment
	 * @param p_val
	 * @throws ProductIOException
	 */
	private final void steps4(int p_val) throws ProductIOException
	{
		int div16 = p_val / 16;
		int mod16 = p_val % 16;

		f_split[0] = div16 / 4;
		f_split[1] = div16 % 4;
		f_split[2] = mod16 / 4;
		f_split[3] = mod16 % 4;

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(f_curPixelCoord[0], f_curPixelCoord[1]));

			int step = (c / 4) * 4;

			setColor(f_curPixelCoord[0], f_curPixelCoord[1], 
							ByteConversion.intToByte(step + f_split[i]));
		}
	}
	
	/**
	 * @update_comment
	 * @param p_val
	 * @throws ProductIOException
	 */
	private final void steps16(int p_val) throws ProductIOException
	{
		f_split[0] = p_val / 16;
		f_split[1] = p_val % 16;

		for (int i = 0; i < 2; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(f_curPixelCoord[0], f_curPixelCoord[1]));

			int step = (c / 16) * 16;

			setColor(f_curPixelCoord[0], f_curPixelCoord[1],
							ByteConversion.intToByte(step + f_split[i]));
		}
	}

	/**
	 * @update_comment
	 * @param p_x
	 * @param p_y
	 * @param p_data
	 */
	private void setColor(int p_x, int p_y, byte p_data)
	{
		if (f_colorIndex == 0)
		{
			f_img.setRGB(p_x, p_y, ImageUtil.setRed(f_img.getRGB(p_x, p_y), p_data));

		}
		else if (f_colorIndex == 1)
		{
			f_img.setRGB(p_x, p_y, ImageUtil.setGreen(f_img.getRGB(p_x, p_y), p_data));
		}
		else
		{
			f_img.setRGB(p_x, p_y, ImageUtil.setBlue(f_img.getRGB(p_x, p_y), p_data));
		}
	}

	/* (non-Javadoc)
	 * @see product.ProductWriter#write(byte[], int, int)
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
	 * @see product.ProductWriter#saveFile(java.io.File, java.lang.String)
	 */
	@Override
	public void saveFile(File p_productStagingFolder, String p_fileName)
	{
		File productFile = new File(p_productStagingFolder.getAbsolutePath(), p_fileName + ".png");
		Logger.log(LogLevel.k_info, "Saving product file: " + productFile.getAbsolutePath());
		
		try
		{
			if (!productFile.getParentFile().exists())
				productFile.getParentFile().mkdirs();
			
			ImageIO.write(f_img, "png", productFile);

			// update progress
			JobStatus.incrementProductsCreated(1);

			try
			{
				//update manager
				f_manager.setFileUsed(f_imgFile);
				
				//update status to show previous image file was used
				if (Settings.trackFileStatus())
					JobStatus.setConversionJobFileStatus(f_imgFile, ConversionJobFileState.FINISHED);
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
			Logger.log(LogLevel.k_fatal, "Cannot save archive file: " + productFile.getAbsolutePath());
		}
	}
}
