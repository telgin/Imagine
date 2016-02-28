package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import archive.ArchiveReader;
import archive.ArchiveIOException;
import key.Key;
import util.ByteConversion;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the reading of an image overlay archive
 */
public class ImageOverlayReader extends ImageOverlay implements ArchiveReader
{
	/**
	 * Constructs an image overlay reader
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read archives
	 */
	public ImageOverlayReader(Algorithm p_algo, Key p_key)
	{
		super(p_algo, p_key);
	}

	/**
	 * Reads a byte of data from the archive
	 * @return The byte that was read
	 * @throws ArchiveIOException if there are no more bytes to read
	 */
	private byte read() throws ArchiveIOException
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
	 * Reads two bits of file data per color
	 * @return The file data byte as an int
	 * @throws ArchiveIOException If there are no bytes left to read
	 */
	private final int steps4() throws ArchiveIOException
	{
		for (int i = 0; i < 4; ++i)
		{
			nextColor();

			int c = ByteConversion.byteToInt(getColor());

			f_split[i] = c % 4;
		}

		int val = (((f_split[0] * 4) + f_split[1]) * 16)
						+ ((f_split[2] * 4) + f_split[3]);
		
		return val;
	}
	
	/**
	 * Reads four bits of file data per color
	 * @return The file data byte as an int
	 * @throws ArchiveIOException If there are no bytes left to read
	 */
	private final int steps16() throws ArchiveIOException
	{
		for (int i = 0; i < 2; ++i)
		{
			nextColor();

			int c = ByteConversion.byteToInt(getColor());

			f_split[i] = c % 16;
		}

		int val = (f_split[0] * 16) + f_split[1];
		
		return val;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReader#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] p_bytes, int p_offset, int p_length)
	{
		for (int x = p_offset; x < p_offset + p_length; ++x)
		{
			try
			{
				p_bytes[x] = read();
			}
			catch (ArchiveIOException e)
			{
				return x;
			}
		}

		return p_offset + p_length;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReader#loadFile(java.io.File)
	 */
	@Override
	public void loadFile(File p_file) throws IOException
	{
		f_img = ImageIO.read(p_file);
		reset();
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReader#skip(long)
	 */
	@Override
	public long skip(long p_bytes)
	{
		long skipped = 0;
		try
		{
			for (long l = 0; l < p_bytes; ++l)
			{
				f_random.nextByte();
				
				if (f_density.equals(InsertionDensity.k_25))
				{
					nextColor();
					nextColor();
					nextColor();
					nextColor();
				}
				else //k_50
				{
					nextColor();
					nextColor();
				}

				++skipped;
			}
		}
		catch (ArchiveIOException e)
		{
			// couldn't skip as many as requested,
			// nothing to do
		}

		return skipped;

	}
}
