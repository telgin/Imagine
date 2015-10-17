package database;

import hibernate.Metadata;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import data.TrackingGroup;
import util.FileSystemUtil;
import util.Hashing;

public class Database {
	private static final int MAX_LOADED_INDEX_FILES = 5;
	private static BlockingQueue<IndexFile> loadedIndexFiles = 
			new LinkedBlockingQueue<IndexFile>();

	public static Metadata getFileMetadata(File f, TrackingGroup group) {
		Metadata indexMetadata = getIndexFile(f, group).getFileMetadata(f);
		
		//the index metadata might be null if the file is new
		//and doesn't exist there yet
		
		return indexMetadata;
	}
	
	public static boolean containsFileHash(byte[] hash, TrackingGroup group)
	{
		//TODO implement database of trackingGroup/fileHash/fragment1UUID
		//why uuids? because in the case of a metadata update, you might not know
		//what the previous fragment1UUID was if it was a metadata update due
		//to a path change. All you'd know is you've seen this hash before,
		//not where it was saved last.
		return false;
	}

	public static void saveMetadata(Metadata metadata, TrackingGroup group) {
		IndexFile index = getIndexFile(metadata.getFile(), group);
		index.saveMetadata(metadata);
	}

	private static IndexFile getIndexFile(File lookup, TrackingGroup group) {
		//search the preloaded index files first
		
		File indexFilePath = IndexFile.findIndexFile(lookup, group);
		for (IndexFile index:loadedIndexFiles)
		{
			if (index.getPath().equals(indexFilePath))
			{
				//index was preloaded, just return it
				return index;
			}
		}
		
		//index was not preloaded
		
		if (loadedIndexFiles.size() >= MAX_LOADED_INDEX_FILES)
		{
			//make some room
			IndexFile removed = loadedIndexFiles.remove();
			removed.save();
		}
		
		IndexFile index = IndexFile.loadIndex(lookup, group);
		if (index != null)
		{
			loadedIndexFiles.add(index);
		}
		return index;
	}
}
