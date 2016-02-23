package algorithms.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductReader;
import util.ByteConversion;
import util.algorithms.ImageUtil;

public class ImageReader extends Image implements ProductReader
{
	public ImageReader(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	private byte read() throws ProductIOException
	{
		byte secured = getImageByte(f_randOrder.next());
		return ByteConversion.intToByte(secured ^ f_random.nextByte());
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		for (int x = offset; x < offset + length; ++x)
		{
			try
			{
				bytes[x] = read();
			}
			catch (ProductIOException e)
			{
				return x;
			}
		}

		return offset + length;
	}

	private byte getImageByte(int index)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / f_img.getWidth();
		int x = pixel % f_img.getWidth();

		if (color == 0)
		{
			return ImageUtil.getRed(f_img.getRGB(x, y));
		}
		else if (color == 1)
		{
			return ImageUtil.getGreen(f_img.getRGB(x, y));
		}
		else
		{
			return ImageUtil.getBlue(f_img.getRGB(x, y));
		}
	}

	@Override
	public void loadFile(File f) throws IOException
	{
		f_img = ImageIO.read(f);
		reset();
	}

	@Override
	public long skip(long bytes)
	{
		long skipped = 0;
		try
		{
			for (long l = 0; l < bytes; ++l)
			{
				f_randOrder.next();
				f_random.nextByte();
				++skipped;
			}
		}
		catch (ProductIOException e)
		{
			// couldn't skip as many as requested,
			// nothing to do
		}

		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes was requested and "
						+ skipped + " were skipped.");

		return skipped;
	}
}
