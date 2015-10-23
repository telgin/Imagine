package product;

import hibernate.Metadata;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import logging.LogLevel;
import logging.Logger;
import stats.ProgressMonitor;
import stats.Stat;
import config.Configuration;
import data.TrackingGroup;
import database.Database;
import database.DatabaseManager;
import util.ByteConversion;
import util.Constants;

public class ProductLoader
{
	//delete factory member? can the factory ever become null?
	//do we ever need another product?
	//private final ProductFactory<? extends Product> factory;
	
	private final byte PRODUCT_VERSION_NUMBER = ByteConversion.intToByte(0);
	
	private byte[] streamUUID;
	private int sequenceNumber;
	private TrackingGroup group;
	private File productStagingFolder;
	
	private Product currentProduct;
	private byte[] currentUUID;
	
	private boolean fileWritten = false;
	private boolean writingFile = false;
	
	public ProductLoader(ProductFactory<? extends Product> factory, TrackingGroup group)
	{
		//this.factory = factory;
		
		streamUUID = ByteConversion.longToBytes(Clock.getUniqueTime());
		sequenceNumber = 0;
		
		this.group = group;
		
		productStagingFolder = group.getProductStagingFolder();
		if (productStagingFolder == null)
			productStagingFolder = Configuration.getProductStagingFolder();
		
		currentProduct = factory.create();
		resetToNextProduct();
	}
	
	public void shutdown()
	{
		while (writingFile)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
		if (fileWritten)
		{
			if (currentProduct.getRemainingBytes() > Constants.END_CODE_SIZE)
				currentProduct.write(ByteConversion.longToBytes(Constants.END_CODE));

			currentProduct.saveFile(productStagingFolder, getSaveName());
		}
	}
	
	private void resetToNextProduct()
	{
		currentProduct.newProduct();
		
		//set the uuid in case it is used internally by the product
		currentUUID = ByteConversion.concat(streamUUID, ByteConversion.intToBytes(sequenceNumber++));
		currentProduct.setUUID(currentUUID);
		
		//stealth products will encrypt data beyond this point
		if (currentProduct.getProductMode().equals(ProductMode.STEALTH))
			currentProduct.secureStream();
		
		//write the product header
		writeProductHeader();
		
		//secure products will secure data beyond this point
		if (currentProduct.getProductMode().equals(ProductMode.SECURE))
			currentProduct.secureStream();

	}
	
	private void writeProductHeader()
	{
		//write version number
		currentProduct.write(PRODUCT_VERSION_NUMBER);
		
		//write algorithm name length
		byte[] algorithmName = currentProduct.getAlgorithmName().getBytes();
		currentProduct.write(ByteConversion.shortToBytes((short)algorithmName.length));
		
		//write algorithm name
		currentProduct.write(algorithmName);
		
		//write algorithm version
		currentProduct.write(ByteConversion.intToByte(currentProduct.getAlgorithmVersionNumber()));
		
		//write product uuid
		currentProduct.write(currentUUID);
		
		//write group name len
		String groupName = group.getName();
		currentProduct.write(ByteConversion.shortToBytes((short)groupName.getBytes().length));
		
		//write group name
		currentProduct.write(groupName.getBytes());		
		
		//write group key name / length
		if (group.getKey().isSecure())
		{
			byte[] groupKeyName = group.getKey().getName().getBytes();
			currentProduct.write(ByteConversion.shortToBytes((short)groupKeyName.length));
			currentProduct.write(groupKeyName);
		}
		else
		{
			currentProduct.write(ByteConversion.shortToBytes((short)0));
		}
	}
	
	private String getSaveName()
	{
		return 
			new String(group.getName()) + 
			"_" + 
			Long.toString(ByteConversion.bytesToLong(streamUUID)) +
			"_" +
			(sequenceNumber-1);
	}
	
	public void writeFile(Metadata fileMetadata) throws IOException
	{
		Logger.log(LogLevel.k_info, "Loading file: " + fileMetadata.getFile().getPath() + " into product " + getSaveName());
		writingFile = true;
		
		
		long fileLengthRemaining = fileMetadata.isMetadataUpdate() ?
										Constants.PRODUCT_UUID_SIZE :
										fileMetadata.getFile().length();
		long fragmentNumber = 1;
		int fileHeaderSize = fileMetadata.getTotalLength() + 
				Constants.FILE_NAME_LENGTH_SIZE +
				Constants.DATE_CREATED_SIZE +
				Constants.DATE_MODIFIED_SIZE + 
				Constants.FILE_LENGTH_REMAINING_SIZE;
		
		//save off the first product uuid where we're saving the file
		//it might actually start in the next one if we're out of space in this one.
		//easy enough to figure out later.
		if (group.isUsingDatabase())
		{
			if (fileMetadata.isMetadataUpdate())
			{
				fileMetadata.setPreviousProductUUID(fileMetadata.getProductUUID());
				fileMetadata.setProductUUID(currentUUID);
			}
			else
			{
				fileMetadata.setProductUUID(currentUUID);
				DatabaseManager.saveProductUUID(fileMetadata);
			}
			
		}
		
		//start a reader
		DataInputStream reader = new DataInputStream(new FileInputStream(fileMetadata.getFile()));
		
		//write the file to one or multiple products
		do
		{
			//check that there is enough space left for the metadata and some data
			if (fileHeaderSize + 1 > currentProduct.getRemainingBytes())
			{
				//there wasn't enough space to add more bytes of the file, so
				//write the end code instead of the next fragment number, and reset
				
				//check to see if there is enough space for the end code
				if (Constants.END_CODE_SIZE <= currentProduct.getRemainingBytes())
				{
					currentProduct.write(ByteConversion.longToBytes(Constants.END_CODE));
				}// end state is assumed if there's no space to write the end code
				
				currentProduct.saveFile(productStagingFolder, getSaveName());
				resetToNextProduct();
				
				//the new product should now have enough space
				assert(currentProduct.getRemainingBytes() >= fileHeaderSize + 1);
			}
			
			//(write file header for each fragment)
			writeFileHeader(fileMetadata, fragmentNumber++, fileLengthRemaining);
			
			//Does the metadata specify a new file or a metadata update for
			//an existing file? This should never be true if the group does
			//not use a database.
			if (fileMetadata.isMetadataUpdate())
			{
				//Since the new metadata was written in the file header,
				//just write the previous first product uuid as the file data.
				//This will act as a sort of pointer to the location of the
				//actual file data.
				currentProduct.write(fileMetadata.getProductUUID());
				fileLengthRemaining -= Constants.PRODUCT_UUID_SIZE;
				assert (fileLengthRemaining == 0);
			}
			else
			{
				//write as much as you can, if the product fills up, we get
				//back to here and start again where we left off
				fileLengthRemaining = writeFileData(reader, fileLengthRemaining);
			}
		}
		while (fileLengthRemaining > 0);
		
		reader.close();
		
		//update the database
		if (group.isUsingDatabase())
		{
			Database.saveMetadata(fileMetadata, group);
		}
		
		writingFile = false;
		fileWritten = true;
		
		//update progress
		Stat stat = ProgressMonitor.getStat("filesProcessed");
		if (stat != null)
			stat.incrementNumericProgress(1);
	}
	
	private void writeFileHeader(Metadata fileMetadata, long fragmentNumber, long fileLengthRemaining)
	{
		//fragment number
		currentProduct.write(ByteConversion.longToBytes(fragmentNumber));
		
		//file hash
		currentProduct.write(fileMetadata.getFileHash());
		
		//file name length
		currentProduct.write(ByteConversion.shortToBytes((short)fileMetadata.getPath().length()));
		
		//file name
		currentProduct.write(fileMetadata.getPath().getBytes());
		
		//date created
		currentProduct.write(ByteConversion.longToBytes(fileMetadata.getDateCreated()));
		
		//date modified
		currentProduct.write(ByteConversion.longToBytes(fileMetadata.getDateModified()));
		
		//permissions
		currentProduct.write(ByteConversion.shortToBytes(fileMetadata.getPermissions()));
		
		//metadata update flag
		currentProduct.write(ByteConversion.booleanToByte(fileMetadata.isMetadataUpdate()));
		
		if (fileMetadata.isMetadataUpdate())
		{
			//previous fragment1 product uuid
			currentProduct.write(fileMetadata.getPreviousProductUUID());
		}
		else
		{
			//length of data that still needs to be written
			currentProduct.write(ByteConversion.longToBytes(fileLengthRemaining));
		}
		
		
	}
	
	private long writeFileData(DataInputStream reader, long fileLengthRemaining) throws IOException
	{			
		int bufferSize = (int) (Math.min(Constants.MAX_READ_BUFFER_SIZE, Math.min(currentProduct.getRemainingBytes(), fileLengthRemaining)));
		byte[] buffer = new byte[bufferSize];
		while (fileLengthRemaining > 0 && currentProduct.getRemainingBytes() > 0)
		{
			reader.read(buffer);
			currentProduct.write(buffer);
			fileLengthRemaining -= bufferSize;
			//later probably should add an indexing mechanism so you don't have to create a new array
			
			//create a new buffer only if the new size is different
			int newSize = (int) (Math.min(Constants.MAX_READ_BUFFER_SIZE, Math.min(currentProduct.getRemainingBytes(), fileLengthRemaining)));
			if (newSize != bufferSize)
			{
				bufferSize = newSize;
				buffer = new byte[bufferSize];
			}
		}
		return fileLengthRemaining;
	}
}
