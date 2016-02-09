package algorithms.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductReader;
import util.ByteConversion;
import util.algorithms.ImageUtil;
import algorithms.Algorithm;
import key.Key;

public class ImageReader extends Image implements ProductReader
{

	public ImageReader(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	private byte read() throws ProductIOException
	{
		// Logger.log(LogLevel.k_debug, "Reading " + 1 + " byte.");
		byte secured = getImageByte(randOrder.next());
		// System.out.print(ByteConversion.bytesToHex(new byte[]{secured}));
		return ByteConversion.intToByte(secured ^ random.nextByte());
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		// Logger.log(LogLevel.k_debug, "Reading " + bytes.length + " bytes.");
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
		//// System.out.println();

		return offset + length;
	}

	private byte getImageByte(int index)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();

		if (color == 0)
		{
			return ImageUtil.getRed(img.getRGB(x, y));
		}
		else if (color == 1)
		{
			return ImageUtil.getGreen(img.getRGB(x, y));
		}
		else
		{
			return ImageUtil.getBlue(img.getRGB(x, y));
		}
	}

	@Override
	public void loadFile(File f) throws IOException
	{
		img = ImageIO.read(f);
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
				randOrder.next();
				random.nextByte();
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
