package archive;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import config.Constants;
import config.Settings;
import data.FileType;
import data.Metadata;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;
import report.Report;
import util.ByteConversion;
import util.Clock;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The archive loader handles writing files. This handles writing
 * file data and metadata into archives using the high level protocol 
 * used by all archives.
 */
public class ArchiveLoader
{
	private static final byte ARCHIVE_VERSION_NUMBER = ByteConversion.intToByte(0);

	private byte[] f_streamUUID;
	private byte[] f_currentUUID;
	private byte[] f_buffer;
	
	private int f_sequenceNumber;
	private int f_dataOffset;
	private int f_dataLength;
	
	private boolean f_fileWritten;
	private boolean f_needsReset;
	
	private FileOutputManager f_fileOutputManager;

	private ArchiveWriter f_currentArchive;

	/**
	 * Creates an archive loader
	 * @param p_factory The archive writer factory
	 * @param p_manager The file output manager which tells where to save archive files
	 */
	public ArchiveLoader(ArchiveWriterFactory<? extends ArchiveWriter> p_factory,
				FileOutputManager p_manager)
	{
		f_fileWritten = false;
		f_needsReset = true;
		f_streamUUID = ByteConversion.longToBytes(Clock.getUniqueTime());
		f_sequenceNumber = 0;

		f_fileOutputManager = p_manager;
		f_currentArchive = p_factory.createWriter();

		f_buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
	}

	/**
	 * Shuts the loader down. It will save the current archive file if
	 * files were written. This function should not be called unless all
	 * write calls have returned.
	 */
	public void shutdown()
	{
		Logger.log(LogLevel.k_debug, "Archive loader shutting down.");

		// fileWritten indicates a file is written, but there is as least some space left
		// if there is more space than the size of the end code, the end code is
		// written to indicate no more reading should be done (there is no next file)
		if (f_fileWritten)
		{
			// if there's not enough space for the end code, the reader logic
			// handles it the same as if it were written
			if (writeFull(ByteConversion.longToBytes(Constants.END_CODE)))
				Logger.log(LogLevel.k_debug, "End code written successfully.");
			else
				Logger.log(LogLevel.k_debug, "Failed to write end code.");

			f_currentArchive.saveFile(f_fileOutputManager.getOutputFolder(), getSaveName());
		}
		
		Logger.log(LogLevel.k_debug, "Archive loader is shut down.");
	}

	/**
	 * Writes the full contents of the array to the archive
	 * @param p_bytes The array to write
	 * @return If all bytes were written
	 */
	private boolean writeFull(byte[] p_bytes)
	{
		return f_currentArchive.write(p_bytes, 0, p_bytes.length) == p_bytes.length;
	}

	/**
	 * Writes the byte to the archive
	 * @param p_byte The byte to write
	 * @return If the byte was written successfully
	 */
	private boolean writeFull(byte p_byte)
	{
		return f_currentArchive.write(p_byte);
	}

	/**
	 * Resets the loader to the next archive, resetting the uuid and
	 * writing the archive header.
	 * @throws ArchiveIOException If data could not be written or a new archive
	 * could not be created
	 */
	private void resetToNextArchive() throws ArchiveIOException
	{
		f_currentArchive.newArchive();

		// no file was written in this archive yet
		f_fileWritten = false;

		// write archive uuid
		f_currentUUID = ByteConversion.concat(f_streamUUID,
						ByteConversion.intToBytes(f_sequenceNumber++));

		if (!writeFull(f_currentUUID))
			throw new ArchiveIOException("Cannot write archive uuid.");

		// set the uuid in case it is used internally by the archive
		f_currentArchive.setUUID(f_currentUUID);

		//secure stream
		f_currentArchive.secureStream();

		// write the archive header
		writeArchiveHeader();
		
		f_needsReset = false;
	}

	/**
	 * Writes the archive header
	 * @return True if the archive header was written, false otherwise
	 * @throws ArchiveIOException If the archive header cannot be written.
	 */
	private void writeArchiveHeader() throws ArchiveIOException
	{
		// write version number
		if (!writeFull(ARCHIVE_VERSION_NUMBER))
			throw new ArchiveIOException("Cannot write archive header.");
	}

	/**
	 * Gets the name to save this archive under
	 * @return The file name, minus the extension which is determined by the algorithm
	 */
	private String getSaveName()
	{
		return FileSystemUtil.getArchiveName(ByteConversion.bytesToLong(f_streamUUID),
						(f_sequenceNumber - 1));

	}

	/**
	 * Writes a file to the archive
	 * @param p_fileMetadata The metadata object for the file to write
	 * @throws IOException If the file could not be written
	 */
	public void writeFile(Metadata p_fileMetadata) throws IOException
	{
		//update file status to indicate we're going to write the file
		if (Settings.trackFileStatus())
			JobStatus.setCreationJobFileStatus(p_fileMetadata.getFile(), CreationJobFileState.WRITING);
		
		//resetting causes the archive header to be written
		//don't reset unless this loader actually writes a file
		//needs reset should only be true after initialization
		if (f_needsReset)
			resetToNextArchive();
		
		f_dataLength = 0;
		f_dataOffset = f_buffer.length;

		Logger.log(LogLevel.k_info, "Loading file: " + p_fileMetadata.getFile().getPath()
						+ " into archive " + getSaveName());
		

		//configure based on file type
		long fileLengthRemaining;
		DataInputStream reader;
		if (p_fileMetadata.getType().equals(FileType.k_file))//k_file
		{
			fileLengthRemaining = p_fileMetadata.getFile().length();
			reader = new DataInputStream(new FileInputStream(p_fileMetadata.getFile()));
		}
		else //k_folder
		{
			fileLengthRemaining = 0;
			reader = null;
		}
		
		long fragmentNumber = Constants.FIRST_FRAGMENT_CODE;

		// save off the first archive uuid where we're saving the file
		// it might actually start in the next one if we're out of space in this
		// one.
		// easy enough to figure out later.
		p_fileMetadata.setArchiveUUID(f_currentUUID);


		// write the file to one or multiple archives
		do
		{
			// write file header
			if (!writeFileHeader(p_fileMetadata, fragmentNumber, fileLengthRemaining))
			{
				// there wasn't enough space, reset
				f_currentArchive.saveFile(f_fileOutputManager.getOutputFolder(), getSaveName());
				resetToNextArchive();

				// writeFileHeaderSize(fileHeaderSize);

				// try again
				if (!writeFileHeader(p_fileMetadata, fragmentNumber, fileLengthRemaining))
				{
					// second failure indicates archive is too small
					throw new ArchiveIOException(
									"Cannot write file header, archive is too small");
				}
			}

			// write data if there is any
			if (fileLengthRemaining > 0)
			{
				// write as much as possible, if the archive fills up, we get
				// back to here and start again where we left off
				fileLengthRemaining = writeFileData(reader, fileLengthRemaining);
			}

			// update fragment number
			++fragmentNumber;
			
			//update file length remaining
			JobStatus.setBytesLeft(p_fileMetadata.getFile(), fileLengthRemaining);

		}
		while (fileLengthRemaining > 0);

		if (reader != null)
			reader.close();

		// update the database
		p_fileMetadata.setFragmentCount(fragmentNumber-1);
		if (Settings.generateReport())
			Report.saveCreationRecord(p_fileMetadata);

		f_fileWritten = true;
		
		//update file status as finished
		if (Settings.trackFileStatus())
			JobStatus.setCreationJobFileStatus(p_fileMetadata.getFile(), CreationJobFileState.FINISHED);

		// update progress
		JobStatus.incrementInputFilesProcessed(1);
	}

	/**
	 * Writes a file header to the archive, which is the file's metadata
	 * @param p_fileMetadata The metadata for the file about to be written
	 * @param p_fragmentNumber The fragment number of the file
	 * @param p_fileLengthRemaining The length of file data that still needs to be written.
	 * @return If writing the file header was successful
	 */
	private boolean writeFileHeader(Metadata p_fileMetadata, long p_fragmentNumber,
					long p_fileLengthRemaining)
	{
		// fragment number
		if (!writeFull(ByteConversion.longToBytes(p_fragmentNumber)))
			return false;
		
		//file type
		if (!writeFull(ByteConversion.intToByte(p_fileMetadata.getType().toInt())))
			return false;
		
		if (p_fileMetadata.getType().equals(FileType.k_file))
		{
			// file name length
			if (!writeFull(ByteConversion
							.shortToBytes((short) p_fileMetadata.getFile().getPath().length())))
				return false;
	
			// file name
			if (!writeFull(p_fileMetadata.getFile().getPath().getBytes(Constants.CHARSET)))
				return false;
	
			// date created
			if (!writeFull(ByteConversion.longToBytes(p_fileMetadata.getDateCreated())))
				return false;
	
			// date modified
			if (!writeFull(ByteConversion.longToBytes(p_fileMetadata.getDateModified())))
				return false;
	
			// permissions
			if (!writeFull(ByteConversion.shortToBytes(p_fileMetadata.getPermissions())))
				return false;
	
			// length of data that still needs to be written
			if (!writeFull(ByteConversion.longToBytes(p_fileLengthRemaining)))
				return false;
		}
		else
		{
			//folder type:
			
			// file name length
			if (!writeFull(ByteConversion
							.shortToBytes((short) p_fileMetadata.getFile().getPath().length())))
				return false;
	
			// file name
			if (!writeFull(p_fileMetadata.getFile().getPath().getBytes(Constants.CHARSET)))
				return false;
		}

		return true;
	}

	/**
	 * Writes a file's data to the archive
	 * @param p_reader The input stream for the file data
	 * @param p_fileLengthRemaining The length of data that still needs to be written
	 * @return The length of data that still needs to be written after writing as much
	 * as possible to this archive
	 * @throws IOException If the data input stream could not be read
	 */
	private long writeFileData(DataInputStream p_reader, long p_fileLengthRemaining)
					throws IOException
	{
		do
		{
			int bytesWritten = f_currentArchive.write(f_buffer, f_dataOffset, f_dataLength);
			
			f_dataOffset += bytesWritten;
			f_dataLength -= bytesWritten;
			p_fileLengthRemaining -= bytesWritten;

			if (f_dataOffset == f_buffer.length)
			{
				// the full thing was written, get more
				f_dataOffset = 0;
				f_dataLength = p_reader.read(f_buffer, f_dataOffset, f_buffer.length);
			}
			else
			{
				// the archive is full, some portion of the data was not written
				return p_fileLengthRemaining;
			}

		}
		while (f_dataLength > 0);

		assert(p_fileLengthRemaining == 0);

		return p_fileLengthRemaining;
	}
}
