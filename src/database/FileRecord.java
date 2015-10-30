package database;

import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;

/**
 * File format: (delimiter may be anything)
 * filePathHash,fileNameHash,fileHash,dateCreated,dateModified,permissions,(
 * fragment1ProductUUID)
 *
 */
public class FileRecord
{

	private static final String FILE_RECORD_DELIMETER = "<~>";

	private byte[] filePathHash;
	private byte[] fileHash;
	private long dateCreated;
	private long dateModified;
	private short permissions;
	private byte[] fragment1ProductUUID;

	public FileRecord()
	{
	}

	public FileRecord(String line)
	{
		String[] parts = line.split(FILE_RECORD_DELIMETER);
		if (parts.length != 6)
		{
			Logger.log(LogLevel.k_error, "Cannot parse file record: " + line);
		}
		else
		{
			setFilePathHash(ByteConversion.hexToBytes(parts[0]));
			setFileHash(ByteConversion.hexToBytes(parts[1]));
			setDateCreated(Long.parseLong(parts[2]));
			setDateModified(Long.parseLong(parts[3]));
			setPermissions(Short.parseShort(parts[4]));
			setFragment1ProductUUID(ByteConversion.hexToBytes(parts[5]));
		}
	}

	public String toString()
	{
		String parts[] = new String[6];

		parts[0] = ByteConversion.bytesToHex(getFilePathHash());
		parts[1] = ByteConversion.bytesToHex(getFileHash());
		parts[2] = Long.toString(getDateCreated());
		parts[3] = Long.toString(getDateModified());
		parts[4] = Short.toString(getPermissions());
		parts[5] = ByteConversion.bytesToHex(getFragment1ProductUUID());

		return String.join(FILE_RECORD_DELIMETER, parts);
	}

	/**
	 * @return the filePathHash
	 */
	public byte[] getFilePathHash()
	{
		return filePathHash;
	}

	/**
	 * @param filePathHash
	 *            the filePathHash to set
	 */
	public void setFilePathHash(byte[] filePathHash)
	{
		this.filePathHash = filePathHash;
	}

	/**
	 * @return the fileHash
	 */
	public byte[] getFileHash()
	{
		return fileHash;
	}

	/**
	 * @param fileHash
	 *            the fileHash to set
	 */
	public void setFileHash(byte[] fileHash)
	{
		this.fileHash = fileHash;
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
	 * @return the fragment1ProductUUID
	 */
	public byte[] getFragment1ProductUUID()
	{
		return fragment1ProductUUID;
	}

	/**
	 * @param fragment1ProductUUID
	 *            the fragment1ProductUUID to set
	 */
	public void setFragment1ProductUUID(byte[] fragment1ProductUUID)
	{
		this.fragment1ProductUUID = fragment1ProductUUID;
	}

	/**
	 * @return the fileRecordDelimeter
	 */
	public static String getFileRecordDelimeter()
	{
		return FILE_RECORD_DELIMETER;
	}

}
