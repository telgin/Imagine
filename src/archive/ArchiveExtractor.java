package archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Constants;
import data.ArchiveFile;
import data.FileType;
import data.Metadata;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The archive extractor class handles reading archives and writing
 * the files contained within them back out into their original form.
 * This may include 'chain reading' when a file spans multiple archives.
 */
public class ArchiveExtractor {
	
	private ArchiveReader f_archive;
	private long f_curFragmentNumber;
	private byte[] f_buffer;
	private File f_enclosingFolder;
	private File f_curArchiveFile;
	private ExtractionManager f_manager;
	private Algorithm f_algo;
	private Key f_key;
	
	/**
	 * Constructs an archive extractor
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_enclosingFolder The folder where the archives are found
	 */
	public ArchiveExtractor(Algorithm p_algo, Key p_key, File p_enclosingFolder)
	{
		this(p_algo, p_key, p_enclosingFolder, new ExtractionManager());
		
		mapHeaders(p_enclosingFolder);
	}
	
	/**
	 * Constructs an archive extractor with a new extraction manager
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @param p_enclosingFolder The folder where the archives are found
	 * @param p_manager The extraction manager which handles creating the actual files and
	 * making extraction more efficient.
	 */
	private ArchiveExtractor(Algorithm p_algo, Key p_key, File p_enclosingFolder, ExtractionManager p_manager)
	{
		setEnclosingFolder(p_enclosingFolder);
		f_archive = AlgorithmRegistry.getArchiveReaderFactory(p_algo, p_key).createReader();
		
		f_buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
		f_algo = p_algo;
		f_key = p_key;
		f_manager = p_manager;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ArchiveExtractor clone()
	{
		return new ArchiveExtractor(f_algo, f_key, f_enclosingFolder, f_manager);
	}
	
	/**
	 * Sets the enclosing folder where archives are found
	 * @param p_folder The enclosing folder
	 */
	public void setEnclosingFolder(File p_folder)
	{
		f_enclosingFolder = p_folder;
	}
	
	/**
	 * Constructs an archive contents object for the given archive file
	 * which will contain information about all the files loaded in this archive.
	 * @param p_archiveFile The archive file
	 * @return The archive contents
	 * @throws IOException If the archive file cannot be parsed or read
	 */
	public ArchiveContents viewAll(File p_archiveFile) throws IOException
	{
		ArchiveContents archiveContents = parseArchiveContents(p_archiveFile);
		
		try
		{
			//keep trying to read files until one can't be read
			FileContents fileContents = readNextFileHeader(true);
			while (fileContents != null) //TODO change to true
			{
				//add header information to archive contents
				archiveContents.addFileContents(fileContents);
				
				//skip over the file data
				boolean allDataSkipped = skipNextFileData(fileContents);
				fileContents.setFragment(!allDataSkipped);
				
				//try to get the next header
				fileContents = readNextFileHeader(true);
			}
		}
		catch (ArchiveIOException e)
		{
			//this will happen whenever the next file cannot be read
			//this is normal if we've reached the end of the archive
			Logger.log(LogLevel.k_debug, "The next file header could not be read "
							+ "(possibly because we're at the end of an archive): " + e.getMessage());
		}
			
		if (archiveContents.getFileContents().isEmpty())
		{
			//There were no files beyond the archive header. This shouldn't happen, so it's probably
			//a corrupted header. Even though the archive header got parsed into something, it's
			//probably not useful.
			Logger.log(LogLevel.k_debug, "Failed to extract from " + p_archiveFile.getName());
			
			throw new ArchiveIOException("There were no files recovered from " + p_archiveFile.getPath());
		}
		
		return archiveContents;
	}
	
	/**
	 * Assembles the current file data into a file. If the file is the first of multiple 
	 * fragments, an extraction chain will start here.
	 * @param p_origArchiveContents The archive contents associated with the first
	 * fragment of this file
	 * @param p_origFileContents The file contents associated with the first fragment
	 * of this file
	 * @param p_extractionFolder The folder where the file will be extracted to
	 * @return The file which was extracted, or null if something went wrong during extraction
	 */
	private File assembleCurrentFileData(ArchiveContents p_origArchiveContents, 
		FileContents p_origFileContents, File p_extractionFolder)
	{
		//create temporary hidden assembly folder
		File assemblyFolder = new File(p_extractionFolder, Constants.ASSEMBLY_FOLDER_NAME);
		if (!assemblyFolder.exists())
			assemblyFolder.mkdir();
		
		//create temporary file for loading all fragment data into
		File assembling = new File(assemblyFolder, "assembling");
		if (assembling.exists())
		{
			try
			{
				Files.delete(assembling.toPath());
			}
			catch (IOException e){}
		}
		
		BufferedOutputStream outStream = null;
		try
		{
			outStream = new BufferedOutputStream(new FileOutputStream(assembling));

			//read the current file data
			long bytesWritten = readNextFileData(p_origFileContents, outStream);
			
			//not finished unless all the bytes were read
			boolean finished = bytesWritten == p_origFileContents.getRemainingData();
			int increment = 1;
			ArchiveExtractor curExtractor = this; 
			while (!finished)
			{
				//set the current extractor's manager's enclosing folder
				curExtractor.f_manager.setEnclosingFolder(curExtractor.f_enclosingFolder);
				
				//there are other fragments that need to be added,
				//find the next archive file
				String searchName = FileSystemUtil.getArchiveName(p_origArchiveContents.getStreamUUID(),
					p_origArchiveContents.getArchiveSequenceNumber() + increment);
				File nextArchiveFile = f_manager.findArchiveFile(searchName,
					curExtractor.f_curArchiveFile.getAbsoluteFile().getParentFile());
				
				if (nextArchiveFile == null)
				{
					Logger.log(LogLevel.k_error, "Could not find referenced archive file: " +
									searchName);
					return null;
				}
				
				//the fragment we're looking for will be the first file in the next archive
				curExtractor = this.clone();

				finished = curExtractor.extractFragmentData(nextArchiveFile, outStream);
				
				//now looking for the next next archive file...
				++increment;
			}
			
			return assembling;
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Failed to read file data.");
			Logger.log(LogLevel.k_error, e, false);
			
			try
			{
				outStream.close();
			}
			catch (IOException | NullPointerException e2){}
			
			try 
			{
				Files.delete(assembling.toPath());
			}
			catch (IOException e2)
			{
				Logger.log(LogLevel.k_error, "The failed part file cannot be deleted: " + assembling.getAbsolutePath());
				Logger.log(LogLevel.k_error, e2, false);
			}
			
			return null;
		}
	}

	/**
	 * Pulls in the data from the first file only. It is assumed that this
	 * will be a fragment from a previous file.
	 * @param p_archiveFile
	 * @param p_outStream
	 * @return True if this was the last fragment of that file, false 
	 * if there's more data in a later file.
	 * @throws IOException If the archive file could not be read
	 */
	private boolean extractFragmentData(File p_archiveFile,
					BufferedOutputStream p_outStream) throws IOException
	{
		parseArchiveContents(p_archiveFile);
		
		//this archive contents will be the fragment we're looking for
		FileContents fileContents = readNextFileHeader(true);

		if (fileContents.getMetadata().getType().equals(FileType.k_file))
		{
			try
			{
				long bytesRead = readNextFileData(fileContents, p_outStream);
				long totalRemainingFileBytes = fileContents.getRemainingData();
				boolean finished = bytesRead >= totalRemainingFileBytes;
				
				//this file must be fully explored if we're not finished
				if (!finished)
				{
					f_manager.setExplored(p_archiveFile);
				}
				
				return finished;
			}
			catch (IOException e)
			{
				throw new ArchiveIOException("Failed to read first file data: " + 
								fileContents.getMetadata().getFile().getPath());
			}	
		}
		else
		{
			//the first thing in this archive wasn't a file, so something's wrong
			throw new ArchiveIOException("The first file in this archive "
							+ "was not a k_file type: " + p_archiveFile.getAbsolutePath());
		}
	}
	
	/**
	 * Maps the headers of all archives in the current file or folder. This allows 
	 * for less searching around every time we need to find a specific archive. It is 
	 * also allows archive files to be renamed without any hit to performance when 
	 * searching for them.
	 * @param p_archiveFile The archive file or folder to map the header(s) of.
	 */
	public void mapHeaders(File p_archiveFile)
	{
		Logger.log(LogLevel.k_info, "Indexing available file IDs...");
		if (p_archiveFile.isDirectory())
		{
			//bfs through folders for archive files
			Queue<File> folders = new LinkedList<File>();
			folders.add(p_archiveFile);
			
			while (folders.size() > 0)
			{
				File folder = folders.poll();
				for (File sub : folder.listFiles())
				{
					if (sub.isDirectory())
					{
						folders.add(sub);
					}
					else
					{
						try
						{
							mapHeader(sub);
						}
						catch (Exception e) //anything could happen!
						{
							//the file could be any file, so it might not even be an archive file
							Logger.log(LogLevel.k_warning, "Could not map file: " + sub.getName());
						}
					}
				}
			}
		}
		else
		{
			try
			{
				mapHeader(p_archiveFile);
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not read archive file: " + p_archiveFile.getName());
			}
		}
	}
	
	/**
	 * Caches the header of an archive file. This allows for less searching around
	 * every time we need to find a specific archive. It is also allows archive files
	 * to be renamed without any hit to performance when searching for them.
	 * @param p_archiveFile The archive file
	 * @throws IOException If the archive file could not be read
	 */
	private void mapHeader(File p_archiveFile) throws IOException
	{
		ArchiveContents archiveContents = parseArchiveContents(p_archiveFile);
		String fileName = FileSystemUtil.getArchiveName(archiveContents.getStreamUUID(),
						archiveContents.getArchiveSequenceNumber());
		f_manager.cacheHeaderLocation(fileName, p_archiveFile);
	}

	/**
	 * Extracts all files from an archive file. If a fragment is read and it is the first
	 * fragment, this will start an extraction chain where we will attempt to load more archives
	 * in order to extract the full file. However, if the fragment is not the first fragment, it will
	 * be ignored.
	 * @param p_archiveFile The archive file to extract all contents from
	 * @param p_extractionFolder The folder to move all extracted files to
	 * @return If the extraction was successful for all files
	 * @throws IOException If the file could not be parsed or read
	 */
	public boolean extractAllFromArchiveFile(File p_archiveFile, File p_extractionFolder) throws IOException
	{
		ArchiveContents archiveContents = parseArchiveContents(p_archiveFile);
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			if (fileContents.getMetadata().getType().equals(FileType.k_file))
			{
				if (fileContents.getFragmentNumber() != Constants.FIRST_FRAGMENT_CODE)
				{
					//file fragments which are not the first fragment will be ignored
					//it is assumed that these will be picked up later when we find
					//the first fragment
					skipNextFileData(fileContents);
					
					//read next header
					fileContents = readNextFileHeader(true);
					
					continue;
				}
				else
				{
					//assemble this file, if it has other fragments, follow the trail of archives
					File assembled = assembleCurrentFileData(archiveContents, fileContents, p_extractionFolder);
					
					if (assembled != null)
					{
						f_manager.moveFileToExtractionFolder(assembled, fileContents, p_extractionFolder);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " +
										fileContents.getMetadata().getFile().getPath());
					}
				}	
			}
			else
			{
				f_manager.moveFolderToExtractionFolder(fileContents, p_extractionFolder);
			}
			
			//read next header
			fileContents = readNextFileHeader(true);
		}
		
		//set this file explored since it's all been read
		f_manager.setExplored(p_archiveFile);

		return true;
	}

	/**
	 * Extracts all files in all archives in the specified folder
	 * @param p_archiveFolder The folder containing archives
	 * @param p_extractionFolder The folder to extract files from archives into
	 * @return If all files were extracted successfully
	 */
	public boolean extractAllFromArchiveFolder(File p_archiveFolder, File p_extractionFolder)
	{
		boolean success = true;
		
		//reset explored files since this is a new run
		f_manager.resetExploredFiles();
		
		//bfs through folders for archive files
		Queue<File> folders = new LinkedList<File>();
		folders.add(p_archiveFolder);
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			File[] contents = folder.listFiles();
			
			//sort based on sequence number
			Arrays.sort(contents, (File a, File b) ->
			{
				try
				{
					String path1 = a.getName();
					String path2 = b.getName();
					
					String[] parts = path1.split("_");
					String last = parts[parts.length-1];
					if (last.contains("."))
						last = last.substring(0, last.indexOf('.'));
					
					int seq1 = Integer.parseInt(last);

					parts = path2.split("_");
					last = parts[parts.length-1];
					if (last.contains("."))
						last = last.substring(0, last.indexOf('.'));
					
					int seq2 = Integer.parseInt(last);
					
					return seq1 - seq2;
				}
				catch (Exception e) {}
				
				return Integer.MAX_VALUE;
			});
			
			for (File sub : contents)
			{
				if (sub.isDirectory())
				{
					folders.add(sub);
				}
				else
				{
					//check if it was already explored first
					if (!f_manager.isExplored(sub))
					{
						try
						{
							extractAllFromArchiveFile(sub, p_extractionFolder);
						}
						catch (Exception e)
						{
							Logger.log(LogLevel.k_error, "Failed to extract all files from " + sub.getAbsolutePath());
							Logger.log(LogLevel.k_debug, e, false);
							success = false;
						}
					}
				}
			}
		}
		
		return success;
	}
	
	/**
	 * Extracts a file within an archive given the index of the file in the archive
	 * @param p_archiveFile The archive file
	 * @param p_extractionFolder The extraction folder where files will be output to
	 * @param p_index The index of the file to extract
	 * @return If the extraction was successful
	 * @throws IOException
	 */
	public boolean extractFileByIndex(File p_archiveFile, File p_extractionFolder, int p_index) throws IOException
	{
		ArchiveContents archiveContents = parseArchiveContents(p_archiveFile);
		
		int curIndex = 0;
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			//wait until we're at the correct index
			if (curIndex == p_index)
			{
				if (fileContents.getMetadata().getType().equals(FileType.k_file))
				{
					
					//assemble this file, if it has other fragments, follow the trail of archives
					File assembled = assembleCurrentFileData(archiveContents, fileContents, p_extractionFolder);
					
					if (assembled != null)
					{
						f_manager.moveFileToExtractionFolder(assembled, fileContents, p_extractionFolder);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " +
										fileContents.getMetadata().getFile().getPath());
						
						return false;
					}	
				}
				else
				{
					f_manager.moveFolderToExtractionFolder(fileContents, p_extractionFolder);
				}
				
				return true;
			}
			else //this wasn't the correct index, just skip the data
			{
				skipNextFileData(fileContents);
				
				//read next header
				fileContents = readNextFileHeader(true);
				
				++curIndex;
			}
		}

		return false;
	}
	
	/**
	 * Parses an archive into an archive contents
	 * @param p_archiveFile The archive file
	 * @return The archive contents
	 * @throws IOException If the file cannot be read
	 */
	private ArchiveContents parseArchiveContents(File p_archiveFile) throws IOException
	{
		if (p_archiveFile.isDirectory())
		{
			throw new ArchiveIOException( "The archive file is a "
				+ "folder, use \"extractAllRecursive\": " + p_archiveFile.getName());
		}
		
		//try to load the archive file
		loadArchive(p_archiveFile);

		//try to read the archive header
		ArchiveContents archiveContents = readArchiveHeader(true);
		
		if (archiveContents == null)
		{
			//if the archive header can't be read,
			//it's assumed that nothing else can be read
			throw new ArchiveIOException("The archive header cannot be read: " + p_archiveFile.getName());
		}
		
		return archiveContents;
	}
	
	/**
	 * Loads the archive data from an archive file
	 * @param p_archiveFile The archive file
	 * @throws IOException If the file cannot be read
	 */
	private void loadArchive(File p_archiveFile) throws IOException
	{
		f_curArchiveFile = p_archiveFile;
		
		try
		{
			f_archive.loadFile(p_archiveFile);
			Logger.log(LogLevel.k_debug, "Loaded Archive File for Reading: " + p_archiveFile.getName());
		}
		catch (IOException e)
		{
			f_curArchiveFile = null;
			Logger.log(LogLevel.k_error, "Failed to load archive file " + p_archiveFile.getName());
			throw e;
		}
	}
	
	/**
	 * Reads the requested length of file data into the buffer
	 * @param p_length The length of bytes to read
	 * @return If all bytes were read successfully
	 */
	private boolean readFull(int p_length)
	{
		return f_archive.read(f_buffer, 0, p_length) == p_length;
	}
	
	/**
	 * Skips the length of data requested
	 * @param p_skip The number of bytes to skip
	 * @return If all bytes were skipped
	 */
	private boolean skipFull(long p_skip)
	{
		return f_archive.skip(p_skip) == p_skip;
	}
	
	/**
	 * Reads the archive header and parses the data into an archive contents
	 * @param p_parseData If data should be parsed and loaded into the file contents
	 * or skipped
	 * @return The archive contents
	 * @throws ArchiveIOException If the archive cannot be parsed or read
	 */
	private ArchiveContents readArchiveHeader(boolean p_parseData) throws ArchiveIOException
	{
		Logger.log(LogLevel.k_debug, "Reading archive header");
		try
		{
			//setup contents
			ArchiveContents contents = null;
			if (p_parseData)
			{
				contents = new ArchiveContents();
			}
			
			//archive uuid:
			//always read this b/c the archive may need it to de-secure stream
			if (!readFull(Constants.ARCHIVE_UUID_SIZE))
				throw new ArchiveIOException("Could not read archive uuid.");
			
			f_archive.setUUID(ByteConversion.subArray(f_buffer, 0, Constants.ARCHIVE_UUID_SIZE));
			
			if (p_parseData)
			{
				contents.setStreamUUID(ByteConversion.getStreamUUID(f_archive.getUUID()));
				contents.setArchiveSequenceNumber(ByteConversion.getArchiveSequenceNumber(f_archive.getUUID()));
			}
				
			f_archive.secureStream();

			//archive version
			if (p_parseData)
			{
				if (!readFull(Constants.ARCHIVE_VERSION_NUMBER_SIZE))
					throw new ArchiveIOException("Could not read archive version number.");
				
				contents.setArchiveVersionNumber(ByteConversion.byteToInt(f_buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.ARCHIVE_VERSION_NUMBER_SIZE))
					throw new ArchiveIOException("Could not skip archive version number.");
			}

			return contents;
		}
		catch (ArchiveIOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			
			//there are lots of bad things that can happen when parsing
			//random data. A general archive IO exception will suffice.
			throw new ArchiveIOException("Failed to read archive header.");
		}
	}
	
	/**
	 * Reads the next file header starting from the current location.
	 * @param p_parseData If data should be parsed and loaded into the file contents
	 * or skipped
	 * @return The file contents obtained from the file header
	 * @throws ArchiveIOException If the header cannot be parsed or read
	 */
	private FileContents readNextFileHeader(boolean p_parseData) throws ArchiveIOException
	{
		Logger.log(LogLevel.k_debug, "Reading file header");

		FileContents contents = null;
		
		if (p_parseData)
		{
			contents = new FileContents();
			contents.setMetadata(new Metadata());
		}
		
		try //TODO convert to throwing ArchiveIOExceptions like the other one
		{
			//fragment number
			if (!readFull(Constants.FRAGMENT_NUMBER_SIZE))
				return null;
			f_curFragmentNumber = ByteConversion.bytesToLong(f_buffer, 0);
			if (p_parseData)
			{
				contents.setFragmentNumber(f_curFragmentNumber);
			}
			
			//if end code, no more files
			if (f_curFragmentNumber == Constants.END_CODE)
			{
				Logger.log(LogLevel.k_debug, "The end code was reached.");
				return null;
			}
			
			//file type
			if (!readFull(Constants.FILE_TYPE_SIZE))
				return null;
			int fileTypeNum = ByteConversion.byteToInt(f_buffer[0]);
			FileType fileType = FileType.toFileType(fileTypeNum);
			if (fileType == null)
				throw new ArchiveIOException("Failed to read file type.");
			
			contents.getMetadata().setType(fileType);
			
			//file name length
			if (!readFull(Constants.FILE_NAME_LENGTH_SIZE))
				return null;
			short fileNameLength = ByteConversion.bytesToShort(f_buffer, 0);

			//file name
			if (p_parseData)
			{
				if (!readFull(fileNameLength))
					return null;
				
				String filePath = new String(f_buffer, 0, fileNameLength, Constants.CHARSET);
				contents.getMetadata().setFile(new ArchiveFile(filePath));
			}
			else
			{
				if (!skipFull(fileNameLength))
					return null;
			}
				
			//file type:
			if (fileType.equals(FileType.k_file))
			{	
				//date created
				if (p_parseData)
				{
					if (!readFull(Constants.DATE_CREATED_SIZE))
						return null;
					contents.getMetadata().setDateCreated(ByteConversion.bytesToLong(f_buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.DATE_CREATED_SIZE))
						return null;
				}
				
				//date modified
				if (p_parseData)
				{
					if (!readFull(Constants.DATE_MODIFIED_SIZE))
						return null;
					contents.getMetadata().setDateModified(ByteConversion.bytesToLong(f_buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.DATE_MODIFIED_SIZE))
						return null;
				}
				
				//permissions
				if (p_parseData)
				{
					if (!readFull(Constants.PERMISSIONS_SIZE))
						return null;
					contents.getMetadata().setPermissions(ByteConversion.bytesToShort(f_buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.PERMISSIONS_SIZE))
						return null;
				}
				
				//file length remaining
				if (!readFull(Constants.FILE_LENGTH_REMAINING_SIZE))
					return null;
				contents.setRemainingData(ByteConversion.bytesToLong(f_buffer, 0));
			}
			
			return contents;

		}
		catch (ArchiveIOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			throw new ArchiveIOException("Failed to read file header: " + e.getMessage());
		}
	}
	
	
	/**
	 * Skips over the current file data
	 * @param p_fileContents The file contents which describes how much
	 * data should be skipped
	 * @return If all the data could be skipped.
	 */
	private boolean skipNextFileData(FileContents p_fileContents)
	{
		long fileLengthRemaining = p_fileContents.getRemainingData();
		
		//This might try to over-read because fileLengthRemaining could be more
		//than what's left in the archive if the file continues on in the next
		//archive. If it does, nothing bad should happen.
		return skipFull(fileLengthRemaining);
	}
	
	/**
	 * Reads the next file data in the archive
	 * @param p_fileContents The file contents associated with the file data
	 * @param p_output The output stream to write the file data to
	 * @return The length of data read
	 * @throws IOException If the archive file could not be read
	 */
	private long readNextFileData(FileContents p_fileContents, BufferedOutputStream p_output) throws IOException
	{
		long fileLengthRemaining = p_fileContents.getRemainingData();
		long totalBytesRead = 0;

		while (fileLengthRemaining > 0)
		{
			//read from archive
			int dataLength = (int) Math.min(f_buffer.length, fileLengthRemaining);
			int bytesRead = f_archive.read(f_buffer, 0, dataLength);
			totalBytesRead += bytesRead;
			
			if (bytesRead == 0)
			{
				//no more data can be read from the archive
				//this is a normal, it happens when there is
				//an additional fragment after this one.
				break;
			}
			
			fileLengthRemaining -= bytesRead;
			
			//write out to part file
			p_output.write(f_buffer, 0, bytesRead);
		}
		
		p_output.flush();
		
		Logger.log(LogLevel.k_info, "Extracting file data belonging to: " + 
						p_fileContents.getMetadata().getFile().getName());
		return totalBytesRead;
	}
}
