package product;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface ProductWriter extends Product
{
	/**
	 * @update_comment
	 * @throws ProductIOException
	 */
	public void newProduct() throws ProductIOException;

	/**
	 * @param p_byte
	 * @return True if written
	 */
	public boolean write(byte p_byte);

	/**
	 * @param p_bytes
	 * @param p_offset
	 * @param p_length
	 * @return The length of bytes written
	 */
	public int write(byte[] p_bytes, int p_offset, int p_length);

	/**
	 * @update_comment
	 * @param p_productStagingFolder
	 * @param p_fileName
	 */
	public void saveFile(File p_productStagingFolder, String p_fileName);

	// should be able to take an input stream reader to make things more
	// efficient.
}
