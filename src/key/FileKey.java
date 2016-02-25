package key;

import java.io.File;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileKey implements Key
{
	private File f_keyLocation;
	private byte[] f_keyHash;

	/**
	 * @update_comment
	 * @param p_keyLocation
	 */
	public FileKey(File p_keyLocation)
	{
		f_keyLocation = p_keyLocation;
	}

	/* (non-Javadoc)
	 * @see key.Key#getKeyHash()
	 */
	@Override
	public byte[] getKeyHash()
	{
		if (f_keyHash == null)
		{
			fetchKey();
		}

		return f_keyHash;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public File getKeyFile()
	{
		return f_keyLocation;
	}

	/**
	 * @update_comment
	 */
	private void fetchKey()
	{
		if (f_keyLocation == null)
		{
			f_keyLocation = UIContext.getUI().promptKeyFileLocation();
			if (f_keyLocation == null)
				Logger.log(LogLevel.k_fatal, "Key file location not set.");
		}

		f_keyHash = Hashing.hash(f_keyLocation);
	}
}
