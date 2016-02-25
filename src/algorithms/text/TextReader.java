package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import key.Key;
import product.ProductIOException;
import product.ProductReader;
import util.ByteConversion;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TextReader extends Text implements ProductReader{

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
	 * @throws ProductIOException
	 */
	private final byte read() throws ProductIOException
	{
		byte xor = f_random.nextByte();
		byte val = f_buffer[f_order.next()];
		
		return ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
	}

	/* (non-Javadoc)
	 * @see product.ProductReader#read(byte[], int, int)
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
		String encoded = new String(Files.readAllBytes(p_file.toPath()));
		if (f_algorithm.getParameter(Definition.ENCODING_PARAM).getValue().equals(Definition.BASE64_ENCODING))
			f_buffer = ByteConversion.base64ToBytes(encoded);
		else
			f_buffer = ByteConversion.hexToBytes(encoded);
		
		f_blockSize = f_buffer.length;
		reset();
	}

	/* (non-Javadoc)
	 * @see product.ProductReader#skip(long)
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
		catch (ProductIOException e)
		{
			// couldn't skip as many as requested,
			// nothing to do
		}

		return skipped;
	}
}
