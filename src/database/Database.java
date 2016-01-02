package database;


import runner.SystemManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
		//load the db for this tracking group if it's not already loaded
		if (!pdbs.containsKey(group.getName()))
		{
			
			ProductDB pdb = new ProductDB(group.getHashDBFile());
			SystemManager.registerActiveComponent(pdb);
			pdb.load();
			pdbs.put(group.getName(), pdb);
		}
		
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
		//load the db for this tracking group if it's not already loaded
		if (!pdbs.containsKey(group.getName()))
		{
			
			ProductDB pdb = new ProductDB(group.getHashDBFile());
			SystemManager.registerActiveComponent(pdb);
			pdb.load();
			pdbs.put(group.getName(), pdb);
		}
		
		//add the record
		pdbs.get(group.getName()).addRecord(fileMetadata.getFileHash(),
						fileMetadata.getProductUUID(), fileMetadata.getFragmentCount());
		
		//update the index file
		fsdb.saveMetadata(fileMetadata, group);
	}
}
