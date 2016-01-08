package database.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.FileType;
import data.Metadata;
import data.TrackingGroup;
import util.ByteConversion;
import util.Constants;
import util.Hashing;
import util.myUtilities;

import logging.LogLevel;
import logging.Logger;

public class IndexFile
{
	private static final String INDEX_FILE_VERSION = "0.0.0";

	private File path;
	private HashMap<String, FileRecord> records;

	private IndexFile(File location)
	{
		path = location;
		records = new HashMap<String, FileRecord>();
	}

	private void addFileRecord(FileRecord fileRecord)
	{
		records.put(ByteConversion.bytesToHex(fileRecord.getFilePathHash()), fileRecord);
	}

	public void saveMetadata(Metadata metadata)
	{
		boolean found = true;
		byte[] pathHash = Hashing.hash(metadata.getFile().getAbsolutePath().getBytes());
		FileRecord record = records.get(pathHash);
		if (record == null)
		{
			found = false;
			record = new FileRecord();
		}

		// update all fields
		record.setFilePathHash(pathHash);
		record.setFileHash(metadata.getFileHash());
		record.setDateCreated(metadata.getDateCreated());
		record.setDateModified(metadata.getDateModified());
		record.setPermissions(metadata.getPermissions());

		if (!found)
		{
			records.put(ByteConversion.bytesToHex(pathHash), record);
		}
	}

	/**
	 * @return the path
	 */
	public File getPath()
	{
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(File path)
	{
		this.path = path;
	}

	public void save()
	{
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(Constants.APPLICATION_NAME_SHORT + " file system tracker - version "
						+ INDEX_FILE_VERSION);

		for (FileRecord record : records.values())
			lines.add(record.toString());

		myUtilities.writeListToFile(path, lines);
	}

	private String getFileKey(File f)
	{
		return ByteConversion.bytesToHex(Hashing.hash(f.getAbsolutePath().getBytes()));
	}

	public Metadata getFileMetadata(File f)
	{
		FileRecord record = records.get(getFileKey(f));

		if (record != null)
		{
			Metadata metadata = new Metadata();
			metadata.setFile(f);
			metadata.setDateCreated(record.getDateCreated());
			metadata.setDateModified(record.getDateModified());
			metadata.setFileHash(record.getFileHash());
			metadata.setPermissions(record.getPermissions());
			
			//index files only record data on files, not folders
			metadata.setType(FileType.k_file);

			return metadata;
		}
		else
		{
			return null;
		}

	}

	// static loading functions:

	public static IndexFile loadIndex(File lookup, TrackingGroup group)
	{
		//Logger.log(LogLevel.k_debug,
		//				"Trying to load index file for " + lookup.getAbsolutePath());

		File indexFileLocation = findIndexFile(lookup, group);

		if (indexFileLocation == null)
		{
			// the file does not exist, and a new one can't be created
			Logger.log(LogLevel.k_debug,
							"Cannot load index file for " + lookup.getAbsolutePath());
			return null;
		}

		//Logger.log(LogLevel.k_debug,
		//				"Using index file: " + indexFileLocation.getAbsolutePath());

		if (!indexFileLocation.exists())
		{
			indexFileLocation.getParentFile().mkdirs();
			return new IndexFile(indexFileLocation);
		}

		return parseIndexFile(indexFileLocation);
	}

	private static IndexFile parseIndexFile(File indexFileLocation)
	{
		IndexFile indexFile = new IndexFile(indexFileLocation);

		List<String> recordLines = myUtilities.readListFromFile(indexFileLocation);

		// get all file records, skip version line for now
		// TODO validate version
		for (int i = 1; i < recordLines.size(); ++i)
			indexFile.addFileRecord(new FileRecord(recordLines.get(i)));

		return indexFile;
	}

	public static File findIndexFile(File lookup, TrackingGroup group)
	{
		File indexFolder = null;

		if (lookup.isDirectory())
		{
			Logger.log(LogLevel.k_error, "Trying to load index file for a directory: "
							+ lookup.getPath());
		}

		File curFolder = lookup.getAbsoluteFile().getParentFile();

		// use the pre-existing folder, or create a new one
		while (indexFolder == null)
		{
			// specify the folder we'd like
			indexFolder = new File(curFolder.getAbsoluteFile(), Constants.INDEX_FOLDER_NAME);

			// try to create it
			if (!indexFolder.exists() && indexFolder.getParentFile().canWrite())
			{
				indexFolder.mkdirs();
			}

			// move to parent if we couldn't write here
			if (!indexFolder.exists())
			{
				indexFolder = null;
				curFolder = curFolder.getParentFile();

				if (curFolder == null)
				{
					Logger.log(LogLevel.k_error,
									"An index folder cannot be created for the folder: "
													+ lookup.getPath());
					return null;
				}
			}
		}

		byte[] nameHash = Hashing.hash(
						(group.getName() + indexFolder.getAbsolutePath()).getBytes());
		String filename = Integer
						.toString(Math.abs(ByteConversion.bytesToInt(nameHash, 0)));

		// this file may or may not already exist
		// should be writable since parent folder is writable
		return new File(indexFolder.getAbsoluteFile(), filename);
	}
}
