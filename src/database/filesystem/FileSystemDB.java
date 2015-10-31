package database.filesystem;

import logging.LogLevel;
import logging.Logger;
import runner.ActiveComponent;
import runner.SystemManager;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import data.Metadata;
import data.TrackingGroup;

public class FileSystemDB implements ActiveComponent
{
	private static final int MAX_LOADED_INDEX_FILES = 50;
	private static BlockingQueue<IndexFile> loadedIndexFiles =
					new LinkedBlockingQueue<IndexFile>();
	private static BlockingQueue<IndexFile> toSave = new LinkedBlockingQueue<IndexFile>();
	private static boolean shutdown;


	public Metadata getFileMetadata(File f, TrackingGroup group)
	{
		Metadata indexMetadata = getIndexFile(f, group).getFileMetadata(f);

		// the index metadata might be null if the file is new
		// and doesn't exist there yet
		if (indexMetadata == null)
			System.err.println("METADATA FOR " + f.getName() + " WAS NULL!!!");

		return indexMetadata;
	}

	public void saveMetadata(Metadata metadata, TrackingGroup group)
	{
		Logger.log(LogLevel.k_debug,
						"Saving metadata for " + metadata.getFile().getAbsolutePath());
		IndexFile index = getIndexFile(metadata.getFile(), group);
		index.saveMetadata(metadata);
	}

	private synchronized IndexFile getIndexFile(File lookup, TrackingGroup group)
	{
		// search the preloaded index files first

		File indexFilePath = IndexFile.findIndexFile(lookup, group);

		// search within loaded files
		for (IndexFile index : loadedIndexFiles)
		{
			if (index.getPath().equals(indexFilePath))
			{
				// index was preloaded, just return it
				return index;
			}
		}

		// index was not preloaded

		if (loadedIndexFiles.size() >= MAX_LOADED_INDEX_FILES)
		{
			// make some room
			for (int i = 0; i < 5 && !loadedIndexFiles.isEmpty(); ++i)
			{
				try
				{
					loadedIndexFiles.peek().save();
					loadedIndexFiles.take();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		IndexFile index = IndexFile.loadIndex(lookup, group);
		if (index != null)
		{
			loadedIndexFiles.add(index);
		}
		return index;
	}

	public void save()
	{
		Logger.log(LogLevel.k_debug,
						"Saving Database (" + loadedIndexFiles.size() + " index files)");
		
		while (loadedIndexFiles.size() > 0)
		{
			try
			{
				loadedIndexFiles.take().save();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown()
	{
		save();
		shutdown = true;
	}

	@Override
	public boolean isShutdown()
	{
		return shutdown;
	}
}
