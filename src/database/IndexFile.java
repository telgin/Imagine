package database;

import hibernate.Metadata;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import data.TrackingGroup;
import util.ByteConversion;
import util.Constants;
import util.Hashing;
import util.myUtilities;

import logging.LogLevel;
import logging.Logger;

public class IndexFile {
	private static final String INDEX_FOLDER_NAME = "." +
			Constants.APPLICATION_NAME_SHORT.toLowerCase();
	private static final String INDEX_FILE_VERSION = "0.0.0";
	
	private File path;
	private HashMap<byte[], FileRecord> records;
	
	private IndexFile()
	{
		records = new HashMap<byte[], FileRecord>();
	}
	
	private void addFileRecord(FileRecord fileRecord) {
		records.put(fileRecord.getFilePathHash(), fileRecord);
	}

	public void saveMetadata(Metadata metadata) {
		boolean found = true;
		byte[] pathHash = Hashing.hash(metadata.getFile().getAbsolutePath().getBytes());
		FileRecord record = records.get(pathHash);
		if (record == null)
		{
			found = false;
			record = new FileRecord();
		}
		
		//update all fields
		record.setFilePathHash(Hashing.hash(metadata.getPath().getBytes()));
		record.setFileHash(metadata.getFileHash());
		record.setDateCreated(metadata.getDateCreated());
		record.setDateModified(metadata.getDateModified());
		record.setPermissions(metadata.getPermissions());
		record.setFragment1ProductUUID(metadata.getProductUUID());
		
		if (!found)
		{
			records.put(pathHash, record);
		}
	}

	/**
	 * @return the path
	 */
	public File getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(File path) {
		this.path = path;
	}
	
	public void save()
	{
		
	}
	
	public Metadata getFileMetadata(File f) {
		FileRecord record = records.get(Hashing.hash(f.getAbsolutePath().getBytes()));
		
		if (record != null)
		{
			Metadata metadata = new Metadata();
			metadata.setFile(f);
			metadata.setDateCreated(record.getDateCreated());
			metadata.setDateModified(record.getDateModified());
			metadata.setFileHash(record.getFileHash());
			metadata.setPermissions(record.getPermissions());
			metadata.setPreviousProductUUID(record.getFragment1ProductUUID());
			return metadata;
		}
		else
		{
			return null;
		}
		
	}
	
	
	
	
	//static loading functions:
	
	public static IndexFile loadIndex(File lookup, TrackingGroup group)
	{		
		File indexFileLocation = findIndexFile(lookup, group);

		if (indexFileLocation == null)
		{
			//the file does not exist, and a new one can't be created
			return null;
		}
		
		return parseIndexFile(indexFileLocation);
			
	}
	
	private static IndexFile parseIndexFile(File indexFileLocation) {
		IndexFile indexFile = new IndexFile();
		
		List<String> recordLines = myUtilities.readListFromFile(indexFileLocation);
		for (String line:recordLines)
			indexFile.addFileRecord(new FileRecord(line));
		
		return indexFile;
	}

	public static File findIndexFile(File lookup, TrackingGroup group) {
		File indexFolder = null;
		
		if (lookup.isDirectory())
		{
			Logger.log(LogLevel.k_error, "Trying to load index file for a directory: " + lookup.getPath());
		}
		
		File curFolder = lookup.getAbsoluteFile().getParentFile();
		
		//use the pre-existing folder, or create a new one
		while (indexFolder == null)
		{
			//specify the folder we'd like
			String path = curFolder.getAbsolutePath() + "/" + INDEX_FOLDER_NAME;
			indexFolder = new File(path);
			
			//try to create it
			if (!indexFolder.exists() && indexFolder.getParentFile().canWrite())
			{
				indexFolder.mkdirs();
			}	
			
			//move to parent if we couldn't write here
			if (!indexFolder.exists())
			{
				indexFolder = null;
				curFolder = curFolder.getParentFile();

				if (curFolder == null)
				{
					Logger.log(LogLevel.k_error, "An index folder cannot be created for the folder: " + lookup.getPath());
					return null;
				}
			}
		}
		
		String path = indexFolder.getAbsolutePath() + "/" +  
				ByteConversion.bytesToHex((group.getName() + indexFolder.getAbsolutePath()).getBytes());
		
		//this file may or may not already exist
		//should be writable since parent folder is writable
		return new File(path);
	}
}
