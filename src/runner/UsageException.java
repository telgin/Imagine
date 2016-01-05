package runner;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class UsageException extends Exception
{
	/**
	 * @update_comment
	 */
	private static final long serialVersionUID = 1L;

	public UsageException(){}
	
	public UsageException(String message)
	{
		super(message);
	}
}
