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
import config.Configuration;

public class ProductReader {

	//delete factory member? can the factory ever become null?
	//do we ever need another product?
	//private final ProductFactory<? extends Product> factory;
	
	private Product product;
	private boolean productEmpty;
	private long curPartLengthRemaining;
	private byte[] curFileHash;
	private long curFragmentNumber;
	
	public ProductReader(ProductFactory<? extends Product> factory)
	{
		//this.factory = factory;
		product = factory.create();
		productEmpty = false;
	}
	
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
			while (!productEmpty)
			{
				
				FileContents fileContents = readNextFileHeader(true);
				if (fileContents != null)
				{
					readNextFileData(false);
					
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
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
		}
		
		return contents;
	}
	
	public ProductContents extractAll(File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		if (!loadProduct(productFile))
			return null;
		
		ProductContents contents = null;
		
		try
		{
			contents = readProductHeader(true);
			
			//secure products will secure data beyond this point
			if (product.getProductMode().equals(ProductMode.SECURE))
				product.secureStream();
			
			while (!productEmpty)
			{
				
				FileContents fileContents = readNextFileHeader(true);
				if (fileContents != null)
				{
					File extracted = readNextFileData(true);
					if (extracted != null)
					{
						fileContents.setExtractedFile(extracted);
					}
					else
					{
						Logger.log(LogLevel.k_error, "Failed to extract file: " + fileContents.getMetadata().getPath());
					}
					
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
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
		}
		
		return contents;
	}
	
	private boolean loadProduct(File productFile)
	{
		try {
			product.loadFile(productFile);
		} catch (IOException e) {
			Logger.log(LogLevel.k_error, "Failed to load product file " + productFile.getName());
			Logger.log(LogLevel.k_error, e, false);
			return false;
		}
		
		return true;
	}

	public FileContents extractFile(byte[] hash, File productFile)
	{
		assert(productFile.exists() && !productFile.isDirectory());
		
		if (!loadProduct(productFile))
			return null;
		
		try
		{
			readProductHeader(false);
			
			//secure products will secure data beyond this point
			if (product.getProductMode().equals(ProductMode.SECURE))
				product.secureStream();
			
			while (!productEmpty)
			{
				FileContents fileContents = readNextFileHeader(true);
				if (fileContents != null)
				{
					if (ByteConversion.bytesEqual(fileContents.getMetadata().getFileHash(), hash))
					{
						File extracted = readNextFileData(true);
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
			Logger.log(LogLevel.k_error, "Failed to extract from " + productFile.getName());
		}
		
		return null;
	}
	
	private ProductContents readProductHeader(boolean parseData)
	{
		byte[] buffer;

		try
		{
			//in this case, the code for reading and skipping is substantially different
			if (parseData)
			{
				ProductContents contents = new ProductContents();

				//if a product used the uuid when the loader first set it, it will have
				//retrieved it upon reset
				
				//product version
				contents.setProductVersionNumber(ByteConversion.byteToInt(product.read()));
				
				//algorithm name length
				buffer = new byte[Constants.ALGORITHM_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupAlgorithmLength = ByteConversion.bytesToShort(buffer);
				
				//algorithm name
				buffer = new byte[groupAlgorithmLength];
				product.read(buffer);
				contents.setAlgorithmName(new String(buffer));
				
				//algorithm version
				contents.setAlgorithmVersionNumber(ByteConversion.byteToInt(product.read()));
				
				//product uuid:
				//stream uuid
				buffer = new byte[Constants.STREAM_UUID_SIZE];
				product.read(buffer);
				contents.setStreamUUID(ByteConversion.bytesToLong(buffer));
				
				//product sequence number
				buffer = new byte[Constants.PRODUCT_SEQUENCE_NUMBER_SIZE];
				product.read(buffer);
				contents.setProductSequenceNumber(ByteConversion.bytesToInt(buffer));
				
				//group name length
				buffer = new byte[Constants.GROUP_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupNameLength = ByteConversion.bytesToShort(buffer);
				
				//group name
				buffer = new byte[groupNameLength];
				product.read(buffer);
				contents.setGroupName(new String(buffer));
				
				//group key name length
				buffer = new byte[Constants.GROUP_KEY_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupKeyNameLength = ByteConversion.bytesToShort(buffer);
				
				//group key name
				buffer = new byte[groupKeyNameLength];
				product.read(buffer);
				contents.setGroupKeyName(new String(buffer));
				
				return contents;
			}
			else // don't parse, skip when possible
			{
				//if a product used the uuid when the loader first set it, it will have
				//retrieved it upon reset
				
				//product version
				product.skip(1);
				
				//algorithm name length
				buffer = new byte[Constants.ALGORITHM_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupAlgorithmLength = ByteConversion.bytesToShort(buffer);
				
				//algorithm name
				product.skip(groupAlgorithmLength);
				
				//algorithm version
				product.skip(1);
				
				//product uuid:
				//stream uuid
				product.skip(Constants.STREAM_UUID_SIZE);
				
				//product sequence number
				product.skip(Constants.PRODUCT_SEQUENCE_NUMBER_SIZE);
				
				//group name length
				buffer = new byte[Constants.GROUP_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupNameLength = ByteConversion.bytesToShort(buffer);
				
				//group name
				product.skip(groupNameLength);
				
				//group key name length
				buffer = new byte[Constants.GROUP_KEY_NAME_LENGTH_SIZE];
				product.read(buffer);
				short groupKeyNameLength = ByteConversion.bytesToShort(buffer);
				
				//group key name
				product.skip(groupKeyNameLength);
				
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
		
		byte[] buffer;
		FileContents contents = null;
		
		if (parseData)
		{
			contents = new FileContents();
		}
		
		try
		{
			//fragment number
			buffer = new byte[Constants.FRAGMENT_NUMBER_SIZE];
			product.read(buffer);
			curFragmentNumber = ByteConversion.bytesToLong(buffer);
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
			
			//file hash
			if (parseData)
			{
				buffer = new byte[Constants.FILE_HASH_SIZE];
				product.read(buffer);
				curFileHash = buffer;
				contents.getMetadata().setFileHash(curFileHash);
			}
			else
			{
				product.skip(Constants.FILE_HASH_SIZE);
			}
			
			//file name length
			buffer = new byte[Constants.FILE_NAME_LENGTH_SIZE];
			short fileNameLength = ByteConversion.bytesToShort(buffer);
			
			//file name
			if (parseData)
			{
				buffer = new byte[fileNameLength];
				product.read(buffer);
				contents.getMetadata().setFile(new File(new String(buffer)));
			}
			else
			{
				product.skip(fileNameLength);
			}
			
			//date created
			if (parseData)
			{
				buffer = new byte[Constants.DATE_CREATED_SIZE];
				product.read(buffer);
				contents.getMetadata().setDateCreated(ByteConversion.bytesToLong(buffer));
			}
			else
			{
				product.skip(Constants.DATE_CREATED_SIZE);
			}
			
			//date modified
			if (parseData)
			{
				buffer = new byte[Constants.DATE_MODIFIED_SIZE];
				product.read(buffer);
				contents.getMetadata().setDateModified(ByteConversion.bytesToLong(buffer));
			}
			else
			{
				product.skip(Constants.DATE_MODIFIED_SIZE);
			}
			
			//permissions byte
			if (parseData)
			{
				int permissions = ByteConversion.byteToInt(product.read());
				contents.getMetadata().setPermissions(permissions);
			}
			else
			{
				product.skip(Constants.PERMISSIONS_SIZE);
			}
			
			//metadata update flag
			boolean metadataUpdate = product.read() != 0;
			if (parseData)
			{
				contents.getMetadata().setMetadataUpdate(metadataUpdate);
			}
			
			if (metadataUpdate)
			{
				//previous first product uuid
				if (parseData)
				{
					buffer = new byte[Constants.PRODUCT_UUID_SIZE];
					product.read(buffer);
					contents.getMetadata().setPreviousProductUUID(buffer);
				}
				else
				{
					product.skip(Constants.PRODUCT_UUID_SIZE);
				}
			}
			else
			{
				//file length remaining
				buffer = new byte[Constants.FILE_LENGTH_REMAINING_SIZE];
				product.read(buffer);
				curPartLengthRemaining = ByteConversion.bytesToLong(buffer);
				
				if (parseData)
				{
					contents.setRemainingData(curPartLengthRemaining);
				}
			}
			
			return contents;

		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "Failed to read file header.");
			Logger.log(LogLevel.k_error, e, false);
			return null;
		}
	}
	
	private File readNextFileData(boolean saveData)
	{
		if (saveData)
		{		
			byte[] buffer = new byte[(int) Math.min(Constants.MAX_READ_BUFFER_SIZE, curPartLengthRemaining)];
			File partFile = getPartFileName(curFileHash, curFragmentNumber);
			BufferedOutputStream bos = null;
			
			try
			{
				bos = new BufferedOutputStream(new FileOutputStream(partFile));
				while (curPartLengthRemaining > 0)
				{
					product.read(buffer);
					bos.write(buffer);
					curPartLengthRemaining -= buffer.length;
					
					if (curPartLengthRemaining < buffer.length)
					{
						buffer = new byte[(int) Math.min(Constants.MAX_READ_BUFFER_SIZE, curPartLengthRemaining)];
					}
				}
				bos.close();
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
			try
			{
				product.skip(curPartLengthRemaining);
			}
			catch (Exception e)
			{
				Logger.log(LogLevel.k_error, "Failed to skip past file data.");
				Logger.log(LogLevel.k_error, e, false);
			}
			return null;
		}
	}
	

	private File getPartFileName(byte[] fileHash, long fragmentNumber)
	{
		String path = Configuration.getExtractionFolder().getAbsolutePath() + "\\" +
				ByteConversion.bytesToHexString(fileHash) +  "_" + fragmentNumber + ".part";
		
		return new File(path);
	}
}
