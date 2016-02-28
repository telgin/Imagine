package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import algorithms.Algorithm;
import archive.ArchiveWriter;
import archive.ArchiveIOException;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;
import util.ByteConversion;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the writing of a text archive
 */
public class TextWriter extends Text implements ArchiveWriter
{
	/**
	 * Constructs a text writer
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to write archives
	 */
	public TextWriter(Algorithm p_algo, Key p_key)
	{
		super(p_algo, p_key);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriter#newArchive()
	 */
	@Override
	public void newArchive()
	{
		f_buffer = new byte[f_blockSize];
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
			byte val = ByteConversion.intToByte(p_byte ^ f_random.nextByte());
			f_buffer[f_order.next()] = val;
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
	public void saveFile(File p_archiveStagingFolder, String p_filename)
	{
		// write random bytes to fill up the buffer
		fillToEnd();

		try
		{
			File toSave = new File(p_archiveStagingFolder.getAbsolutePath(), p_filename + ".txt");
			Logger.log(LogLevel.k_info, "Saving archive file: " + toSave.getAbsolutePath());

			PrintWriter writer = new PrintWriter(toSave);
			if (f_algorithm.getParameter(Definition.ENCODING_PARAM).getValue().equals(Definition.BASE64_ENCODING))
				writer.print(ByteConversion.bytesToBase64(f_buffer));
			else
				writer.print(ByteConversion.bytesToHex(f_buffer));

			writer.close();

			// update progress
			JobStatus.incrementArchivesCreated(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Fills up the rest of the buffer with random bytes
	 */
	private void fillToEnd()
	{
		while (f_order.hasRemainingNumbers())
		{
			write(f_random.nextByte());
		}
	}
}
