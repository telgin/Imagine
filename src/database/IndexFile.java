package database;

import hibernate.Metadata;

import java.io.File;
import java.util.List;

import util.ByteConversion;
import util.Constants;
import util.Hashing;
import util.myUtilities;

import logging.LogLevel;
import logging.Logger;

public class IndexFile {
	private static final String INDEX_FOLDER_NAME = "." + Constants.APPLICATION_NAME_SHORT.toLowerCase();

	private static final String INDEX_FILE_DELIMETER = "<~>";
	
	private File path;
	
	private IndexFile()
	{
		
	}
	
	public static IndexFile loadIndex(File curFolder)
	{
		assert(!curFolder.isDirectory());
		
		File indexFileLocation = findIndexFile(curFolder);

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
			indexFile.addFileRecord(new FileRecord(line.split(INDEX_FILE_DELIMETER)));
		
		return indexFile;
	}

	private void addFileRecord(FileRecord fileRecord) {
		// TODO Auto-generated method stub
		
	}

	private static File findIndexFile(File folder) {
		File indexFolder = null;
		File curFolder = new File(folder.getAbsolutePath());
		
		//use the pre-existing folder, or create a new one
		while (indexFolder == null)
		{
			//specify the folder we'd like
			String path = curFolder.getAbsolutePath() + "\\" + INDEX_FOLDER_NAME;
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
					Logger.log(LogLevel.k_error, "An index folder cannot be created for the folder: " + folder.getPath());
					return null;
				}
			}
		}
		
		String path = indexFolder.getAbsolutePath() + "\\" +
				ByteConversion.bytesToHexString(folder.getAbsolutePath().getBytes());
		
		//this file may or may not already exist
		//should be writable since parent folder is writable
		return new File(path);
	}
	
	public void save()
	{
		
	}
	
	public static Metadata getFileMetadata(File f) {
		Metadata temp = new Metadata();
		temp.setPath(f.getAbsolutePath());
		temp.setDateCreated(0);
		temp.setDateModified(0);
		
		return temp;
	}

	public static byte[] getFileHash(File f) {
		
		// should be looking this up from the database, not hashing it here.
		return Hashing.hash(f);
	}

	public static Metadata getNewestMetadata(byte[] fileHash) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateMetadata(Metadata fileMetadata) {
		// TODO Auto-generated method stub
		
	}

	public static void saveMetadata(Metadata metadata) {
		// TODO Auto-generated method stub
		
	}

	public static boolean containsFileRecord(byte[] fileHash) {
		// TODO Auto-generated method stub
		return false;
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
}
