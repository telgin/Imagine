package key;

import java.io.File;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.Hashing;

public class FileKey implements Key
{
	private File keyLocation;
	private byte[] keyHash;
	private final String TYPE = "FileKey";

	public FileKey(File keyLocation)
	{
		this.keyLocation = keyLocation;
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
	
	public File getKeyFile()
	{
		return keyLocation;
	}

	private void fetchKey()
	{
		if (keyLocation == null)
		{
			keyLocation = UIContext.getUI().promptKeyFileLocation();
			if (keyLocation == null)
				Logger.log(LogLevel.k_fatal, "Key file location not set.");
		}

		keyHash = Hashing.hash(keyLocation);
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
