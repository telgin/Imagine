package product;

import java.io.IOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ProductIOException extends IOException
{
	private static final long serialVersionUID = -2058437202993005146L;

	/**
	 * @update_comment
	 */
	public ProductIOException(){}
	
	/**
	 * @update_comment
	 * @param p_message
	 */
	public ProductIOException(String p_message)
	{
		super(p_message);
	}
}
