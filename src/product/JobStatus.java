package product;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class JobStatus
{
	private static int productsCreated;
	private static int inputFilesProcessed;
	
	private static Map<File, FileStatus> fileStati;
	
	
	

	public static void reset()
	{
		productsCreated = 0;
		inputFilesProcessed = 0;
		
		fileStati = new HashMap<File, FileStatus>();
		
	}
	
	public static FileStatus getFileStatus(File file)
	{
		return fileStati.get(file);
	}

	/**
	 * @return the productsCreated
	 */
	public static int getProductsCreated()
	{
		return productsCreated;
	}

	/**
	 * @param productsCreated the productsCreated to set
	 */
	public static void incrementProductsCreated(int increment)
	{
		productsCreated += increment;
	}

	/**
	 * @return the inputFilesProcessed
	 */
	public static int getInputFilesProcessed()
	{
		return inputFilesProcessed;
	}

	/**
	 * @param inputFilesProcessed the inputFilesProcessed to set
	 */
	public static void incrementInputFilesProcessed(int increment)
	{
		inputFilesProcessed += increment;
	}
}
