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

public class PartAssembler
{
	

	/**
	 * @update_comment
	 * @param streamUUID
	 * @param productSequenceNumber
	 * @param enclosingFolder2
	 * @return
	 */
	public static File findProductFile(long streamUUID, int productSequenceNumber,
					File enclosingFolder, File curProductFolder)
	{
		
		String searchName = Long.toString(streamUUID) + "_" + Integer.toString(productSequenceNumber);
		
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
					String fileName = sub.getName().split(".")[0];
					if (fileName.equals(searchName))
						return sub;
				}
			}
		}
		
		//couldn't find the file, notify user (TODO)
		Logger.log(LogLevel.k_debug, "Could not find next product file: " + searchName);
		return null;
	}
	
	
	
	
	/**
	 * Assembles the parts seen in the part folder into the output file
	 * 
	 * @param partFolder
	 *            It is assumed that this folder only contains the parts of one
	 *            file.
	 * @param outputFile
	 * @return True if it seems to be successful
	 */
	public static boolean assemble(File partFolder, File outputFile)
	{
		try
		{
			for (File partFile : partFolder.listFiles())
			{
				if (partFile.getName().endsWith("_1.part"))
				{
					FileOutputStream fos = new FileOutputStream(outputFile);
					File curPart = partFile;
					while (curPart != null)
					{
						fos.write(Files.readAllBytes(curPart.toPath()));

						// get the next part
						curPart = getNextPartFile(curPart);
					}
					fos.close();

					break;
				}
			}
			return true;
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error,
							"Exception while writing files: " + e.getMessage());
			return false;
		}

	}

	private static File getNextPartFile(File partFile)
	{
		if (partFile.isDirectory())
		{
			Logger.log(LogLevel.k_debug,
							"The partfile supplied is a folder: " + partFile.getPath());
			return null;
		}
		else
		{
			// format: fileID_partNum.part
			String filename = partFile.getName();
			if (filename.contains(".part"))
			{
				String[] nameParts = filename.split("_");
				String filenameID = nameParts[0];
				String partNumberString = nameParts[1].split("\\.")[0];

				try
				{
					int partNumber = Integer.parseInt(partNumberString);
					File nextFile = new File(partFile.getParentFile().getAbsolutePath()
									+ "/" + filenameID + "_" + (partNumber + 1)
									+ ".part");

					if (nextFile.exists())
						return nextFile;
					else
					{
						Logger.log(LogLevel.k_debug,
										"Could not find the next part file, assuming we're done. : "
														+ partFile.getPath());
						return null;
					}
				}
				catch (Exception e)
				{
					Logger.log(LogLevel.k_debug, "The partfile name could not be parsed: "
									+ partFile.getPath());
					return null;
				}
			}
			else
			{
				Logger.log(LogLevel.k_debug, "The partfile does not contain '.part': "
								+ partFile.getPath());
				return null;
			}
		}
	}




	/**
	 * @update_comment
	 * @param assembled
	 * @param fileContents
	 * @param group
	 */
	public static void moveToExtractionFolder(File assembled, FileContents fileContents,
					TrackingGroup group)
	{
		File created = new File(group.getExtractionFolder().toURI()
						.relativize(fileContents.getMetadata().getFile().toURI()));
		
		File parent = created.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		try
		{
			Files.move(assembled.toPath(), created.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
					TrackingGroup group)
	{
		File created = new File(group.getExtractionFolder(),
						fileContents.getMetadata().getFile().getPath());
		
		created.mkdirs();
	}
}
