package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import key.Key;
import product.ProductIOException;
import product.ProductReader;
import util.ByteConversion;

public class TextReader extends Text implements ProductReader{

	public TextReader(Algorithm algo, Key key) {
		super(algo, key);
	}
	
	private final byte read() throws ProductIOException
	{
		byte xor = random.nextByte();
		byte val = buffer[order.next()];
		
		return ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
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
		String encoded = new String(Files.readAllBytes(f.toPath()));
		if (algorithm.getParameter(Definition.ENCODING_PARAM).getValue().equals(Definition.BASE64_ENCODING))
			buffer = ByteConversion.base64ToBytes(encoded);
		else
			buffer = ByteConversion.hexToBytes(encoded);
		
		blockSize = buffer.length;
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
				//order matters
				random.nextByte();
				order.next();
				
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
