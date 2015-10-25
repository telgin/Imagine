package product;

import java.io.File;
import java.io.IOException;

public interface ProductReader extends Product
{
	public int read(byte[] bytes, int offset, int length);

	public void loadFile(File f) throws IOException;

	public long skip(long bytes);
}
