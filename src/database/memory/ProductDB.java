package database.memory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import runner.ActiveComponent;
import util.ByteConversion;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ProductDB implements ActiveComponent
{
	private Map<String, String> db;
	private Set<String> queued;
	private final String FIELD_DELIMETER = ":";
	private boolean isShutdown;
	private File dbFile;
	
	public ProductDB(File dbFile)
	{
		//Format is: hash:f1uuid:productCount
		//last two fields are stored as a string literal to save memory
		db = new HashMap<String, String>();
		
		//a place for file hashes which are queued to be loaded
		queued = new HashSet<String>();
		
		this.dbFile = dbFile;
		
		//no db is loaded yet
		isShutdown = true;
	}
	
	public void load()
	{
		isShutdown = false;
		List<String> lines = myUtilities.readListFromFile(dbFile);
		for (String line : lines)
		{
			if (line.length() > 0)
			{
				String[] parts = line.split(FIELD_DELIMETER);
				String hash = parts[0];
				String f1uuid = parts[1];
				String fragmentCount = parts[2];
				db.put(hash, f1uuid + FIELD_DELIMETER + fragmentCount);
			}
		}
	}
	
	public void save()
	{
		LinkedList<String> lines = new LinkedList<String>();
		for (String hash : db.keySet())
		{
			lines.add(hash + FIELD_DELIMETER + db.get(hash));
		}
		myUtilities.writeListToFile(dbFile, lines);
		isShutdown = true;
	}
	
	public void addRecord(byte[] hash, byte[] f1uuid, long fragmentCount)
	{
		isShutdown = false;
		String hashString = ByteConversion.bytesToHex(hash);
		db.put(hashString, ByteConversion.bytesToHex(f1uuid) + FIELD_DELIMETER +
						Long.toString(fragmentCount));
		queued.remove(hashString);
	}
	
	public boolean containsFileHash(byte[] hash)
	{
		return containsFileHash(ByteConversion.bytesToBase64(hash));
	}
	
	public synchronized void queueFileHash(String hash)
	{
		queued.add(hash);
	}
	
	public synchronized boolean isQueued(String hash)
	{
		return queued.contains(hash);
	}
	
	public boolean containsFileHash(String hash)
	{
		return db.containsKey(hash);
	}

	/* (non-Javadoc)
	 * @see runner.ActiveComponent#shutdown()
	 */
	@Override
	public void shutdown()
	{
		save();
	}

	/* (non-Javadoc)
	 * @see runner.ActiveComponent#isShutdown()
	 */
	@Override
	public boolean isShutdown()
	{
		return isShutdown;
	}
}
