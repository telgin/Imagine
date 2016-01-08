package product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ExtractionManager
{
	private Map<String, File> extractedFiles;
	private Map<String, File> cachedFileNames;
	
	public ExtractionManager()
	{
		extractedFiles = new HashMap<String, File>();
		cachedFileNames = new HashMap<String, File>();
	}
	
	public void addExtractedFile(byte[] hash, File finalLocation)
	{
		extractedFiles.put(ByteConversion.bytesToHex(hash), finalLocation);
	}
	
	public File getPreviouslyExtractedFile(byte[] hash)
	{
		return extractedFiles.get(ByteConversion.bytesToHex(hash));
	}
	
	/**
	 * @update_comment
	 * @param fileName
	 * @param productFile
	 */
	public void cacheHeaderLocation(String fileName, File productFile)
	{
		cachedFileNames.put(fileName, productFile);
	}
	
	public File getCachedFile(String fileName)
	{
		return cachedFileNames.get(fileName);
	}
	
	/**
	 * @update_comment
	 * @param streamUUID
	 * @param productSequenceNumber
	 * @param enclosingFolder2
	 * @return
	 */
	public File findProductFile(String productSearchName,
					File enclosingFolder, File curProductFolder)
	{
		Logger.log(LogLevel.k_debug, "Looking for product file: " + productSearchName);
		
		//first see if it was cached already
		if (cachedFileNames.containsKey(productSearchName))
			return cachedFileNames.get(productSearchName);
		
		//bfs through folders for product files
		Queue<File> folders = new LinkedList<File>();
		folders.add(curProductFolder);
		if (!curProductFolder.equals(enclosingFolder))
		{
			folders.add(enclosingFolder);
		}
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			for (File sub : folder.listFiles())
			{
				//check sub folders unless we're back at the current product folder
				if (sub.isDirectory() && !sub.equals(curProductFolder))
				{
					folders.add(sub);
				}
				else
				{
					//check the file to see if its name matches
					String subName = sub.getName();
					if (subName.contains("."))
						subName = subName.substring(0, subName.indexOf("."));
					
					if (subName.equals(productSearchName))
					{
						Logger.log(LogLevel.k_debug, "Found product file match: " + sub.getName());
						return sub;
					}
				}
			}
		}
		
		//couldn't find the file, notify user (TODO)
		Logger.log(LogLevel.k_debug, "Could not find next product file: " + productSearchName);
		return null;
	}

	/**
	 * @update_comment
	 * @param assembled
	 * @param fileContents
	 * @param group
	 */
	public void moveFileToExtractionFolder(File assembled, FileContents fileContents,
					File extractionFolder)
	{
		File created = new File(extractionFolder, fileContents.getMetadata().getFile().getPath());
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.move(assembled.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(LogLevel.k_info, "Assembled file moved to: "
							+ created.getAbsolutePath());
			addExtractedFile(fileContents.getMetadata().getFileHash(), created);
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
	 * @param assembled
	 * @param fileContents
	 * @param group
	 */
	public void copyFileToExtractionFolder(File source, FileContents fileContents,
					File extractionFolder)
	{
		File created = new File(extractionFolder, fileContents.getMetadata().getFile().getPath());
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.copy(source.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(LogLevel.k_info, "Assembled file moved to: "
							+ created.getAbsolutePath());
			
			//update cache that this file was already extracted
			addExtractedFile(fileContents.getMetadata().getFileHash(), created);
			
			//set permissions of file
			FileSystemUtil.setNumericFilePermissions(created, fileContents.getMetadata().getPermissions());
			
			//set file dates
			FileSystemUtil.setFileDates(created, fileContents.getMetadata().getDateCreated(),
							fileContents.getMetadata().getDateModified());
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
	 * @param fileContents
	 * @param group
	 */
	public void moveFolderToExtractionFolder(FileContents fileContents,
					File extractionFolder)
	{
		File created = new File(extractionFolder, fileContents.getMetadata().getFile().getPath());
		
		created.mkdirs();
	}
}
