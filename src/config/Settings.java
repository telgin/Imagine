package config;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Settings
{
	private static File s_outputFolder;
	private static boolean s_usesStructuredOutput;
	private static boolean s_trackFileStatus;
	private static boolean s_generateReport;
	
	/**
	 * @update_comment
	 */
	public static void reset()
	{
		s_outputFolder = new File(".");
		s_usesStructuredOutput = false;
		s_trackFileStatus = false;
		s_generateReport = false;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public static boolean useStructuredOutput()
	{
		return s_usesStructuredOutput;
	}
	
	/**
	 * @update_comment
	 * @param p_structured
	 */
	public static void setUsingStructuredOutput(boolean p_structured)
	{
		s_usesStructuredOutput = p_structured;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static File getOutputFolder()
	{
		return s_outputFolder;
	}

	/**
	 * @update_comment
	 * @param s_outputFolder
	 */
	public static void setOutputFolder(File p_folder)
	{
		s_outputFolder = p_folder;
	}

	/**
	 * @return the trackFileStatus
	 */
	public static boolean trackFileStatus()
	{
		return s_trackFileStatus;
	}

	/**
	 * @param p_trackFileStatus the trackFileStatus to set
	 */
	public static void setTrackFileStatus(boolean p_trackFileStatus)
	{
		s_trackFileStatus = p_trackFileStatus;
	}

	/**
	 * @return the generateReport
	 */
	public static boolean generateReport()
	{
		return s_generateReport;
	}

	/**
	 * @param p_generateReport the generateReport to set
	 */
	public static void setGenerateReport(boolean p_generateReport)
	{
		s_generateReport = p_generateReport;
	}

}
