package algorithms.imageoverlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import api.UsageException;
import config.Settings;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJobFileState;
import product.JobStatus;
import product.ProductIOException;
import product.ProductWriter;
import util.ByteConversion;
import util.algorithms.ImageUtil;

public class ImageOverlayWriter extends ImageOverlay implements ProductWriter
{
	private InputImageManager manager;
	private File imgFile;

	public ImageOverlayWriter(Algorithm algo, Key key)
	{
		super(algo, key);
		File imageFolder = new File(algo.getParameterValue(Definition.IMAGE_FOLDER_PARAM));
		
		//prompt for the image folder if the one listed doesn't exist
		if (!imageFolder.exists())
		{
			try
			{
				algo.setParameter(Definition.IMAGE_FOLDER_PARAM, Option.PROMPT_OPTION.getValue());
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_fatal, e.getMessage());
			}
			
			imageFolder = new File(algo.getParameterValue(Definition.IMAGE_FOLDER_PARAM));
		}
		
		ConsumptionMode mode = ConsumptionMode.parseMode(
						algo.getParameterValue(Definition.IMAGE_CONSUMPTION_MODE_PARAM));
		manager = InputImageManager.getInstance(imageFolder, mode);
	}

	@Override
	public void newProduct() throws ProductIOException
	{
		loadCleanFile();
		reset();
	}

	private void loadCleanFile() throws ProductIOException
	{		
		//get the next image
		imgFile = manager.nextImageFile();
		
		//ran out of images
		if (imgFile == null)
		{
			throw new ProductIOException("No input images remain.");
		}
		else
		{
			//update status to show this new image file is about to be used
			if (Settings.trackFileStatus())
				JobStatus.setConversionJobFileStatus(imgFile, ConversionJobFileState.WRITING);
		}
		
		boolean foundFile = false;
		while (imgFile != null && !foundFile)
		{
			try
			{
				img = ImageIO.read(imgFile);
				
				//using the faster rgb math operations requires
				//a standard color model
				if (img.getType() != BufferedImage.TYPE_INT_RGB)
				{
					reinterpretColorModel();
				}
				foundFile = true;
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not interpret input image file: " + imgFile.getName());
			}
		}
		
		//ran out of images after trying some unsuccessfully
		if (imgFile == null)
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
		
		BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		
		img = copy;
	}

	@Override
	public boolean write(byte b)
	{
		try
		{
			int secured = ByteConversion
							.byteToInt(ByteConversion.intToByte(b ^ random.nextByte()));
			
			if (density.equals(InsertionDensity.k_25))
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
	
	private final void steps4(int val) throws ProductIOException
	{
		int div16 = val / 16;
		int mod16 = val % 16;

		split[0] = div16 / 4;
		split[1] = div16 % 4;
		split[2] = mod16 / 4;
		split[3] = mod16 % 4;

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(curPixelCoord[0], curPixelCoord[1]));

			int step = (c / 4) * 4;

			setColor(curPixelCoord[0], curPixelCoord[1], 
							ByteConversion.intToByte(step + split[i]));
		}
	}
	
	private final void steps16(int val) throws ProductIOException
	{
		split[0] = val / 16;
		split[1] = val % 16;

		for (int i = 0; i < 2; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(curPixelCoord[0], curPixelCoord[1]));

			int step = (c / 16) * 16;

			setColor(curPixelCoord[0], curPixelCoord[1],
							ByteConversion.intToByte(step + split[i]));
		}
	}

	private void setColor(int x, int y, byte data)
	{
		if (colorIndex == 0)
		{
			img.setRGB(x, y, ImageUtil.setRed(img.getRGB(x, y), data));

		}
		else if (colorIndex == 1)
		{
			img.setRGB(x, y, ImageUtil.setGreen(img.getRGB(x, y), data));
		}
		else
		{
			img.setRGB(x, y, ImageUtil.setBlue(img.getRGB(x, y), data));
		}
	}

	@Override
	public int write(byte[] bytes, int offset, int length)
	{
		for (int x = offset; x < offset + length; ++x)
		{
			if (!write(bytes[x]))
				return x - offset;
		}

		return length;
	}

	@Override
	public void saveFile(File productStagingFolder, String fileName)
	{
		File productFile = new File(productStagingFolder.getAbsolutePath(), fileName + ".png");
		Logger.log(LogLevel.k_info, "Saving product file: " + productFile.getAbsolutePath());
		
		try
		{
			if (!productFile.getParentFile().exists())
				productFile.getParentFile().mkdirs();
			
			ImageIO.write(img, "png", productFile);

			// update progress
			JobStatus.incrementProductsCreated(1);

			try
			{
				//update manager
				manager.setFileUsed(imgFile);
				
				//update status to show previous image file was used
				if (Settings.trackFileStatus())
					JobStatus.setConversionJobFileStatus(imgFile, ConversionJobFileState.FINISHED);
			}
			catch (IOException e)
			{
				//probably the file doesn't exist anymore, that's ok
				//otherwise it's a permissions problem
				Logger.log(LogLevel.k_debug, "The target image file could not be 'set used'. "
								+ "This may be ok. " + imgFile.getAbsolutePath());
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
