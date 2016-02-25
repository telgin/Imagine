package api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.ProductContents;
import product.ProductExtractor;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class ConversionAPI
{
	/**
	 * @update_comment
	 * @param p_inputFiles
	 * @param p_algo
	 * @param p_key
	 * @param p_threads
	 * @return
	 */
	public static ConversionJob runConversion(List<File> p_inputFiles, Algorithm p_algo, 
		Key p_key, int p_threads)
	{
		Logger.log(LogLevel.k_general, "Running conversion...");
		
		ConversionJob job = new ConversionJob(p_inputFiles, p_algo, p_key, p_threads);
		Thread jobThread = new Thread(job);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
		return job;
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_productFile
	 * @return
	 * @throws IOException
	 * @throws UsageException
	 */
	public static ProductContents openArchive(Algorithm p_algo, Key p_key, 
		File p_productFile) throws IOException, UsageException
	{
		if (!p_productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (p_productFile.isDirectory())
			throw new UsageException("The specified file path must "
				+ "name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(p_algo, p_key,
						p_productFile.getAbsoluteFile().getParentFile());
		
		return extractor.viewAll(p_productFile);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_productLocation
	 * @param p_extractionFolder
	 * @throws IOException
	 * @throws UsageException
	 */
	public static void extractAll(Algorithm p_algo, Key p_key,
		File p_productLocation, File p_extractionFolder) throws IOException, UsageException
	{
		if (!p_productLocation.exists())
			throw new UsageException("The specified product location cannot be found.");
		
		//specifying a directory indicates it is also the enclosing folder
		File enclosingFolder = null;
		if (p_productLocation.isDirectory())
			enclosingFolder = p_productLocation;
		else
			enclosingFolder = p_productLocation.getAbsoluteFile().getParentFile();
		
		ProductExtractor extractor = new ProductExtractor(p_algo, p_key, enclosingFolder);
		
		if (p_productLocation.isDirectory())
			extractor.extractAllFromProductFolder(p_productLocation, p_extractionFolder);
		else
			extractor.extractAllFromProduct(p_productLocation, p_extractionFolder);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_productFile
	 * @param p_extractionFolder
	 * @param p_index
	 * @throws IOException
	 * @throws UsageException
	 */
	public static void extractFile(Algorithm p_algo, Key p_key, File p_productFile,
		File p_extractionFolder, int p_index) throws IOException, UsageException
	{
		if (!p_productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (p_productFile.isDirectory())
			throw new UsageException("The specified file path must "
				+ "name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(p_algo, p_key,
						p_productFile.getAbsoluteFile().getParentFile());
		
		extractor.extractFileByIndex(p_productFile, p_extractionFolder, p_index);
	}
}
