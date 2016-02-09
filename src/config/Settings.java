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
	
	/**
	 * @update_comment
	 * @return
	 */
	public static boolean useStructuredOutput()
	{
		return usesStructuredOutput;
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

}
