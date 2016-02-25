package api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import archive.ArchiveContents;
import archive.ConversionJob;
import archive.ArchiveExtractor;
import key.Key;
import logging.LogLevel;
import logging.Logger;

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
	 * @param p_archiveFile
	 * @return
	 * @throws IOException
	 * @throws UsageException
	 */
	public static ArchiveContents openArchive(Algorithm p_algo, Key p_key, 
		File p_archiveFile) throws IOException, UsageException
	{
		if (!p_archiveFile.exists())
			throw new UsageException("The specified archive file cannot be found.");
		
		if (p_archiveFile.isDirectory())
			throw new UsageException("The specified file path must "
				+ "name a file, not a directory.");
		
		ArchiveExtractor extractor = new ArchiveExtractor(p_algo, p_key,
						p_archiveFile.getAbsoluteFile().getParentFile());
		
		return extractor.viewAll(p_archiveFile);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_archiveLocation
	 * @param p_extractionFolder
	 * @throws IOException
	 * @throws UsageException
	 */
	public static void extractAll(Algorithm p_algo, Key p_key,
		File p_archiveLocation, File p_extractionFolder) throws IOException, UsageException
	{
		if (!p_archiveLocation.exists())
			throw new UsageException("The specified archive location cannot be found.");
		
		//specifying a directory indicates it is also the enclosing folder
		File enclosingFolder = null;
		if (p_archiveLocation.isDirectory())
			enclosingFolder = p_archiveLocation;
		else
			enclosingFolder = p_archiveLocation.getAbsoluteFile().getParentFile();
		
		ArchiveExtractor extractor = new ArchiveExtractor(p_algo, p_key, enclosingFolder);
		
		if (p_archiveLocation.isDirectory())
			extractor.extractAllFromArchiveFolder(p_archiveLocation, p_extractionFolder);
		else
			extractor.extractAllFromArchive(p_archiveLocation, p_extractionFolder);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_archiveFile
	 * @param p_extractionFolder
	 * @param p_index
	 * @throws IOException
	 * @throws UsageException
	 */
	public static void extractFile(Algorithm p_algo, Key p_key, File p_archiveFile,
		File p_extractionFolder, int p_index) throws IOException, UsageException
	{
		if (!p_archiveFile.exists())
			throw new UsageException("The specified archive file cannot be found.");
		
		if (p_archiveFile.isDirectory())
			throw new UsageException("The specified file path must "
				+ "name a file, not a directory.");
		
		ArchiveExtractor extractor = new ArchiveExtractor(p_algo, p_key,
						p_archiveFile.getAbsoluteFile().getParentFile());
		
		extractor.extractFileByIndex(p_archiveFile, p_extractionFolder, p_index);
	}
}
