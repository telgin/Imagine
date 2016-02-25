package api;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class UsageException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @update_comment
	 */
	public UsageException(){}
	
	/**
	 * @update_comment
	 * @param p_message
	 */
	public UsageException(String p_message)
	{
		super(p_message);
	}
}
