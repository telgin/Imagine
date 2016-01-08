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
import data.Key;
import data.Metadata;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.Constants;
import util.FileSystemUtil;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Comparisons
{
	public static void compareMetadata(Metadata m1, Metadata m2)
	{
		if (m1 == null || m2 == null)
		{
			assertEquals(m1, null);
			assertEquals(m2, null);
		}
		else
		{
			assertEquals(m1.getFile().getAbsolutePath(), m2.getFile().getAbsolutePath());
			assertArrayEquals(m1.getFileHash(), m2.getFileHash());
			assertEquals(m1.getFile().getPath(), m2.getFile().getPath());
			assertEquals(m1.getPermissions(), m2.getPermissions());
			assertEquals(m1.getDateCreated(), m2.getDateCreated());
			assertEquals(m1.getDateModified(), m2.getDateModified());
			assertEquals(m1.getProductUUID(), m2.getProductUUID());
			assertEquals(m1.getType(), m2.getType());
		}
	}

	public static void compareMetadataFile(File f, Metadata m)
	{
		assertEquals(f.getAbsolutePath(), m.getFile().getAbsolutePath());
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(f), m.getFileHash()));
		assertEquals(f.getPath(), m.getFile().getPath());
		assertEquals(FileSystemUtil.getNumericFilePermissions(f), m.getPermissions());
		assertEquals(FileSystemUtil.getDateCreated(f), m.getDateCreated());
		assertEquals(FileSystemUtil.getDateModified(f), m.getDateModified());
	}
	
	public static void compareAlgorithms(Algorithm a1, Algorithm a2)
	{
		if (a1 == null || a2 == null)
		{
			assertEquals(a1, a2);
		}
		else
		{
			assertEquals(a1.getName(), a2.getName());
			assertEquals(a1.getPresetName(), a2.getPresetName());
			assertEquals(a1.getVersion(), a2.getVersion());
			assertEquals(a1.getParameters().size(), a2.getParameters().size());
			
			for (Parameter p1 : a1.getParameters())
			{
				Parameter p2 = a2.getParameter(p1.getName());
				assertTrue(p2 != null);
				
				assertEquals(p1.getName(), p2.getName());
				assertEquals(p1.getType(), p2.getType());
				assertEquals(p1.getValue(), p2.getValue());
			}
		}
	}
	
	public static void compareTrackingGroups(TrackingGroup g1, TrackingGroup g2)
	{
		if (g1 == null || g2 == null)
		{
			assertEquals(g1, g2);
		}
		else
		{
			assertEquals(g1.getName(), g2.getName());
			assertEquals(g1.getStaticOutputFolder(), g2.getStaticOutputFolder());
			assertEquals(g1.getHashDBFile(), g2.getHashDBFile());
			
			compareFileSets(g1.getTrackedFiles(), g2.getTrackedFiles());
			compareFileSets(g1.getUntrackedFiles(), g2.getUntrackedFiles());
			
			compareAlgorithms(g1.getAlgorithm(), g2.getAlgorithm());
			
			compareKeys(g1.getKey(), g2.getKey());
		}
		
	}
	
	public static void compareFileSets(Set<File> set1, Set<File> set2)
	{
		assertEquals(set1.size(), set2.size());
		List<String> paths1 = new ArrayList<String>();
		List<String> paths2 = new ArrayList<String>();
		for (File f1 : set1)
			paths1.add(f1.getPath());
		for (File f2 : set2)
			paths2.add(f2.getPath());
		
		paths1.sort(null);
		paths2.sort(null);
		
		for (int i=0; i<paths1.size(); ++i)
		{
			assertEquals(paths1.get(i), paths2.get(i));
		}
	}
	
	public static void compareExtractedFileStructure(File originalRoot, File extractedRoot, boolean absolutePaths)
	{
		Logger.log(LogLevel.k_debug, "Comparing file structure...");
		
		//bfs through folders
		Queue<File> folders = new LinkedList<File>();
		folders.add(originalRoot);
		
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
						
						File expected = getExpectedExtractionFile(originalRoot, extractedRoot, sub, absolutePaths);
						System.out.println("Expecting file: " + expected.getPath());
						if (!expected.exists())
							System.out.println("FILE NOT FOUND");
						assertTrue(expected.exists());
					}
				}
				else
				{
					File expected = getExpectedExtractionFile(originalRoot, extractedRoot, sub, absolutePaths);
					System.out.println("Expecting file: " + expected.getPath());
					if (!expected.exists())
						System.out.println("FILE NOT FOUND");
					assertTrue(expected.exists());
					compareFileHashes(sub, expected);
				}
			}
		}
		
		
		assertEquals(countFiles(originalRoot), countFiles(extractedRoot));
	}
	
	/**
	 * @update_comment
	 * @param root
	 * @return
	 */
	private static int countFiles(File root)
	{
		int count = 0;
		
		//bfs through folders
		Queue<File> folders = new LinkedList<File>();
		folders.add(root);
		
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

	public static File getExpectedExtractionFile(File originalRoot, File extractedRoot,
					File originalFile, boolean absolutePaths)
	{
		if (absolutePaths)
		{
			return new File(extractedRoot, originalFile.getAbsolutePath());
		}
		else
		{
			//String relPath = originalRoot.toURI().relativize(originalFile.toURI()).getPath();
			//return new File(extractedRoot, relPath);
			
			return new File(extractedRoot, originalFile.getPath());
		}
	}
	
	public static void compareFileHashes(File f1, File f2)
	{
		//use this correctly
		assertTrue(!f1.isDirectory());
		assertTrue(!f2.isDirectory());
		
		assertArrayEquals(Hashing.hash(f1), Hashing.hash(f2));
	}
	
	public static void compareFilePaths(File f1, File f2)
	{
		assertEquals(f1.getPath(), f2.getPath());
	}
	
	public static void compareKeys(Key k1, Key k2)
	{
		if (k1 == null || k2 == null)
		{
			assertEquals(k1, k2);
		}
		else
		{
			assertEquals(k1.getName(), k2.getName());
			assertEquals(k1.getType(), k2.getType());
			assertArrayEquals(k1.getKeyHash(), k2.getKeyHash());
		}
	}
	
	
}
