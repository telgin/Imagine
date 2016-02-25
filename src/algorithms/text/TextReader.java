package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import archive.ArchiveReader;
import archive.ArchiveIOException;
import key.Key;
import util.ByteConversion;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TextReader extends Text implements ArchiveReader{

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public TextReader(Algorithm p_algo, Key p_key) {
		super(p_algo, p_key);
	}
	
	/**
	 * @update_comment
	 * @return
	 * @throws ArchiveIOException
	 */
	private final byte read() throws ArchiveIOException
	{
		byte xor = f_random.nextByte();
		byte val = f_buffer[f_order.next()];
		
		return ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
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
		String encoded = new String(Files.readAllBytes(p_file.toPath()));
		if (f_algorithm.getParameter(Definition.ENCODING_PARAM).getValue().equals(Definition.BASE64_ENCODING))
			f_buffer = ByteConversion.base64ToBytes(encoded);
		else
			f_buffer = ByteConversion.hexToBytes(encoded);
		
		f_blockSize = f_buffer.length;
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
				//order matters
				f_random.nextByte();
				f_order.next();
				
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
