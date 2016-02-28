package algorithms.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import archive.ArchiveReader;
import archive.ArchiveIOException;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.algorithms.ImageUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the reading of an image archive
 */
public class ImageReader extends Image implements ArchiveReader
{
	/**
	 * Constructs an image reader
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read archives
	 */
	public ImageReader(Algorithm p_algo, Key p_key)
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
		byte secured = getImageByte(f_randOrder.next());
		return ByteConversion.intToByte(secured ^ f_random.nextByte());
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

	/**
	 * Gets the byte of the image associated with the given index
	 * @param p_index The image index
	 * @return The byte at this index
	 */
	private byte getImageByte(int p_index)
	{
		int color = p_index % 3;
		int pixel = p_index / 3;
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
				f_randOrder.next();
				f_random.nextByte();
				++skipped;
			}
		}
		catch (ArchiveIOException e)
		{
			// couldn't skip as many as requested,
			// nothing to do
		}

		Logger.log(LogLevel.k_debug, "Skipping " + p_bytes + " bytes was requested and "
			+ skipped + " were skipped.");

		return skipped;
	}
}
