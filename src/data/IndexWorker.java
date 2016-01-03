package data;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import database.Database;
import database.derby.EmbeddedDB;
import database.filesystem.FileSystemDB;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.Constants;
import util.FileSystemUtil;
import util.Hashing;

public class IndexWorker implements Runnable
{

	private BlockingQueue<Metadata> queue;
	private TrackingGroup trackingGroup;
	private boolean shuttingDown;
	private boolean active;
	private Element root;
	private List<Metadata> deferred;
	private DeferredQueuer dq;

	public IndexWorker(BlockingQueue<Metadata> queue, Element tree,
					TrackingGroup trackingGroup)
	{
		this.queue = queue;
		this.trackingGroup = trackingGroup;
		shuttingDown = false;
		active = true;
		root = tree;
		deferred = new LinkedList<Metadata>();
		dq = new DeferredQueuer();
	}


	public void shutdown()
	{
		shuttingDown = true;
	}

	@Override
	public void run()
	{
		active = true;
		
		//start the thread which adds deferred references when
		//after the original file is updated in the database
		new Thread(dq).start();

		Logger.log(LogLevel.k_debug, "Index worker running, " + 
						root.getElementsByTagName("file").getLength() +
						" initial files/folders");

		// index all top level folders
		Node topLevel = root.getFirstChild();
		while (topLevel != null && !shuttingDown)
		{
			crawl((Element) topLevel, new File(((Element)topLevel).getAttribute("parent")));
			topLevel = topLevel.getNextSibling();
		}

		active = false;
	}

	/**
	 * @update_comment
	 * @param topLevel
	 * @param attribute
	 */
	private void crawl(Element ele, File parentFile)
	{
		if (!shuttingDown)
		{
		
			if (ele.getTagName().equals("folder"))
			{
				//recurse through the folders
				Node child = ele.getFirstChild();
				while (child != null)
				{
					crawl((Element) child, new File(parentFile, ele.getAttribute("name")));
					child = child.getNextSibling();
				}
			}
			else if (ele.getTagName().equals("file"))
			{
				//create metadata from the file element
				Metadata fileMetadata = new Metadata();
				fileMetadata.setFile(new File(parentFile, ele.getAttribute("name")));
				fileMetadata.setDateCreated(Long.parseLong(ele.getAttribute("created")));
				fileMetadata.setDateModified(Long.parseLong(ele.getAttribute("modified")));
				fileMetadata.setFileHash(ByteConversion.hexToBytes(ele.getAttribute("hash")));
				fileMetadata.setPermissions(Short.parseShort(ele.getAttribute("perms")));
				
				
//				if (trackingGroup.isUsingDatabase())
//				{
				
				//TODO stuff that is queued only needs to wait for the db
				//to update the record before queuing a reference
				
				//check to see if the file hash was already added earlier
				if (Database.isQueued(fileMetadata, trackingGroup))
				{
					//the file to which a reference will be made to hasn't
					//been loaded yet. we must wait until it's loaded.
					fileMetadata.setType(FileType.k_reference);
					
					Logger.log(LogLevel.k_debug, "Deferring metadata reference for file: " + 
									fileMetadata.getFile().getAbsolutePath());
					
					deferred.add(fileMetadata);
					
					//show on the element that the file will not be converted directly
					ele.setAttribute("reference", "1");
				}
				else if (!Database.containsFileHash(fileMetadata.getFileHash(), trackingGroup))
				{
					fileMetadata.setType(FileType.k_file);
					
					//new file, so add to queue
					Logger.log(LogLevel.k_debug, "Queueing metadata for file: " + 
									fileMetadata.getFile().getAbsolutePath());
					Database.queueMetadata(fileMetadata, trackingGroup);
					queue.add(fileMetadata);
					
					//show on the element that the file will be converted
					ele.setAttribute("reference", "0");
				}
				else
				{
					//this file is contained within the database already, so it's a reference
					fileMetadata.setType(FileType.k_reference);
					
					//the file was already queued or added, so just add a reference
					Logger.log(LogLevel.k_debug, "Queueing metadata reference for file: " + 
									fileMetadata.getFile().getAbsolutePath());
					queue.add(fileMetadata);
					
					//show on the element that the file will not be converted directly
					ele.setAttribute("reference", "1");
				}
				
				
				
//				}
//				else
//				{
//					//no tracking, so we'll always add the file
//					Logger.log(LogLevel.k_debug, "Queueing metadata for file: " + fileMetadata.getFile().getAbsolutePath());
//					queue.add(fileMetadata);
//					
//					//show on the element that the file will be converted
//					ele.setAttribute("queued", "1");
//				}
			}
			else
			{
				//empty node, add metadata just for the parent folder
				Metadata folderMetadata = new Metadata();
				folderMetadata.setEmptyFolder(true);
				folderMetadata.setPermissions(FileSystemUtil.getNumericFilePermissions(parentFile));
				folderMetadata.setFile(parentFile);
				folderMetadata.setType(FileType.k_folder);
				
				Logger.log(LogLevel.k_debug, "Queueing metadata for folder: " + folderMetadata.getFile().getAbsolutePath());
				queue.add(folderMetadata);
				
				//show on the element that the folder will be converted
				ele.setAttribute("queued", "1");
			}
		}
	}

	public boolean isActive()
	{
		// System.out.println("Returning active state: " + active + ", " +
		// initialFolders.size() + " initialFolders left");
		return active || dq.isActive();
	}
	
	private class DeferredQueuer implements Runnable
	{

		private boolean deferredActive = false;
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			deferredActive = true;
			
			while (!shuttingDown)
			{
				deferredActive = !deferred.isEmpty();
				
				for (int i=0; i<deferred.size(); ++i)
				{
					Metadata metadata = deferred.get(i);
					if (Database.containsFileHash(metadata.getFileHash(), trackingGroup))
					{
						Logger.log(LogLevel.k_debug, "Queueing metadata reference for file: " + 
										metadata.getFile().getAbsolutePath());
						queue.add(metadata);
						
						//remove metadata from deferred list
						deferred.remove(i);
						--i;
					}
				}
				
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e){}
			}
			
			deferredActive = false;
		}

		/**
		 * @return the active
		 */
		public boolean isActive()
		{
			return deferredActive;
		}
	}
}
