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

public abstract class ConversionAPI
{
	public static ConversionJob runConversion(List<File> inputFiles, Algorithm algo, Key key, int threads)
	{
		Logger.log(LogLevel.k_general, "Running conversion...");
		
		ConversionJob job = new ConversionJob(inputFiles, algo, key, threads);
		Thread jobThread = new Thread(job);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
		return job;
	}
	
	public static ProductContents openArchive(Algorithm algo, Key key, File productFile) throws IOException, UsageException
	{
		if (!productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (productFile.isDirectory())
			throw new UsageException("The specified file path must name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(algo, key,
						productFile.getAbsoluteFile().getParentFile());
		
		return extractor.viewAll(productFile);
	}
	
	public static void extractAll(Algorithm algo, Key key, File productLocation, File extractionFolder) throws IOException, UsageException
	{
		if (!productLocation.exists())
			throw new UsageException("The specified product location cannot be found.");
		
		//specifying a directory indicates it is also the enclosing folder
		File enclosingFolder = null;
		if (productLocation.isDirectory())
			enclosingFolder = productLocation;
		else
			enclosingFolder = productLocation.getAbsoluteFile().getParentFile();
		
		ProductExtractor extractor = new ProductExtractor(algo, key, enclosingFolder);
		
		if (productLocation.isDirectory())
			extractor.extractAllFromProductFolder(productLocation, extractionFolder);
		else
			extractor.extractAllFromProduct(productLocation, extractionFolder);
	}
	
	public static void extractFile(Algorithm algo, Key key, File productFile, File extractionFolder, int index) throws IOException, UsageException
	{
		if (!productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (productFile.isDirectory())
			throw new UsageException("The specified file path must name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(algo, key,
						productFile.getAbsoluteFile().getParentFile());
		
		extractor.extractFileByIndex(productFile, extractionFolder, index);
	}
}
