package testing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import algorithms.Algorithm;
import algorithms.Parameter;
import config.Constants;
import data.Metadata;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.FileSystemUtil;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A test support class which compares various data  and file structures
 * for archive creation and parsing.
 */
public class Comparisons
{
	/**
	 * Compares the fields of two metadata objects
	 * @param p_metadata1 The first metadata object
	 * @param p_metadata2 The second metadata object
	 */
	public static void compareMetadata(Metadata p_metadata1, Metadata p_metadata2)
	{
		if (p_metadata1 == null || p_metadata2 == null)
		{
			assertEquals(p_metadata1, null);
			assertEquals(p_metadata2, null);
		}
		else
		{
			assertEquals(p_metadata1.getFile().getAbsolutePath(), p_metadata2.getFile().getAbsolutePath());
			assertEquals(p_metadata1.getFile().getPath(), p_metadata2.getFile().getPath());
			assertEquals(p_metadata1.getPermissions(), p_metadata2.getPermissions());
			assertEquals(p_metadata1.getDateCreated(), p_metadata2.getDateCreated());
			assertEquals(p_metadata1.getDateModified(), p_metadata2.getDateModified());
			assertEquals(p_metadata1.getArchiveUUID(), p_metadata2.getArchiveUUID());
			assertEquals(p_metadata1.getType(), p_metadata2.getType());
		}
	}

	/**
	 * Compares data that can be found within the file system for some file to the
	 * data contained in a metadata object.
	 * @param p_file The file
	 * @param p_metadata The metadata object
	 */
	public static void compareMetadataFile(File p_file, Metadata p_metadata)
	{
		assertEquals(p_file.getAbsolutePath(), p_metadata.getFile().getAbsolutePath());
		assertEquals(p_file.getPath(), p_metadata.getFile().getPath());
		assertEquals(FileSystemUtil.getNumericFilePermissions(p_file), p_metadata.getPermissions());
		assertEquals(FileSystemUtil.getDateCreated(p_file), p_metadata.getDateCreated());
		assertEquals(FileSystemUtil.getDateModified(p_file), p_metadata.getDateModified());
	}
	
	/**
	 * Compares the first algorithm
	 * @param p_algorithm1 The first algorithm
	 * @param p_algorithm2 The second algorithm
	 */
	public static void compareAlgorithms(Algorithm p_algorithm1, Algorithm p_algorithm2)
	{
		if (p_algorithm1 == null || p_algorithm2 == null)
		{
			assertEquals(p_algorithm1, p_algorithm2);
		}
		else
		{
			assertEquals(p_algorithm1.getName(), p_algorithm2.getName());
			assertEquals(p_algorithm1.getPresetName(), p_algorithm2.getPresetName());
			assertEquals(p_algorithm1.getVersion(), p_algorithm2.getVersion());
			assertEquals(p_algorithm1.getParameters().size(), p_algorithm2.getParameters().size());
			
			for (Parameter p1 : p_algorithm1.getParameters())
			{
				Parameter p2 = p_algorithm2.getParameter(p1.getName());
				assertTrue(p2 != null);
				
				assertEquals(p1.getName(), p2.getName());
				assertEquals(p1.getType(), p2.getType());
				assertEquals(p1.getValue(), p2.getValue());
			}
		}
	}
	
	/**
	 * Compares two sets of files to see if they both contain files with the same paths
	 * @param p_set1 The first set
	 * @param p_set2 The second set
	 */
	public static void compareFileSets(Set<File> p_set1, Set<File> p_set2)
	{
		assertEquals(p_set1.size(), p_set2.size());
		List<String> paths1 = new ArrayList<String>();
		List<String> paths2 = new ArrayList<String>();
		for (File f1 : p_set1)
			paths1.add(f1.getPath());
		for (File f2 : p_set2)
			paths2.add(f2.getPath());
		
		paths1.sort(null);
		paths2.sort(null);
		
		for (int i=0; i<paths1.size(); ++i)
		{
			assertEquals(paths1.get(i), paths2.get(i));
		}
	}
	
	/**
	 * Compares the extracted file structure with the file structure before the inputs were
	 * added to an archive. Used to test if file structures are being properly written and read.
	 * @param p_originalRoot The original root folder of the input files
	 * @param p_extractedRoot The folder the extracted files were output to
	 * @param p_absolutePaths If absolute paths should be used
	 */
	public static void compareExtractedFileStructure(File p_originalRoot, File p_extractedRoot, boolean p_absolutePaths)
	{
		Logger.log(LogLevel.k_debug, "Comparing file structure...");
		
		//bfs through folders
		Queue<File> folders = new LinkedList<File>();
		folders.add(p_originalRoot);
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			for (File sub : folder.listFiles())
			{
				if (sub.isDirectory())
				{
					if (!sub.getName().equals(Constants.INDEX_FOLDER_NAME))
					{
						folders.add(sub);
						
						File expected = getExpectedExtractionFile(p_originalRoot, p_extractedRoot, sub, p_absolutePaths);
						System.out.println("Expecting file: " + expected.getPath());
						if (!expected.exists())
							System.out.println("FILE NOT FOUND");
						assertTrue(expected.exists());
					}
				}
				else
				{
					File expected = getExpectedExtractionFile(p_originalRoot, p_extractedRoot, sub, p_absolutePaths);
					System.out.println("Expecting file: " + expected.getPath());
					if (!expected.exists())
						System.out.println("FILE NOT FOUND");
					assertTrue(expected.exists());
					compareFileHashes(sub, expected);
				}
			}
		}
		
		
		assertEquals(countFiles(p_originalRoot), countFiles(p_extractedRoot));
	}
	
	/**
	 * Counts the number of files in some folder (recursive count)
	 * @param p_root The root folder to count from
	 * @return The number of files in this folder
	 */
	private static int countFiles(File p_root)
	{
		int count = 0;
		
		//bfs through folders
		Queue<File> folders = new LinkedList<File>();
		folders.add(p_root);
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			for (File sub : folder.listFiles())
			{
				if (sub.isDirectory())
				{
					if (!sub.getName().equals(Constants.INDEX_FOLDER_NAME))
					{
						folders.add(sub);
					}
				}
				else
				{
					++count;
				}
			}
		}
		
		return count;
	}

	/**
	 * Gets the expected extraction file location based on a number of parameters.
	 * @param p_originalRoot The original file root
	 * @param p_extractedRoot The extraction root folder
	 * @param p_originalFile The original file
	 * @param p_absolutePaths If absolute paths should be enforced
	 * @return The expected file location when a file is extracted
	 */
	public static File getExpectedExtractionFile(File p_originalRoot, File p_extractedRoot,
					File p_originalFile, boolean p_absolutePaths)
	{
		if (p_absolutePaths)
		{
			return new File(p_extractedRoot, p_originalFile.getAbsolutePath());
		}
		else
		{
			return new File(p_extractedRoot, p_originalFile.getPath());
		}
	}
	
	/**
	 * Compares two files by hashing both and comparing the hashes
	 * @param p_file1 The first file
	 * @param p_file2 The second file
	 */
	public static void compareFileHashes(File p_file1, File p_file2)
	{
		//use this correctly
		assertTrue(!p_file1.isDirectory());
		assertTrue(!p_file2.isDirectory());
		
		assertArrayEquals(Hashing.hash(p_file1), Hashing.hash(p_file2));
	}
	
	/**
	 * Compares the file paths of two files
	 * @param p_file1 The first file
	 * @param p_file2 The second file
	 */
	public static void compareFilePaths(File p_file1, File p_file2)
	{
		assertEquals(p_file1.getPath(), p_file2.getPath());
	}
	
	/**
	 * Compares keys to see if their subclasses and hashes are the same
	 * @param p_key1 The first key
	 * @param p_key2 The second key
	 */
	public static void compareKeys(Key p_key1, Key p_key2)
	{
		if (p_key1 == null || p_key2 == null)
		{
			assertEquals(p_key1, p_key2);
		}
		else
		{
			assertEquals(p_key1.getClass(), p_key2.getClass());
			assertArrayEquals(p_key1.getKeyHash(), p_key2.getKeyHash());
		}
	}
	
	
}
