package api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import archive.ArchiveContents;
import archive.CreationJob;
import data.ArchiveFile;
import archive.ArchiveExtractor;
import key.Key;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Contains functions which provide a standard means to create and interpret archives
 */
public abstract class ConversionAPI
{
	/**
	 * Creates archives from the input files using the given algorithm and key
	 * @param p_inputFiles The list of input files or folders
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_threads The number of archive writer threads to use
	 * @return The job which will be running the archive creation
	 */
	public static CreationJob createArchives(List<ArchiveFile> p_inputFiles, Algorithm p_algo, 
		Key p_key, int p_threads)
	{
		Logger.log(LogLevel.k_general, "Running creation...");
		
		CreationJob job = new CreationJob(p_inputFiles, p_algo, p_key, p_threads);
		Thread jobThread = new Thread(job);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
		return job;
	}
	
	/**
	 * Opens an archive in order to view the contents
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_archiveFile The archive file to view
	 * @return The archive contents as a list of files (which haven't been extracted)
	 * @throws IOException If the archive cannot be viewed
	 * @throws UsageException If the archive file cannot be found
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
	 * Extracts all files contained within the specified archive, or all files in all archives
	 * contained within the specified folder.
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_archiveLocation The archive to extract files from, or a folder of archives to
	 * extract files from.
	 * @param p_extractionFolder The folder to write the extracted files to
	 * @throws IOException If only a single archive is being extracted and it failed to be read, 
	 * or any extracted files cannot be written. (If a folder of archives is being extracted, some
	 * can fail while others can still succeed.)
	 * @throws UsageException If the archive location cannot be found.
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
			extractor.extractAllFromArchiveFile(p_archiveLocation, p_extractionFolder);
	}
	
	/**
	 * Extracts a file from an archive with the given index (position in the archive)
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_archiveFile The archive file to extract a file from
	 * @param p_extractionFolder The folder to write the extracted file to
	 * @param p_index The index of the file contained in the archive to extract
	 * @throws IOException If the file cannot be read or extracted files cannot be written.
	 * @throws UsageException If the archive file cannot be found.
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
