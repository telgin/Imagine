package report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import archive.ConversionJobFileState;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class JobStatus
{
	private static int archivesCreated;
	private static int inputFilesProcessed;
	
	private static Map<File, FileStatus> fileStati;

	public static void reset()
	{
		archivesCreated = 0;
		inputFilesProcessed = 0;
		
		fileStati = new HashMap<File, FileStatus>();
		
	}
	
	public static FileStatus getFileStatus(File file)
	{
		FileStatus fileStatus = fileStati.get(file);
		
		if (fileStatus == null)
		{
			fileStatus = new FileStatus(file);
			fileStatus.setState(ConversionJobFileState.NOT_STARTED);
			fileStati.put(file, fileStatus);
		}
		
		return fileStatus;
	}

	/**
	 * @return the archivesCreated
	 */
	public static int getArchivesCreated()
	{
		return archivesCreated;
	}

	/**
	 * @param archivesCreated the archivesCreated to set
	 */
	public static void incrementArchivesCreated(int increment)
	{
		archivesCreated += increment;
	}

	/**
	 * @return the inputFilesProcessed
	 */
	public static int getInputFilesProcessed()
	{
		return inputFilesProcessed;
	}

	/**
	 * @param inputFilesProcessed the inputFilesProcessed to set
	 */
	public static void incrementInputFilesProcessed(int increment)
	{
		inputFilesProcessed += increment;
	}
	
	public static void setBytesLeft(File file, long bytesLeft)
	{
		if (fileStati.containsKey(file))
		{
			fileStati.get(file).setBytesLeft(bytesLeft);
		}
		else
		{
			FileStatus fileStatus = new FileStatus(file);
			fileStatus.setBytesLeft(bytesLeft);
			fileStati.put(file, fileStatus);
		}
	}
	
	public static void setConversionJobFileStatus(File file, ConversionJobFileState status)
	{
		if (fileStati.containsKey(file))
		{
			fileStati.get(file).setState(status);
		}
		else
		{
			FileStatus fileStatus = new FileStatus(file);
			fileStatus.setState(status);
			fileStati.put(file, fileStatus);
		}
	}
}
