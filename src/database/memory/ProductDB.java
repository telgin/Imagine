package database.memory;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.ByteConversion;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ProductDB
{
	private Map<String, String> db;
	private final String FIELD_DELIMETER = ":";
	
	public ProductDB()
	{
		//Format is: hash:f1uuid:productCount
		//last two fields are stored as a string literal to save memory
		db = new HashMap<String, String>();
	}
	
	public void load(File dbFile)
	{
		List<String> lines = myUtilities.readListFromFile(dbFile);
		for (String line : lines)
		{
			String[] parts = line.split(FIELD_DELIMETER);
			String hash = parts[0];
			String f1uuid = parts[1];
			String fragmentCount = parts[2];
			db.put(hash, f1uuid + FIELD_DELIMETER + fragmentCount);
		}
	}
	
	public void save(File dbFile)
	{
		LinkedList<String> lines = new LinkedList<String>();
		for (String hash : db.keySet())
		{
			lines.add(hash + FIELD_DELIMETER + db.get(hash));
		}
		myUtilities.writeListToFile(dbFile, lines);
	}
	
	public void addRecord(byte[] hash, byte[] f1uuid, long fragmentCount)
	{
		db.put(ByteConversion.bytesToHex(hash), 
						ByteConversion.bytesToHex(f1uuid) + FIELD_DELIMETER +
						Long.toString(fragmentCount));
	}
	
	public boolean containsFileHash(byte[] hash)
	{
		return containsFileHash(ByteConversion.bytesToBase64(hash));
	}
	
	public boolean containsFileHash(String hash)
	{
		return db.containsKey(hash);
	}
}
