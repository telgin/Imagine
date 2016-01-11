package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import algorithms.Algorithm;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductReader;
import util.ByteConversion;

public class ImageOverlayReader extends ImageOverlay implements ProductReader
{

	public ImageOverlayReader(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	private byte read() throws ProductIOException
	{
		byte xor = random.nextByte();

		if (density.equals(InsertionDensity.k_25))
		{
			return ByteConversion.intToByte(ByteConversion.intToByte(steps4()) ^ xor);
		}
		else //k_50
		{
			return ByteConversion.intToByte(ByteConversion.intToByte(steps16()) ^ xor);
		}
	}
	
	private final int steps4() throws ProductIOException
	{
		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(curPixelCoord[0], curPixelCoord[1]));

			split[i] = c % 4;
		}

		int val = (((split[0] * 4) + split[1]) * 16)
						+ ((split[2] * 4) + split[3]);
		
		return val;
	}
	
	private final int steps16() throws ProductIOException
	{
		for (int i = 0; i < 2; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(curPixelCoord[0], curPixelCoord[1]));

			split[i] = c % 16;
		}

		int val = (split[0] * 16) + split[1];
		
		return val;
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		//Logger.log(LogLevel.k_debug, "Reading " + length + " bytes.");
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

	@Override
	public void loadFile(File f) throws IOException
	{
		img = ImageIO.read(f);
		reset();
	}

	@Override
	public long skip(long bytes)
	{
		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes.");

		long skipped = 0;
		try
		{
			for (long l = 0; l < bytes; ++l)
			{
				random.nextByte();
				
				if (density.equals(InsertionDensity.k_25))
				{
					nextPair();
					nextPair();
					nextPair();
					nextPair();
				}
				else //k_50
				{
					nextPair();
					nextPair();
				}

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
