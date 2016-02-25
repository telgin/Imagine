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
 * @update_comment
 */
public class Comparisons
{
	/**
	 * @update_comment
	 * @param p_metadata1
	 * @param p_metadata2
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
			assertEquals(p_metadata1.getProductUUID(), p_metadata2.getProductUUID());
			assertEquals(p_metadata1.getType(), p_metadata2.getType());
		}
	}

	/**
	 * @update_comment
	 * @param p_file
	 * @param p_metadata
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
	 * @update_comment
	 * @param p_algorithm1
	 * @param p_algorithm2
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
	 * @update_comment
	 * @param p_set1
	 * @param p_set2
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
	 * @update_comment
	 * @param p_originalRoot
	 * @param p_extractedRoot
	 * @param p_absolutePaths
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
	 * @update_comment
	 * @param p_root
	 * @return
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
	 * @update_comment
	 * @param p_originalRoot
	 * @param p_extractedRoot
	 * @param p_originalFile
	 * @param p_absolutePaths
	 * @return
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
	 * @update_comment
	 * @param p_file1
	 * @param p_file2
	 */
	public static void compareFileHashes(File p_file1, File p_file2)
	{
		//use this correctly
		assertTrue(!p_file1.isDirectory());
		assertTrue(!p_file2.isDirectory());
		
		assertArrayEquals(Hashing.hash(p_file1), Hashing.hash(p_file2));
	}
	
	/**
	 * @update_comment
	 * @param p_file1
	 * @param p_file2
	 */
	public static void compareFilePaths(File p_file1, File p_file2)
	{
		assertEquals(p_file1.getPath(), p_file2.getPath());
	}
	
	/**
	 * @update_comment
	 * @param p_key1
	 * @param p_key2
	 */
	public static void compareKeys(Key p_key1, Key p_key2)
	{
		if (p_key1 == null || p_key2 == null)
		{
			assertEquals(p_key1, p_key2);
		}
		else
		{
			assertEquals(p_key1.getType(), p_key2.getType());
			assertArrayEquals(p_key1.getKeyHash(), p_key2.getKeyHash());
		}
	}
	
	
}
