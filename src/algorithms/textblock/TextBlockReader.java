package algorithms.textblock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import product.ProductMode;
import product.ProductReader;

public class TextBlockReader extends TextBlock implements ProductReader{

	public TextBlockReader(Algorithm algo) {
		super(algo);
	}

	@Override
	public ProductMode getProductMode() {
		return algorithm.getProductSecurityLevel();
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		int toRead = Math.min((buffer.length - 1) - index, length);
		System.arraycopy(buffer, index, bytes, offset, toRead);
		index += toRead;
		return toRead;
		//System.out.println("Read: " + bytes.length);
	}

	@Override
	public void loadFile(File f) throws IOException
	{
		index = 0;
		buffer = Files.readAllBytes(f.toPath());
	}

	@Override
	public long skip(long bytes)
	{
		long toSkip = Math.min((buffer.length - 1) - index, bytes);
		index += toSkip;
		return toSkip;
	}
}
