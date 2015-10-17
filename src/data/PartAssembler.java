package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import logging.LogLevel;
import logging.Logger;

public class PartAssembler {
	
	/**
	 * Assembles the parts seen in the part folder into the output file
	 * @param partFolder It is assumed that this folder only contains
	 * the parts of one file.
	 * @param outputFile
	 * @return True if it seems to be successful
	 */
	public static boolean assemble(File partFolder, File outputFile){
		try
		{
			for (File partFile:partFolder.listFiles())
			{
				if (partFile.getName().endsWith("_1.part"))
				{	
					FileOutputStream fos = new FileOutputStream(outputFile);
					File curPart = partFile;
					while (curPart != null)
					{
						fos.write(Files.readAllBytes(curPart.toPath()));
						
						//get the next part
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
			Logger.log(LogLevel.k_error, "Exception while writing files: " + e.getMessage());
			return false;
		}
		
	}

	private static File getNextPartFile(File partFile) {
		if (partFile.isDirectory())
		{
			Logger.log(LogLevel.k_debug, "The partfile supplied is a folder: " + partFile.getPath());
			return null;
		}
		else
		{
			//format: fileID_partNum.part
			String filename = partFile.getName();
			if (filename.contains(".part"))
			{
				String[] nameParts = filename.split("_");
				String filenameID = nameParts[0];
				String partNumberString = nameParts[1].split("\\.")[0];
				
				try
				{
					int partNumber = Integer.parseInt(partNumberString);
					File nextFile = new File(partFile.getParentFile().getAbsolutePath() + "/" +
							filenameID + "_" + (partNumber + 1) + ".part");
					
					if (nextFile.exists())
						return nextFile;
					else
					{
						Logger.log(LogLevel.k_debug, "Could not find the next part file, assuming we're done. : " + partFile.getPath());
						return null;
					}
				}
				catch (Exception e)
				{
					Logger.log(LogLevel.k_debug, "The partfile name could not be parsed: " + partFile.getPath());
					return null;
				}
			}
			else
			{
				Logger.log(LogLevel.k_debug, "The partfile does not contain '.part': " + partFile.getPath());
				return null;
			}	
		}
	}
}
