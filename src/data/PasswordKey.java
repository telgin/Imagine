package data;

import ui.UIContext;
import util.Hashing;

public class PasswordKey implements Key
{
	private boolean secure;
	private String name;
	private String groupName;
	private byte[] keyHash;
	private final String TYPE = "PasswordKey";

	public PasswordKey(String keyName, String groupName)
	{
		this.name = keyName;
		this.groupName = groupName;
		secure = true;
	}

	@Override
	public boolean isSecure()
	{
		return secure;
	}

	@Override
	public byte[] getKeyHash()
	{
		if (keyHash == null)
		{
			fetchKey();
		}

		return keyHash;
	}

	private void fetchKey()
	{
		if (keyHash == null)
		{
			keyHash = Hashing.hash(
							UIContext.getUI().promptKey(name, groupName).getBytes());
		}
	}

	@Override
	public String getName()
	{
		return name;
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
