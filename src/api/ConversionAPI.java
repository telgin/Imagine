package api;

import java.io.File;
import java.io.IOException;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.ProductContents;
import product.ProductExtractor;
import util.Constants;
import config.Configuration;
import data.Key;
import data.NullKey;
import data.TrackingGroup;

public abstract class ConversionAPI
{

	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName, Key key, File selection)
	{
		String groupName = Constants.TEMP_RESERVED_GROUP_NAME;
		Algorithm algorithm = Configuration.getAlgorithmPreset(algoPresetName);
		TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
		group.addTrackedPath(selection);
		
		return group;
	}
	
	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName, File selection)
	{
		return createTemporaryTrackingGroup(algoPresetName, new NullKey(), selection);
	}
	
	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName, Key key)
	{
		String groupName = Constants.TEMP_RESERVED_GROUP_NAME;
		Algorithm algorithm = Configuration.getAlgorithmPreset(algoPresetName);
		TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
		
		return group;
	}
	
	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName)
	{
		return createTemporaryTrackingGroup(algoPresetName, new NullKey());
	}

	public static ConversionJob runConversion(TrackingGroup group, int threads)
	{
		Logger.log(LogLevel.k_general, "Running conversion for group: " + group.getName());
		
		ConversionJob job = new ConversionJob(group, threads);
		Thread jobThread = new Thread(job);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
		return job;
	}
	
	public static ProductContents openArchive(TrackingGroup group, File productFile) throws IOException, UsageException
	{
		if (!productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (productFile.isDirectory())
			throw new UsageException("The specified file path must name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(group,
						productFile.getAbsoluteFile().getParentFile());
		
		return extractor.viewAll(productFile);
	}
	
	public static void extractAll(TrackingGroup group, File productFile, File extractionFolder) throws IOException, UsageException
	{
		if (!productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		//specifying a directory indicates it is also the enclosing folder
		File enclosingFolder = null;
		if (productFile.isDirectory())
			enclosingFolder = productFile;
		else
			enclosingFolder = productFile.getAbsoluteFile().getParentFile();
		
		ProductExtractor extractor = new ProductExtractor(group, enclosingFolder);
		
		if (productFile.isDirectory())
			extractor.extractAllFromProductFolder(productFile, extractionFolder);
		else
			extractor.extractAllFromProduct(productFile, extractionFolder);
	}
	
	public static void extractFile(TrackingGroup group, File productFile, File extractionFolder, int index) throws IOException, UsageException
	{
		if (!productFile.exists())
			throw new UsageException("The specified product file cannot be found.");
		
		if (productFile.isDirectory())
			throw new UsageException("The specified file path must name a file, not a directory.");
		
		ProductExtractor extractor = new ProductExtractor(group,
						productFile.getAbsoluteFile().getParentFile());
		
		extractor.extractFileByIndex(productFile, extractionFolder, index);
	}
}
