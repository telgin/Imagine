package data;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
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

	public IndexWorker(BlockingQueue<Metadata> queue, Element tree,
					TrackingGroup trackingGroup)
	{
		this.queue = queue;
		this.trackingGroup = trackingGroup;
		shuttingDown = false;
		active = true;
		root = tree;
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

		// wait to check again for more files
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
		}
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
				
				
				
				//check to see if the file hash was already added earlier
				if (!Database.containsFileHash(fileMetadata.getFileHash(), trackingGroup) && 
								!Database.isQueued(fileMetadata, trackingGroup))
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
					fileMetadata.setType(FileType.k_reference);
					
					//the fall was alread queued or added, so just add a reference
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
		return active;
	}
}
