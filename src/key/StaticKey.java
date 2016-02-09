package key;

import util.Hashing;

public class StaticKey implements Key
{
	private final String TYPE = "StaticKey";

	public StaticKey()
	{
	}

	@Override
	public byte[] getKeyHash()
	{
		//TODO feed in more interesting seed
		return Hashing.hash("asdf".getBytes());
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
