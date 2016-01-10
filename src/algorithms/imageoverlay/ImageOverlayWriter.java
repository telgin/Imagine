package algorithms.imageoverlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import algorithms.Algorithm;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductWriter;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.algorithms.ImageUtil;

public class ImageOverlayWriter extends ImageOverlay implements ProductWriter
{
	private InputImageManager manager;

	public ImageOverlayWriter(Algorithm algo, Key key)
	{
		super(algo, key);
		File imageFolder = new File(algo.getParameterValue("ImageFolder"));
		ConsumptionMode mode = ConsumptionMode.parseMode(
						algo.getParameterValue("ImageConsumptionMode"));
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
		File imgFile = manager.nextImageFile();
		
		//ran out of images
		if (imgFile == null)
		{
			throw new ProductIOException("No input images remain.");
		}
		
		boolean foundFile = false;
		while (imgFile != null && !foundFile)
		{
			try
			{
				img = ImageIO.read(imgFile);
				if (img.getColorModel().hasAlpha())
				{
					removeAlpha();
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
	private void removeAlpha()
	{
		Logger.log(LogLevel.k_debug, "Removing alpha from input image.");
		
		BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		
		img = copy;
	}

	@Override
	public boolean write(byte b)
	{
		// Note: can get 18% using 4x4 or 16% using 16x2 (vs. current of 3%)
		// ~24% using 4x4
		// 22.6% using binary and zeros
		// 45.2% using '4 enforcement'
		// remains to be seen how this will affect png compression
		// 21MB may become larger? There's technically 47MB of rgb's
		// would then become 25%
		// though maybe not that bad because won't be as random as image
		// everything will still be close
		// could be even more confusing and switch modes from 1,4,16
		// based on space available
		// really complicated function, but could probably be efficient too
		
		try
		{
			int secured = ByteConversion
							.byteToInt(ByteConversion.intToByte(b ^ random.nextByte()));

			fourEnforcement(secured);
		}
		catch (ProductIOException e)
		{
			//this happens naturally when the image file get's filled up
			return false;
		}

		++byteCount;
		return true;
	}

	private final void fourEnforcement(int val) throws ProductIOException
	{
		int div16 = val / 16;
		int mod16 = val % 16;

		int[] fourVals = new int[] { div16 / 4, div16 % 4, mod16 / 4, mod16 % 4 };

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));

			int avg = (c1 + c2) / 2;
			int toSet = -1;

			if (avg > 2)
			{
				toSet = (avg - 3) + fourVals[i];
				setColor(pv[0], pv[1], ByteConversion.intToByte(toSet));
			}
			else
			{
				toSet = (avg + 3) - fourVals[i];
				setColor(pv[0], pv[1], ByteConversion.intToByte(toSet));
			}
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
		try
		{
			File imgFile = new File(productStagingFolder.getAbsolutePath(), fileName + ".png");
			Logger.log(LogLevel.k_info,
							"Saving product file: " + imgFile.getAbsolutePath());
			
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			
			ImageIO.write(img, "png", imgFile);

			// update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
