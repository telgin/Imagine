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
	public boolean assemble(File partFolder, File outputFile){
		try
		{
			for (File partFile:partFolder.listFiles())
			{
				if (partFile.getName().endsWith(".part0"))
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

	private File getNextPartFile(File partFile) {
		if (partFile.isDirectory())
		{
			Logger.log(LogLevel.k_debug, "The partfile supplied is a folder: " + partFile.getPath());
			return null;
		}
		else
		{
			String filename = partFile.getName();
			int partIndex = filename.lastIndexOf(".part");
			if (partIndex >= 0)
			{
				String filenameFirstHalf = filename.substring(0, partIndex);
				String partNumberString = filename.substring(partIndex+5, filename.length());
				
				try
				{
					int partNumber = Integer.parseInt(partNumberString);
					File nextFile = new File(partFile.getParentFile().getAbsolutePath() + "\\" +
							filenameFirstHalf + ".part" + (partNumber + 1));
					
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
