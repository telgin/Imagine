package data;

import hibernate.Metadata;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import logging.LogLevel;
import logging.Logger;

import database.Database;
import util.ByteConversion;
import util.FileSystemUtil;
import util.Hashing;

public class IndexWorker implements Runnable{

	private BlockingQueue<Metadata> queue;
	private Collection<File> initialFolders;
	private TrackingGroup trackingGroup;
	private boolean shuttingDown;
	private boolean active;
	
	public IndexWorker(BlockingQueue<Metadata> queue,
			Collection<File> initialFolders, TrackingGroup trackingGroup)
	{
		this.queue = queue;
		this.initialFolders = initialFolders;
		this.trackingGroup = trackingGroup;
		shuttingDown = false;
		active = false;
	}
	
	public IndexWorker(BlockingQueue<Metadata> queue, File file, TrackingGroup trackingGroup)
	{
		Logger.log(LogLevel.k_debug, "Creating index worker for " + file.getPath());
		this.queue = queue;
		this.trackingGroup = trackingGroup;
		
		LinkedList<File> folders = new LinkedList<File>();
		folders.add(file);
		initialFolders = folders;
	}
	
	public void shutdown()
	{
		shuttingDown = true;
	}
	

	@Override
	public void run() {
		active = !initialFolders.isEmpty();
		
		while (!shuttingDown)
		{
			Logger.log(LogLevel.k_debug, "Index worker running...");
			
			//index all top level folders
			for(File top:initialFolders)
			{
				crawl(top, 0);
			}
			
			active = false;
			
			//wait to check again for more files
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * depth first traversal
	 * @param f
	 * @param depth
	 */
	private void crawl(File f, int depth){
		active = true;
		Logger.log(LogLevel.k_debug, "Depth: " + depth);
		if (!shuttingDown)
		{
			//if the file list is not null, go through all the files
			//otherwise, f is either a file or not readable.
			if(f.listFiles() != null)
			{
				for(File child:f.listFiles())
				{
					if (child.canRead() && !FileSystemUtil.trackedBy(child, trackingGroup.getUntrackedFiles()))
					{
						if(child.isDirectory())
						{
							crawl(child, depth+1);
						}
						else if (!shuttingDown)
						{
							process(child);
						}
					}
					
				}
			}
			else if(!f.isDirectory() && f.canRead() && //make sure f is a readable file
					!FileSystemUtil.trackedBy(f, trackingGroup.getUntrackedFiles()))
			{
				//f was a file, process it
				process(f);
			}
		}
	}
	
	private void process(File file)
	{
		//byte[] fileHash = Hashing.hash(file);
		
		if (trackingGroup.isUsingDatabase())
		{
			Metadata recordMetadata = Database.getFileMetadata(file, trackingGroup);
			if (recordMetadata != null) //there is a previous metadata record of the file
			{
				//to avoid hashing, compare the date modified of the file
				//to what is in the recorded metadata
				long currentDateModified = FileSystemUtil.getDateModified(file);
				
				//add the file only if it is newer than the database record
				if (currentDateModified > recordMetadata.getDateModified())
				{
					//create a new metadata, load from file system
					Metadata fileMetadata = new Metadata();
					fileMetadata.setDateModified(currentDateModified);
					FileSystemUtil.loadMetadataFromFile(fileMetadata, file);
					
					//if the file hashes are the same, this is just a metadata update
					if (ByteConversion.bytesEqual(recordMetadata.getFileHash(), fileMetadata.getFileHash()))
					{
						fileMetadata.setMetadataUpdate(true);
					}
					
					fileMetadata.setPreviousProductUUID(recordMetadata.getProductUUID());
					queue.add(fileMetadata);
				}
			}
			else
			{
				//the metadata was not found within the file system database, so
				//load it from the file system directly
				Metadata metadata = FileSystemUtil.loadMetadataFromFile(file);
				
				//check the database to see if the hash already exists,
				//if so, it's a metadata update (possibly due to copying or moving/renaming)
				if (Database.containsFileHash(metadata.getFileHash(), trackingGroup))
				{
					metadata.setMetadataUpdate(true);
				}
				
				queue.add(metadata);
			}
		}
		else
		{
			//The group is not using a database, so we always get current
			//metadata from the file system and add the file.
			//(It cannot be a metadata update)
			queue.add(FileSystemUtil.loadMetadataFromFile(file));
		}
	}

	public boolean isActive() {
		return active;
	}
}
