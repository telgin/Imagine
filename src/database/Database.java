package database;


import runner.SystemManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import data.FileType;
import data.Metadata;
import data.TrackingGroup;
import database.derby.EmbeddedDB;
import database.filesystem.FileSystemDB;
import database.memory.ProductDB;

public class Database
{
	private static FileSystemDB fsdb;
	//private static EmbeddedDB embdb;
	private static Map<String, ProductDB> pdbs;

	static
	{
		fsdb = new FileSystemDB();
		SystemManager.registerActiveComponent(fsdb);
		
//		embdb = new EmbeddedDB();
//		SystemManager.registerActiveComponent(embdb);
		
		pdbs = new HashMap<String, ProductDB>();
	}

	public static Metadata getFileMetadata(File f, TrackingGroup group)
	{
		return fsdb.getFileMetadata(f, group);
	}

	public static void saveMetadata(Metadata metadata, TrackingGroup group)
	{
		fsdb.saveMetadata(metadata, group);
	}
	
//	public static void saveProductUUID(Metadata fileMetadata, TrackingGroup group)
//	{
//		embdb.saveProductUUID(fileMetadata, group);
//	}
//
//	public static void saveFileHash(Metadata fileMetadata, TrackingGroup group)
//	{
//		embdb.saveFileHash(fileMetadata, group);
//	}
//
//	public static Integer addTrackingGroup(TrackingGroup group)
//	{
//		return embdb.addTrackingGroup(group);
//	}

	public static boolean containsFileHash(byte[] hash, TrackingGroup group)
	{
		checkPDB(group);
		
		return pdbs.get(group.getName()).containsFileHash(hash);
	}

	public static void save()
	{
		fsdb.save();
		for (ProductDB pdb : pdbs.values())
			pdb.save();
		
		//embdb.save();
	}

	/**
	 * @update_comment
	 * @param fileHash
	 * @param trackingGroup
	 * @return
	 */
//	public static byte[] getFragment1ProductUUID(byte[] fileHash, TrackingGroup group)
//	{
//		return EmbeddedDB.getFragment1ProductUUID(fileHash, group);
//	}

	/**
	 * @update_comment
	 * @param fileMetadata
	 * @param group
	 */
	public static void saveConversionRecord(Metadata fileMetadata, TrackingGroup group)
	{
		//use the pdb as a cache, show which files have been added.
		//the cache will still function if the group isn't using a database
		//so the group still gets the compression benefits. In this case, the pdb
		//will be deleted after the job completes.
		if (fileMetadata.getType().equals(FileType.k_file))
		{
			checkPDB(group);
			
			//add the record
			pdbs.get(group.getName()).addRecord(fileMetadata.getFileHash(),
							fileMetadata.getProductUUID(), fileMetadata.getFragmentCount());
		}
		
		
		//only be using the fsb if the group uses a database
		if (group.isUsingDatabase() && !fileMetadata.getType().equals(FileType.k_folder))
		{
			//update the index file
			fsdb.saveMetadata(fileMetadata, group);
		}
	}
	
	public static void queueMetadata(Metadata fileMetadata, TrackingGroup group)
	{
		checkPDB(group);
		
		//queue the metadata
		pdbs.get(group.getName()).queueFileHash(fileMetadata.getFileHash());
		
	}
	
	public static boolean isQueued(Metadata fileMetadata, TrackingGroup group)
	{
		checkPDB(group);
		
		//check queue
		return pdbs.get(group.getName()).isQueued(fileMetadata.getFileHash());
	}
	
	public static byte[] getCachedF1UUID(byte[] hash, TrackingGroup group)
	{
		checkPDB(group);
		
		//db lookup
		return pdbs.get(group.getName()).getF1UUID(hash);
	}
	
	public static long getCachedFragmentCount(byte[] hash, TrackingGroup group)
	{
		checkPDB(group);
		
		//db lookup
		return pdbs.get(group.getName()).getFragmentCount(hash);
	}
	
	private static void checkPDB(TrackingGroup group)
	{
		//load the db for this tracking group if it's not already loaded
		if (!pdbs.containsKey(group.getName()))
		{
			ProductDB pdb = new ProductDB(group.getHashDBFile());
			SystemManager.registerActiveComponent(pdb);
			pdb.load();
			pdbs.put(group.getName(), pdb);
		}
	}
}
