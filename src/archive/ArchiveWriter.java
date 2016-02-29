package archive;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Defines the functions necessary to write archives
 */
public interface ArchiveWriter extends Archive
{
	/**
	 * Resets the archive writer to a new archive
	 * @throws ArchiveIOException If a new archive cannot be created
	 */
	public void newArchive() throws ArchiveIOException;

	/**
	 * Writes a byte of data to the archive
	 * @param p_byte The byte of data to write
	 * @return True if written
	 */
	public boolean write(byte p_byte);

	/**
	 * Writes multiple bytes of data to an archive
	 * @param p_bytes The array of bytes
	 * @param p_offset The offset to start writing from
	 * @param p_length The length to write
	 * @return The length of bytes written
	 */
	public int write(byte[] p_bytes, int p_offset, int p_length);

	/**
	 * Saves the archive file given a file name and a folder to put the file in.
	 * @param p_archiveStagingFolder The folder where the archive file should be written
	 * @param p_fileName The file name of the archive (minus the extension which is determined
	 * by the specific writer.)
	 */
	public void saveFile(File p_archiveStagingFolder, String p_fileName);
}
