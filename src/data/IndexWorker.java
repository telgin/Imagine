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
 * Moves through every input file or folder recursively and loads
 * its metadata so it can be added to queue for writing.
 */
public class IndexWorker implements Runnable
{
	private BlockingQueue<Metadata> f_queue;
	private boolean f_shuttingDown;
	private boolean f_active;
	private List<ArchiveFile> f_inputFiles;

	/**
	 * Constructs an index worker
	 * @param p_queue The queue to load input file metadata into
	 * @param p_inputFiles The list of input files and folders to be added to archives.
	 */
	public IndexWorker(BlockingQueue<Metadata> p_queue, List<ArchiveFile> p_inputFiles)
	{
		f_queue = p_queue;
		f_inputFiles = p_inputFiles;
		f_shuttingDown = false;
		f_active = true;
	}

	/**
	 * Shuts this worker down. This is done to stop a job.
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
		for (ArchiveFile inputFile : f_inputFiles)
		{
			if (!f_shuttingDown)
				crawl(inputFile);
		}

		f_active = false;
		
		Logger.log(LogLevel.k_debug, "Index worker is shutdown.");
	}

	/**
	 * Crawls through a file or folder, adding every file and empty folder
	 * found to the queue. Recursive.
	 * @param p_currentFile The file to crawl through. If it is a file or an empty folder,
	 * it will simply be added to the queue, if it is a folder, it will be recursed through.
	 */
	private void crawl(ArchiveFile p_currentFile)
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
						crawl(new ArchiveFile(p_currentFile, child));
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
	 * Tells if this index worker is still in the process of adding files
	 * to the queue.
	 * @return If this index worker is active
	 */
	public boolean isActive()
	{
		return f_active;
	}
}
