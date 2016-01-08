package testing.highlevel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import api.ConversionAPI;
import data.Metadata;
import data.FileType;
import data.TrackingGroup;
import database.Database;
import product.ConversionJob;
import product.FileContents;
import product.ProductContents;
import product.ProductExtractor;
import system.SystemManager;
import testing.Comparisons;
import testing.TestFileTrees;
import util.ByteConversion;
import util.Constants;
import util.FileSystemUtil;
import util.Hashing;

public class ProductIOTest
{
	static File homeFolder = new File("testing/highlevel/");
	private static File outputFolder = new File("testing/output/");
	private static File extractionFolder = new File("testing/extraction/");
	private static File hashdbFile = new File("testing/resources/hashdb.db");
	private static boolean shutdownCalled = true;
	private static ArrayList<ConversionJob> jobs = new ArrayList<ConversionJob>();

	private static void shutdown()
	{
		SystemManager.shutdown();
		for (ConversionJob job : jobs)
			job.shutdown();
		jobs = new ArrayList<ConversionJob>();
		shutdownCalled = true;
	}

	public static void testEmptyFolder(TrackingGroup group, int threads)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

		// setup
		String treeName = "noFiles";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName + "(threads=" + threads + ")");

		//set temp hashdb location
		group.setHashDBFile(hashdbFile);
		
		// set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setStaticOutputFolder(outputFolder);

		runJob(group, threads);

		// see what we got:
		// should be 1, just an empty folder was added
		assertEquals(1, outputFolder.listFiles().length);

		// should get the one folder
		ProductExtractor extractor = new ProductExtractor(group, outputFolder);
		File productFile = outputFolder.listFiles()[0];
		try
		{
			ProductContents productContents = extractor.viewAll(productFile);
			assertEquals(1, productContents.getFileContents().size());
			
			FileContents fileContents = productContents.getFileContents().get(0);
			assertEquals(FileType.k_folder, fileContents.getMetadata().getType());
			assertEquals(inputFolder.getName(), fileContents.getMetadata().getFile().getName());
			
			extractor.extractAllFromProduct(productFile, extractionFolder);
			
			File expected = Comparisons.getExpectedExtractionFile(
							inputFolder.getParentFile(), extractionFolder, inputFolder, true);
			assertTrue(expected.exists());
			assertTrue(expected.isDirectory());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		assertEquals(1, extractionFolder.listFiles().length);

		shutdown();
	}
/**
	public static void testSmallFile(TrackingGroup group, int threads)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

		// setup
		String treeName = "smallFile";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName + "(threads=" + threads + ")");

		//set temp hashdb location
		group.setHashDBFile(hashdbFile);
		
		// set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setProductStagingFolder(outputFolder);
		group.setExtractionFolder(extractionFolder);
		
		//set temp hashdb location
		group.setHashDBFile(hashdbFile);

		// specify the original single test file
		File testFile = inputFolder.listFiles()[0];
		if (group.usesAbsolutePaths())
			testFile = testFile.getAbsoluteFile();

		runJob(group, threads);

		// get the metadata of our single test file now
		// that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, group);
		Comparisons.compareMetadataFile(testFile, previousMetadata);

		// see what we got:
		// should be only one file
		assertEquals(1, outputFolder.listFiles().length);

		File productFile = outputFolder.listFiles()[0];

		// read the file, make sure the fields are all the same
		ProductExtractor reader = new ProductExtractor(group, outputFolder);
		ProductContents productContents = reader.viewAll(productFile);
		// System.out.println(productContents.toString());
		assertEquals(productContents.getAlgorithmName(), group.getAlgorithm().getName());
		assertEquals(productContents.getAlgorithmVersionNumber(),
						group.getAlgorithm().getVersion());
		assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
		assertEquals(productContents.getGroupName(), group.getName());
		assertEquals(productContents.getProductVersionNumber(), 0);

		List<FileContents> files = productContents.getFileContents();
		assertEquals(1, files.size());
		FileContents fileContents = files.get(0);

		Metadata extractedMetadata = fileContents.getMetadata();
		Comparisons.compareMetadata(previousMetadata, extractedMetadata);

		assertTrue(reader.extractAllFromProductFolder(outputFolder));
		
		//temporary thing TODO make this general?
		File assembled = new File("/home/tom/git/Imagine/testing/extraction/home/"
						+ "tom/git/Imagine/testing/highlevel/smallFile/message.txt");

		assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled),
						Hashing.hash(testFile)));

		//assertEquals(1, assemblyFolder.listFiles().length);

		shutdown();
	}*/
/**
	public static void testSmallTree(TrackingGroup group, int threads)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

		// setup
		String treeName = "smallTree";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName + "(threads=" + threads + ")");

		//set temp hashdb location
		group.setHashDBFile(hashdbFile);
				
		// set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();

		// more complicated one: folder/file names indicate tracked status
		// t=tracked, u=untracked, _r=set it explicitly as a rule for the
		// tracking group
		// t files go from 1 to 15, u files go from 1 to 10
		// all files have the same hash
		File tracked_topfolder_r = new File(inputFolder, "tracked_topfolder_r");
		File tracked_subfolder1 = new File(tracked_topfolder_r, "tracked_subfolder1");
		File redundant_tracked1_subfolder_1_r =
						new File(tracked_subfolder1, "redundant_tracked1_subfolder_1_r");
		File u1_r = new File(tracked_subfolder1, "u1_r.txt");
		File u2_r = new File(tracked_subfolder1, "tracked_subfolder2/u2_r.txt");
		File untracked1_r = new File(tracked_subfolder1, "untracked1_r");
		File redundant_untracked1_subfolder_1_r =
						new File(untracked1_r, "redundant_untracked1_subfolder_1_r");
		File tracked_subfolder2_r = new File(untracked1_r, "tracked_subfolder2_r");
		File u9_r = new File(tracked_subfolder2_r, "u9_r.txt");
		File t10_r = new File(untracked1_r, "t10_r.txt");
		File tracked_subfolder3_r = new File(untracked1_r,
						"untracked_subfolder1/tracked_subfolder3_r");
		File u8_r = new File(tracked_subfolder3_r, "u8_r.txt");

		// make sure these all exist first:
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

		// add the rules
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

		// specify the test file (all files are the same)
		File testFile = u1_r;

		runJob(group, threads);

		// verify all metadata is correct
		try
		{
			Files.walk(inputFolder.toPath()).filter(Files::isRegularFile).forEach((f) ->
			{
				File file = f.toFile();

				if (!file.getParentFile().getName().equals(Constants.INDEX_FOLDER_NAME))
				{
					//System.out.println("Walked: " + file.getAbsolutePath());
					Metadata previousMetadata = Database.getFileMetadata(file, group);
					if (file.getName().startsWith("u"))
					{
						assertNull(previousMetadata);
					}
					else
					{
						Comparisons.compareMetadataFile(file, previousMetadata);
					}
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}

		for (File productFile : outputFolder.listFiles())
		{
			// read the file, make sure the fields are all the same
			ProductExtractor reader =
							new ProductExtractor(group, outputFolder);
			group.setExtractionFolder(extractionFolder);
			ProductContents productContents = reader.viewAll(productFile);

			assertEquals(productContents.getAlgorithmName(),
							group.getAlgorithm().getName());
			assertEquals(productContents.getAlgorithmVersionNumber(),
							group.getAlgorithm().getVersion());
			assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
			assertEquals(productContents.getGroupName(), group.getName());
			assertEquals(productContents.getProductVersionNumber(), 0);

			List<FileContents> files = productContents.getFileContents();

			for (FileContents fc : files)
			{
				Metadata extractedMetadata = fc.getMetadata();

				// verify all file contents are correct
				Comparisons.compareMetadata(Database.getFileMetadata(extractedMetadata.getFile(),
								group), extractedMetadata);

				// extract file
				File assembled = new File(assemblyFolder.getAbsolutePath() + "/"
								+ extractedMetadata.getFile().getName());
				//FileAssembler.assemble(fc.getExtractedFile(), assembled);
				assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled),
								Hashing.hash(testFile)));

				assertEquals(assembled.getParentFile().getAbsolutePath(),
								assemblyFolder.getAbsolutePath());
			}
		}

		assertEquals(15, assemblyFolder.listFiles().length);

		shutdown();
	}*/

	public static void testBigFile(TrackingGroup group, int threads)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

		// setup
		String treeName = "bigFile";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName + "(threads=" + threads + ")");
		
		//set temp hashdb location
		group.setHashDBFile(hashdbFile);
				
		// set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();
		group.addTrackedPath(inputFolder);
		group.setStaticOutputFolder(outputFolder);

		// specify the original single test file
		File testFile = inputFolder.listFiles()[0];
		if (group.usesAbsolutePaths())
			testFile = testFile.getAbsoluteFile();

		runJob(group, threads);

		// get the metadata of our single test file now
		// that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, group);
		Comparisons.compareMetadataFile(testFile, previousMetadata);

		// see what we got:
		// should be only one product file
		assertTrue(outputFolder.listFiles().length > 1);

		// extract from all files in the output folder
		ProductExtractor reader = new ProductExtractor(group, outputFolder);
		for (File productFile : outputFolder.listFiles())
		{
			// read the file, make sure the fields are all the same
			ProductContents productContents = null;
			try
			{
				productContents = reader.viewAll(productFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail();
			}
			// System.out.println(productContents.toString());
			assertEquals(productContents.getAlgorithmName(),
							group.getAlgorithm().getName());
			assertEquals(productContents.getAlgorithmVersionNumber(),
							group.getAlgorithm().getVersion());
			assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
			assertEquals(productContents.getGroupName(), group.getName());
			assertEquals(productContents.getProductVersionNumber(), 0);

			List<FileContents> files = productContents.getFileContents();
			
			//should be only one file in each product
			assertEquals(1, files.size());
			FileContents fileContents = files.get(0);

			Metadata extractedMetadata = fileContents.getMetadata();
			Comparisons.compareMetadata(previousMetadata, extractedMetadata);

			//if (extractedFolder == null)
			//	extractedFolder = fileContents.getExtractedFile();
		}

		// assemble all part files into the specified extracted filename
		assertTrue(reader.extractAllFromProductFolder(outputFolder, extractionFolder));
		
		Comparisons.compareExtractedFileStructure(inputFolder,
						extractionFolder, group.usesAbsolutePaths());


		shutdown();
	}

	/**
	 * Focuses on verifying that every file is correct and that the files are
	 * assembled into folders which mirror the tree
	 * 
	 * @param group
	 * @param indexWorkers
	 * @param threads
	 *//**
	public static void testBigTree(TrackingGroup group, int threads)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

		// setup
		String treeName = "bigTree";
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		System.out.println("Running test for tree: " + treeName + "(threads=" + threads + ")");
		
		//set temp hashdb location
		group.setHashDBFile(hashdbFile);
		
		// set tracked paths
		group.clearTrackedPaths();
		group.clearUntrackedPaths();

		// take in the whole folder
		File eclipse_installer = new File(inputFolder, "eclipse-installer");

		// make sure these all exist first:
		assertTrue(eclipse_installer.isDirectory());

		// add the rules
		group.addTrackedPath(eclipse_installer);

		group.setProductStagingFolder(outputFolder);

		// collect paths and hashes for all content before io
		int[] oldFileCount = new int[1];
		HashMap<String, byte[]> hashes = new HashMap<String, byte[]>();
		try
		{
			Files.walk(eclipse_installer.toPath()).filter(Files::isRegularFile)
				.forEach((f) ->
				{
					File file = f.toFile();

					if (!file.getParentFile().getName()
									.equals(Constants.INDEX_FOLDER_NAME))
					{
						String relativized = inputFolder.toURI()
										.relativize(file.toURI()).getPath();
						if (relativized.endsWith(".lock"))
							System.err.println("Relativized: " + relativized);
						hashes.put(relativized, Hashing.hash(file));
						oldFileCount[0] += 1;
					}
				});
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}

		runJob(group, threads);

		// verify all metadata is correct
		try
		{
			Files.walk(inputFolder.toPath()).filter(Files::isRegularFile).forEach((f) ->
			{
				File file = f.toFile();

				if (!file.getParentFile().getName().equals(Constants.INDEX_FOLDER_NAME))
				{
					//if (file.getAbsolutePath().endsWith(".lock"))
					//	System.out.println("Walked: " + file.getAbsolutePath());
					Metadata previousMetadata = Database.getFileMetadata(file, group);
					Comparisons.compareMetadataFile(file, previousMetadata);
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}

		HashMap<File, File> extractionFolders = new HashMap<File, File>();

		for (File productFile : outputFolder.listFiles())
		{
			// read the file, make sure the fields are all the same
			ProductExtractor reader =
							new ProductExtractor(group, outputFolder);
			group.setExtractionFolder(extractionFolder);
			ProductContents productContents = reader.viewAll(productFile);
			System.out.println(productFile.getPath());
			System.out.println(productContents.toString());

			assertEquals(productContents.getAlgorithmName(),
							group.getAlgorithm().getName());
			assertEquals(productContents.getAlgorithmVersionNumber(),
							group.getAlgorithm().getVersion());
			assertEquals(group.getKey().getName(), productContents.getGroupKeyName());
			assertEquals(productContents.getGroupName(), group.getName());
			assertEquals(productContents.getProductVersionNumber(), 0);

			List<FileContents> files = productContents.getFileContents();
			for (FileContents fc : files)
			{
				Metadata extractedMetadata = fc.getMetadata();

				// verify all file contents are correct
				Comparisons.compareMetadata(Database.getFileMetadata(extractedMetadata.getFile(),
								group), extractedMetadata);
			}
		}

		// assemble all files
		int newFileCount = 0;
		Set<String> extractedFileSet = new HashSet<String>();
		for (File partFolder : extractionFolders.keySet())
		{
			File assembleTo = extractionFolders.get(partFolder);
			if (!assembleTo.getParentFile().exists())
				assembleTo.getParentFile().mkdirs();
			//FileAssembler.assemble(partFolder, assembleTo);

			// make sure the files are the same
			String relativized = assemblyFolder.toURI().relativize(assembleTo.toURI())
							.getPath();
			extractedFileSet.add(relativized);
			// TODO shouldn't be recording absolute paths when crawling,
			// should be relativizing there based on root folder
			byte[] oldHash = hashes.get(relativized);
			byte[] newHash = Hashing.hash(assembleTo);
			assertArrayEquals(oldHash, newHash);
			++newFileCount;
		}

		Set<String> originalFileSet = new HashSet<String>(hashes.keySet());
		Set<String> skippedFileSet = new HashSet<String>(originalFileSet);
		skippedFileSet.removeAll(extractedFileSet);

		if (!skippedFileSet.isEmpty())
		{
			System.err.println("Some files were skipped: ");
			for (String path : skippedFileSet)
				System.err.println(path);
			fail();
		}

		assertEquals(oldFileCount[0], newFileCount);

		shutdown();
	}*/

	private static void reset(String treeName)
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		try
		{
			Files.delete(hashdbFile.toPath());
		}
		catch (IOException e){}
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

	

	private static void runJob(TrackingGroup group, int threads)
	{
		// create a conversion job
		ConversionJob job = ConversionAPI.runConversion(group, threads);
		jobs.add(job);

		// wait for the job to finish
		while (!job.isFinished())
		{
			try
			{
				// job.printState();
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				// job.printState();
			}
		}

		// artificially save off our database here
		Database.save();
	}
}
