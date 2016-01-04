package product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import algorithms.ProductIOException;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.Constants;
import util.FileSystemUtil;
import util.Hashing;
import data.FileType;
import data.Metadata;
import data.FileAssembler;
import data.TrackingGroup;

public class ProductExtractor {
	
	private ProductReader product;
	private byte[] curFileHash;
	private long curFragmentNumber;
	private byte[] buffer;
	private TrackingGroup group;
	private File enclosingFolder;
	private File curProductFile;
	
	
	public ProductExtractor(TrackingGroup group, File enclosingFolder)
	{
		this.group = group;
		setEnclosingFolder(enclosingFolder);
		product = group.getProductReaderFactory().createReader();
		
		buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
	}
	
	public void setEnclosingFolder(File folder)
	{
		enclosingFolder = folder;
	}
	
	public ProductContents viewAll(File productFile)
	{
		//try to load the product file
		if (!loadProduct(productFile))
		{
			return null;
		}
		
		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		if (productContents == null)
		{
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return null;
		}
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			//add header information to product contents
			productContents.addFileContents(fileContents);
			
			//skip over the file data
			skipNextFileData(fileContents);
			
			//try to get the next header
			fileContents = readNextFileHeader(true);
		}
			
		if (productContents.getFileContents().isEmpty())
		{
			//There were no files beyond the product header. This shouldn't happen, so it's probably
			//a corrupted header.
			Logger.log(LogLevel.k_debug, "There were no files recovered from " + productFile.getPath());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			
			//return null even though the product header got parsed into something... it's
			//probably not useful.
			return null;
		}
		
		return productContents;
	}
	
	private File assembleCurrentFileData(ProductContents origProductContents, FileContents origFileContents)
	{
		//create temporary hidden assembly folder
		//TODO make this path configurable or put it on the same drive as the product files
		File assemblyFolder = new File(group.getExtractionFolder(), ".assembly");
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
			ProductContents curProductContents = origProductContents;
			
			//read the current file data
			long bytesWritten = readNextFileData(curFileContents, outStream);
			
			//not finished unless all the bytes were read
			boolean finished = bytesWritten == curFileContents.getRemainingData();
			int increment = 1;
			ProductExtractor curExtractor = this;
			while (!finished)
			{
				//there are other fragments that need to be added,
				//find the next product file
				File nextProductFile = FileAssembler.findProductFile(
								FileSystemUtil.getProductName(group, curProductContents.getStreamUUID(),
												curProductContents.getProductSequenceNumber() + increment),
								curExtractor.enclosingFolder,
								curExtractor.curProductFile.getAbsoluteFile().getParentFile());
				
				//the fragment we're looking for will be the first file in the next product
				curExtractor = new ProductExtractor(group, enclosingFolder);
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
	 * @throws ProductIOException 
	 */
	private boolean extractFragmentData(File productFile,
					BufferedOutputStream outStream) throws ProductIOException
	{
		if (productFile.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + productFile.getName());
			throw new ProductIOException("The product file is a folder: " + productFile.getAbsolutePath());
		}
		
		//try to load the product file
		if (!loadProduct(productFile))
		{
			throw new ProductIOException("Failed to load product file: " + productFile.getAbsolutePath());
		}
		
		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			throw new ProductIOException("Failed to extract product header from: " + productFile.getAbsolutePath());
		}
		
		//this product contents will be the fragment we're looking for
		FileContents fileContents = readNextFileHeader(true);

		if (fileContents.getMetadata().getType().equals(FileType.k_file))
		{
			try
			{
				long bytesRead = readNextFileData(fileContents, outStream);
				return bytesRead >= fileContents.getRemainingData();
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

	public boolean extractAllFromProduct(File productFile)
	{
		if (productFile.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + productFile.getName());
			return false;
		}
		
		//try to load the product file
		if (!loadProduct(productFile))
		{
			return false;
		}
		
		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return false;
		}
		
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
					File assembled = assembleCurrentFileData(productContents, fileContents);
					
					if (assembled != null)
					{
						FileAssembler.moveToExtractionFolder(assembled, fileContents, group);
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
				FileAssembler.moveFolderToExtractionFolder(fileContents, group);
			}
			else //k_reference
			{
				byte[] refHash = fileContents.getMetadata().getFileHash();
				byte[] refUUID = fileContents.getMetadata().getRefProductUUID();
				long refStreamUUID = ByteConversion.getStreamUUID(refUUID);
				int refSequenceNum = ByteConversion.getProductSequenceNumber(refUUID);
				
				File refProductFile = FileAssembler.findProductFile(
								FileSystemUtil.getProductName(group, refStreamUUID, refSequenceNum),
								enclosingFolder, productFile.getParentFile());
				
				if (refProductFile == null)
				{
					Logger.log(LogLevel.k_error, "Could not find referenced product file: " +
									refStreamUUID + "_" + refSequenceNum);
					
					//read next header
					fileContents = readNextFileHeader(true);
					
					continue;
				}
				else
				{
					ProductExtractor subExtractor = new ProductExtractor(group, enclosingFolder);
					subExtractor.extractFileByFirstHashMatch(refProductFile, refHash);
				}
					
			}
			
			//read next header
			fileContents = readNextFileHeader(true);
		}

		return true;
	}

	

	public boolean extractAllFromProductFolder(File productFolder)
	{
		if (!productFolder.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product folder is a "
							+ "file, use \"extractAll\": " + productFolder.getName());
			return false;
		}
		
		//bfs through folders for product files
		Queue<File> folders = new LinkedList<File>();
		folders.add(productFolder);
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			for (File sub : folder.listFiles())
			{
				if (sub.isDirectory())
					folders.add(sub);
				else
					extractAllFromProduct(sub);
			}
		}
		
		return true;
	}
	
	public boolean extractFileByIndex(File productFile, int index)
	{
		if (productFile.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + productFile.getName());
			return false;
		}
		
		//try to load the product file
		if (!loadProduct(productFile))
		{
			return false;
		}
		
		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return false;
		}
		
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
					File assembled = assembleCurrentFileData(productContents, fileContents);
					
					if (assembled != null)
					{
						FileAssembler.moveToExtractionFolder(assembled, fileContents, group);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " +
										fileContents.getMetadata().getFile().getPath());
					}	
				}
				else if (fileContents.getMetadata().getType().equals(FileType.k_folder))
				{
					FileAssembler.moveFolderToExtractionFolder(fileContents, group);
				}
				else //k_reference
				{
					byte[] refHash = fileContents.getMetadata().getFileHash();
					byte[] refUUID = fileContents.getMetadata().getRefProductUUID();
					long refStreamUUID = ByteConversion.getStreamUUID(refUUID);
					int refSequenceNum = ByteConversion.getProductSequenceNumber(refUUID);
					
					File refProductFile = FileAssembler.findProductFile(
									FileSystemUtil.getProductName(group, refStreamUUID, refSequenceNum),
									enclosingFolder, productFile.getParentFile());
					
					if (refProductFile == null)
					{
						Logger.log(LogLevel.k_error, "Could not find referenced product file: " +
										refStreamUUID + "_" + refSequenceNum);
						continue;
					}
					else
					{
						ProductExtractor subExtractor = new ProductExtractor(group, enclosingFolder);
						subExtractor.extractFileByFirstHashMatch(refProductFile, refHash);
					}	
				}
			}
			else //this wasn't the correct index, just skip the data
			{
				skipNextFileData(fileContents);
				continue;
			}

			//read next header
			fileContents = readNextFileHeader(true);
			
			++curIndex;
		}

		return true;
	}
	
	
	/**
	 * @update_comment
	 * @param refProductFile
	 * @param fileHash
	 */
	private boolean extractFileByFirstHashMatch(File productFile, byte[] fileHash)
	{
		if (productFile.isDirectory())
		{
			Logger.log(LogLevel.k_error, "The product file is a "
							+ "folder, use \"extractAllRecursive\": " + productFile.getName());
			return false;
		}
		
		//try to load the product file
		if (!loadProduct(productFile))
		{
			return false;
		}
		
		//try to read the product header
		ProductContents productContents = readProductHeader(true);
		if (productContents == null)
		{
			//if the product header can't be read,
			//it's assumed that nothing else can be read
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return false;
		}
		
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
					File assembled = assembleCurrentFileData(productContents, fileContents);
					
					if (assembled != null)
					{
						FileAssembler.moveToExtractionFolder(assembled, fileContents, group);
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
	
	private boolean loadProduct(File productFile)
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
			Logger.log(LogLevel.k_error, e, false);
			return false;
		}
		
		return true;
	}
	
	private boolean readFull(int length)
	{
		return product.read(buffer, 0, length) == length;
	}
	
	private boolean skipFull(long skip)
	{
		return product.skip(skip) == skip;
	}
	
	private ProductContents readProductHeader(boolean parseData)
	{
		System.err.println("Reading product header");
		try
		{
			//product uuid:
			if (!readFull(Constants.PRODUCT_UUID_SIZE))
				return null;
			product.setUUID(ByteConversion.subArray(buffer, 0, Constants.PRODUCT_UUID_SIZE));
			System.out.println("Read " + ByteConversion.getStreamUUID(product.getUUID()));
			System.out.println("Read " + ByteConversion.getProductSequenceNumber(product.getUUID()));
				
			//stealth secure stream now
			if (product.getProductMode().equals(ProductMode.STEALTH))
			{
				product.secureStream();
			}
			
			//setup contents
			ProductContents contents = new ProductContents();

			//product version
			if (parseData)
			{
				if (!readFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					return null;
				contents.setProductVersionNumber(ByteConversion.byteToInt(buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					return null;
			}
				
			//algorithm name length
			if (!readFull(Constants.ALGORITHM_NAME_LENGTH_SIZE))
				return null;
			short algorithmNameLength = ByteConversion.bytesToShort(buffer, 0);
				
			//algorithm name
			if (parseData)
			{
				if (!readFull(algorithmNameLength))
					return null;
				contents.setAlgorithmName(new String(buffer, 0, algorithmNameLength));
			}
			else
			{
				if (!skipFull(algorithmNameLength))
					return null;
			}
				
			//algorithm version
			if (parseData)
			{
				if (!readFull(Constants.ALGORITHM_VERSION_NUMBER_SIZE))
					return null;
				contents.setAlgorithmVersionNumber(ByteConversion.byteToInt(buffer[0]));
			}
			else
			{
				if (!skipFull(Constants.ALGORITHM_VERSION_NUMBER_SIZE))
					return null;
			}
			
			//stream uuid
			if (parseData)
			{
				contents.setStreamUUID(ByteConversion.getStreamUUID(product.getUUID()));
			}
			else
			{
				if (!skipFull(Constants.STREAM_UUID_SIZE))
					return null;
			}
				
			//product sequence number
			if (parseData)
			{
				contents.setProductSequenceNumber(ByteConversion.getProductSequenceNumber(product.getUUID()));
			}
			else
			{
				if (!skipFull(Constants.PRODUCT_SEQUENCE_NUMBER_SIZE))
					return null;
			}
				
			//group name length
			if (!readFull(Constants.GROUP_NAME_LENGTH_SIZE))
				return null;
			short groupNameLength = ByteConversion.bytesToShort(buffer, 0);
			System.out.println("groupNameLength: " + groupNameLength);
				
			//group name
			if (parseData)
			{
				if (!readFull(groupNameLength))
					return null;
				contents.setGroupName(new String(buffer, 0, groupNameLength));
			}
			else
			{
				if (!skipFull(groupNameLength))
					return null;
			}
			
			//group key name length
			if (!readFull(Constants.GROUP_KEY_NAME_LENGTH_SIZE))
				return null;
			short groupKeyNameLength = ByteConversion.bytesToShort(buffer, 0);
			//System.out.println("Read group key name length of: " + groupKeyNameLength);
				
			//group key name
			if (parseData)
			{
				if (!readFull(groupKeyNameLength))
					return null;
				contents.setGroupKeyName(new String(buffer, 0, groupKeyNameLength));
				System.out.println("Read group key name of: " + new String(buffer, 0, groupKeyNameLength));
			}
			else
			{
				if (!skipFull(groupKeyNameLength))
					return null;
			}
				
			//secure products secure the stream now
			if (product.getProductMode().equals(ProductMode.SECURE))
			{
				product.secureStream();
			}
				
			return contents;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "Failed to read product header.");
			Logger.log(LogLevel.k_error, e, false);
			return null;
		}
	}
	
	private FileContents readNextFileHeader(boolean parseData)
	{
		System.err.println("Reading file header");

		FileContents contents = null;
		
		if (parseData)
		{
			contents = new FileContents();
			contents.setMetadata(new Metadata());
		}
		
		try
		{
			//fragment number
			if (!readFull(Constants.FRAGMENT_NUMBER_SIZE))
				return null;
			curFragmentNumber = ByteConversion.bytesToLong(buffer, 0);
			if (parseData)
			{
				contents.setFragmentNumber(curFragmentNumber);
			}
			
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
			System.out.println("File type number: " + fileTypeNum);
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
					System.out.println(ByteConversion.bytesToHex((curFileHash)));
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
				
				System.out.println("Read file name length of " + fileNameLength);
				
				//file name
				if (parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(buffer, 0, fileNameLength)));
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
				
				System.out.println("Read file name length of " + fileNameLength);
				
				//file name
				if (parseData)
				{
					if (!readFull(fileNameLength))
						return null;
					contents.getMetadata().setFile(new File(new String(buffer, 0, fileNameLength)));
				}
				else
				{
					if (!skipFull(fileNameLength))
						return null;
				}
			}
				
			System.out.println("parse data? " + parseData);
			if (contents != null)
				System.out.println(contents.toString());
				
			
			return contents;

		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "Failed to read file header.");
			Logger.log(LogLevel.k_error, e, false);
			return null;
		}
	}
	
	
	private void skipNextFileData(FileContents fileContents)
	{
		long fileLengthRemaining = fileContents.getRemainingData();
		
		//This might try to over-read because fileLengthRemaining could be more
		//than what's left in the product if the file continues on in the next
		//product. If it does, nothing bad should happen.
		skipFull(fileLengthRemaining);
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
		
		Logger.log(LogLevel.k_info, "Extracted file data belonging to: " + 
						fileContents.getMetadata().getFile().getPath());
		return totalBytesRead;
	}
}
