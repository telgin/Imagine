package key;

import config.Constants;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DefaultKey implements Key
{
	private static final String TYPE = "StaticKey";
	private static final String s_defaultSeed = "Cheese Machine";

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
		return Hashing.hash(s_defaultSeed.getBytes(Constants.CHARSET));
	}

	/* (non-Javadoc)
	 * @see data.Key#getType()
	 */
	@Override
	public String getType()
	{
		return TYPE;
	}

}
