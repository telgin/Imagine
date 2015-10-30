package product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.Constants;
import util.Hashing;
import config.Configuration;
import data.Metadata;
import data.TrackingGroup;

public class ProductExtractor {

	//delete factory member? can the factory ever become null?
	//do we ever need another product?
	//private final ProductFactory<? extends Product> factory;
	
	private ProductReader product;
	private byte[] curFileHash;
	private long curFragmentNumber;
	private File extractionFolder;
	private byte[] buffer;
	
	public ProductExtractor(ProductReaderFactory<? extends ProductReader> factory)
	{
		//this.factory = factory;
		product = factory.createReader();
		
		buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
	}
	/*
	public ProductContents getProductHeader(File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		if (!loadProduct(productFile))
			return null;
	
		return readProductHeader(true);
	}
	
	
	public ProductContents getProductContents(File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		if (!loadProduct(productFile))
			return null;
		
		ProductContents contents = null;
		
		try
		{
			contents = readProductHeader(true);
			while (product.getRemainingBytes() >= Constants.FRAGMENT_NUMBER_SIZE)
			{
				
				FileContents fileContents = readNextFileHeader(true);
				if (fileContents != null)
				{
					readNextFileData(false, fileContents.getMetadata().getFile());
					
					//at least get the metadata even if a failure occurred
					contents.addFileContents(fileContents);
				}
				else
				{
					Logger.log(LogLevel.k_error, "Failed to extract next file from product: " + productFile.getPath());
					break; // the metadata of a file was corrupted, probably not going to get back on track
				}
			}
				
		}
		catch(Exception e)
		{
			System.out.println("Here1");
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
		}
		
		return contents;
	}
	*/
	
	public ProductContents extractAll(File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		//try to load the product file
		if (!loadProduct(productFile))
		{
			return null;
		}
		
		//try to read the product header
		ProductContents contents = readProductHeader(true);
		if (contents == null)
		{
			Logger.log(LogLevel.k_debug, "The product header cannot be read: " + productFile.getName());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return null;
		}
		
		//keep trying to read files until one can't be read
		FileContents fileContents = readNextFileHeader(true);
		while (fileContents != null)
		{
			if (!fileContents.getMetadata().isMetadataUpdate())
			{
				File extracted = readNextFileData(true, fileContents);
				if (extracted != null)
				{
					fileContents.setExtractedFile(extracted.getParentFile());
					contents.addFileContents(fileContents);
				}
				else
				{
					Logger.log(LogLevel.k_error, "Failed to extract file: " + fileContents.getMetadata().getPath());
					//don't break, the file header was good so the next file might be ok?
					//questionably the metadata without the attached file could be added here,
					//but it's probably corrupted.
				}
			}
			else
			{
				//file is a metadata update, just add it
				contents.addFileContents(fileContents);
			}
			
			//try to get the next one
			fileContents = readNextFileHeader(true);
		}
			
		//There were no files beyond the product header. This shouldn't happen, so it's probably
		//a corrupted header.
		if (contents.getFileContents().isEmpty())
		{
			Logger.log(LogLevel.k_debug, "There were no files recovered from " + productFile.getPath());
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
			return null;
		}
		
		return contents;
	}
	
	private boolean loadProduct(File productFile)
	{
		try {
			product.loadFile(productFile);
			Logger.log(LogLevel.k_debug, "Loaded Product File for Reading: " + productFile.getName());
		} catch (IOException e) {
			Logger.log(LogLevel.k_error, "Failed to load product file " + productFile.getName());
			Logger.log(LogLevel.k_error, e, false);
			return false;
		}
		
		return true;
	}

	/*
	public FileContents extractFile(byte[] hash, File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		if (!loadProduct(productFile))
			return null;
		
		try
		{
			readProductHeader(false);
			
			while (product.getRemainingBytes() >= Constants.FRAGMENT_NUMBER_SIZE)
			{
				FileContents fileContents = readNextFileHeader(true);
				if (fileContents != null)
				{
					if (ByteConversion.bytesEqual(fileContents.getMetadata().getFileHash(), hash))
					{
						File extracted = readNextFileData(true, fileContents.getMetadata().getFile());
						if (extracted != null)
						{
							fileContents.setExtractedFile(extracted);
							return fileContents;
						}
						else
						{
							Logger.log(LogLevel.k_error, "Failed to extract file: " + fileContents.getMetadata().getPath());
							break;
						}
					}
				}
				else
				{
					//either we've reached the end of the product file, or it was corrupted
					break;
				}
			}
				
		}
		catch(Exception e)
		{
			System.out.println("Here3");
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
		}
		
		return null;
	}
	*/
	
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
			
			//in this case, the code for reading and skipping is substantially different
			if (parseData)
			{
				ProductContents contents = new ProductContents();

				//product version
				if (!readFull(Constants.PRODUCT_VERSION_NUMBER_SIZE))
					return null;
				contents.setProductVersionNumber(ByteConversion.byteToInt(buffer[0]));
				
				//algorithm name length
				if (!readFull(Constants.ALGORITHM_NAME_LENGTH_SIZE))
					return null;
				short algorithmNameLength = ByteConversion.bytesToShort(buffer, 0);
				
				//algorithm name
				if (!readFull(algorithmNameLength))
					return null;
				contents.setAlgorithmName(new String(buffer, 0, algorithmNameLength));
				
				//algorithm version
				if (!readFull(Constants.ALGORITHM_VERSION_NUMBER_SIZE))
					return null;
				contents.setAlgorithmVersionNumber(ByteConversion.byteToInt(buffer[0]));

				//stream uuid
				contents.setStreamUUID(ByteConversion.getStreamUUID(product.getUUID()));
				
				//product sequence number
				contents.setProductSequenceNumber(ByteConversion.getProductSequenceNumber(product.getUUID()));
				
				//group name length
				if (!readFull(Constants.GROUP_NAME_LENGTH_SIZE))
					return null;
				short groupNameLength = ByteConversion.bytesToShort(buffer, 0);
				System.out.println("groupNameLength: " + groupNameLength);
				
				//group name
				if (!readFull(groupNameLength))
					return null;
				contents.setGroupName(new String(buffer, 0, groupNameLength));
				
				//group key name length
				if (!readFull(Constants.GROUP_KEY_NAME_LENGTH_SIZE))
					return null;
				short groupKeyNameLength = ByteConversion.bytesToShort(buffer, 0);
				System.out.println("Read group key name length of: " + groupKeyNameLength);
				
				//group key name
				if (!readFull(groupKeyNameLength))
					return null;
				contents.setGroupKeyName(new String(buffer, 0, groupKeyNameLength));
				System.out.println("Read group key name of: " + new String(buffer, 0, groupKeyNameLength));
				
				//secure products secure the stream now
				if (product.getProductMode().equals(ProductMode.SECURE))
				{
					product.secureStream();
				}
				
				System.out.println(contents);
				
				return contents;
			}
			else // don't parse, skip when possible
			{
				//if a product used the uuid when the loader first set it, it will have
				//retrieved it upon reset
				//TODO this won't work, combine reading and skipping sections, include stream securing
				
				//product version
				if (!skipFull(1))
					return null;
				
				//algorithm name length
				if (!readFull(Constants.ALGORITHM_NAME_LENGTH_SIZE))
					return null;
				short groupAlgorithmLength = ByteConversion.bytesToShort(buffer, 0);
				
				//algorithm name
				if (!skipFull(groupAlgorithmLength))
					return null;
				
				//algorithm version
				if (!skipFull(1))
					return null;
				
				//product uuid:
				//stream uuid
				if (!skipFull(Constants.STREAM_UUID_SIZE))
					return null;
				
				//product sequence number
				if (!skipFull(Constants.PRODUCT_SEQUENCE_NUMBER_SIZE))
					return null;
				
				//group name length
				if (!readFull(Constants.GROUP_NAME_LENGTH_SIZE))
					return null;
				short groupNameLength = ByteConversion.bytesToShort(buffer, 0);
				
				//group name
				if (!skipFull(groupNameLength))
					return null;
				
				//group key name length
				if (!readFull(Constants.GROUP_KEY_NAME_LENGTH_SIZE))
					return null;
				short groupKeyNameLength = ByteConversion.bytesToShort(buffer, 0);
				
				//group key name
				if (!skipFull(groupKeyNameLength))
					return null;
				
				//secure products secure the stream now
				if (product.getProductMode().equals(ProductMode.SECURE))
				{
					product.secureStream();
				}
				
				return null;
			}
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
			
			System.out.println("End code? " + curFragmentNumber + " " + (curFragmentNumber == Constants.END_CODE));
			//if end code, no more files
			if (curFragmentNumber == Constants.END_CODE)
			{
				Logger.log(LogLevel.k_debug, "The end code was reached.");
				return null;
			}
			
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
			
			//metadata update flag
			if (!readFull(Constants.METADATA_UPDATE_FLAG_SIZE))
				return null;
			boolean metadataUpdate = ByteConversion.byteToInt(buffer[0]) != 0;
			if (parseData)
			{
				contents.getMetadata().setMetadataUpdate(metadataUpdate);
			}
			
			if (metadataUpdate)
			{
				//previous first product uuid
				if (parseData)
				{
					if (!readFull(Constants.PRODUCT_UUID_SIZE))
						return null;
					contents.getMetadata().setPreviousProductUUID(
							ByteConversion.subArray(buffer, 0, Constants.PRODUCT_UUID_SIZE));
				}
				else
				{
					if (!skipFull(Constants.PRODUCT_UUID_SIZE))
						return null;
				}
			}
			else
			{
				//file length remaining
				if (!readFull(Constants.FILE_LENGTH_REMAINING_SIZE))
					return null;
				contents.setRemainingData(ByteConversion.bytesToLong(buffer, 0));
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
	
	private File readNextFileData(boolean saveData, FileContents fileContents)
	{
		long fileLengthRemaining = fileContents.getRemainingData();
		
		if (saveData)
		{	
			//create part file
			File partFile = getPartFileName(fileContents.getMetadata().getFile(), curFragmentNumber);
			if (!partFile.getParentFile().exists())
				partFile.getParentFile().mkdir();
			
			BufferedOutputStream bos = null;
			try
			{
				bos = new BufferedOutputStream(new FileOutputStream(partFile));
				while (fileLengthRemaining > 0)
				{
					//read from product
					int dataLength = (int) Math.min(buffer.length, fileLengthRemaining);
					int bytesRead = product.read(buffer, 0, dataLength);
					
					if (bytesRead == 0)
					{
						//no more data can be read from the product
						break;
					}
					
					fileLengthRemaining -= bytesRead;
					
					//write out to part file
					bos.write(buffer, 0, bytesRead);
				}
				
				bos.close();
				Logger.log(LogLevel.k_info, "Extracted part file: " + partFile.getAbsolutePath());
				return partFile;
			}
			catch (Exception e)
			{
				Logger.log(LogLevel.k_error, "Failed to read file data.");
				Logger.log(LogLevel.k_error, e, false);
				
				//try to close the stream
				if (bos != null)
				{
					try {
						bos.close();
					} catch (IOException e1) {
						Logger.log(LogLevel.k_error, "The output stream cannot be closed.");
						Logger.log(LogLevel.k_error, e1, false);
					}
				}
				
				//try to remove the failed part file
				if (partFile.exists())
				{
					Path partFilePath = partFile.getAbsoluteFile().toPath();
					if (partFile.isDirectory())
					{
						Logger.log(LogLevel.k_debug, "The partfile is a directory, something is wrong: " + partFilePath.toString());
					}
					else
					{
						try {
							Files.delete(partFilePath);
						} catch (IOException e1) {
							Logger.log(LogLevel.k_error, "The failed part file cannot be deleted: " + partFilePath.toString());
							Logger.log(LogLevel.k_error, e1, false);
						}
					}
				}
				return null;
			}
		}
		else
		{
			//This might try to over-read because fileLengthRemaining could be more
			//than what's left in the product if the file continues on in the next
			//product. If it does, nothing bad should happen.
			skipFull(fileLengthRemaining);
	
			//returning null regardless
			return null;
		}
	}
	

	private File getPartFileName(File origFile, long fragmentNumber)
	{
		byte[] pathHash = Hashing.hash(origFile.getAbsolutePath().getBytes());
		String fileID = Integer.toString(Math.abs(ByteConversion.bytesToInt(pathHash, 0)));
		String path = extractionFolder.getAbsolutePath() + "/" + fileID + "/" + fileID +
				"_" + fragmentNumber + ".part";
		
		return new File(path);
	}

	/**
	 * @return the extractionFolder
	 */
	public File getExtractionFolder()
	{
		return extractionFolder;
	}

	/**
	 * @param extractionFolder the extractionFolder to set
	 */
	public void setExtractionFolder(File extractionFolder)
	{
		this.extractionFolder = extractionFolder;
	}
}
