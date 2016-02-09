package key;

import ui.UIContext;
import util.Hashing;

public class PasswordKey implements Key
{
	private byte[] keyHash;
	private final String TYPE = "PasswordKey";

	@Override
	public synchronized byte[] getKeyHash()
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
			keyHash = Hashing.hash(UIContext.getUI().promptKey().getBytes());
		}
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
