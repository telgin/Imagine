package algorithms.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import product.ProductWriter;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.Key;
import algorithms.Algorithm;
import algorithms.Parameter;
import algorithms.ProductIOException;

public class ImageWriter extends Image implements ProductWriter
{

	public ImageWriter(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	@Override
	public void newProduct()
	{
		// should really use the rgb configuration parameter somehow
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		reset();
	}

	@Override
	public boolean write(byte b)
	{
		try
		{
			// Logger.log(LogLevel.k_debug, "Writing " + 1 + " byte.");
			int index = randOrder.next();
			byte toSet = ByteConversion.intToByte(b ^ random.nextByte());
			setImageByte(index, toSet);
			// System.out.print(ByteConversion.bytesToHex(new byte[]{toSet}));
			return true;
		}
		catch (ProductIOException e)
		{
			return false;
		}
	}

	@Override
	public int write(byte[] bytes, int offset, int length)
	{
		// Logger.log(LogLevel.k_debug, "Writing " + bytes.length + " bytes.");
		for (int x = offset; x < offset + length; ++x)
		{
			if (!write(bytes[x]))
				return x - offset;
		}
		//// System.out.println();

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
			ImageIO.write(img, "PNG", imgFile);

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

	private void setImageByte(int index, byte data)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();

		if (color == 0)
		{
			img.setRGB(x, y, ImageUtil.setRed(img.getRGB(x, y), data));
		}
		else if (color == 1)
		{
			img.setRGB(x, y, ImageUtil.setGreen(img.getRGB(x, y), data));
		}
		else
		{
			img.setRGB(x, y, ImageUtil.setBlue(img.getRGB(x, y), data));
		}
	}
}
