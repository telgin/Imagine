package archive;

import java.io.File;
import java.io.IOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Describes how archives are read.
 */
public interface ArchiveReader extends Archive
{
	/**
	 * Read into an array of bytes
	 * @param p_bytes The array of bytes
	 * @param p_offset The index to start at
	 * @param p_length The length to read
	 * @return The index where reading stopped. This may be less than
	 * p_offset + p_length if not as many bytes could be read as were
	 * requested to be read.
	 */
	public int read(byte[] p_bytes, int p_offset, int p_length);

	/**
	 * Loads the specified archive file for reading
	 * @param p_file The archive file to load
	 * @throws IOException If the file could not be loaded
	 */
	public void loadFile(File p_file) throws IOException;

	/**
	 * Skips the requested number of bytes
	 * @param p_bytes The number of bytes to skip
	 * @return The number of bytes that were actually skipped
	 */
	public long skip(long p_bytes);
}
