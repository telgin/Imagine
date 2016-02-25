package algorithms.imageoverlay;

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

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageOverlayReader extends ImageOverlay implements ProductReader
{
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public ImageOverlayReader(Algorithm p_algo, Key p_key)
	{
		super(p_algo, p_key);
	}

	/**
	 * @update_comment
	 * @return
	 * @throws ProductIOException
	 */
	private byte read() throws ProductIOException
	{
		byte xor = f_random.nextByte();

		if (f_density.equals(InsertionDensity.k_25))
		{
			return ByteConversion.intToByte(ByteConversion.intToByte(steps4()) ^ xor);
		}
		else //k_50
		{
			return ByteConversion.intToByte(ByteConversion.intToByte(steps16()) ^ xor);
		}
	}
	
	/**
	 * @update_comment
	 * @return
	 * @throws ProductIOException
	 */
	private final int steps4() throws ProductIOException
	{
		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(f_curPixelCoord[0], f_curPixelCoord[1]));

			f_split[i] = c % 4;
		}

		int val = (((f_split[0] * 4) + f_split[1]) * 16)
						+ ((f_split[2] * 4) + f_split[3]);
		
		return val;
	}
	
	/**
	 * @update_comment
	 * @return
	 * @throws ProductIOException
	 */
	private final int steps16() throws ProductIOException
	{
		for (int i = 0; i < 2; ++i)
		{
			nextPair();

			int c = ByteConversion.byteToInt(getColor(f_curPixelCoord[0], f_curPixelCoord[1]));

			f_split[i] = c % 16;
		}

		int val = (f_split[0] * 16) + f_split[1];
		
		return val;
	}

	/* (non-Javadoc)
	 * @see product.ProductReader#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] p_bytes, int p_offset, int p_length)
	{
		//Logger.log(LogLevel.k_debug, "Reading " + length + " bytes.");
		for (int x = p_offset; x < p_offset + p_length; ++x)
		{
			try
			{
				p_bytes[x] = read();
			}
			catch (ProductIOException e)
			{
				return x;
			}
		}

		return p_offset + p_length;
	}

	/* (non-Javadoc)
	 * @see product.ProductReader#loadFile(java.io.File)
	 */
	@Override
	public void loadFile(File p_file) throws IOException
	{
		f_img = ImageIO.read(p_file);
		reset();
	}

	/* (non-Javadoc)
	 * @see product.ProductReader#skip(long)
	 */
	@Override
	public long skip(long p_bytes)
	{
		Logger.log(LogLevel.k_debug, "Skipping " + p_bytes + " bytes.");

		long skipped = 0;
		try
		{
			for (long l = 0; l < p_bytes; ++l)
			{
				f_random.nextByte();
				
				if (f_density.equals(InsertionDensity.k_25))
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

//		Logger.log(LogLevel.k_debug, "Skipping " + p_bytes + " bytes was requested and "
//			+ skipped + " were skipped.");

		return skipped;

	}
}
