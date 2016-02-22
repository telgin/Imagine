package util;

import logging.LogLevel;
import logging.Logger;

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
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import data.FileType;
import data.Metadata;

public class FileSystemUtil
{

	public static void loadMetadataFromFile(Metadata metadata, File file)
	{
		// check every field, only add if it's not set already
		if (metadata.getDateCreated() == -1)
			metadata.setDateCreated(getDateCreated(file));

		if (metadata.getDateModified() == -1)
			metadata.setDateModified(getDateModified(file));

		if (metadata.getFileHash() == null)
			metadata.setFileHash(Hashing.hash(file));

		if (metadata.getFile() == null)
			metadata.setFile(file);

		if (metadata.getPermissions() == -1)
			metadata.setPermissions(getNumericFilePermissions(file));
		
		if (metadata.getType() == null)
			metadata.setType(file.isDirectory() ? FileType.k_folder : FileType.k_file);
	}

	public static short getNumericFilePermissions(File file)
	{
		try
		{
			Set<PosixFilePermission> permissions =
							Files.getPosixFilePermissions(file.toPath());
			return (short) permissionsToInt(permissions);
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, e, false);
			return 444; // you got a better idea?
		}
	}
	
	public static void setNumericFilePermissions(File file, short permissions)
	{
		try
		{
			Files.setPosixFilePermissions(file.toPath(), intToPermissions(permissions));
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_warning, "Cannot set permissions for file: " + file.getName());
		}
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * @param mode
	 * @return
	 */
	public static Set<PosixFilePermission> intToPermissions(int mode)
	{
		Set<PosixFilePermission> result = EnumSet.noneOf(PosixFilePermission.class);

		if (isSet(mode, 0400))
			result.add(PosixFilePermission.OWNER_READ);

		if (isSet(mode, 0200))
			result.add(PosixFilePermission.OWNER_WRITE);

		if (isSet(mode, 0100))
			result.add(PosixFilePermission.OWNER_EXECUTE);

		if (isSet(mode, 040))
			result.add(PosixFilePermission.GROUP_READ);

		if (isSet(mode, 020))
			result.add(PosixFilePermission.GROUP_WRITE);

		if (isSet(mode, 010))
			result.add(PosixFilePermission.GROUP_EXECUTE);

		if (isSet(mode, 04))
			result.add(PosixFilePermission.OTHERS_READ);

		if (isSet(mode, 02))
			result.add(PosixFilePermission.OTHERS_WRITE);

		if (isSet(mode, 01))
			result.add(PosixFilePermission.OTHERS_EXECUTE);

		return result;
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * @param mode
	 * @param testbit
	 * @return
	 */
	private static boolean isSet(int mode, int testbit)
	{
		return (mode & testbit) == testbit;
	}

	/**
	 * @credit https://github.com/gradle/gradle
	 * @param permissions
	 * @return
	 */
	public static int permissionsToInt(Set<PosixFilePermission> permissions)
	{
		int result = 0;

		if (permissions.contains(PosixFilePermission.OWNER_READ))
			result = result | 0400;

		if (permissions.contains(PosixFilePermission.OWNER_WRITE))
			result = result | 0200;

		if (permissions.contains(PosixFilePermission.OWNER_EXECUTE))
			result = result | 0100;

		if (permissions.contains(PosixFilePermission.GROUP_READ))
			result = result | 040;

		if (permissions.contains(PosixFilePermission.GROUP_WRITE))
			result = result | 020;

		if (permissions.contains(PosixFilePermission.GROUP_EXECUTE))
			result = result | 010;

		if (permissions.contains(PosixFilePermission.OTHERS_READ))
			result = result | 04;

		if (permissions.contains(PosixFilePermission.OTHERS_WRITE))
			result = result | 02;

		if (permissions.contains(PosixFilePermission.OTHERS_EXECUTE))
			result = result | 01;

		return result;
	}

	public static long getDateCreated(File file)
	{
		try
		{
			BasicFileAttributes attr = Files.readAttributes(file.toPath(),
							BasicFileAttributes.class);
			return attr.creationTime().toMillis();
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Cannot date created for: " + file.getPath());
			return -1;
		}
	}

	public static long getDateModified(File file)
	{
		try
		{
			BasicFileAttributes attr = Files.readAttributes(file.toPath(),
							BasicFileAttributes.class);
			return attr.lastModifiedTime().toMillis();
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_error, "Cannot date created for: " + file.getPath());
			return -1;
		}
	}
	
	/**
	 * @update_comment
	 * @credit http://stackoverflow.com/questions/9198184/setting-file-creation-timestamp-in-java
	 * @param file
	 * @param dateModified
	 * @param dateAccessed
	 * @param dateCreated
	 * @throws IOException
	 */
	public static void setFileDates(File file, long dateCreated, long dateModified,
					long dateAccessed)
	{

        BasicFileAttributeView attributes = Files.getFileAttributeView(
        				file.toPath(), BasicFileAttributeView.class);
        try
		{
			attributes.setTimes(FileTime.fromMillis(dateModified), 
							FileTime.fromMillis(dateAccessed), 
							FileTime.fromMillis(dateCreated));
		}
		catch (IOException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_warning, "Cannot set dates for file: " + file.getName());
		}
    }
	
	public static void setFileDates(File file, long dateCreated, long dateModified)
	{
		setFileDates(file, dateCreated, dateModified, dateModified);
	}

	public static Metadata loadMetadataFromFile(File file)
	{
		Metadata metadata = new Metadata();
		loadMetadataFromFile(metadata, file);
		return metadata;
	}

	public static boolean trackedBy(File f, HashSet<File> fileSet)
	{
		if (fileSet.contains(f))
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
				parent = f.getCanonicalFile().getParentFile();
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_error,
								"Cannot get canonical file for: " + f.getPath());
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
				if (fileSet.contains(file))
				{
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * @credit Jeff Learman
	 * @credit http://stackoverflow.com/questions/20281835/how-to-delete-a-
	 *         folder-with-files-using-java
	 * @param file
	 */
	public static void deleteDir(File file)
	{
		File[] contents = file.listFiles();
		if (contents != null)
		{
			for (File f : contents)
			{
				deleteDir(f);
			}
		}
		file.delete();
	}

	/**
	 * @credit http://www.mkyong.com/java/how-to-copy-directory-in-java/
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyDir2(File src, File dest) throws IOException
	{
		if (src.isDirectory())
		{

			if (!dest.exists())
				dest.mkdir();

			for (String file : src.list())
			{
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);

				copyDir2(srcFile, destFile);
			}

		}
		else
		{
			Files.copy(src.toPath(), dest.toPath());
		}
	}

	/**
	 * (Can't believe it's this complicated in Java)
	 * 
	 * @credit http://javatutorialhq.com/java/example-source-code/io/nio/folder-
	 *         copy/
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void copyDir(File from, File to) throws IOException
	{
		Path source = from.toPath();
		Path target = to.toPath();

		CopyOption[] copyOptions = new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING };

		if (Files.isDirectory(source))
		{
			Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
				Integer.MAX_VALUE, new FileVisitor<Path>()
				{
					@Override
					public FileVisitResult postVisitDirectory(Path dir,
									IOException exc) throws IOException
					{
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult preVisitDirectory(Path dir,
									BasicFileAttributes attrs)
					{
						Path newDirectory = target
										.resolve(source.relativize(dir));
						try
						{
							Files.copy(dir, newDirectory, copyOptions);
						}
						catch (FileAlreadyExistsException x)
						{
						}
						catch (IOException x)
						{
							return FileVisitResult.SKIP_SUBTREE;
						}

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file,
									BasicFileAttributes attrs)
													throws IOException
					{
						Files.copy(source, target, copyOptions);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file,
									IOException exc) throws IOException
					{
						return FileVisitResult.CONTINUE;
					}
				});
		}
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
	
	public static String getProductName(long streamUUID, long sequenceNumber)
	{
		return streamUUID + "_" + sequenceNumber;
	}
	
	public static String getProductName(byte[] productUUID)
	{
		return getProductName(ByteConversion.getStreamUUID(productUUID),
						ByteConversion.getProductSequenceNumber(productUUID));
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
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static int countEligableFiles(File file) throws IOException
	{
		if (file.isDirectory())
		{
			File[] children = file.listFiles();
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
	 * @param folder
	 * @return
	 */
	public static boolean directoryEmpty(File folder)
	{
		try
		{
			DirectoryStream<Path> dirStream = Files.newDirectoryStream(folder.toPath());
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
