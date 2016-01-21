package product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import config.Constants;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.FileSystemUtil;
import util.Hashing;
import data.FileType;
import data.Metadata;
import data.TrackingGroup;

public class ProductExtractor {
	
	private ProductReader product;
	private byte[] curFileHash;
	private long curFragmentNumber;
	private byte[] buffer;
	private TrackingGroup group;
	private File enclosingFolder;
	private File curProductFile;
	private ExtractionManager manager;
	
	
	public ProductExtractor(TrackingGroup group, File enclosingFolder)
	{
		this(group, enclosingFolder, new ExtractionManager());
		
		mapHeaders(enclosingFolder);
	}
	
	private ProductExtractor(TrackingGroup group, File enclosingFolder, ExtractionManager manager)
	{
		this.group = group;
		setEnclosingFolder(enclosingFolder);
		product = group.getProductReaderFactory().createReader();
		
		buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
		this.manager = manager;
	}
	
	@Override
	public ProductExtractor clone()
	{
		return new ProductExtractor(group, enclosingFolder, manager);
	}
	
	public void setEnclosingFolder(File folder)
	{
		enclosingFolder = folder;
	}
	
	public ProductContents viewAll(File productFile) throws IOException
	{
		ProductContents productContents = parseProductContents(productFile);
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			//add header information to product contents
			productContents.addFileContents(fileContents);
			
			//skip over the file data
			boolean allDataSkipped = skipNextFileData(fileContents);
			fileContents.setFragment(!allDataSkipped);
			
			//try to get the next header
			fileContents = readNextFileHeader(true);
		}
			
		if (productContents.getFileContents().isEmpty())
		{
			//There were no files beyond the product header. This shouldn't happen, so it's probably
			//a corrupted header.
			Logger.log(LogLevel.k_error, "There were no files recovered from " + productFile.getPath());
			Logger.log(LogLevel.k_debug, "Failed to extract from " + productFile.getName());
			
			//return null even though the product header got parsed into something... it's
			//probably not useful.
			return null;
		}
		
		return productContents;
	}
	
	private File assembleCurrentFileData(ProductContents origProductContents, FileContents origFileContents, File extractionFolder)
	{
		//create temporary hidden assembly folder
		File assemblyFolder = new File(extractionFolder, Constants.ASSEMBLY_FOLDER_NAME);
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
			
			FileContents curFileContents = origFileContents;
			ProductContents curProductContents = origProductContents;//TODO this makes no sense
			
			//read the current file data
			long bytesWritten = readNextFileData(curFileContents, outStream);
			
			//not finished unless all the bytes were read
			boolean finished = bytesWritten == curFileContents.getRemainingData();
			int increment = 1;
			ProductExtractor curExtractor = this; 
			while (!finished)
			{
				//set the current extractor's manager's enclosing folder
				curExtractor.manager.setEnclosingFolder(curExtractor.enclosingFolder);
				
				//there are other fragments that need to be added,
				//find the next product file
				String searchName = FileSystemUtil.getProductName(group, curProductContents.getStreamUUID(),
												curProductContents.getProductSequenceNumber() + increment);
				File nextProductFile = manager.findProductFile(searchName,
								curExtractor.curProductFile.getAbsoluteFile().getParentFile());
				
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
	 * @param outStream
	 * @return True if this was the last fragment of that file, false 
	 * if there's more data in a later file.
	 * @throws IOException 
	 */
	private boolean extractFragmentData(File productFile,
					BufferedOutputStream outStream) throws IOException
	{
		parseProductContents(productFile);
		
		//this product contents will be the fragment we're looking for
		FileContents fileContents = readNextFileHeader(true);

		if (fileContents.getMetadata().getType().equals(FileType.k_file))
		{
			try
			{
				long bytesRead = readNextFileData(fileContents, outStream);
				long totalRemainingFileBytes = fileContents.getRemainingData();
				boolean finished = bytesRead >= totalRemainingFileBytes;
				
				//this file must be fully explored if we're not finished
				if (!finished)
				{
					manager.setExplored(productFile);
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
							+ "was not a k_file type: " + productFile.getAbsolutePath());
		}
	}
	
	public void mapHeaders(File productFile)
	{
		Logger.log(LogLevel.k_info, "Indexing available file IDs...");
		if (productFile.isDirectory())
		{
			//bfs through folders for product files
			Queue<File> folders = new LinkedList<File>();
			folders.add(productFile);
			
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
						catch (IOException e)
						{
							Logger.log(LogLevel.k_warning, "Could not read product file: " + sub.getName());
						}
					}
				}
			}
		}
		else
		{
			try
			{
				mapHeader(productFile);
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not read product file: " + productFile.getName());
			}
		}
	}
	
	private void mapHeader(File productFile) throws IOException
	{
		ProductContents productContents = parseProductContents(productFile);
		String fileName = FileSystemUtil.getProductName(
						group, productContents.getStreamUUID(),
						productContents.getProductSequenceNumber());
		manager.cacheHeaderLocation(fileName, productFile);
	}

	public boolean extractAllFromProduct(File productFile, File extractionFolder) throws IOException
	{
		ProductContents productContents = parseProductContents(productFile);
		
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
					File assembled = assembleCurrentFileData(productContents, fileContents, extractionFolder);
					
					if (assembled != null)
					{
						manager.moveFileToExtractionFolder(assembled, fileContents, extractionFolder);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " +
										fileContents.getMetadata().getFile().getPath());
					}
				}
					
			}
			else if (fileContents.getMetadata().getType().equals(FileType.k_folder))
			{
				manager.moveFolderToExtractionFolder(fileContents, extractionFolder);
			}
			else //k_reference
			{
				byte[] refHash = fileContents.getMetadata().getFileHash();
				byte[] refUUID = fileContents.getMetadata().getRefProductUUID();
				long refStreamUUID = ByteConversion.getStreamUUID(refUUID);
				int refSequenceNum = ByteConversion.getProductSequenceNumber(refUUID);
				
				//a copy of the file may have already been extracted
				//if so, copy it instead of searching for it in a product file
				File existing = manager.getPreviouslyExtractedFile(refHash);
				if (existing != null)
				{
					manager.copyFileToExtractionFolder(existing, fileContents, extractionFolder);
				}
				else
				{
					manager.setEnclosingFolder(enclosingFolder);
					
					String searchName = FileSystemUtil.getProductName(group, refStreamUUID, refSequenceNum);
					File refProductFile = manager.findProductFile(searchName,
									productFile.getAbsoluteFile().getParentFile());
					
					if (refProductFile == null)
					{
						Logger.log(LogLevel.k_error, "Could not find referenced product file: " +
										searchName);
						
						//read next header
						fileContents = readNextFileHeader(true);
						
						continue;
					}
					else
					{
						ProductExtractor subExtractor = this.clone();
						subExtractor.extractAndCopyFirstHashMatch(refProductFile, extractionFolder, refHash, fileContents);
					}
				}
					
			}
			
			//read next header
			fileContents = readNextFileHeader(true);
		}
		
		//set this file explored since it's all been read
		manager.setExplored(productFile);

		return true;
	}

	

	public boolean extractAllFromProductFolder(File productFolder, File extractionFolder)
	{
		if (!productFolder.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product folder is a "
							+ "file, use \"extractAll\": " + productFolder.getName());
			return false;
		}
		
		boolean success = true;
		
		//reset explored files since this is a new run
		manager.resetExploredFiles();
		
		//bfs through folders for product files
		Queue<File> folders = new LinkedList<File>();
		folders.add(productFolder);
		
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
					if (!manager.isExplored(sub))
					{
						try
						{
							extractAllFromProduct(sub, extractionFolder);
						}
						catch (IOException e)
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
	
	public boolean extractFileByIndex(File productFile, File extractionFolder, int index) throws IOException
	{
		ProductContents productContents = parseProductContents(productFile);
		
		int curIndex = 0;
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			//wait until we're at the correct index
			if (curIndex == index)
			{
				if (fileContents.getMetadata().getType().equals(FileType.k_file))
				{
					
					//assemble this file, if it has other fragments, follow the trail of products
					File assembled = assembleCurrentFileData(productContents, fileContents, extractionFolder);
					
					if (assembled != null)
					{
						manager.moveFileToExtractionFolder(assembled, fileContents, extractionFolder);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " +
										fileContents.getMetadata().getFile().getPath());
						
						return false;
					}	
				}
				else if (fileContents.getMetadata().getType().equals(FileType.k_folder))
				{
					manager.moveFolderToExtractionFolder(fileContents, extractionFolder);
				}
				else //k_reference
				{
					byte[] refHash = fileContents.getMetadata().getFileHash();
					byte[] refUUID = fileContents.getMetadata().getRefProductUUID();
					long refStreamUUID = ByteConversion.getStreamUUID(refUUID);
					int refSequenceNum = ByteConversion.getProductSequenceNumber(refUUID);
					
					//a copy of the file may have already been extracted
					//if so, copy it instead of searching for it in a product file
					File existing = manager.getPreviouslyExtractedFile(refHash);
					if (existing != null)
					{
						manager.copyFileToExtractionFolder(existing, fileContents, extractionFolder);
					}
					else
					{
						manager.setEnclosingFolder(enclosingFolder);
						
						String searchName = FileSystemUtil.getProductName(group, refStreamUUID, refSequenceNum);
						File refProductFile = manager.findProductFile(searchName,
										productFile.getAbsoluteFile().getParentFile());
						
						if (refProductFile == null)
						{
							Logger.log(LogLevel.k_error, "Could not find referenced product file: " +
											searchName);
							return false;
						}
						else
						{
							ProductExtractor subExtractor = this.clone();
							subExtractor.extractAndCopyFirstHashMatch(refProductFile, extractionFolder, refHash, fileContents);
						}
					}
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
	
	
	private ProductContents parseProductContents(File productFile) throws IOException
	{
		if (productFile.isDirectory())
		{
			throw new ProductIOException( "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + productFile.getName());
		}
		
		//try to load the product file
		loadProduct(productFile);

		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			throw new ProductIOException("The product header cannot be read: " + productFile.getName());
		}
		
		return productContents;
	}
	
	/**
	 * @update_comment
	 * @param refProductFile
	 * @param fileHash
	 * @throws IOException 
	 */
	private boolean extractAndCopyFirstHashMatch(File productFile, 
					File extractionFolder, byte[] fileHash, FileContents destContents) throws IOException
	{
		ProductContents productContents = parseProductContents(productFile);
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			if (fileContents.getMetadata().getType().equals(FileType.k_file))
			{
				byte[] curHash = fileContents.getMetadata().getFileHash();
				if (ByteConversion.bytesEqual(curHash, fileHash))
				{
					//assemble this file, if it has other fragments, follow the trail of products
					File assembled = assembleCurrentFileData(productContents, fileContents, extractionFolder);
					
					if (assembled != null)
					{
						manager.moveFileToExtractionFolder(assembled, destContents, extractionFolder);
						return true;
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
					//skip on to the next file if this one didn't match
					skipNextFileData(fileContents);
				}	
			}
			
			//file types which aren't k_file don't have file data,
			//so, nothing to skip
			
			//read next header
			fileContents = readNextFileHeader(true);
		}

		//couldn't find the file
		Logger.log(LogLevel.k_error, "Failed to find file with hash: " +
						ByteConversion.bytesToHex(fileHash) + " in product file: " +
						productFile.getName());
		return false;
	}
	
	private void loadProduct(File productFile) throws IOException
	{
		curProductFile = productFile;
		
		try
		{
			product.loadFile(productFile);
			Logger.log(LogLevel.k_debug, "Loaded Product File for Reading: " + productFile.getName());
		}
		catch (IOException e)
		{
			curProductFile = null;
			Logger.log(LogLevel.k_error, "Failed to load product file " + productFile.getName());
			throw e;
		}
	}
	
	private boolean readFull(int length)
	{
		return product.read(buffer, 0, length) == length;
	}
	
	private boolean skipFull(long skip)
	{
		return product.skip(skip) == skip;
	}
	
	private ProductContents readProductHeader(boolean parseData) throws ProductIOException
	{
		Logger.log(LogLevel.k_debug, "Reading product header");
		try
		{
			//setup contents
			ProductContents contents = null;
			if (parseData)
			{
				contents = new ProductContents();
			}
			
			//product uuid:
			//always read this b/c the product may need it to de-secure stream
			if (!readFull(Constants.PRODUCT_UUID_SIZE))
				throw new ProductIOException("Could not read product uuid.");
			
			product.setUUID(ByteConversion.subArray(buffer, 0, Constants.PRODUCT_UUID_SIZE));
			
			if (parseData)
			{
				contents.setStreamUUID(ByteConversion.getStreamUUID(product.getUUID()));
				contents.setProductSequenceNumber(ByteConversion.getProductSequenceNumber(product.getUUID()));
			}
			
			//System.out.println("Read stream uuid " + ByteConversion.getStreamUUID(product.getUUID()));
			//System.out.println("Read product sequence number " + ByteConversion.getProductSequenceNumber(product.getUUID()));
				
			//stealth secure stream now
			if (product.getProductMode().equals(ProductMode.k_secure))
			{
				product.secureStream();
			}

			//product version
			if (parseData)
			{
				if (!readFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not read product version number.");
				
				contents.setProductVersionNumber(ByteConversion.byteToInt(buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not skip product version number.");
			}
				
			//algorithm name length
			if (!readFull(Constants.ALGORITHM_NAME_LENGTH_SIZE))
				throw new ProductIOException("Could not read algorithm length.");
			
			short algorithmNameLength = ByteConversion.bytesToShort(buffer, 0);
				
			//algorithm name
			if (parseData)
			{
				if (!readFull(algorithmNameLength))
					throw new ProductIOException("Could not read algorithm name.");
				
				contents.setAlgorithmName(new String(buffer, 0, algorithmNameLength, Constants.CHARSET));
			}
			else
			{
				if (!skipFull(algorithmNameLength))
					throw new ProductIOException("Could not skip algorithm name.");
			}
				
			//algorithm version
			if (parseData)
			{
				if (!readFull(Constants.ALGORITHM_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not read algorithm version number.");
				
				contents.setAlgorithmVersionNumber(ByteConversion.byteToInt(buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.ALGORITHM_VERSION_NUMBER_SIZE))
					throw new ProductIOException("Could not skip algorithm version number.");
			}
				
			//group name length
			if (!readFull(Constants.GROUP_NAME_LENGTH_SIZE))
				throw new ProductIOException("Could not read group name length.");
			
			short groupNameLength = ByteConversion.bytesToShort(buffer, 0);
			//System.out.println("groupNameLength: " + groupNameLength);
				
			//group name
			if (parseData)
			{
				if (!readFull(groupNameLength))
					throw new ProductIOException("Could not read group name.");
				
				contents.setGroupName(new String(buffer, 0, groupNameLength, Constants.CHARSET));
			}
			else
			{
				if (!skipFull(groupNameLength))
					throw new ProductIOException("Could not skip group name.");
			}
			
			//group key name length
			if (!readFull(Constants.GROUP_KEY_NAME_LENGTH_SIZE))
				throw new ProductIOException("Could not read group key name length.");
			
			short groupKeyNameLength = ByteConversion.bytesToShort(buffer, 0);
			//System.out.println("Read group key name length of: " + groupKeyNameLength);
				
			//group key name
			if (parseData)
			{
				if (!readFull(groupKeyNameLength))
					throw new ProductIOException("Could not read group key name.");
				
				contents.setGroupKeyName(new String(buffer, 0, groupKeyNameLength, Constants.CHARSET));
				//System.out.println("Read group key name of: " + new String(buffer, 0, groupKeyNameLength, Constants.CHARSET));
			}
			else
			{
				if (!skipFull(groupKeyNameLength))
					throw new ProductIOException("Could not skip group key name.");
			}
				
			//secure products secure the stream now
			if (product.getProductMode().equals(ProductMode.k_trackable))
			{
				product.secureStream();
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
	
	private FileContents readNextFileHeader(boolean parseData)
	{
		Logger.log(LogLevel.k_debug, "Reading file header");

		FileContents contents = null;
		
		if (parseData)
		{
			contents = new FileContents();
			contents.setMetadata(new Metadata());
		}
		
		try //TODO convert to throwing ProductIOExceptions like the other one
		{
			//fragment number
			if (!readFull(Constants.FRAGMENT_NUMBER_SIZE))
				return null;
			curFragmentNumber = ByteConversion.bytesToLong(buffer, 0);
			if (parseData)
			{
				contents.setFragmentNumber(curFragmentNumber);
			}
			
			//Logger.log(LogLevel.k_debug, "read fragment number: " + curFragmentNumber + 
			//				", " + (curFragmentNumber == Constants.END_CODE));
			
			//if end code, no more files
			if (curFragmentNumber == Constants.END_CODE)
			{
				Logger.log(LogLevel.k_debug, "The end code was reached.");
				return null;
			}
			
			//file type
			if (!readFull(Constants.FILE_TYPE_SIZE))
				return null;
			int fileTypeNum = ByteConversion.byteToInt(buffer[0]);
			//System.out.println("File type number: " + fileTypeNum);
			FileType fileType = FileType.toFileType(fileTypeNum);
			contents.getMetadata().setType(fileType);
			
			//file or reference type:
			if (fileType.equals(FileType.k_file) || fileType.equals(FileType.k_reference))
			{
				//file hash
				if (parseData)
				{
					if (!readFull(Constants.FILE_HASH_SIZE))
						return null;
					curFileHash = ByteConversion.subArray(buffer, 0, Constants.FILE_HASH_SIZE);
					//System.out.println(ByteConversion.bytesToHex((curFileHash)));
					contents.getMetadata().setFileHash(curFileHash);
				}
				else
				{
					if (!skipFull(Constants.FILE_HASH_SIZE))
						return null;
				}
				
				//file name length
				if (!readFull(Constants.FILE_NAME_LENGTH_SIZE))
					return null;
				short fileNameLength = ByteConversion.bytesToShort(buffer, 0);
				
				//System.out.println("Read file name length of " + fileNameLength);
				
				//file name
				if (parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(buffer, 0, fileNameLength, Constants.CHARSET)));
				}
				else
				{
					if (!skipFull(fileNameLength))
						return null;
				}
				
				//date created
				if (parseData)
				{
					if (!readFull(Constants.DATE_CREATED_SIZE))
						return null;
					contents.getMetadata().setDateCreated(ByteConversion.bytesToLong(buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.DATE_CREATED_SIZE))
						return null;
				}
				
				//date modified
				if (parseData)
				{
					if (!readFull(Constants.DATE_MODIFIED_SIZE))
						return null;
					contents.getMetadata().setDateModified(ByteConversion.bytesToLong(buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.DATE_MODIFIED_SIZE))
						return null;
				}
				
				//permissions
				if (parseData)
				{
					if (!readFull(Constants.PERMISSIONS_SIZE))
						return null;
					contents.getMetadata().setPermissions(ByteConversion.bytesToShort(buffer, 0));
				}
				else
				{
					if (!skipFull(Constants.PERMISSIONS_SIZE))
						return null;
				}
				
				//file length remaining
				if (!readFull(Constants.FILE_LENGTH_REMAINING_SIZE))
					return null;
				contents.setRemainingData(ByteConversion.bytesToLong(buffer, 0));
				
				if (fileType.equals(FileType.k_reference))
				{
					//pf1uuid
					if (parseData)
					{
						if (!readFull(Constants.PRODUCT_UUID_SIZE))
							return null;
						
						contents.getMetadata().setRefProductUUID(
										ByteConversion.subArray(buffer, 0, Constants.PRODUCT_UUID_SIZE));
					}
					else
					{
						if (!skipFull(Constants.PRODUCT_UUID_SIZE))
							return null;
					}
					
					//fragment count
					if (parseData)
					{
						if (!readFull(Constants.FRAGMENT_NUMBER_SIZE))
							return null;
						
						contents.getMetadata().setFragmentCount(ByteConversion.bytesToLong(buffer, 0));
					}
					else
					{
						if (!skipFull(Constants.FRAGMENT_NUMBER_SIZE))
							return null;
					}
				}
			}
			else
			{
				//folder type:
				
				//file name length
				if (!readFull(Constants.FILE_NAME_LENGTH_SIZE))
					return null;
				short fileNameLength = ByteConversion.bytesToShort(buffer, 0);
				
				//System.out.println("Read file name length of " + fileNameLength);
				
				//file name
				if (parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(buffer, 0, fileNameLength, Constants.CHARSET)));
				}
				else
				{
					if (!skipFull(fileNameLength))
						return null;
				}
			}
				
			//System.out.println("parse data? " + parseData);
			//if (contents != null)
			//	System.out.println(contents.toString());
			
			//if (contents != null)
			//	System.out.println(contents.toString());
			
			return contents;

		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "Failed to read file header.");
			Logger.log(LogLevel.k_error, e, false);
			return null;
		}
	}
	
	
	private boolean skipNextFileData(FileContents fileContents)
	{
		long fileLengthRemaining = fileContents.getRemainingData();
		
		//This might try to over-read because fileLengthRemaining could be more
		//than what's left in the product if the file continues on in the next
		//product. If it does, nothing bad should happen.
		return skipFull(fileLengthRemaining);
	}
	
	private long readNextFileData(FileContents fileContents, BufferedOutputStream output) throws IOException
	{
		long fileLengthRemaining = fileContents.getRemainingData();
		long totalBytesRead = 0;

		while (fileLengthRemaining > 0)
		{
			//read from product
			int dataLength = (int) Math.min(buffer.length, fileLengthRemaining);
			int bytesRead = product.read(buffer, 0, dataLength);
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
			output.write(buffer, 0, bytesRead);
		}
		
		output.flush();
		
		Logger.log(LogLevel.k_info, "Extracting file data belonging to: " + 
						fileContents.getMetadata().getFile().getName());
		return totalBytesRead;
	}
}
