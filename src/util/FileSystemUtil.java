package util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import data.FileType;
import data.Metadata;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileSystemUtil
{
	/**
	 * @update_comment
	 * @param p_metadata
	 * @param p_file
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
	 * @update_comment
	 * @param p_file
	 * @return
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
	 * @update_comment
	 * @param p_file
	 * @param p_permissions
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
	 * @param p_mode
	 * @return
	 */
	/**
	 * @update_comment
	 * @param p_mode
	 * @return
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
	 * @param p_mode
	 * @param p_testbit
	 * @return
	 */
	private static boolean isSet(int p_mode, int p_testbit)
	{
		return (p_mode & p_testbit) == p_testbit;
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * @param p_permissions
	 * @return
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
	 * @update_comment
	 * @param p_file
	 * @return
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
	 * @update_comment
	 * @param p_file
	 * @return
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
	 * @update_comment
	 * @credit http://stackoverflow.com/questions/9198184/setting-file-creation-timestamp-in-java
	 * @param p_file
	 * @param p_dateModified
	 * @param p_dateAccessed
	 * @param p_dateCreated
	 * @throws IOException
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
	 * @update_comment
	 * @param p_file
	 * @param p_dateCreated
	 * @param p_dateModified
	 */
	public static void setFileDates(File p_file, long p_dateCreated, long p_dateModified)
	{
		setFileDates(p_file, p_dateCreated, p_dateModified, p_dateModified);
	}

	/**
	 * @update_comment
	 * @param p_file
	 * @return
	 */
	public static Metadata loadMetadataFromFile(File p_file)
	{
		Metadata metadata = new Metadata();
		loadMetadataFromFile(metadata, p_file);
		return metadata;
	}

	/**
	 * @update_comment
	 * @param p_file
	 * @param p_fileSet
	 * @return
	 */
	public static boolean trackedBy(File p_file, Set<File> p_fileSet)
	{
		if (p_fileSet.contains(p_file))
		{
			// the file or folder is specifically listed in the file set
			return true;
		}
		else
		{
			// get all parent files
			ArrayList<File> parents = new ArrayList<File>();

			File parent = null;
			try
			{
				parent = p_file.getCanonicalFile().getParentFile();
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_error, "Cannot get canonical file for: " + p_file.getPath());
			}

			while (parent != null)
			{
				parents.add(parent);
				parent = parent.getParentFile();
			}

			// the file can only be tracked if one its parents is specifically
			// listed
			for (File file : parents)
			{
				if (p_fileSet.contains(file))
				{
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * @credit Jeff Learman
	 * @credit http://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
	 * @param p_file
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
	 * @param p_src
	 * @param p_dest
	 * @throws IOException
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
	 * @update_comment
	 * @param p_input
	 * @return
	 */
	public static File relativizeByCurrentLocation(File p_input)
	{
		return Paths.get("").toAbsolutePath().relativize(p_input.toPath()).toFile();
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/7883542/getting-the-computer-name-in-java
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @param p_streamUUID
	 * @param p_sequenceNumber
	 * @return
	 */
	public static String getProductName(long p_streamUUID, long p_sequenceNumber)
	{
		return p_streamUUID + "_" + p_sequenceNumber;
	}
	
	/**
	 * @update_comment
	 * @param p_productUUID
	 * @return
	 */
	public static String getProductName(byte[] p_productUUID)
	{
		return getProductName(ByteConversion.getStreamUUID(p_productUUID),
			ByteConversion.getProductSequenceNumber(p_productUUID));
	}
	
	/**
	 * @credit http://www.java2s.com/Code/Java/2D-Graphics-GUI/ListAllreaderandwriterformatssupportedbyImageIO.htm
	 * @update_comment
	 * @return
	 */
	public static void getSupportedImageIOTypes()
	{
		String names[] = ImageIO.getReaderFormatNames();
	    for (int i = 0; i < names.length; ++i) {
	      System.out.println("reader " + names[i]);
	    }

	    names = ImageIO.getWriterFormatNames();
	    for (int i = 0; i < names.length; ++i) {
	      System.out.println("writer " + names[i]);
	    }
	}
	
	
	/**
	 * Counts the number of files which could be added to an archive. Specifically,
	 * this would be any files and empty folders. If the input file a leaf, this function
	 * will return 1.
	 * @param p_file
	 * @return
	 * @throws IOException 
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
	 * @update_comment
	 * @param p_folder
	 * @return
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
			return true;
		}
	}
}
