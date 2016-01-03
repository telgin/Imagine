package data;

import java.io.File;

import logging.LogLevel;
import logging.Logger;
import runner.Runner;
import util.Hashing;

public class FileKey implements Key
{
	private boolean secure;
	private File keyLocation;
	private String name;
	private String groupName;
	private byte[] keyHash;
	private final String TYPE = "FileKey";

	public FileKey(String keyName, String groupName, File keyLocation)
	{
		this.name = keyName;
		this.groupName = groupName;
		this.keyLocation = keyLocation;
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
	
	public File getKeyFile()
	{
		return keyLocation;
	}

	private void fetchKey()
	{
		if (keyLocation == null)
		{
			keyLocation = Runner.getActiveGUI().promptKeyFileLocation(name, groupName);
		}

		if (keyLocation == null)
		{
			Logger.log(LogLevel.k_fatal, "Could not retrieve key file for tracking group "
							+ groupName);
		}

		keyHash = Hashing.hash(keyLocation);
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
