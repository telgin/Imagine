package data;

import hibernate.Metadata;
import logging.LogLevel;
import logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import util.Hashing;

public class FileSystemUtil {

	/**
	private static HashMap<String, String> trackedFiles;
	private static HashMap<String, String> trackedFolders;
	
	static
	{
		//initialize maps
		trackedFiles = new HashMap<String, String>();
		trackedFolders = new HashMap<String, String>();
		
		//separate all files and folders
		for (TrackingGroup group : Configuration.getTrackingGroups())
		{
			for (File f : group.getFileSet())
			{
				if (f.isDirectory())
					try {
						trackedFolders.put(f.getCanonicalPath(), group.getName());
					} catch (IOException e) {
						//This should already be checked for existence prior to here
						e.printStackTrace();
					}
				else
					try {
						trackedFiles.put(f.getCanonicalPath(), group.getName());
					} catch (IOException e) {
						//This should already be checked for existence prior to here
						e.printStackTrace();
					}
			}
		}
	}
	
	
	public static boolean notExplicitlyTrackedByOther(File f, String trackingGroup) {

		try
		{
			if (!f.isDirectory())
			{
				String result = trackedFiles.get(f.getCanonicalPath());
	
				boolean answer = result == null || result.equals(trackingGroup);
				if (!answer)
				{
					System.out.println("Path owned by other: " + result);
					System.out.println("\t" + f.getPath());
				}
				return answer;
			}
			else
			{
				
				String result = trackedFolders.get(f.getCanonicalPath());
				boolean answer = result == null || result.equals(trackingGroup);
				if (!answer)
				{
					System.out.println("Path owned by other: " + result);
					System.out.println("\t" + f.getPath());
				}
				return answer;
			}			
		}
		catch (IOException e)
		{
			return true;
		}
		
	}
	*/
	
	public static void loadMetadataFromFile(Metadata metadata, File file)
	{
		if (metadata.getFileHash() == null)
			metadata.setFileHash(Hashing.hash(file));
		if (metadata.getFile() == null)
			metadata.setFile(file);
	}
	
	public static Metadata loadMetadataFromFile(File file)
	{
		Metadata metadata = new Metadata();
		loadMetadataFromFile(metadata, file);
		return metadata;
	}


	public static boolean trackedBy(File f, HashSet<File> fileSet)
	{
		if (fileSet.contains(f))
		{
			//the file or folder is specifically listed in the file set
			return true;
		}
		else
		{
			//get all parent files
			ArrayList<File> parents = new ArrayList<File>();
			
			File parent = null;
			try {
				parent = f.getCanonicalFile().getParentFile();
			} catch (IOException e) {
				Logger.log(LogLevel.k_error, "Cannot get canonical file for: " + f.getPath());
			}
			
			while (parent != null)
			{
				parents.add(parent);
				parent = parent.getParentFile();
			}
			
			//the file can only be tracked if one its parents is specifically listed
			for (File file:parents)
			{
				if (fileSet.contains(file))
				{
					return true;
				}
			}
			
			return false;
		}
	}

}
