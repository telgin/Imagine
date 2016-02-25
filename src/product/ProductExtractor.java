package product;

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
import data.FileType;
import data.Metadata;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.FileSystemUtil;

public class ProductExtractor {
	
	private ProductReader f_product;
	private long f_curFragmentNumber;
	private byte[] f_buffer;
	private File f_enclosingFolder;
	private File f_curProductFile;
	private ExtractionManager f_manager;
	private Algorithm f_algo;
	private Key f_key;
	
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_enclosingFolder
	 */
	public ProductExtractor(Algorithm p_algo, Key p_key, File p_enclosingFolder)
	{
		this(p_algo, p_key, p_enclosingFolder, new ExtractionManager());
		
		mapHeaders(p_enclosingFolder);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @param p_enclosingFolder
	 * @param p_manager
	 */
	private ProductExtractor(Algorithm p_algo, Key p_key, File p_enclosingFolder, ExtractionManager p_manager)
	{
		setEnclosingFolder(p_enclosingFolder);
		f_product = AlgorithmRegistry.getProductReaderFactory(p_algo, p_key).createReader();
		
		f_buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
		f_algo = p_algo;
		f_key = p_key;
		f_manager = p_manager;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ProductExtractor clone()
	{
		return new ProductExtractor(f_algo, f_key, f_enclosingFolder, f_manager);
	}
	
	/**
	 * @update_comment
	 * @param p_folder
	 */
	public void setEnclosingFolder(File p_folder)
	{
		f_enclosingFolder = p_folder;
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 * @return
	 * @throws IOException
	 */
	public ProductContents viewAll(File p_productFile) throws IOException
	{
		ProductContents productContents = parseProductContents(p_productFile);
		
		try
		{
			//keep trying to read files until one can't be read
			FileContents fileContents = readNextFileHeader(true);
			while (fileContents != null) //TODO change to true
			{
				//add header information to product contents
				productContents.addFileContents(fileContents);
				
				//skip over the file data
				boolean allDataSkipped = skipNextFileData(fileContents);
				fileContents.setFragment(!allDataSkipped);
				
				//try to get the next header
				fileContents = readNextFileHeader(true);
			}
		}
		catch (ProductIOException e)
		{
			//this will happen whenever the next file cannot be read
			//this is normal if we've reached the end of the product
			Logger.log(LogLevel.k_debug, "The next file header could not be read "
							+ "(possibly because we're at the end of a product): " + e.getMessage());
		}
			
		if (productContents.getFileContents().isEmpty())
		{
			//There were no files beyond the product header. This shouldn't happen, so it's probably
			//a corrupted header. Even though the product header got parsed into something, it's
			//probably not useful.
			Logger.log(LogLevel.k_debug, "Failed to extract from " + p_productFile.getName());
			
			throw new ProductIOException("There were no files recovered from " + p_productFile.getPath());
		}
		
		return productContents;
	}
	
	/**
	 * @update_comment
	 * @param p_origProductContents
	 * @param p_origFileContents
	 * @param p_extractionFolder
	 * @return
	 */
	private File assembleCurrentFileData(ProductContents p_origProductContents, FileContents p_origFileContents, File p_extractionFolder)
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
			
			FileContents curFileContents = p_origFileContents;
			ProductContents curProductContents = p_origProductContents;//TODO this makes no sense
			
			//read the current file data
			long bytesWritten = readNextFileData(curFileContents, outStream);
			
			//not finished unless all the bytes were read
			boolean finished = bytesWritten == curFileContents.getRemainingData();
			int increment = 1;
			ProductExtractor curExtractor = this; 
			while (!finished)
			{
				//set the current extractor's manager's enclosing folder
				curExtractor.f_manager.setEnclosingFolder(curExtractor.f_enclosingFolder);
				
				//there are other fragments that need to be added,
				//find the next product file
				String searchName = FileSystemUtil.getProductName(curProductContents.getStreamUUID(),
												curProductContents.getProductSequenceNumber() + increment);
				File nextProductFile = f_manager.findProductFile(searchName,
								curExtractor.f_curProductFile.getAbsoluteFile().getParentFile());
				
				if (nextProductFile == null)
				{
					Logger.log(LogLevel.k_error, "Could not find referenced product file: " +
									searchName);
					return null;
				}
				
				//the fragment we're looking for will be the first file in the next product
				curExtractor = this.clone();

				finished = curExtractor.extractFragmentData(nextProductFile, outStream);
				
				//now looking for the next next product file...
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
	 * @param nextProductFile
	 * @param p_outStream
	 * @return True if this was the last fragment of that file, false 
	 * if there's more data in a later file.
	 * @throws IOException 
	 */
	private boolean extractFragmentData(File p_productFile,
					BufferedOutputStream p_outStream) throws IOException
	{
		parseProductContents(p_productFile);
		
		//this product contents will be the fragment we're looking for
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
					f_manager.setExplored(p_productFile);
				}
				
				return finished;
			}
			catch (IOException e)
			{
				throw new ProductIOException("Failed to read first file data: " + 
								fileContents.getMetadata().getFile().getPath());
			}	
		}
		else
		{
			//the first thing in this product wasn't a file, so something's wrong
			throw new ProductIOException("The first file in this product "
							+ "was not a k_file type: " + p_productFile.getAbsolutePath());
		}
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 */
	public void mapHeaders(File p_productFile)
	{
		Logger.log(LogLevel.k_info, "Indexing available file IDs...");
		if (p_productFile.isDirectory())
		{
			//bfs through folders for product files
			Queue<File> folders = new LinkedList<File>();
			folders.add(p_productFile);
			
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
							//the file could be any file, so it might not even be a product file
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
				mapHeader(p_productFile);
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not read product file: " + p_productFile.getName());
			}
		}
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 * @throws IOException
	 */
	private void mapHeader(File p_productFile) throws IOException
	{
		ProductContents productContents = parseProductContents(p_productFile);
		String fileName = FileSystemUtil.getProductName(productContents.getStreamUUID(),
						productContents.getProductSequenceNumber());
		f_manager.cacheHeaderLocation(fileName, p_productFile);
	}

	/**
	 * @update_comment
	 * @param p_productFile
	 * @param p_extractionFolder
	 * @return
	 * @throws IOException
	 */
	public boolean extractAllFromProduct(File p_productFile, File p_extractionFolder) throws IOException
	{
		ProductContents productContents = parseProductContents(p_productFile);
		
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
					//assemble this file, if it has other fragments, follow the trail of products
					File assembled = assembleCurrentFileData(productContents, fileContents, p_extractionFolder);
					
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
		f_manager.setExplored(p_productFile);

		return true;
	}

	

	/**
	 * @update_comment
	 * @param p_productFolder
	 * @param p_extractionFolder
	 * @return
	 */
	public boolean extractAllFromProductFolder(File p_productFolder, File p_extractionFolder)
	{
		if (!p_productFolder.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product folder is a "
							+ "file, use \"extractAll\": " + p_productFolder.getName());
			return false;
		}
		
		boolean success = true;
		
		//reset explored files since this is a new run
		f_manager.resetExploredFiles();
		
		//bfs through folders for product files
		Queue<File> folders = new LinkedList<File>();
		folders.add(p_productFolder);
		
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
							extractAllFromProduct(sub, p_extractionFolder);
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
	 * @update_comment
	 * @param p_productFile
	 * @param p_extractionFolder
	 * @param p_index
	 * @return
	 * @throws IOException
	 */
	public boolean extractFileByIndex(File p_productFile, File p_extractionFolder, int p_index) throws IOException
	{
		ProductContents productContents = parseProductContents(p_productFile);
		
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
					
					//assemble this file, if it has other fragments, follow the trail of products
					File assembled = assembleCurrentFileData(productContents, fileContents, p_extractionFolder);
					
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
	 * @update_comment
	 * @param p_productFile
	 * @return
	 * @throws IOException
	 */
	private ProductContents parseProductContents(File p_productFile) throws IOException
	{
		if (p_productFile.isDirectory())
		{
			throw new ProductIOException( "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + p_productFile.getName());
		}
		
		//try to load the product file
		loadProduct(p_productFile);

		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			throw new ProductIOException("The product header cannot be read: " + p_productFile.getName());
		}
		
		return productContents;
	}
	
	/**
	 * @update_comment
	 * @param p_productFile
	 * @throws IOException
	 */
	private void loadProduct(File p_productFile) throws IOException
	{
		f_curProductFile = p_productFile;
		
		try
		{
			f_product.loadFile(p_productFile);
			Logger.log(LogLevel.k_debug, "Loaded Product File for Reading: " + p_productFile.getName());
		}
		catch (IOException e)
		{
			f_curProductFile = null;
			Logger.log(LogLevel.k_error, "Failed to load product file " + p_productFile.getName());
			throw e;
		}
	}
	
	/**
	 * @update_comment
	 * @param p_length
	 * @return
	 */
	private boolean readFull(int p_length)
	{
		return f_product.read(f_buffer, 0, p_length) == p_length;
	}
	
	/**
	 * @update_comment
	 * @param p_skip
	 * @return
	 */
	private boolean skipFull(long p_skip)
	{
		return f_product.skip(p_skip) == p_skip;
	}
	
	/**
	 * @update_comment
	 * @param p_parseData
	 * @return
	 * @throws ProductIOException
	 */
	private ProductContents readProductHeader(boolean p_parseData) throws ProductIOException
	{
		Logger.log(LogLevel.k_debug, "Reading product header");
		try
		{
			//setup contents
			ProductContents contents = null;
			if (p_parseData)
			{
				contents = new ProductContents();
			}
			
			//product uuid:
			//always read this b/c the product may need it to de-secure stream
			if (!readFull(Constants.PRODUCT_UUID_SIZE))
				throw new ProductIOException("Could not read product uuid.");
			
			f_product.setUUID(ByteConversion.subArray(f_buffer, 0, Constants.PRODUCT_UUID_SIZE));
			
			if (p_parseData)
			{
				contents.setStreamUUID(ByteConversion.getStreamUUID(f_product.getUUID()));
				contents.setProductSequenceNumber(ByteConversion.getProductSequenceNumber(f_product.getUUID()));
			}
				
			f_product.secureStream();

			//product version
			if (p_parseData)
			{
				if (!readFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not read product version number.");
				
				contents.setProductVersionNumber(ByteConversion.byteToInt(f_buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not skip product version number.");
			}

			return contents;
		}
		catch (ProductIOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			
			//there are lots of bad things that can happen when parsing
			//random data. A general product IO exception will suffice.
			throw new ProductIOException("Failed to read product header.");
		}
	}
	
	/**
	 * @update_comment
	 * @param p_parseData
	 * @return
	 * @throws ProductIOException
	 */
	private FileContents readNextFileHeader(boolean p_parseData) throws ProductIOException
	{
		Logger.log(LogLevel.k_debug, "Reading file header");

		FileContents contents = null;
		
		if (p_parseData)
		{
			contents = new FileContents();
			contents.setMetadata(new Metadata());
		}
		
		try //TODO convert to throwing ProductIOExceptions like the other one
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
				throw new ProductIOException("Failed to read file type.");
			
			contents.getMetadata().setType(fileType);
			
			//file type:
			if (fileType.equals(FileType.k_file))
			{
				//file name length
				if (!readFull(Constants.FILE_NAME_LENGTH_SIZE))
					return null;
				short fileNameLength = ByteConversion.bytesToShort(f_buffer, 0);

				//file name
				if (p_parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(f_buffer, 0, fileNameLength, Constants.CHARSET)));
				}
				else
				{
					if (!skipFull(fileNameLength))
						return null;
				}
				
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
			else
			{
				//folder type:
				
				//file name length
				if (!readFull(Constants.FILE_NAME_LENGTH_SIZE))
					return null;
				short fileNameLength = ByteConversion.bytesToShort(f_buffer, 0);

				//file name
				if (p_parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(f_buffer, 0, fileNameLength, Constants.CHARSET)));
				}
				else
				{
					if (!skipFull(fileNameLength))
						return null;
				}
			}
			
			return contents;

		}
		catch (ProductIOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			throw new ProductIOException("Failed to read file header: " + e.getMessage());
		}
	}
	
	
	/**
	 * @update_comment
	 * @param p_fileContents
	 * @return
	 */
	private boolean skipNextFileData(FileContents p_fileContents)
	{
		long fileLengthRemaining = p_fileContents.getRemainingData();
		
		//This might try to over-read because fileLengthRemaining could be more
		//than what's left in the product if the file continues on in the next
		//product. If it does, nothing bad should happen.
		return skipFull(fileLengthRemaining);
	}
	
	/**
	 * @update_comment
	 * @param p_fileContents
	 * @param p_output
	 * @return
	 * @throws IOException
	 */
	private long readNextFileData(FileContents p_fileContents, BufferedOutputStream p_output) throws IOException
	{
		long fileLengthRemaining = p_fileContents.getRemainingData();
		long totalBytesRead = 0;

		while (fileLengthRemaining > 0)
		{
			//read from product
			int dataLength = (int) Math.min(f_buffer.length, fileLengthRemaining);
			int bytesRead = f_product.read(f_buffer, 0, dataLength);
			totalBytesRead += bytesRead;
			
			if (bytesRead == 0)
			{
				//no more data can be read from the product
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
