package data;

public class NullKey implements Key
{
	private final String TYPE = "NullKey";

	public NullKey()
	{
	}

	@Override
	public boolean isSecure()
	{
		return false;
	}

	@Override
	public byte[] getKeyHash()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return null;
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
