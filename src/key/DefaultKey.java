package key;

import config.Constants;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DefaultKey implements Key
{
	private static final String DEFAULT_PASSWORD = "Cheese Machine";

	/**
	 * @update_comment
	 */
	public DefaultKey()
	{
	}

	/* (non-Javadoc)
	 * @see key.Key#getKeyHash()
	 */
	@Override
	public byte[] getKeyHash()
	{
		return Hashing.hash(DEFAULT_PASSWORD.getBytes(Constants.CHARSET));
	}
}
