package data;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Holds the metadata of a file so that it can be written to
 * archive files along with the actual file data.
 */
public class Metadata
{
	private ArchiveFile f_file;
	private long f_dateCreated;
	private long f_dateModified;
	private short f_permissions;
	private FileType f_type;
	private long f_fragmentCount;
	private byte[] f_archiveUUID;

	/**
	 * Creates a blank metadata object
	 */
	public Metadata()
	{
		f_dateCreated = -1;
		f_dateModified = -1;
		f_permissions = -1;
		f_fragmentCount = -1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return f_file.getName();
	}

	/**
	 * @return the dateModified
	 */
	public long getDateModified()
	{
		return f_dateModified;
	}

	/**
	 * @param p_dateModified the dateModified to set
	 */
	public void setDateModified(long p_dateModified)
	{
		this.f_dateModified = p_dateModified;
	}

	/**
	 * @return the dateCreated
	 */
	public long getDateCreated()
	{
		return f_dateCreated;
	}

	/**
	 * @param p_dateCreated the dateCreated to set
	 */
	public void setDateCreated(long p_dateCreated)
	{
		this.f_dateCreated = p_dateCreated;
	}

	/**
	 * Sets the archive uuid that this file was added to
	 * @param p_uuid The archive uuid
	 */
	public void setArchiveUUID(byte[] p_uuid)
	{
		this.f_archiveUUID = p_uuid;
	}

	/**
	 * Gets the archive uuid that this file was added to.
	 * @return The archive uuid
	 */
	public byte[] getArchiveUUID()
	{
		return f_archiveUUID;
	}

	/**
	 * Gets the file this metadata is about. This file should exist
	 * if archives are being written, but it may not exist if archives
	 * are being read.
	 * @return The file this metadata is about.
	 */
	public ArchiveFile getFile()
	{
		return f_file;
	}

	/**
	 * Sets the file this metadata is about
	 * @param p_file The file to set
	 */
	public void setFile(ArchiveFile p_file)
	{
		this.f_file = p_file;
	}

	/**
	 * @return the permissions
	 */
	public short getPermissions()
	{
		return f_permissions;
	}

	/**
	 * @param p_permissions the permissions to set
	 */
	public void setPermissions(short p_permissions)
	{
		this.f_permissions = p_permissions;
	}

	/**
	 * @return the fragmentCount
	 */
	public long getFragmentCount()
	{
		return f_fragmentCount;
	}

	/**
	 * @param p_fragmentCount the fragmentCount to set
	 */
	public void setFragmentCount(long p_fragmentCount)
	{
		this.f_fragmentCount = p_fragmentCount;
	}

	/**
	 * @return the type
	 */
	public FileType getType()
	{
		return f_type;
	}

	/**
	 * @param p_type the type to set
	 */
	public void setType(FileType p_type)
	{
		this.f_type = p_type;
	}
}
