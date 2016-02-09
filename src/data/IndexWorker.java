package data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.FileSystemUtil;

public class IndexWorker implements Runnable
{
	private BlockingQueue<Metadata> queue;
	private final int MAX_QUEUE_SIZE = 2000;
	private boolean shuttingDown;
	private boolean active;
	private List<File> inputFiles;

	public IndexWorker(BlockingQueue<Metadata> queue, List<File> inputFiles)
	{
		this.queue = queue;
		this.inputFiles = inputFiles;
		shuttingDown = false;
		active = true;
	}


	public void shutdown()
	{
		shuttingDown = true;
	}

	@Override
	public void run()
	{
		active = true;

		Logger.log(LogLevel.k_debug, "Index worker running, " + 
						inputFiles.size() +
						" initial files/folders");

		// index all top level folders
		for (File inputFile : inputFiles)
		{
			if (!shuttingDown)
				crawl(inputFile);
		}

		active = false;
		
		Logger.log(LogLevel.k_debug, "Index worker is shutdown.");
	}

	/**
	 * @update_comment
	 * @param topLevel
	 * @param attribute
	 */
	private void crawl(File currentFile)
	{
		//wait if the queue is too full
		while (!shuttingDown && queue.size() >= MAX_QUEUE_SIZE)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e){}
		}
		
		if (!shuttingDown)
		{
			if (currentFile.isDirectory())
			{
				if (currentFile.listFiles().length == 0)
				{
					//add this folder
					Metadata folderMetadata = new Metadata();
					folderMetadata.setEmptyFolder(true);
					folderMetadata.setPermissions(FileSystemUtil.getNumericFilePermissions(currentFile));
					folderMetadata.setFile(currentFile);
					folderMetadata.setType(FileType.k_folder);
					
					Logger.log(LogLevel.k_debug, "Queueing metadata for folder: " + folderMetadata.getFile().getAbsolutePath());
					queue.add(folderMetadata);
				}
				else
				{
					//recurse through the folder contents
					for (File child : currentFile.listFiles())
					{
						crawl(child);
					}
				}
			}
			else
			{
				//create metadata from the file element
				Metadata fileMetadata = FileSystemUtil.loadMetadataFromFile(currentFile);
					
				//new file, so add to queue
				Logger.log(LogLevel.k_debug, "Queueing metadata for file: " + 
								fileMetadata.getFile().getAbsolutePath());
				queue.add(fileMetadata);
			}
		}
	}

	public boolean isActive()
	{
		// System.out.println("Returning active state: " + active + ", " +
		// initialFolders.size() + " initialFolders left");
		return active;
	}
}
