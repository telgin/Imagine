package report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import archive.CreationJobFileState;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Allows archive creation jobs to report on their status
 */
public abstract class JobStatus
{
	private static int s_archivesCreated;
	private static int s_inputFilesProcessed;
	private static Map<File, FileStatus> s_fileStatuses;

	/**
	 * Resets the job stats and clears the file statuses
	 */
	public static void reset()
	{
		s_archivesCreated = 0;
		s_inputFilesProcessed = 0;
		
		s_fileStatuses = new HashMap<File, FileStatus>();
	}
	
	/**
	 * Gets the file status object for a particular file. One will be created
	 * if it doesn't yet exist.
	 * @param p_file The file to get the status of
	 * @return The file status object for this file
	 */
	public static FileStatus getFileStatus(File p_file)
	{
		FileStatus fileStatus = s_fileStatuses.get(p_file);
		
		if (fileStatus == null)
		{
			fileStatus = new FileStatus(p_file);
			fileStatus.setState(CreationJobFileState.NOT_STARTED);
			s_fileStatuses.put(p_file, fileStatus);
		}
		
		return fileStatus;
	}

	/**
	 * @return the archivesCreated
	 */
	public static int getArchivesCreated()
	{
		return s_archivesCreated;
	}

	/**
	 * @param s_archivesCreated the archivesCreated to set
	 */
	public static void incrementArchivesCreated(int p_increment)
	{
		s_archivesCreated += p_increment;
	}

	/**
	 * @return the inputFilesProcessed
	 */
	public static int getInputFilesProcessed()
	{
		return s_inputFilesProcessed;
	}

	/**
	 * @param s_inputFilesProcessed the inputFilesProcessed to set
	 */
	public static void incrementInputFilesProcessed(int p_increment)
	{
		s_inputFilesProcessed += p_increment;
	}
	
	/**
	 * Sets the number of bytes remaining for a file that is being written
	 * @param p_file The file being written
	 * @param p_bytesLeft The number of bytes left to be written
	 */
	public static void setBytesLeft(File p_file, long p_bytesLeft)
	{
		if (s_fileStatuses.containsKey(p_file))
		{
			s_fileStatuses.get(p_file).setBytesLeft(p_bytesLeft);
		}
		else
		{
			FileStatus fileStatus = new FileStatus(p_file);
			fileStatus.setBytesLeft(p_bytesLeft);
			s_fileStatuses.put(p_file, fileStatus);
		}
	}
	
	/**
	 * Sets the files status for a file being written
	 * @param p_file The file
	 * @param p_status The file status
	 */
	public static void setCreationJobFileStatus(File p_file, CreationJobFileState p_status)
	{
		if (s_fileStatuses.containsKey(p_file))
		{
			s_fileStatuses.get(p_file).setState(p_status);
		}
		else
		{
			FileStatus fileStatus = new FileStatus(p_file);
			fileStatus.setState(p_status);
			s_fileStatuses.put(p_file, fileStatus);
		}
	}
}
