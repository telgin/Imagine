package config;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This class contains settings for creation jobs
 */
public class Settings
{
	private static File s_outputFolder;
	private static boolean s_usesStructuredOutput;
	private static boolean s_trackFileStatus;
	private static boolean s_generateReport;
	
	/**
	 * Resets the settings to the default values
	 */
	public static void reset()
	{
		s_outputFolder = new File(".");
		s_usesStructuredOutput = false;
		s_trackFileStatus = false;
		s_generateReport = false;
	}
	
	/**
	 * Tells if archive files will be written in a special structured way
	 * such that groups of consecutive archives are output in different index
	 * folders. This is done because on many file systems, performance decreases
	 * if there are too many files in one directory.
	 * @return If structured output will be used
	 */
	public static boolean useStructuredOutput()
	{
		return s_usesStructuredOutput;
	}
	
	/**
	 * Sets the flag for if this creation job should be using structured output.
	 * If this is turned on, groups of consecutive archives are output in different index
	 * folders. This is done because on many file systems, performance decreases
	 * if there are too many files in one directory.
	 * @param p_structured If structured output should be used
	 */
	public static void setUsingStructuredOutput(boolean p_structured)
	{
		s_usesStructuredOutput = p_structured;
	}

	/**
	 * Gets the output folder where archives should be written to
	 * @return The output folder
	 */
	public static File getOutputFolder()
	{
		return s_outputFolder;
	}

	/**
	 * Sets the output folder where archive files should be written to
	 * @param s_outputFolder The output folder
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
