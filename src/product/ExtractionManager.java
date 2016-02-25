package product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ExtractionManager
{
	private Map<String, File> f_cachedFileNames;
	private Set<File> f_exploredFiles;
	private File f_enclosingFolder;
	
	/**
	 * @update_comment
	 */
	public ExtractionManager()
	{
		f_cachedFileNames = new HashMap<String, File>();
		f_exploredFiles = new HashSet<File>();
	}
	
	/**
	 * @update_comment
	 * @param p_folder
	 */
	public void setEnclosingFolder(File p_folder)
	{
		f_enclosingFolder = p_folder;
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 */
	public void setExplored(File p_productFile)
	{
		f_exploredFiles.add(p_productFile);
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 * @return
	 */
	public boolean isExplored(File p_productFile)
	{
		return f_exploredFiles.contains(p_productFile);
	}

	/**
	 * @update_comment
	 * @param p_fileName
	 * @param p_productFile
	 */
	public void cacheHeaderLocation(String p_fileName, File p_productFile)
	{
		f_cachedFileNames.put(p_fileName, p_productFile);
	}
	
	/**
	 * @update_comment
	 * @param p_fileName
	 * @return
	 */
	public File getCachedFile(String p_fileName)
	{
		return f_cachedFileNames.get(p_fileName);
	}

	/**
	 * @update_comment
	 * @param p_productSearchName
	 * @param p_curProductFolder
	 * @return
	 */
	public File findProductFile(String p_productSearchName, File p_curProductFolder)
	{
		//first see if it was cached already
		if (f_cachedFileNames.containsKey(p_productSearchName))
			return f_cachedFileNames.get(p_productSearchName);
		
		//search until the user provides the correct enclosing folder
		//or the user gives up
		while (true)
		{
			Logger.log(LogLevel.k_debug, "Looking for product file: " + p_productSearchName);
			
			//the enclosing folder is the current product folder if it wasn't set
			if (f_enclosingFolder == null)
				f_enclosingFolder = p_curProductFolder;
			
			//bfs through folders for product files
			Queue<File> folders = new LinkedList<File>();
			folders.add(p_curProductFolder);
			if (!p_curProductFolder.getAbsoluteFile().equals(f_enclosingFolder.getAbsolutePath()))
			{
				folders.add(f_enclosingFolder);
			}
			
			while (folders.size() > 0)
			{
				File folder = folders.poll();
				for (File sub : folder.listFiles())
				{
					//check sub folders unless we're back at the current product folder
					if (sub.isDirectory() && !sub.equals(p_curProductFolder))
					{
						folders.add(sub);
					}
					else
					{
						//check the file to see if its name matches
						String subName = sub.getName();
						if (subName.contains("."))
							subName = subName.substring(0, subName.indexOf("."));
						
						if (subName.equals(p_productSearchName))
						{
							Logger.log(LogLevel.k_debug, "Found product file match: " + sub.getName());
							return sub;
						}
					}
				}
			}
			
			Logger.log(LogLevel.k_debug, "Could not find next product file: " + p_productSearchName);
			
			File newEnclosingFolder = UIContext.getUI().promptEnclosingFolder(
							f_enclosingFolder, p_curProductFolder, p_productSearchName);
			
			if (newEnclosingFolder == null)
			{
				return null;
			}
			else
			{
				f_enclosingFolder = newEnclosingFolder;
			}
		}
	}

	/**
	 * @update_comment
	 * @param p_assembled
	 * @param p_fileContents
	 * @param p_extractionFolder
	 */
	public void moveFileToExtractionFolder(File p_assembled, FileContents p_fileContents,
					File p_extractionFolder)
	{
		File created = new File(p_extractionFolder, p_fileContents.getMetadata().getFile().getPath());
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.move(p_assembled.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(LogLevel.k_info, "Assembled file moved to: "
							+ created.getAbsolutePath());
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Could not move extracted file to destination location: "
							+ created.getAbsolutePath());
			Logger.log(LogLevel.k_error, e, false);
		}
	}

	/**
	 * @update_comment
	 * @param p_source
	 * @param p_fileContents
	 * @param p_extractionFolder
	 */
	public void copyFileToExtractionFolder(File p_source, FileContents p_fileContents,
					File p_extractionFolder)
	{
		File created = new File(p_extractionFolder, p_fileContents.getMetadata().getFile().getPath());
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.copy(p_source.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(LogLevel.k_info, "Assembled file moved to: "
							+ created.getAbsolutePath());
			
			//set permissions of file
			FileSystemUtil.setNumericFilePermissions(created, p_fileContents.getMetadata().getPermissions());
			
			//set file dates
			FileSystemUtil.setFileDates(created, p_fileContents.getMetadata().getDateCreated(),
							p_fileContents.getMetadata().getDateModified());
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Could not move extracted file to destination location: "
							+ created.getAbsolutePath());
			Logger.log(LogLevel.k_error, e, false);
		}
	}

	/**
	 * @update_comment
	 * @param p_fileContents
	 * @param p_extractionFolder
	 */
	public void moveFolderToExtractionFolder(FileContents p_fileContents,
					File p_extractionFolder)
	{
		File created = new File(p_extractionFolder, p_fileContents.getMetadata().getFile().getPath());
		
		created.mkdirs();
	}

	/**
	 * @update_comment
	 */
	public void resetExploredFiles()
	{
		f_exploredFiles = new HashSet<File>();
	}
}
