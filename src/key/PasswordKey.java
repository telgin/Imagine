package key;

import ui.UIContext;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This class stores the password hash when a user enters a password.
 * The user is not prompted to enter a password until the hash is requested
 * the first time.
 */
public class PasswordKey implements Key
{
	private byte[] f_keyHash;

	/* (non-Javadoc)
	 * @see key.Key#getKeyHash()
	 */
	@Override
	public synchronized byte[] getKeyHash()
	{
		if (f_keyHash == null)
		{
			fetchKey();
		}

		return f_keyHash;
	}

	/**
	 * Prompts for the password from the user and hashes it.
	 */
	private void fetchKey()
	{
		if (f_keyHash == null)
		{
			f_keyHash = Hashing.hash(UIContext.getUI().promptKey().getBytes());
		}
	}
}
