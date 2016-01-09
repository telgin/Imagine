package product;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.TrackingGroup;

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
		this.startTime = formatTime(System.currentTimeMillis());
	}
	
	/**
	 * @update_comment
	 * @param currentTimeMillis
	 * @return
	 */
	private String formatTime(long millis)
	{
		Date date = new Date(millis);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		return formatter.format(date);
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
