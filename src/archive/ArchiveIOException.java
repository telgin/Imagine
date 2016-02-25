package archive;

import java.io.IOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ArchiveIOException extends IOException
{
	private static final long serialVersionUID = -2058437202993005146L;

	/**
	 * @update_comment
	 */
	public ArchiveIOException(){}
	
	/**
	 * @update_comment
	 * @param p_message
	 */
	public ArchiveIOException(String p_message)
	{
		super(p_message);
	}
}
