package data;

import hibernate.Metadata;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import logging.LogLevel;
import logging.Logger;

import database.Database;

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
		active = true;
		
		Logger.log(LogLevel.k_debug, "Index worker running...");
		
		//index all top level folders
		for(File top:initialFolders){
			crawl(top, 0);
		}
		
		active = false;
	}
	
	/**
	 * depth first traversal
	 * @param folder
	 * @param depth
	 */
	private void crawl(File folder, int depth){
		System.out.println("Depth: " + depth);
		if(folder.listFiles() != null && !shuttingDown){
			for(File child:folder.listFiles()){
				if (child.canRead() && !FileSystemUtil.trackedBy(child, trackingGroup.getUntrackedFiles())){
					if(child.isDirectory()){
						crawl(child, depth+1);
					}else{
						if (!shuttingDown)
							process(child);
					}
				}
				
			}
		}else if(!folder.isDirectory() && folder.canRead() &&
				!FileSystemUtil.trackedBy(folder, trackingGroup.getUntrackedFiles())){
			process(folder);
		}
	}
	
	private void process(File file)
	{
		byte[] fileHash = Hashing.hash(file);
		
		if (trackingGroup.isUsingDatabase())
		{
			if (Database.containsFileRecord(fileHash)) //for efficiency, might be better to take a null here
			{
				Metadata databaseMetadata = Database.getNewestMetadata(fileHash);
				Metadata fileMetadata = new Metadata();
				fileMetadata.setFileHash(fileHash);
				fileMetadata.setFile(file);
				FileSystemUtil.loadMetadataFromFile(fileMetadata, file);
				
				//add the file only if it is newer than the database record
				if (fileMetadata.isNewerThan(databaseMetadata))
				{
					fileMetadata.setMetadataUpdate(true);
					fileMetadata.setPreviousProductUUID(databaseMetadata.getProductUUID());
					Database.updateMetadata(fileMetadata); //should probably be called only after written?
					queue.add(fileMetadata);
				}
			}
			else
			{
				Metadata metadata = new Metadata();
				metadata.setFileHash(fileHash);
				metadata.setFile(file);
				FileSystemUtil.loadMetadataFromFile(metadata, file);
				Database.saveMetadata(metadata); //should probably be called only after written?
				queue.add(metadata);
			}
		}
		else
		{
			Metadata metadata = new Metadata();
			metadata.setFileHash(fileHash);
			metadata.setFile(file);
			FileSystemUtil.loadMetadataFromFile(metadata, file);
			queue.add(metadata);
		}
	}

	public boolean isActive() {
		return active;
	}
}
