package config;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Settings
{
	private static File outputFolder = new File(".");
	private static boolean usesStructuredOutput = false;
	private static boolean trackFileStatus;
	private static boolean generateReport;
	
	/**
	 * @update_comment
	 * @return
	 */
	public static boolean useStructuredOutput()
	{
		return usesStructuredOutput;
	}
	
	public static void setUsingStructuredOutput(boolean structured)
	{
		usesStructuredOutput = structured;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static File getOutputFolder()
	{
		return outputFolder;
	}

	/**
	 * @update_comment
	 * @param outputFolder
	 */
	public static void setOutputFolder(File folder)
	{
		outputFolder = folder;
	}

	/**
	 * @return the trackFileStatus
	 */
	public static boolean trackFileStatus()
	{
		return trackFileStatus;
	}

	/**
	 * @param trackFileStatus the trackFileStatus to set
	 */
	public static void setTrackFileStatus(boolean trackFileStatus)
	{
		Settings.trackFileStatus = trackFileStatus;
	}

	/**
	 * @return the generateReport
	 */
	public static boolean generateReport()
	{
		return generateReport;
	}

	/**
	 * @param generateReport the generateReport to set
	 */
	public static void setGenerateReport(boolean generateReport)
	{
		Settings.generateReport = generateReport;
	}

}
