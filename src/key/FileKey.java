package key;

import java.io.File;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The file key hashes a file to be used as a seed for
 * the algorithms.
 */
public class FileKey implements Key
{
	private File f_keyLocation;
	private byte[] f_keyHash;

	/**
	 * Constructs a file key for some specified file. The hashing is
	 * done at the first time the hash is requested, not during construction.
	 * @param p_keyLocation The key file location
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
	 * Gets the file this key is using
	 * @return The key file location
	 */
	public File getKeyFile()
	{
		return f_keyLocation;
	}

	/**
	 * Gets the key file location if it is not specified and then
	 * hashes the file.
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
