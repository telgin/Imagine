package algorithms.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import archive.ArchiveWriter;
import archive.ArchiveIOException;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;
import util.ByteConversion;
import util.algorithms.ImageUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the writing of an image archive
 */
public class ImageWriter extends Image implements ArchiveWriter
{

	/**
	 * Constructs an image writer
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to write archives
	 */
	public ImageWriter(Algorithm p_algo, Key p_key)
	{
		super(p_algo, p_key);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#newArchive()
	 */
	@Override
	public void newArchive()
	{
		// should really use the rgb configuration parameter somehow
		f_img = new BufferedImage(f_width, f_height, BufferedImage.TYPE_INT_RGB);

		reset();
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#write(byte)
	 */
	@Override
	public boolean write(byte p_byte)
	{
		try
		{
			int index = f_randOrder.next();
			byte toSet = ByteConversion.intToByte(p_byte ^ f_random.nextByte());
			setImageByte(index, toSet);
			return true;
		}
		catch (ArchiveIOException e)
		{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#write(byte[], int, int)
	 */
	@Override
	public int write(byte[] p_bytes, int p_offset, int p_length)
	{
		for (int x = p_offset; x < p_offset + p_length; ++x)
		{
			if (!write(p_bytes[x]))
				return x - p_offset;
		}

		return p_length;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#saveFile(java.io.File, java.lang.String)
	 */
	@Override
	public void saveFile(File p_archiveStagingFolder, String p_fileName)
	{
		try
		{
			File imgFile = new File(p_archiveStagingFolder.getAbsolutePath(), p_fileName + ".png");
			Logger.log(LogLevel.k_info,
							"Saving archive file: " + imgFile.getAbsolutePath());
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			ImageIO.write(f_img, "PNG", imgFile);

			// update progress
			JobStatus.incrementArchivesCreated(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sets the byte of data in the image at the given index
	 * @param p_index The image index
	 * @param p_data The byte to set
	 */
	private void setImageByte(int p_index, byte p_data)
	{
		int color = p_index % 3;
		int pixel = p_index / 3;
		int y = pixel / f_img.getWidth();
		int x = pixel % f_img.getWidth();

		if (color == 0)
		{
			f_img.setRGB(x, y, ImageUtil.setRed(f_img.getRGB(x, y), p_data));
		}
		else if (color == 1)
		{
			f_img.setRGB(x, y, ImageUtil.setGreen(f_img.getRGB(x, y), p_data));
		}
		else
		{
			f_img.setRGB(x, y, ImageUtil.setBlue(f_img.getRGB(x, y), p_data));
		}
	}
}
