package product;

import java.io.File;

import config.Settings;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileOutputManager
{
	private File outputParentFolder;
	private int fileCount;
	private String startTime;
	private final int maxFilesPerFolder = 1000;
	
	public FileOutputManager(File outputParentFolder)
	{
		this.outputParentFolder = outputParentFolder;
		this.startTime = myUtilities.formatDateTimeFileSafe(System.currentTimeMillis());
	}

	public File getOutputFolder()
	{
		if (Settings.useStructuredOutput())
		{
			++fileCount;
			File runFolder = new File(outputParentFolder, "output_" + startTime);
			int indexNumber = (fileCount / maxFilesPerFolder) + 1;
			File indexFolder = new File(runFolder, "Index " + indexNumber);
			return indexFolder;
		}
		else
		{
			return outputParentFolder;
		}
	}
	
}