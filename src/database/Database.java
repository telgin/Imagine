package database;

import hibernate.Metadata;

import java.io.File;

import util.Constants;
import util.Hashing;

public class Database {

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

}
