package product;

import java.io.File;

public interface ProductWriter extends Product
{
	public void newProduct() throws ProductIOException;

	/**
	 * @param b
	 * @return True if written
	 */
	public boolean write(byte b);

	/**
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return The length of bytes written
	 */
	public int write(byte[] bytes, int offset, int length);

	public void saveFile(File productStagingFolder, String fileName);

	// should be able to take an input stream reader to make things more
	// efficient.
}
