package testing.highlevel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import data.FileKey;
import data.Key;
import data.PartAssembler;
import data.TrackingGroup;
import database.Database;
import hibernate.Metadata;
import product.FileContents;
import product.ProductContents;
import product.ProductReader;
import runner.BackupJob;
import runner.SystemManager;
import testing.TestFileTrees;
import util.ByteConversion;
import util.Constants;
import util.FileSystemUtil;
import util.Hashing;

public class ProductIOTest {
	private static File homeFolder = new File("testing/highlevel/");
	private static File outputFolder = new File("testing/output/");
	private static File extractionFolder = new File("testing/extraction/");
	private static File assemblyFolder = new File("testing/assembly/");
	private static boolean shutdownCalled = true;
	private static ArrayList<BackupJob> jobs = new ArrayList<BackupJob>();

	public static class TestFullPNG
	{
			/**
			 * Default parameters
			 * File Key
			 * Uses Database
			 */
			public static class TreeTests1
			{
				static Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("FullPNG");
			
				//tracking group setup
				static String keyName = "testKeyName";
				static String groupName = "testGroupName";
				static Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));
		
				static TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
				
				@Test(timeout = 10000)
				public void testNoFiles_1_1(){ testNoFiles(group, 1, 1); }
				@Test(timeout = 10000)
				public void testSmallFile_1_1(){ testSmallFile(group, 1, 1); }
				@Test(timeout = 20000)
				public void testSmallTree_1_1(){ testSmallTree(group, 1, 1); }
				@Test(timeout = 20000)
				public void testBigFile_1_1(){ testBigFile(group, 1, 1); }
//				@Test(timeout = 10000)
//				public void testBigTree_1_1(){ testBigTree(group, 1, 1); }
				
				@Test(timeout = 10000)
				public void testNoFiles_5_5(){ testNoFiles(group, 5, 5); }
				@Test(timeout = 10000)
				public void testSmallFile_5_5(){ testSmallFile(group, 5, 5); }
				@Test(timeout = 20000)
				public void testSmallTree_5_5(){ testSmallTree(group, 5, 5); }
				@Test(timeout = 20000)
				public void testBigFile_5_5(){ testBigFile(group, 5, 5); }
//				@Test(timeout = 10000)
//				public void testBigTree_5_5(){ testBigTree(group, 5, 5); }
//				
//				@Test(timeout = 10000)
//				public void testBigTree_1_5(){ testBigTree(group, 1, 5); }
//				@Test(timeout = 10000)
//				public void testBigTree_5_1(){ testBigTree(group, 5, 1); }
			}
	}
	
	public static class TestTextBlock
	{
			/**
			 * Default parameters
			 * File Key
			 * Uses Database
			 */
			public static class TreeTests1
			{
				static Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("TextBlock");
			
				//tracking group setup
				static String keyName = "testKeyName";
				static String groupName = "testGroupName";
				static Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));
		
				static TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
				
				@Test(timeout = 10000)
				public void testNoFiles_1_1(){ testNoFiles(group, 1, 1); }
				@Test(timeout = 10000)
				public void testSmallFile_1_1(){ testSmallFile(group, 1, 1); }
				@Test(timeout = 18000)
				public void testSmallTree_1_1(){ testSmallTree(group, 1, 1); }
				@Test(timeout = 10000)
				public void testBigFile_1_1(){ testBigFile(group, 1, 1); }
//				@Test(timeout = 10000)
//				public void testBigTree_1_1(){ testBigTree(group, 1, 1); }
				
				@Test(timeout = 10000)
				public void testNoFiles_5_5(){ testNoFiles(group, 5, 5); }
				@Test(timeout = 10000)
				public void testSmallFile_5_5(){ testSmallFile(group, 5, 5); }
				@Test(timeout = 10000)
				public void testSmallTree_5_5(){ testSmallTree(group, 5, 5); }
				@Test(timeout = 10000)
				public void testBigFile_5_5(){ testBigFile(group, 5, 5); }
//				@Test(timeout = 10000)
//				public void testBigTree_5_5(){ testBigTree(group, 5, 5); }
//				
//				@Test(timeout = 10000)
//				public void testBigTree_1_5(){ testBigTree(group, 1, 5); }
//				@Test(timeout = 10000)
//				public void testBigTree_5_1(){ testBigTree(group, 5, 1); }
			}
	}
	
	private static void shutdown()
	{
		SystemManager.shutdown();
		for (BackupJob job : jobs)
			job.shutdown();
		jobs = new ArrayList<BackupJob>();
		shutdownCalled = true;
	}
	
	private static void testNoFiles(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;
		
		//setup
		String treeName = "noFiles";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName +
				"(" + indexWorkers + ", " + productWorkers + ")");
		
		//set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setProductStagingFolder(outputFolder);
		
		
		runJob(group, indexWorkers, productWorkers);
		
		
		//see what we got:
		//should be nothing
		assertEquals(0, outputFolder.listFiles().length);
		
		//something's pretty wrong if these aren't empty too
		assertEquals(0, extractionFolder.listFiles().length);
		assertEquals(0, assemblyFolder.listFiles().length);

		shutdown();
	}
	
	private static void testSmallFile(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;
		
		//setup
		String treeName = "smallFile";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName +
				"(" + indexWorkers + ", " + productWorkers + ")");
		
		//set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setProductStagingFolder(outputFolder);
		
		//specify the original single test file
		File testFile = inputFolder.listFiles()[0];
		
		runJob(group, indexWorkers, productWorkers);
		
		//get the metadata of our single test file now
		//that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, group);
		compareMetadataFile(testFile, previousMetadata);
		assertFalse(previousMetadata.isMetadataUpdate());
		
		//see what we got:
		//should be only one file
		assertEquals(1, outputFolder.listFiles().length);
		
		File productFile = outputFolder.listFiles()[0];
		
		//read the file, make sure the fields are all the same
		ProductReader reader = new ProductReader(group.getProductFactory());
		reader.setExtractionFolder(extractionFolder);
		ProductContents productContents = reader.extractAll(productFile);
		//System.out.println(productContents.toString());
		assertEquals(productContents.getAlgorithmName(), group.getAlgorithm().getName());
		assertEquals(productContents.getAlgorithmVersionNumber(), group.getAlgorithm().getVersion());
		assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
		assertEquals(productContents.getGroupName(), group.getName());
		assertEquals(productContents.getProductVersionNumber(), 0);
		
		
		List<FileContents> files = productContents.getFileContents();
		assertEquals(1, files.size());
		FileContents fileContents = files.get(0);

		Metadata extractedMetadata = fileContents.getMetadata();
		compareMetadata(previousMetadata, extractedMetadata);
		
		File assembled = new File(assemblyFolder.getAbsolutePath() + "/" +
				previousMetadata.getFile().getName());
			PartAssembler.assemble(fileContents.getExtractedFile(), assembled);
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled), Hashing.hash(testFile)));
		
		assertEquals(assembled.getParentFile().getAbsolutePath(), assemblyFolder.getAbsolutePath());
		assertEquals(1, assemblyFolder.listFiles().length);
		
		shutdown();
	}
	
	private static void testSmallTree(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;
		
		//setup
		String treeName = "smallTree";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName +
				"(" + indexWorkers + ", " + productWorkers + ")");
		
		//set tracked paths 
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		
		//more complicated now: folder/file names indicate tracked status
		//t=tracked, u=untracked, _r=set it explicitly as a rule for the tracking group
		//t files go from 1 to 15, u files go from 1 to 10
		//all files have the same hash
		File tracked_topfolder_r = new File(inputFolder, "tracked_topfolder_r");
		File tracked_subfolder1 = new File(tracked_topfolder_r, "tracked_subfolder1");
		File redundant_tracked1_subfolder_1_r = new File(tracked_subfolder1, "redundant_tracked1_subfolder_1_r");
		File u1_r = new File(tracked_subfolder1, "u1_r.txt");
		File u2_r = new File(tracked_subfolder1, "tracked_subfolder2/u2_r.txt");
		File untracked1_r = new File(tracked_subfolder1, "untracked1_r");
		File redundant_untracked1_subfolder_1_r = new File(untracked1_r, "redundant_untracked1_subfolder_1_r");
		File tracked_subfolder2_r = new File(untracked1_r, "tracked_subfolder2_r");
		File u9_r = new File(tracked_subfolder2_r, "u9_r.txt");
		File t10_r = new File(untracked1_r, "t10_r.txt");
		File tracked_subfolder3_r = new File(untracked1_r, "untracked_subfolder1/tracked_subfolder3_r");
		File u8_r = new File(tracked_subfolder3_r, "u8_r.txt");
		
		//make sure these all exist first:
		assertTrue(tracked_topfolder_r.isDirectory());
		assertTrue(tracked_subfolder1.isDirectory());
		assertTrue(redundant_tracked1_subfolder_1_r.isDirectory());
		assertTrue(u1_r.exists());
		assertTrue(u2_r.exists());
		assertTrue(untracked1_r.isDirectory());
		assertTrue(redundant_untracked1_subfolder_1_r.isDirectory());
		assertTrue(tracked_subfolder2_r.isDirectory());
		assertTrue(u9_r.exists());
		assertTrue(t10_r.exists());
		assertTrue(tracked_subfolder3_r.isDirectory());
		assertTrue(u8_r.exists());
		
		//add the rules
		group.addTrackedPath(tracked_topfolder_r);
		group.addTrackedPath(redundant_tracked1_subfolder_1_r);
		group.addTrackedPath(tracked_subfolder2_r);
		group.addTrackedPath(t10_r);
		group.addTrackedPath(tracked_subfolder3_r);
		
		group.addUntrackedPath(u1_r);
		group.addUntrackedPath(u2_r);
		group.addUntrackedPath(untracked1_r);
		group.addUntrackedPath(redundant_untracked1_subfolder_1_r);
		group.addUntrackedPath(u9_r);
		group.addUntrackedPath(u8_r);
		
		group.setProductStagingFolder(outputFolder);
		
		//specify the test file (all files are the same)
		File testFile = u1_r;
		
		runJob(group, indexWorkers, productWorkers);
		
		//verify all metadata is correct
		try {
			Files.walk(inputFolder.toPath())
			.filter(Files::isRegularFile)
			.forEach((f) ->
				{
					File file = f.toFile();
					
					if (!file.getParentFile().getName().equals(Constants.INDEX_FOLDER_NAME))
					{
						System.out.println("Walked: " + file.getAbsolutePath());
						Metadata previousMetadata = Database.getFileMetadata(file, group);
						if (file.getName().startsWith("u"))
						{
							assertNull(previousMetadata);
						}
						else
						{
							compareMetadataFile(file, previousMetadata);
							assertFalse(previousMetadata.isMetadataUpdate());
						}
					}
				}
			);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		
		for (File productFile: outputFolder.listFiles())
		{
			//read the file, make sure the fields are all the same
			ProductReader reader = new ProductReader(group.getProductFactory());
			reader.setExtractionFolder(extractionFolder);
			ProductContents productContents = reader.extractAll(productFile);
	
			assertEquals(productContents.getAlgorithmName(), group.getAlgorithm().getName());
			assertEquals(productContents.getAlgorithmVersionNumber(), group.getAlgorithm().getVersion());
			assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
			assertEquals(productContents.getGroupName(), group.getName());
			assertEquals(productContents.getProductVersionNumber(), 0);
			
			
			List<FileContents> files = productContents.getFileContents();
			for (FileContents fc:files)
			{
				Metadata extractedMetadata = fc.getMetadata();
				
				//verify all file contents are correct
				compareMetadata(Database.getFileMetadata(extractedMetadata.getFile(), group), extractedMetadata);
				
				//extract file
				File assembled = new File(assemblyFolder.getAbsolutePath() + "/" +
					extractedMetadata.getFile().getName());
				PartAssembler.assemble(fc.getExtractedFile(), assembled);
				assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled), Hashing.hash(testFile)));
			
				assertEquals(assembled.getParentFile().getAbsolutePath(), assemblyFolder.getAbsolutePath());
			}
		}

		assertEquals(15, assemblyFolder.listFiles().length);
		
		shutdown();
	}
	
	private static void testBigFile(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;
		
		//setup
		String treeName = "bigFile";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName +
				"(" + indexWorkers + ", " + productWorkers + ")");
		
		//set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setProductStagingFolder(outputFolder);
		
		//specify the original single test file
		File testFile = inputFolder.listFiles()[0];
		
		runJob(group, indexWorkers, productWorkers);
		
		//get the metadata of our single test file now
		//that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, group);
		compareMetadataFile(testFile, previousMetadata);
		assertFalse(previousMetadata.isMetadataUpdate());
		
		//see what we got:
		//should be only one file
		assertTrue(outputFolder.listFiles().length > 1);
		
		//extract from all files in the output folder
		ProductReader reader = new ProductReader(group.getProductFactory());
		reader.setExtractionFolder(extractionFolder);
		File extractedFolder = null;
		for (File productFile : outputFolder.listFiles())
		{
			//read the file, make sure the fields are all the same
			ProductContents productContents = reader.extractAll(productFile);
			//System.out.println(productContents.toString());
			assertEquals(productContents.getAlgorithmName(), group.getAlgorithm().getName());
			assertEquals(productContents.getAlgorithmVersionNumber(), group.getAlgorithm().getVersion());
			assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
			assertEquals(productContents.getGroupName(), group.getName());
			assertEquals(productContents.getProductVersionNumber(), 0);
			
			List<FileContents> files = productContents.getFileContents();
			assertEquals(1, files.size());
			FileContents fileContents = files.get(0);
	
			Metadata extractedMetadata = fileContents.getMetadata();
			compareMetadata(previousMetadata, extractedMetadata);
			
			if (extractedFolder == null)
				extractedFolder = fileContents.getExtractedFile();
		}
		
		//assemble all part files into the specified extracted filename
		File assembled = new File(assemblyFolder.getAbsolutePath() + "/" +
				previousMetadata.getFile().getName());
			PartAssembler.assemble(extractedFolder, assembled);
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled), Hashing.hash(testFile)));
		
		assertEquals(assembled.getParentFile().getAbsolutePath(), assemblyFolder.getAbsolutePath());
		assertEquals(1, assemblyFolder.listFiles().length);
		
		shutdown();
	}
	
	private static void testBigTree(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;
		
		//setup
		String treeName = "bigTree";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName +
				"(" + indexWorkers + ", " + productWorkers + ")");
		
		//set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setProductStagingFolder(outputFolder);
		
		//specify the original single test file
		File testFile = inputFolder.listFiles()[0];
		
		runJob(group, indexWorkers, productWorkers);
		
		//get the metadata of our single test file now
		//that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, group);
		compareMetadataFile(testFile, previousMetadata);
		assertFalse(previousMetadata.isMetadataUpdate());
		
		//see what we got:
		//should be only one file
		assertEquals(1, outputFolder.listFiles().length);
		
		File productFile = outputFolder.listFiles()[0];
		
		//read the file, make sure the fields are all the same
		ProductReader reader = new ProductReader(group.getProductFactory());
		reader.setExtractionFolder(extractionFolder);
		ProductContents productContents = reader.extractAll(productFile);
		//System.out.println(productContents.toString());
		assertEquals(productContents.getAlgorithmName(), group.getAlgorithm().getName());
		assertEquals(productContents.getAlgorithmVersionNumber(), group.getAlgorithm().getVersion());
		assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
		assertEquals(productContents.getGroupName(), group.getName());
		assertEquals(productContents.getProductVersionNumber(), 0);
		
		
		List<FileContents> files = productContents.getFileContents();
		assertEquals(1, files.size());
		FileContents fileContents = files.get(0);

		Metadata extractedMetadata = fileContents.getMetadata();
		compareMetadata(previousMetadata, extractedMetadata);

		assertFalse(extractedMetadata.isMetadataUpdate());
		
		File assembled = new File(assemblyFolder.getAbsolutePath() + "/" +
				extractedMetadata.getFile().getName());
		PartAssembler.assemble(extractionFolder, assembled);
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled), Hashing.hash(testFile)));
		
		assertEquals(assembled.getParentFile().getAbsolutePath(), assemblyFolder.getAbsolutePath());
		assertEquals(1, assemblyFolder.listFiles().length);
		
		shutdown();
	}
	
	private static void reset(String treeName)
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		clearFolder(assemblyFolder);
		TestFileTrees.reset(homeFolder, treeName);
	}
	
	private static void clearFolder(File folder)
	{
		if (folder != null)
		{
			FileSystemUtil.deleteDir(folder);
			folder.mkdir();
		}
	}
	
	private static void compareMetadata(Metadata m1, Metadata m2)
	{
		assertEquals(m1.getFile().getAbsolutePath(), m2.getFile().getAbsolutePath());
		assertArrayEquals(m1.getFileHash(), m2.getFileHash());
		assertEquals(m1.getPath(), m2.getPath());
		assertEquals(m1.getPermissions(), m2.getPermissions());
		assertEquals(m1.getDateCreated(), m2.getDateCreated());
		assertEquals(m1.getDateModified(), m2.getDateModified());
		assertEquals(m1.getProductUUID(), m2.getProductUUID());
		assertEquals(m1.isMetadataUpdate(), m2.isMetadataUpdate());
	}
	
	private static void compareMetadataFile(File f, Metadata m)
	{
		System.err.println(f + ", " + m.getFile());
		assertEquals(f.getAbsolutePath(), m.getFile().getAbsolutePath());
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(f), m.getFileHash()));
		assertEquals(f.getAbsolutePath(), m.getPath());
		assertEquals(FileSystemUtil.getNumericFilePermissions(f), m.getPermissions());
		assertEquals(FileSystemUtil.getDateCreated(f), m.getDateCreated());
		assertEquals(FileSystemUtil.getDateModified(f), m.getDateModified());
	}
	
	private static void runJob(TrackingGroup group, int indexWorkers, int productWorkers)
	{
		//create a backup job
		BackupJob job = new BackupJob(group, indexWorkers, productWorkers);
		jobs.add(job);
		Thread jobThread = new Thread(job);
		jobThread.start();
		
		//wait for the job to finish
		while (!job.isFinished())
		{
			try {
				//job.printState();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				//job.printState();
			}
		}
		
		//artificially save off our database here
		Database.save();
	}
}
