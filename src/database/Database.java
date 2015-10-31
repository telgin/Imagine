package database;


import runner.SystemManager;

import java.io.File;
import data.Metadata;
import data.TrackingGroup;
import database.derby.EmbeddedDB;
import database.filesystem.FileSystemDB;

public class Database
{
	private static FileSystemDB fsdb;
	private static EmbeddedDB embdb;

	static
	{
		fsdb = new FileSystemDB();
		SystemManager.registerActiveComponent(fsdb);
		
		embdb = new EmbeddedDB();
		SystemManager.registerActiveComponent(embdb);
	}

	public static Metadata getFileMetadata(File f, TrackingGroup group)
	{
		return fsdb.getFileMetadata(f, group);
	}

	public static void saveMetadata(Metadata metadata, TrackingGroup group)
	{
		fsdb.saveMetadata(metadata, group);
	}
	
	public static void saveProductUUID(Metadata fileMetadata, TrackingGroup group)
	{
		embdb.saveProductUUID(fileMetadata, group);
	}

	public static void saveFileHash(Metadata fileMetadata, TrackingGroup group)
	{
		embdb.saveFileHash(fileMetadata, group);
	}

	public static Integer addTrackingGroup(TrackingGroup group)
	{
		return embdb.addTrackingGroup(group);
	}

	public static boolean containsFileHash(byte[] hash, TrackingGroup group)
	{
		return embdb.containsFileHash(hash, group);
	}

	public static void save()
	{
		fsdb.save();
		embdb.save();
	}

	/**
	 * @update_comment
	 * @param fileHash
	 * @param trackingGroup
	 * @return
	 */
	public static byte[] getFragment1ProductUUID(byte[] fileHash, TrackingGroup group)
	{
		return EmbeddedDB.getFragment1ProductUUID(fileHash, group);
	}
}
