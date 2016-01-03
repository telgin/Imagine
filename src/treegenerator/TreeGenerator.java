package treegenerator;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import config.Configuration;
import data.Metadata;
import data.TrackingGroup;
import database.Database;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.ConfigUtil;
import util.Constants;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TreeGenerator
{
	private Document doc;
	private TrackingGroup group;
	
	
	public TreeGenerator(TrackingGroup group)
	{
		this.group = group;
	}
	
	public void generateTree()
	{
		//create the xml
		doc = ConfigUtil.getNewDocument();
		Element root = mkElement("filetree");
		Element pc = mkPC();
		root.appendChild(pc);
		for (File included : group.getTrackedFiles())
		{
			Element topLevelTracked = mkBranch(included);
			if (topLevelTracked == null)
			{
				//warn user that a tracked region couldn't be found
				Logger.log(LogLevel.k_warning, "Could not find tracked region: " + included.getAbsolutePath());
			}
			else
			{
				//add information about the parent or drive uuid
				File parent = included.getParentFile();
				topLevelTracked.setAttribute("parent", parent != null ? parent.getAbsolutePath() : "");
				//topLevelTracked.setAttribute("driveuuid", FileSystemUtil.getDriveUUID(included));
				pc.appendChild(topLevelTracked);
			}	
		}
		doc.appendChild(root);
	}
	
	public void save(File outFile)
	{
		//make the output file
		if (!outFile.getParentFile().exists())
			outFile.getParentFile().mkdirs();
				
		//save the file
		ConfigUtil.saveConfig(doc, outFile);
	}
	
	public Element getRoot()
	{
		return (Element) doc.getElementsByTagName("filetree").item(0);
	}
	
	/**
	 * This information is used in the tree viewer.
	 * @update_comment
	 * @return
	 */
	private Element mkPC()
	{
		Element pc = mkElement("pc");
		String hostName = FileSystemUtil.getHostName();
		pc.setAttribute("name", hostName == null ? "" : hostName);
		pc.setAttribute("uuid", Configuration.getInstallationUUID());
		
		return pc;
	}

	/**
	 * @update_comment
	 * @param included
	 * @param group
	 * @return
	 */
	private Element mkBranch(File included)
	{
		if (!included.exists())
			return null;
		
		Element branchRoot = null;
		
		if (!included.isDirectory())
		{
			branchRoot = mkFile(included);
		}
		else
		{
			branchRoot = mkFolder(included);
			processChildren(branchRoot, included.listFiles());
		}
		
		return branchRoot;
	}

	/**
	 * @update_comment
	 * @param branchRoot
	 * @param listFiles
	 * @param group
	 */
	private void processChildren(Element parent, File[] children)
	{
		for (File child : children)
		{
			if (!child.isDirectory())
			{
				Element fileNode = mkFile(child);
				if (fileNode != null)
					parent.appendChild(fileNode);
			}
			else
			{
				Element folderNode = mkFolder(child);
				if (folderNode != null)
				{
					processChildren(folderNode, child.listFiles());
					parent.appendChild(folderNode);
				}
			}
		}
		
	}

	/**
	 * @update_comment
	 * @param included
	 * @return
	 */
	private Element mkFolder(File included)
	{
		if (!included.exists())
			return null;
		
		//don't index the index folders
		if (included.getName().equals(Constants.INDEX_FOLDER_NAME))
			return null;
		
		if (group.getUntrackedFiles().contains(included))
			return null;
		
		Element folder = mkElement("folder");
		folder.setAttribute("name", included.getName());
		
		//add an empty node if the folder is empty
		//this makes things easier for parsing
		if (included.listFiles().length == 0)
			folder.appendChild(mkElement("empty"));
		
		return folder;
	}

	/**
	 * @update_comment
	 * @param included
	 * @return
	 */
	private Element mkFile(File included)
	{
		if (!included.exists())
			return null;
		
		if (group.getUntrackedFiles().contains(included))
			return null;
		
		boolean update = false;
		if (group.isUsingDatabase())
		{
			long lastModified = FileSystemUtil.getDateModified(included);
			Metadata cachedMetadata = Database.getFileMetadata(included, group);
			if (cachedMetadata == null || lastModified == -1 ||
						lastModified > cachedMetadata.getDateModified())
			{
				update = true;
			}
		}
		else
		{
			//always add the file if we're not tracking files
			update = true;
		}

		if (update)
		{
			//The file needs to be added to the tree
			Element file = mkElement("file");
			
			//generate a new metadata object, hash file, etc.
			Metadata curMetadata = FileSystemUtil.loadMetadataFromFile(included);
			
			//set the node attributes to the properties of the current metadata
			//when the conversion runs, this data will be read and used to create
			//metadata objects for saving back to the file system db
			file.setAttribute("name", included.getName());
			file.setAttribute("created", Long.toString(curMetadata.getDateCreated()));
			file.setAttribute("modified", Long.toString(curMetadata.getDateModified()));
			file.setAttribute("perms", Short.toString(curMetadata.getPermissions()));
			file.setAttribute("hash", ByteConversion.bytesToHex(curMetadata.getFileHash()));
			
			return file;
		}
		else
		{
			//The file doesn't need to be added to the tree
			return null;
		}
	}

	private Element mkElement(String tagName)
	{
		return doc.createElement(tagName);
	}
}
