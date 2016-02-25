package data;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import config.Constants;
import logging.LogLevel;
import logging.Logger;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class IndexWorker implements Runnable
{
	private BlockingQueue<Metadata> f_queue;
	private boolean f_shuttingDown;
	private boolean f_active;
	private List<File> f_inputFiles;

	/**
	 * @update_comment
	 * @param p_queue
	 * @param p_inputFiles
	 */
	public IndexWorker(BlockingQueue<Metadata> p_queue, List<File> p_inputFiles)
	{
		f_queue = p_queue;
		f_inputFiles = p_inputFiles;
		f_shuttingDown = false;
		f_active = true;
	}

	/**
	 * @update_comment
	 */
	public void shutdown()
	{
		f_shuttingDown = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		f_active = true;

		Logger.log(LogLevel.k_debug, "Index worker running, " + 
			f_inputFiles.size() + " initial files/folders");

		// index all top level folders
		for (File inputFile : f_inputFiles)
		{
			if (!f_shuttingDown)
				crawl(inputFile);
		}

		f_active = false;
		
		Logger.log(LogLevel.k_debug, "Index worker is shutdown.");
	}

	/**
	 * @update_comment
	 * @param topLevel
	 * @param attribute
	 */
	private void crawl(File p_currentFile)
	{
		//wait if the queue is too full
		while (!f_shuttingDown && f_queue.size() >= Constants.MAX_FILE_QUEUE_SIZE)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e){}
		}
		
		if (!f_shuttingDown)
		{
			if (p_currentFile.isDirectory())
			{
				if (p_currentFile.listFiles().length == 0)
				{
					//add this folder
					Metadata folderMetadata = new Metadata();
					folderMetadata.setPermissions(FileSystemUtil.getNumericFilePermissions(p_currentFile));
					folderMetadata.setFile(p_currentFile);
					folderMetadata.setType(FileType.k_folder);
					
					Logger.log(LogLevel.k_debug, "Queueing metadata for folder: " + folderMetadata.getFile().getAbsolutePath());
					f_queue.add(folderMetadata);
				}
				else
				{
					//recurse through the folder contents
					for (File child : p_currentFile.listFiles())
					{
						crawl(child);
					}
				}
			}
			else
			{
				//create metadata from the file element
				Metadata fileMetadata = FileSystemUtil.loadMetadataFromFile(p_currentFile);
					
				//new file, so add to queue
				Logger.log(LogLevel.k_debug, "Queueing metadata for file: " + 
					fileMetadata.getFile().getAbsolutePath());
				f_queue.add(fileMetadata);
			}
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean isActive()
	{
		return f_active;
	}
}
