package api;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A custom exception which indicates that an api call was called incorrectly. More
 * generally, this is meant to indicate programmer or user error in regards to usage.
 */
public class UsageException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a usage exception
	 */
	public UsageException(){}
	
	/**
	 * Constructs a usage exception with a message
	 * @param p_message The exception message
	 */
	public UsageException(String p_message)
	{
		super(p_message);
	}
}
