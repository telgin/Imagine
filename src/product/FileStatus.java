package product;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileStatus
{
	private File file;
	private long bytesLeft;

	
	public FileStatus(File file)
	{
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @return the bytesLeft
	 */
	public long getBytesLeft()
	{
		return bytesLeft;
	}

	/**
	 * @param bytesLeft the bytesLeft to set
	 */
	public void setBytesLeft(long bytesLeft)
	{
		this.bytesLeft = bytesLeft;
	}
}
