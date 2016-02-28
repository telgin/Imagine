package util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import javax.imageio.ImageIO;

import data.FileType;
import data.Metadata;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A collection of common file system related operations
 */
public class FileSystemUtil
{
	/**
	 * Loads a metadata object from a file, filling in any missing elements
	 * @param p_metadata A partially constructed metadata object
	 * @param p_file The file to get the metadata from
	 */
	public static void loadMetadataFromFile(Metadata p_metadata, File p_file)
	{
		// check every field, only add if it's not set already
		if (p_metadata.getDateCreated() == -1)
			p_metadata.setDateCreated(getDateCreated(p_file));

		if (p_metadata.getDateModified() == -1)
			p_metadata.setDateModified(getDateModified(p_file));

		if (p_metadata.getFile() == null)
			p_metadata.setFile(p_file);

		if (p_metadata.getPermissions() == -1)
			p_metadata.setPermissions(getNumericFilePermissions(p_file));
		
		if (p_metadata.getType() == null)
			p_metadata.setType(p_file.isDirectory() ? FileType.k_folder : FileType.k_file);
	}

	/**
	 * Gets the posix numeric file permissions
	 * @param p_file The file to get the permissions of
	 * @return The posix file permissions, or 444 if it could not be retrieved
	 */
	public static short getNumericFilePermissions(File p_file)
	{
		try
		{
			Set<PosixFilePermission> permissions =
							Files.getPosixFilePermissions(p_file.toPath());
			return (short) permissionsToInt(permissions);
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, e, false);
			return 444; // you got a better idea?
		}
	}
	
	/**
	 * Sets the permissions of a file with a short representing the posix numeric file permissions
	 * @param p_file The file to set the permissions of
	 * @param p_permissions The permissions to set
	 */
	public static void setNumericFilePermissions(File p_file, short p_permissions)
	{
		try
		{
			Files.setPosixFilePermissions(p_file.toPath(), intToPermissions(p_permissions));
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_warning, "Cannot set permissions for file: " + p_file.getName());
		}
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * Parses posix file permissions from an integer
	 * @param p_mode The numeric file permissions
	 * @return The set of permissions represented by the mode
	 */
	public static Set<PosixFilePermission> intToPermissions(int p_mode)
	{
		Set<PosixFilePermission> result = EnumSet.noneOf(PosixFilePermission.class);

		if (isSet(p_mode, 0400))
			result.add(PosixFilePermission.OWNER_READ);

		if (isSet(p_mode, 0200))
			result.add(PosixFilePermission.OWNER_WRITE);

		if (isSet(p_mode, 0100))
			result.add(PosixFilePermission.OWNER_EXECUTE);

		if (isSet(p_mode, 040))
			result.add(PosixFilePermission.GROUP_READ);

		if (isSet(p_mode, 020))
			result.add(PosixFilePermission.GROUP_WRITE);

		if (isSet(p_mode, 010))
			result.add(PosixFilePermission.GROUP_EXECUTE);

		if (isSet(p_mode, 04))
			result.add(PosixFilePermission.OTHERS_READ);

		if (isSet(p_mode, 02))
			result.add(PosixFilePermission.OTHERS_WRITE);

		if (isSet(p_mode, 01))
			result.add(PosixFilePermission.OTHERS_EXECUTE);

		return result;
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * Tells if a given bit is set, within the context of permissions
	 * @param p_mode The numeric file permissions
	 * @param p_testbit The bit to test
	 * @return If the bit is set
	 */
	private static boolean isSet(int p_mode, int p_testbit)
	{
		return (p_mode & p_testbit) == p_testbit;
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * Converts a set of posix permissions to an integer
	 * @param p_permissions The set of permissions
	 * @return The integer representation
	 */
	public static int permissionsToInt(Set<PosixFilePermission> p_permissions)
	{
		int result = 0;

		if (p_permissions.contains(PosixFilePermission.OWNER_READ))
			result = result | 0400;

		if (p_permissions.contains(PosixFilePermission.OWNER_WRITE))
			result = result | 0200;

		if (p_permissions.contains(PosixFilePermission.OWNER_EXECUTE))
			result = result | 0100;

		if (p_permissions.contains(PosixFilePermission.GROUP_READ))
			result = result | 040;

		if (p_permissions.contains(PosixFilePermission.GROUP_WRITE))
			result = result | 020;

		if (p_permissions.contains(PosixFilePermission.GROUP_EXECUTE))
			result = result | 010;

		if (p_permissions.contains(PosixFilePermission.OTHERS_READ))
			result = result | 04;

		if (p_permissions.contains(PosixFilePermission.OTHERS_WRITE))
			result = result | 02;

		if (p_permissions.contains(PosixFilePermission.OTHERS_EXECUTE))
			result = result | 01;

		return result;
	}

	/**
	 * Gets the date created from a file
	 * @param p_file The file
	 * @return The date created in epoch time
	 */
	public static long getDateCreated(File p_file)
	{
		try
		{
			BasicFileAttributes attr = Files.readAttributes(p_file.toPath(),
							BasicFileAttributes.class);
			return attr.creationTime().toMillis();
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Cannot date created for: " + p_file.getPath());
			return -1;
		}
	}

	/**
	 * Gets the date modified from a file
	 * @param p_file The file
	 * @return The date modified in epoch time
	 */
	public static long getDateModified(File p_file)
	{
		try
		{
			BasicFileAttributes attr = Files.readAttributes(p_file.toPath(),
							BasicFileAttributes.class);
			return attr.lastModifiedTime().toMillis();
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Cannot date created for: " + p_file.getPath());
			return -1;
		}
	}
	
	/**
	 * Attempts to set a file's creation, modification, and access dates in the file system
	 * @credit http://stackoverflow.com/questions/9198184/setting-file-creation-timestamp-in-java
	 * @param p_file The file
	 * @param p_dateModified The date modified in epoch time
	 * @param p_dateAccessed The date accessed in epoch time
	 * @param p_dateCreated The date created in epoch time
	 */
	public static void setFileDates(File p_file, long p_dateCreated, long p_dateModified,
		long p_dateAccessed)
	{

        BasicFileAttributeView attributes = Files.getFileAttributeView(
        				p_file.toPath(), BasicFileAttributeView.class);
        try
		{
			attributes.setTimes(FileTime.fromMillis(p_dateModified), 
							FileTime.fromMillis(p_dateAccessed), 
							FileTime.fromMillis(p_dateCreated));
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_warning, "Cannot set dates for file: " + p_file.getName());
		}
    }
	
	/**
	 * Tries to set the date created and date modified for a file
	 * @param p_file The file
	 * @param p_dateCreated The date created in epoch time
	 * @param p_dateModified The date modified in epoch time
	 */
	public static void setFileDates(File p_file, long p_dateCreated, long p_dateModified)
	{
		setFileDates(p_file, p_dateCreated, p_dateModified, p_dateModified);
	}

	/**
	 * Loads a metadata object from a file
	 * @param p_file The file
	 * @return The file metadata
	 */
	public static Metadata loadMetadataFromFile(File p_file)
	{
		Metadata metadata = new Metadata();
		loadMetadataFromFile(metadata, p_file);
		return metadata;
	}

	/**
	 * @credit http://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
	 * Deletes a directory and its contents
	 * @param p_file The directory file
	 */
	public static void deleteDir(File p_file)
	{
		File[] contents = p_file.listFiles();
		if (contents != null)
		{
			for (File f : contents)
			{
				deleteDir(f);
			}
		}
		p_file.delete();
	}

	/**
	 * @credit http://www.mkyong.com/java/how-to-copy-directory-in-java/
	 * Copies a directory and its contents to a new directory
	 * @param p_src The source directory
	 * @param p_dest The destination directory
	 * @throws IOException If the copy process fails
	 */
	public static void copyDir2(File p_src, File p_dest) throws IOException
	{
		if (p_src.isDirectory())
		{
			if (!p_dest.exists())
				p_dest.mkdir();

			for (String file : p_src.list())
			{
				File srcFile = new File(p_src, file);
				File destFile = new File(p_dest, file);

				copyDir2(srcFile, destFile);
			}
		}
		else
		{
			Files.copy(p_src.toPath(), p_dest.toPath());
		}
	}
	
	/**
	 * Relativizes a file by the working directory ("." as an absolute path)
	 * @param p_input The file to relativize
	 * @return The relativized file
	 */
	public static File relativizeByCurrentLocation(File p_input)
	{
		return Paths.get("").toAbsolutePath().relativize(p_input.toPath()).toFile();
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/7883542/getting-the-computer-name-in-java
	 * Attempts to get this PC's host name
	 * @return The host name as a string, or null if networking is not set up
	 */
	public static String getHostName()
	{
		try
		{
		    return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException ex)
		{
		    return null;
		}
	}
	
	/**
	 * Generates a file name in a standard way based on a stream uuid and archive sequence number: 
	 * streamUUID_sequenceNumber
	 * @param p_streamUUID The stream uuid
	 * @param p_sequenceNumber The archive sequence number
	 * @return A standard file name.
	 */
	public static String getArchiveName(long p_streamUUID, long p_sequenceNumber)
	{
		return p_streamUUID + "_" + p_sequenceNumber;
	}
	
	/**
	 * Generates a file name in a standard way based on an archive uuid
	 * @param p_archiveUUID The archive uuid
	 * @return The archive name string
	 */
	public static String getArchiveName(byte[] p_archiveUUID)
	{
		return getArchiveName(ByteConversion.getStreamUUID(p_archiveUUID),
			ByteConversion.getArchiveSequenceNumber(p_archiveUUID));
	}
	
	/**
	 * @credit http://www.java2s.com/Code/Java/2D-Graphics-GUI/ListAllreaderandwriterformatssupportedbyImageIO.htm
	 * Lists the file types supported by default in ImageIO
	 */
	public static void getSupportedImageIOTypes()
	{
		String names[] = ImageIO.getReaderFormatNames();
	    for (int i = 0; i < names.length; ++i)
	      System.out.println("reader " + names[i]);

	    names = ImageIO.getWriterFormatNames();
	    for (int i = 0; i < names.length; ++i)
	      System.out.println("writer " + names[i]);
	}
	
	
	/**
	 * Counts the number of files which could be added to an archive. Specifically,
	 * this would be any files and empty folders. If the input file a leaf, this function
	 * will return 1.
	 * @param p_file The file or folder to start at.
	 * @return The number of eligible files
	 * @throws IOException if files cannot be viewed
	 */
	public static int countEligableFiles(File p_file) throws IOException
	{
		if (p_file.isDirectory())
		{
			File[] children = p_file.listFiles();
			if (children.length == 0)
			{
				return 1;
			}
			else
			{
				int count = 0;
				for (File child : children)
					count += countEligableFiles(child);
				
				return count;
			}
		}
		else
		{
			return 1;
		}
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/5930087/how-to-check-if-a-directory-is-empty-in-java
	 * A function which checks if a directory is empty. Meant to be more efficient than listing the full
	 * contents and checking if the list is empty.
	 * @param p_folder The folder to check
	 * @return True if the directory is empty or it doesn't exist.
	 */
	public static boolean directoryEmpty(File p_folder)
	{
		try
		{
			DirectoryStream<Path> dirStream = Files.newDirectoryStream(p_folder.toPath());
			boolean empty = !dirStream.iterator().hasNext();
			dirStream.close();
			return empty;
		}
		catch (IOException e)
		{
			//this is kind of bad, but logically, with the way this will be used, 
			//a directory which doesn't exist doesn't have files in it.
			//TODO this works now, but handle this situation outside here
			return true;
		}
	}
}
