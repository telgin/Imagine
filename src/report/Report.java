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
 * @update_comment
 */
public abstract class Report
{
	private static List<String> s_lines;
	
	/**
	 * @update_comment
	 */
	public static void reset()
	{
		s_lines = new LinkedList<String>();
	}
	
	/**
	 * @update_comment
	 * @param p_fileMetadata
	 */
	public static void saveConversionRecord(Metadata p_fileMetadata)
	{
		String filePath = p_fileMetadata.getFile().getPath();
		String f1uuid = FileSystemUtil.getArchiveName(p_fileMetadata.getArchiveUUID());
		String fragmentCount = Long.toString(p_fileMetadata.getFragmentCount());
		s_lines.add(filePath + Constants.FILE_DELIMITER + f1uuid + Constants.FILE_DELIMITER + fragmentCount);
	}
	
	/**
	 * @update_comment
	 * @param p_reportFile
	 */
	public static void writeReport(File p_reportFile)
	{
		StandardUtil.writeListToFile(p_reportFile, s_lines);
		
		Logger.log(LogLevel.k_general, "Generated Archive Contents Report: " + p_reportFile.getAbsolutePath());
	}

}
