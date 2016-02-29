package archive;

import java.io.IOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * An exception used when something goes wrong with reading or writing an archive.
 */
public class ArchiveIOException extends IOException
{
	private static final long serialVersionUID = -2058437202993005146L;

	/**
	 * Constructs an archive io exception
	 */
	public ArchiveIOException(){}
	
	/**
	 * Constructs an archive io exception
	 * @param p_message The message text
	 */
	public ArchiveIOException(String p_message)
	{
		super(p_message);
	}
}
