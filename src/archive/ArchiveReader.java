package archive;

import java.io.File;
import java.io.IOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface ArchiveReader extends Archive
{
	/**
	 * @update_comment
	 * @param p_bytes
	 * @param p_offset
	 * @param p_length
	 * @return
	 */
	public int read(byte[] p_bytes, int p_offset, int p_length);

	/**
	 * @update_comment
	 * @param p_file
	 * @throws IOException
	 */
	public void loadFile(File p_file) throws IOException;

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public long skip(long p_bytes);
}
