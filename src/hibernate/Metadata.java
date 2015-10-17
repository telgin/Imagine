package hibernate;

import java.io.File;
import util.ByteConversion;
import util.Constants;

public class Metadata {
	
	private long dateCreated;
	private long dateModified;
	private byte[] fileHash;
	private File file;
	private long fragment1StreamUUID;
	private int fragment1ProductSequenceNumber;
	private boolean metadataUpdate;
	private short permissions;
	
	public Metadata()
	{
		dateCreated = -1;
		dateModified = -1;
		permissions = -1;
	}
	
	public int getTotalLength()
	{
		int totalLength = 0;
		
		totalLength += file.getAbsolutePath().getBytes().length;
		totalLength += Constants.DATE_CREATED_SIZE;
		totalLength += Constants.DATE_MODIFIED_SIZE;
		//totalLength += fileHash.length;
		totalLength += Constants.METADATA_UPDATE_FLAG_SIZE;
		/*
		if (metadataUpdate)
		{
			totalLength += Constants.STREAM_UUID_SIZE;
			totalLength += Constants.PRODUCT_SEQUENCE_NUMBER_SIZE;
		}
		*/
		
		return totalLength;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return file.getAbsolutePath();
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		file = new File(path);
	}

	/**
	 * @return the dateModified
	 */
	public long getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	/**
	 * @return the dateCreated
	 */
	public long getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void setProductUUID(byte[] uuid) {
		fragment1StreamUUID = ByteConversion.bytesToLong(uuid, 0);
		fragment1ProductSequenceNumber = ByteConversion.bytesToInt(uuid,
				Constants.STREAM_UUID_SIZE-1);
	}
	
	public byte[] getProductUUID(){
		return ByteConversion.concat(
				ByteConversion.longToBytes(fragment1StreamUUID),
				ByteConversion.intToBytes(fragment1ProductSequenceNumber));
	}

	public boolean isMetadataUpdate() {
		return metadataUpdate;
	}

	public void setMetadataUpdate(boolean metadataUpdate) {
		this.metadataUpdate = metadataUpdate;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getFileHash() {
		return fileHash;
	}

	public void setFileHash(byte[] fileHash) {
		this.fileHash = fileHash;
	}

	public boolean isNewerThan(Metadata other) {
		return dateModified > other.getDateModified() ||
				!(file.getAbsolutePath().equals(other.getFile().getAbsolutePath()));
	}

	public void setPreviousProductUUID(byte[] productUUID) {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the permissions
	 */
	public short getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(short permissions) {
		this.permissions = permissions;
	}



}
