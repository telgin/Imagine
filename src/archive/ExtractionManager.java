package archive;

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
 * Manages the extraction of files and where copy them to when they
 * are fully extracted. Also caches archive uuids and explored files for
 * more efficient extracting.
 */
public class ExtractionManager
{
	private Map<String, File> f_cachedFileNames;
	private Set<File> f_exploredFiles;
	private File f_enclosingFolder;
	
	/**
	 * Constructs an extraction manager
	 */
	public ExtractionManager()
	{
		f_cachedFileNames = new HashMap<String, File>();
		f_exploredFiles = new HashSet<File>();
	}
	
	/**
	 * Sets the enclosing folder which contains the archive files for this
	 * extraction job.
	 * @param p_folder The enclosing folder
	 */
	public void setEnclosingFolder(File p_folder)
	{
		f_enclosingFolder = p_folder;
	}
	
	/**
	 * Set an archive file as explored
	 * @param p_archiveFile The archive file
	 */
	public void setExplored(File p_archiveFile)
	{
		f_exploredFiles.add(p_archiveFile);
	}
	
	/**
	 * Tells if the given archive file was already explored
	 * @param p_archiveFile The archive file
	 * @return If the archive file was previously visited
	 */
	public boolean isExplored(File p_archiveFile)
	{
		return f_exploredFiles.contains(p_archiveFile);
	}

	/**
	 * Caches the location of a file header (archive uuid) with a file. This
	 * is useful if the archive files were renamed.
	 * @param p_fileName The original name of the archive file (archive uuid)
	 * @param p_archiveFile The archive file
	 */
	public void cacheHeaderLocation(String p_fileName, File p_archiveFile)
	{
		f_cachedFileNames.put(p_fileName, p_archiveFile);
	}
	
	/**
	 * Gets the cached file location based on the archive name
	 * @param p_fileName The archive file name
	 * @return The cached archive file
	 */
	public File getCachedFile(String p_fileName)
	{
		return f_cachedFileNames.get(p_fileName);
	}

	/**
	 * Finds an archive file given what would have been the original name of
	 * the archive file (uuid) and the current archive folder
	 * @param p_archiveSearchName The original archive file name to search for. If archives
	 * were renamed, this information can still be found by checking the headers (which were probably
	 * mapped already)
	 * @param p_curArchiveFolder The folder containing the current archive file
	 * @return The archive file we're looking for, or null if it could not be found
	 */
	public File findArchiveFile(String p_archiveSearchName, File p_curArchiveFolder)
	{
		//first see if it was cached already
		if (f_cachedFileNames.containsKey(p_archiveSearchName))
			return f_cachedFileNames.get(p_archiveSearchName);
		
		//search until the user provides the correct enclosing folder
		//or the user gives up
		while (true)
		{
			Logger.log(LogLevel.k_debug, "Looking for archive file: " + p_archiveSearchName);
			
			//the enclosing folder is the current archive folder if it wasn't set
			if (f_enclosingFolder == null)
				f_enclosingFolder = p_curArchiveFolder;
			
			//bfs through folders for archive files
			Queue<File> folders = new LinkedList<File>();
			folders.add(p_curArchiveFolder);
			if (!p_curArchiveFolder.getAbsoluteFile().equals(f_enclosingFolder.getAbsolutePath()))
			{
				folders.add(f_enclosingFolder);
			}
			
			while (folders.size() > 0)
			{
				File folder = folders.poll();
				for (File sub : folder.listFiles())
				{
					//check sub folders unless we're back at the current archive folder
					if (sub.isDirectory() && !sub.equals(p_curArchiveFolder))
					{
						folders.add(sub);
					}
					else
					{
						//check the file to see if its name matches
						String subName = sub.getName();
						if (subName.contains("."))
							subName = subName.substring(0, subName.indexOf("."));
						
						if (subName.equals(p_archiveSearchName))
						{
							Logger.log(LogLevel.k_debug, "Found archive file match: " + sub.getName());
							return sub;
						}
					}
				}
			}
			
			Logger.log(LogLevel.k_debug, "Could not find next archive file: " + p_archiveSearchName);
			
			File newEnclosingFolder = UIContext.getUI().promptEnclosingFolder(
							f_enclosingFolder, p_curArchiveFolder, p_archiveSearchName);
			
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
	 * Moves a file from its current location to the correct place in the extraction folder.
	 * @param p_assembled The assembled file to be moved
	 * @param p_fileContents The contents of the file being moved
	 * @param p_extractionFolder The top level extraction folder
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
	 * Copies a file to its correct place in the extraction folder
	 * @param p_source The file to copy
	 * @param p_fileContents The file contents which which contains the original metadata for this file
	 * @param p_extractionFolder The top level extraction folder
	 */
	public void copyFileToExtractionFolder(File p_source, FileContents p_fileContents, File p_extractionFolder)
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
	 * Moves a folder to the extraction folder in its correct place
	 * @param p_fileContents The file contents which describe a folder
	 * @param p_extractionFolder The top level extraction folder
	 */
	public void moveFolderToExtractionFolder(FileContents p_fileContents, File p_extractionFolder)
	{
		File created = new File(p_extractionFolder, p_fileContents.getMetadata().getFile().getPath());
		
		created.mkdirs();
	}

	/**
	 * Resets the cache of explored files
	 */
	public void resetExploredFiles()
	{
		f_exploredFiles = new HashSet<File>();
	}
}
