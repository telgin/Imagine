package testing.highlevel;

import java.io.File;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import data.FileKey;
import data.Key;
import data.TrackingGroup;

@RunWith(Enclosed.class)
public class StealthPNGTest
{
	/**
	 * Default parameters
	 * File Key
	 * Uses Database
	 */
	public static class TreeTest
	{
		static Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("StealthPNG");
		
		static
		{
			File imageFolder = new File(ProductIOTest.homeFolder, "images/");
			algorithm.setParameter("imageFolder", imageFolder.getPath());
		}
	
		//tracking group setup
		static String keyName = "testKeyName";
		static String groupName = "testGroupName";
		static Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));

		static TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
		
//		@Test(timeout = 120000)
//		public void testNoFiles_1(){ ProductIOTest.testNoFiles(group, 1); }
		@Test(timeout = 120000)
		public void testSmallFile_1(){ ProductIOTest.testSmallFile(group, 1); }
//		@Test(timeout = 120000)
//		public void testSmallTree_1(){ ProductIOTest.testSmallTree(group, 1); }
//		@Test(timeout = 120000)
//		public void testBigFile_1(){ ProductIOTest.testBigFile(group, 1); }
//		@Test(timeout = 120000)
//		public void testBigTree_1(){ ProductIOTest.testBigTree(group, 1); }
//		
//		@Test(timeout = 120000)
//		public void testNoFiles_5(){ ProductIOTest.testNoFiles(group, 5); }
//		@Test(timeout = 120000)
//		public void testSmallFile_5(){ ProductIOTest.testSmallFile(group, 5); }
//		@Test(timeout = 120000)
//		public void testSmallTree_5(){ ProductIOTest.testSmallTree(group, 5); }
//		@Test(timeout = 120000)
//		public void testBigFile_5(){ ProductIOTest.testBigFile(group, 5); }
//		@Test(timeout = 120000)
//		public void testBigTree_5(){ ProductIOTest.testBigTree(group, 5); }
	}
}
