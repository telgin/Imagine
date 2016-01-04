package data;

import java.io.File;
import util.Constants;

public class Metadata
{
	private long dateCreated;
	private long dateModified;
	private byte[] fileHash;
	private File file;
	private byte[] productUUID;
	private byte[] refProductUUID;
	private long fragmentCount;
	private short permissions;
	private boolean emptyFolder;
	private FileType type;

	public Metadata()
	{
		dateCreated = -1;
		dateModified = -1;
		permissions = -1;
		fragmentCount = -1;
		emptyFolder = false;
	}

	public String toString()
	{
		return file.getName();
	}

	public int getTotalLength()
	{
		// TODO update this to be correct for a file header size
		int totalLength = 0;

		totalLength += file.getAbsolutePath().getBytes().length;
		totalLength += Constants.DATE_CREATED_SIZE;
		totalLength += Constants.DATE_MODIFIED_SIZE;
		totalLength += fileHash.length;
		totalLength += Constants.PERMISSIONS_SIZE;

		return totalLength;
	}

	/**
	 * @return the dateModified
	 */
	public long getDateModified()
	{
		return dateModified;
	}

	/**
	 * @param dateModified
	 *            the dateModified to set
	 */
	public void setDateModified(long dateModified)
	{
		this.dateModified = dateModified;
	}

	/**
	 * @return the dateCreated
	 */
	public long getDateCreated()
	{
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(long dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	public void setProductUUID(byte[] uuid)
	{
		this.productUUID = uuid;
	}

	public byte[] getProductUUID()
	{
		return productUUID;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public byte[] getFileHash()
	{
		return fileHash;
	}

	public void setFileHash(byte[] fileHash)
	{
		this.fileHash = fileHash;
	}

	public boolean isNewerThan(Metadata other)
	{
		return dateModified > other.getDateModified() || !(file.getAbsolutePath()
						.equals(other.getFile().getAbsolutePath()));
	}

	/**
	 * @return the permissions
	 */
	public short getPermissions()
	{
		return permissions;
	}

	/**
	 * @param permissions
	 *            the permissions to set
	 */
	public void setPermissions(short permissions)
	{
		this.permissions = permissions;
	}

	/**
	 * @return the fragmentCount
	 */
	public long getFragmentCount()
	{
		return fragmentCount;
	}

	/**
	 * @param fragmentCount the fragmentCount to set
	 */
	public void setFragmentCount(long fragmentCount)
	{
		this.fragmentCount = fragmentCount;
	}

	/**
	 * @return the emptyFolder
	 */
	public boolean isEmptyFolder()
	{
		return emptyFolder;
	}

	/**
	 * @param emptyFolder the emptyFolder to set
	 */
	public void setEmptyFolder(boolean emptyFolder)
	{
		this.emptyFolder = emptyFolder;
	}

	/**
	 * @return the type
	 */
	public FileType getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FileType type)
	{
		this.type = type;
	}

	/**
	 * @return the refProductUUID
	 */
	public byte[] getRefProductUUID()
	{
		return refProductUUID;
	}

	/**
	 * @param refProductUUID the refProductUUID to set
	 */
	public void setRefProductUUID(byte[] refProductUUID)
	{
		this.refProductUUID = refProductUUID;
	}

}
