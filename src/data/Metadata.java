package data;

import java.io.File;

import config.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Metadata
{
	private File f_file;
	private long f_dateCreated;
	private long f_dateModified;
	private short f_permissions;
	private FileType f_type;
	private long f_fragmentCount;
	private byte[] f_archiveUUID;

	/**
	 * @update_comment
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
	 * @update_comment
	 * @param p_uuid
	 */
	public void setArchiveUUID(byte[] p_uuid)
	{
		this.f_archiveUUID = p_uuid;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public byte[] getArchiveUUID()
	{
		return f_archiveUUID;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public File getFile()
	{
		return f_file;
	}

	/**
	 * @update_comment
	 * @param p_file
	 */
	public void setFile(File p_file)
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
