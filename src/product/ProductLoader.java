package product;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import algorithms.ProductIOException;
import logging.LogLevel;
import logging.Logger;
import stats.ProgressMonitor;
import stats.Stat;
import config.Configuration;
import data.Metadata;
import data.TrackingGroup;
import database.Database;
import database.derby.EmbeddedDB;
import database.filesystem.FileSystemDB;
import util.ByteConversion;
import util.Constants;

public class ProductLoader
{
	// delete factory member? can the factory ever become null?
	// do we ever need another product?
	// private final ProductFactory<? extends Product> factory;

	private final byte PRODUCT_VERSION_NUMBER = ByteConversion.intToByte(0);

	private byte[] streamUUID;
	private int sequenceNumber;
	private TrackingGroup group;
	private File productStagingFolder;

	private ProductWriter currentProduct;
	private byte[] currentUUID;
	private byte[] buffer;
	private int dataOffset;
	private int dataLength;

	private boolean fileWritten = false;
	private boolean writingFile = false;
	private boolean needsReset = true;

	public ProductLoader(ProductWriterFactory<? extends ProductWriter> factory,
					TrackingGroup group)
	{
		// this.factory = factory;

		streamUUID = ByteConversion.longToBytes(Clock.getUniqueTime());
		sequenceNumber = 0;

		this.group = group;

		productStagingFolder = group.getProductStagingFolder();
		if (productStagingFolder == null)
			productStagingFolder = Configuration.getProductStagingFolder();

		currentProduct = factory.createWriter();

		buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];

		try
		{
			resetToNextProduct();
		}
		catch (ProductIOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void shutdown()
	{
		System.out.println("Product loader shutting down.");
		while (writingFile)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
			}
		}

		// fileWritten indicates a file is written, but there is as least some
		// space left
		// if there is more space than the size of the end code, the end code is
		// written to indicate no more reading should be done (there is no next
		// file)
		if (fileWritten)
		{
			// if there's not enough space for the end code, the reader logic
			// handles it the same as if it were written
			System.out.print("Writing end code...");
			if (writeFull(ByteConversion.longToBytes(Constants.END_CODE)))
				System.out.println("success.");
			else
				System.out.println("failure.");

			currentProduct.saveFile(productStagingFolder, getSaveName());
		}
	}

	private boolean writeFull(byte[] bytes)
	{
		return currentProduct.write(bytes, 0, bytes.length) == bytes.length;
	}

	private boolean writeFull(byte b)
	{
		return currentProduct.write(b);
	}

	private void resetToNextProduct() throws ProductIOException
	{
		currentProduct.newProduct();

		// no file was written in this product yet
		fileWritten = false;

		// write product uuid
		currentUUID = ByteConversion.concat(streamUUID,
						ByteConversion.intToBytes(sequenceNumber++));

		if (!writeFull(currentUUID))
			throw new ProductIOException("Cannot write product uuid.");

		System.out.println("Was " + ByteConversion.bytesToLong(streamUUID));
		System.out.println("Was " + (sequenceNumber - 1));
		System.out.println("Wrote " + ByteConversion.getStreamUUID(currentUUID));
		System.out.println(
						"Wrote " + ByteConversion.getProductSequenceNumber(currentUUID));

		// set the uuid in case it is used internally by the product
		currentProduct.setUUID(currentUUID);

		// stealth products will encrypt data beyond this point
		if (currentProduct.getProductMode().equals(ProductMode.STEALTH))
			currentProduct.secureStream();

		// write the product header
		if (!writeProductHeader())
			throw new ProductIOException("Cannot write product header.");

		// secure products will secure data beyond this point
		if (currentProduct.getProductMode().equals(ProductMode.SECURE))
			currentProduct.secureStream();
	}

	private boolean writeProductHeader() throws ProductIOException
	{
		// write version number
		if (!writeFull(PRODUCT_VERSION_NUMBER))
			return false;

		// write algorithm name length
		byte[] algorithmName = currentProduct.getAlgorithmName().getBytes();
		if (!writeFull(ByteConversion.shortToBytes((short) algorithmName.length)))
			return false;

		// write algorithm name
		if (!writeFull(algorithmName))
			return false;

		// write algorithm version
		if (!writeFull(ByteConversion
						.intToByte(currentProduct.getAlgorithmVersionNumber())))
			return false;

		// write group name len
		String groupName = group.getName();
		if (!writeFull(ByteConversion.shortToBytes((short) groupName.getBytes().length)))
			return false;

		// write group name
		if (!writeFull(groupName.getBytes()))
			return false;

		// write group key name / length
		if (group.getKey().isSecure())
		{
			System.out.println("Writing key name: " + group.getKey().getName()
							+ " of length: "
							+ group.getKey().getName().getBytes().length);
			byte[] groupKeyName = group.getKey().getName().getBytes();
			if (!writeFull(ByteConversion.shortToBytes((short) groupKeyName.length)))
				return false;
			if (!writeFull(groupKeyName))
				return false;
		}
		else
		{
			System.out.println("Not writing key name because the product is not secure.");
			if (!writeFull(ByteConversion.shortToBytes((short) 0)))
				return false;
		}

		return true;
	}

	private String getSaveName()
	{
		return new String(group.getName()) + "_"
						+ Long.toString(ByteConversion.bytesToLong(streamUUID)) + "_"
						+ (sequenceNumber - 1);
	}

	public void writeFile(Metadata fileMetadata) throws IOException
	{
		// if (needsReset)
		// {
		// resetToNextProduct();
		// needsReset = false;
		// }
		//
		dataLength = 0;
		dataOffset = buffer.length;

		Logger.log(LogLevel.k_info, "Loading file: " + fileMetadata.getFile().getPath()
						+ " into product " + getSaveName());
		writingFile = true;

		long fileLengthRemaining = fileMetadata.isMetadataUpdate() ? 0
						: fileMetadata.getFile().length();
		long fragmentNumber = 1;
		// int fileHeaderSize = fileMetadata.getTotalLength();

		// save off the first product uuid where we're saving the file
		// it might actually start in the next one if we're out of space in this
		// one.
		// easy enough to figure out later.
		if (group.isUsingDatabase())
		{
			if (fileMetadata.isMetadataUpdate())
			{
				//TODO decide if this makes sense or not... it doesn't work currently
				//fileMetadata.setPreviousProductUUID(fileMetadata.getProductUUID());
				fileMetadata.setProductUUID(currentUUID);
			}
			else
			{
				fileMetadata.setProductUUID(currentUUID);
				Database.saveProductUUID(fileMetadata, group);
			}

		}

		// start a reader
		DataInputStream reader =
						new DataInputStream(new FileInputStream(fileMetadata.getFile()));

		// write the file to one or multiple products
		do
		{
			// write file header size
			// writeFileHeaderSize(fileHeaderSize);

			// write file header
			if (!writeFileHeader(fileMetadata, fragmentNumber, fileLengthRemaining))
			{
				// there wasn't enough space, reset
				currentProduct.saveFile(productStagingFolder, getSaveName());
				resetToNextProduct();

				// writeFileHeaderSize(fileHeaderSize);

				// try again
				if (!writeFileHeader(fileMetadata, fragmentNumber, fileLengthRemaining))
				{
					// second failure indicates product is too small
					throw new ProductIOException(
									"Cannot write file header, product is too small");
				}
			}

			// write data if there is any
			if (!fileMetadata.isMetadataUpdate())
			{
				// write as much as possible, if the product fills up, we get
				// back to here and start again where we left off
				fileLengthRemaining = writeFileData(reader, fileLengthRemaining);
			}

			// update fragment number
			++fragmentNumber;

		}
		while (fileLengthRemaining > 0);

		reader.close();

		// update the database
		if (group.isUsingDatabase())
		{
			Database.saveMetadata(fileMetadata, group);
		}

		writingFile = false;
		fileWritten = true;

		// update progress
		Stat stat = ProgressMonitor.getStat("filesProcessed");
		if (stat != null)
			stat.incrementNumericProgress(1);
	}

	private void writeFileHeaderSize(int fileHeaderSize) throws ProductIOException
	{
		if (!writeFull(ByteConversion.intToBytes(fileHeaderSize)))
		{
			// there wasn't enough space, reset
			currentProduct.saveFile(productStagingFolder, getSaveName());
			resetToNextProduct();

			// try again
			if (!writeFull(ByteConversion.intToBytes(fileHeaderSize)))
			{
				// a second failure after reset means that the product size is
				// too small
				throw new ProductIOException(
								"Cannot write file header, product size is too small.");
			}
		}
	}

	private boolean writeFileHeader(Metadata fileMetadata, long fragmentNumber,
					long fileLengthRemaining)
	{
		System.out.println("Fragment number: " + fragmentNumber);
		// fragment number
		if (!writeFull(ByteConversion.longToBytes(fragmentNumber)))
			return false;

		// file hash
		if (!writeFull(fileMetadata.getFileHash()))
			return false;

		// file name length
		if (!writeFull(ByteConversion
						.shortToBytes((short) fileMetadata.getPath().length())))
			return false;

		// file name
		if (!writeFull(fileMetadata.getPath().getBytes()))
			return false;

		// date created
		if (!writeFull(ByteConversion.longToBytes(fileMetadata.getDateCreated())))
			return false;

		// date modified
		if (!writeFull(ByteConversion.longToBytes(fileMetadata.getDateModified())))
			return false;

		// permissions
		if (!writeFull(ByteConversion.shortToBytes(fileMetadata.getPermissions())))
			return false;

		// metadata update flag
		if (!writeFull(ByteConversion.booleanToByte(fileMetadata.isMetadataUpdate())))
			return false;

		// Does the metadata specify a new file or a metadata update for
		// an existing file? This should never be true if the group does
		// not use a database.
		if (fileMetadata.isMetadataUpdate())
		{
			// Since the new metadata was written in the file header,
			// just write the previous first product uuid as the file data.
			// This will act as a sort of pointer to the location of the
			// actual file data.
			if (!writeFull(fileMetadata.getPreviousProductUUID()))
				return false;
		}
		else
		{
			// length of data that still needs to be written
			if (!writeFull(ByteConversion.longToBytes(fileLengthRemaining)))
				return false;
		}

		return true;
	}

	private long writeFileData(DataInputStream reader, long fileLengthRemaining)
					throws IOException
	{

		do
		{
			System.out.println("Bytes requested to be written: " + (dataLength));
			int bytesWritten = currentProduct.write(buffer, dataOffset, dataLength);
			System.out.println("Buffer length: " + buffer.length);
			System.out.println("bytes written: " + bytesWritten);
			dataOffset += bytesWritten;
			dataLength -= bytesWritten;
			fileLengthRemaining -= bytesWritten;

			System.out.println("File length remaining: " + fileLengthRemaining);

			if (dataOffset == buffer.length)
			{
				// the full thing was written, get more
				dataOffset = 0;
				dataLength = reader.read(buffer, dataOffset, buffer.length);
			}
			else
			{
				// the product is full, some portion of the data was not written
				return fileLengthRemaining;
			}

		}
		while (dataLength > 0);

		assert(fileLengthRemaining == 0);

		return fileLengthRemaining;
	}
}
