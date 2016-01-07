package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.Queue;

import logging.LogLevel;
import logging.Logger;
import product.FileContents;

public class FileAssembler
{
	/**
	 * @update_comment
	 * @param streamUUID
	 * @param productSequenceNumber
	 * @param enclosingFolder2
	 * @return
	 */
	public static File findProductFile(String productSearchName,
					File enclosingFolder, File curProductFolder)
	{
		Logger.log(LogLevel.k_debug, "Looking for product file: " + productSearchName);
		
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
	public static void moveToExtractionFolder(File assembled, FileContents fileContents,
					File extractionFolder)
	{
		File created = new File(extractionFolder, fileContents.getMetadata().getFile().getPath());
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.move(assembled.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(LogLevel.k_debug, "Assembled file moved to: "
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
	 * @param fileContents
	 * @param group
	 */
	public static void moveFolderToExtractionFolder(FileContents fileContents,
					File extractionFolder)
	{
		File created = new File(extractionFolder, fileContents.getMetadata().getFile().getPath());
		
		created.mkdirs();
	}
}
