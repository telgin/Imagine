package archive;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Represents the contents of an archive file, including the header and a list of files.
 */
public class ArchiveContents
{
	private int f_archiveVersionNumber;
	private long f_streamUUID;
	private int f_archiveSequenceNumber;
	
	private ArrayList<FileContents> f_files;

	/**
	 * Constructs a new archive contents object
	 */
	public ArchiveContents()
	{
		f_files = new ArrayList<FileContents>();
	}

	/**
	 * Adds a file contents
	 * @param p_contents The file contents to add
	 */
	public void addFileContents(FileContents p_contents)
	{
		f_files.add(p_contents);
	}

	/**
	 * @return the archiveVersionNumber
	 */
	public int getArchiveVersionNumber()
	{
		return f_archiveVersionNumber;
	}

	/**
	 * @param p_archiveVersionNumber the archiveVersionNumber to set
	 */
	public void setArchiveVersionNumber(int p_archiveVersionNumber)
	{
		this.f_archiveVersionNumber = p_archiveVersionNumber;
	}

	/**
	 * @return the archiveSequenceNumber
	 */
	public int getArchiveSequenceNumber()
	{
		return f_archiveSequenceNumber;
	}

	/**
	 * @param p_archiveSequenceNumber the archiveSequenceNumber to set
	 */
	public void setArchiveSequenceNumber(int p_archiveSequenceNumber)
	{
		this.f_archiveSequenceNumber = p_archiveSequenceNumber;
	}

	/**
	 * @return the streamUUID
	 */
	public long getStreamUUID()
	{
		return f_streamUUID;
	}

	/**
	 * @param p_uuid the streamUUID to set
	 */
	public void setStreamUUID(long p_uuid)
	{
		this.f_streamUUID = p_uuid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String text = "Archive Contents:";

		text += "\nArchive Version: " + f_archiveVersionNumber;
		text += "\nStreamUUID: " + f_streamUUID;
		text += "\nSequence Number: " + f_archiveSequenceNumber;
		for (FileContents fc : f_files)
			text += "\n" + fc.toString();

		return text;
	}

	/**
	 * Gets the list of file contents objects for this archive contents object.
	 * @return The list of file contents objects
	 */
	public List<FileContents> getFileContents()
	{
		return f_files;
	}
}
