package hibernate;

import java.io.File;
import util.ByteConversion;
import util.Constants;

public class Metadata {
	
	private long dateCreated;
	private long dateModified;
	private byte[] fileHash;
	private File file;
	private byte[] productUUID;
	private byte[] previousProductUUID;
	private boolean metadataUpdate;
	private short permissions;
	
	public Metadata()
	{
		dateCreated = -1;
		dateModified = -1;
		permissions = -1;
	}
	
	public String toString()
	{
		return file.getName();
	}
	
	public int getTotalLength()
	{
		//TODO update this to be correct for a file header size
		int totalLength = 0;
		
		totalLength += file.getAbsolutePath().getBytes().length;
		totalLength += Constants.DATE_CREATED_SIZE;
		totalLength += Constants.DATE_MODIFIED_SIZE;
		totalLength += fileHash.length;
		totalLength += Constants.METADATA_UPDATE_FLAG_SIZE;
		
		if (metadataUpdate)
		{
			totalLength += Constants.STREAM_UUID_SIZE;
			totalLength += Constants.PRODUCT_SEQUENCE_NUMBER_SIZE;
		}
		
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
		this.productUUID = uuid;
	}
	
	public byte[] getProductUUID(){
		return productUUID;
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

	/**
	 * @return the previousProductUUID
	 */
	public byte[] getPreviousProductUUID() {
		return previousProductUUID;
	}

	/**
	 * @param previousProductUUID the previousProductUUID to set
	 */
	public void setPreviousProductUUID(byte[] uuid) {
		this.previousProductUUID = uuid;
	}



}
