package product;

import java.io.File;

import data.TrackingGroup;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileOutputManager
{
	private TrackingGroup group;
	private File productStagingFolder;
	private int fileCount;
	private String startTime;
	private final int maxFilesPerFolder = 1000;
	
	public FileOutputManager(TrackingGroup group, File productStagingFolder)
	{
		this.group = group;
		this.productStagingFolder = productStagingFolder;
		this.startTime = myUtilities.formatDateTimeFileSafe(System.currentTimeMillis());
	}

	public File getOutputFolder()
	{
		if (group.usesStructuredProductOutput())
		{
			++fileCount;
			File groupFolder = new File(productStagingFolder, group.getName() + "_output_" + startTime);
			int indexNumber = (fileCount / maxFilesPerFolder) + 1;
			File indexFolder = new File(groupFolder, "Index " + indexNumber);
			return indexFolder;
		}
		else
		{
			return group.getStaticOutputFolder();
		}
	}
	
}