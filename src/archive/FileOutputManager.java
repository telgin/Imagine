package archive;

import java.io.File;

import config.Constants;
import config.Settings;
import util.StandardUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileOutputManager
{
	private File f_outputParentFolder;
	private int f_fileCount;
	private String f_startTime;
	
	/**
	 * @update_comment
	 * @param p_outputParentFolder
	 */
	public FileOutputManager(File p_outputParentFolder)
	{
		this.f_outputParentFolder = p_outputParentFolder;
		this.f_startTime = StandardUtil.formatDateTimeFileSafe(System.currentTimeMillis());
	}

	/**
	 * @update_comment
	 * @return
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