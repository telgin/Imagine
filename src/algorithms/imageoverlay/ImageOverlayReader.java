package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.imageoverlay.patterns.Pattern;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductMode;
import product.ProductReader;
import scratch.Scratch;
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

		int[] fourVals = new int[] { 0, 0, 0, 0 };

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c0 = ByteConversion.byteToInt(getColor(pv[0], pv[1]));
			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));

			int avg = (c1 + c2) / 2;
			if (avg > 2)
			{
				fourVals[i] = c0 - (avg - 3);
			}
			else
			{
				fourVals[i] = (avg + 3) - c0;
			}
		}

		int val = (((fourVals[0] * 4) + fourVals[1]) * 16)
						+ ((fourVals[2] * 4) + fourVals[3]);

		++byteCount;

		byte toReturn =  ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
		
		return toReturn;
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
				
				for (int i = 0; i < 4; ++i)
				{
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
		byteCount += bytes;

		return skipped;

	}
}
