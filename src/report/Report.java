package report;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import config.Constants;
import data.Metadata;
import logging.LogLevel;
import logging.Logger;
import util.FileSystemUtil;
import util.StandardUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles reporting of which files went into which archives.
 */
public abstract class Report
{
	private static List<String> s_lines;
	
	/**
	 * Clears the list of report records
	 */
	public static void reset()
	{
		s_lines = new LinkedList<String>();
	}
	
	/**
	 * Adds a creation record to the report
	 * @param p_fileMetadata The metadata for a file which was added to an archive
	 */
	public static void saveCreationRecord(Metadata p_fileMetadata)
	{
		String filePath = p_fileMetadata.getFile().getPath();
		String f1uuid = FileSystemUtil.getArchiveName(p_fileMetadata.getArchiveUUID());
		String fragmentCount = Long.toString(p_fileMetadata.getFragmentCount());
		s_lines.add(filePath + Constants.FILE_DELIMITER + f1uuid + Constants.FILE_DELIMITER + fragmentCount);
	}
	
	/**
	 * Writes the report to a file
	 * @param p_reportFile The file to write the report to
	 */
	public static void writeReport(File p_reportFile)
	{
		StandardUtil.writeListToFile(p_reportFile, s_lines);
		
		Logger.log(LogLevel.k_general, "Generated Archive Contents Report: " + p_reportFile.getAbsolutePath());
	}

}
