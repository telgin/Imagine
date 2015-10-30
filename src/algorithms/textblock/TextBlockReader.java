package algorithms.textblock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import algorithms.ProductIOException;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductMode;
import product.ProductReader;
import util.ByteConversion;

public class TextBlockReader extends TextBlock implements ProductReader{

	public TextBlockReader(Algorithm algo, Key key) {
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
		////System.out.println();
		
		return offset + length;
	}

	@Override
	public void loadFile(File f) throws IOException
	{
		String base64 = new String(Files.readAllBytes(f.toPath()));
		buffer = ByteConversion.base64ToBytes(base64);
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
				order.next();
				random.nextByte();
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
