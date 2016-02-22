package report;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import data.Metadata;
import logging.LogLevel;
import logging.Logger;
import util.FileSystemUtil;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class Report
{
	private static List<String> lines;
	
	static
	{
		reset();
	}
	
	public static void reset()
	{
		lines = new LinkedList<String>();
	}
	
	/**
	 * @update_comment
	 * @param fileMetadata
	 */
	public static void saveConversionRecord(Metadata fileMetadata)
	{
		String fileName = fileMetadata.getFile().getName();
		String f1uuid = FileSystemUtil.getProductName(fileMetadata.getProductUUID());
		String fragmentCount = Long.toString(fileMetadata.getFragmentCount());
		lines.add(fileName + ", " + f1uuid + ", " + fragmentCount);
	}
	
	public static void writeReport(File reportFile)
	{
		myUtilities.writeListToFile(reportFile, lines);
		
		Logger.log(LogLevel.k_general, "Generated Archive Contents Report: " + reportFile.getAbsolutePath());
	}

}
