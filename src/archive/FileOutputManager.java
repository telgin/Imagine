package archive;

import java.io.File;

import config.Constants;
import config.Settings;
import util.StandardUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Manages where archive files are output to. This could be just simply
 * the output folder or a specific index folder if structured output is
 * being used.
 */
public class FileOutputManager
{
	private File f_outputParentFolder;
	private int f_fileCount;
	private String f_startTime;
	
	/**
	 * Creates a file output manager
	 * @param p_outputParentFolder The set output folder
	 */
	public FileOutputManager(File p_outputParentFolder)
	{
		this.f_outputParentFolder = p_outputParentFolder;
		this.f_startTime = StandardUtil.formatDateTimeFileSafe(System.currentTimeMillis());
	}

	/**
	 * Gets the actual output folder to save the archive file in. This may be
	 * different from the output folder specified in the settings if structured
	 * output is being used.
	 * @return The output folder location
	 */
	public File getOutputFolder()
	{
		if (Settings.useStructuredOutput())
		{
			++f_fileCount;
			File runFolder = new File(f_outputParentFolder, "output_" + f_startTime);
			int indexNumber = (f_fileCount / Constants.MAX_STRUCTURED_OUTPUT_FILES_PER_INDEX) + 1;
			File indexFolder = new File(runFolder, "Index " + indexNumber);
			return indexFolder;
		}
		else
		{
			return f_outputParentFolder;
		}
	}
	
}