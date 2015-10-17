package testing.highlevel;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.Test;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import data.FileKey;
import data.Key;
import data.NullKey;
import data.PartAssembler;
import data.TrackingGroup;
import database.Database;
import hibernate.Metadata;
import product.FileContents;
import product.ProductContents;
import product.ProductReader;
import runner.BackupJob;
import testing.TestFileTrees;
import util.ByteConversion;
import util.FileSystemUtil;
import util.Hashing;

public class ProductIOTest {
	private File homeFolder = new File("testing/highlevel/");
	private File inputFolder = TestFileTrees.getRoot(homeFolder, 1);
	private File outputFolder = new File("testing/output/");
	private File extractionFolder = new File("testing/extraction/");
	private File testFile = new File("testing/highlevel/testFiles1/message.txt");
	

	@Test
	public void testFullPNG() {
		//reset();
		fail("Not yet implemented");
	}
	
	@Test
	public void testTextBlock() {
		reset();
		
		//tracking group setup
		Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("TextBlock");
		String keyName = "testKeyName";
		String groupName = "testGroupName";
		Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));
		
		//should use null Key?
		//key = new FileKey(keyName, groupName, );
		TrackingGroup testGroup = new TrackingGroup(groupName, true, algorithm, key);
		testGroup.addTrackedPath(inputFolder);
		testGroup.setProductStagingFolder(outputFolder);
		
		//create a backup job
		BackupJob job = new BackupJob(testGroup, 1, 1);
		Thread jobThread = new Thread(job);
		jobThread.start();
		
		//wait for the job to finish
		while (!job.isFinished())
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//artificially save off our database here
		Database.save();
		
		//get the metadata of our single test file now
		//that it should have been saved
		Metadata previousMetadata = Database.getFileMetadata(testFile, testGroup);
		assertEquals(previousMetadata.getFile(), testFile);
		assertTrue(ByteConversion.bytesEqual(previousMetadata.getFileHash(), Hashing.hash(testFile)));
		assertEquals(previousMetadata.getPath(), testFile.getAbsolutePath());
		assertEquals(previousMetadata.getPermissions(), FileSystemUtil.getNumericFilePermissions(testFile));
		assertEquals(previousMetadata.getDateCreated(), FileSystemUtil.getDateCreated(testFile));
		assertEquals(previousMetadata.getDateModified(), FileSystemUtil.getDateModified(testFile));
		assertFalse(previousMetadata.isMetadataUpdate());
		
		//see what we got:
		//should be only one file
		assertEquals(1, outputFolder.listFiles().length);
		
		File productFile = outputFolder.listFiles()[0];
		assertTrue(productFile.getName().endsWith(".txt"));
		
		//read the file, make sure the fields are all the same
		ProductReader reader = new ProductReader(testGroup.getProductFactory());
		reader.setExtractionFolder(extractionFolder);
		ProductContents productContents = reader.extractAll(productFile);
		System.out.println(productContents.toString());
		assertEquals(productContents.getAlgorithmName(), algorithm.getName());
		assertEquals(productContents.getAlgorithmVersionNumber(), algorithm.getVersion());
		assertEquals(keyName, productContents.getGroupKeyName());
		assertEquals(productContents.getGroupName(), testGroup.getName());
		assertEquals(productContents.getProductVersionNumber(), 0);
		
		
		List<FileContents> files = productContents.getFileContents();
		assertEquals(files.size(), 1);
		FileContents fileContents = files.get(0);

		Metadata extractedMetadata = fileContents.getMetadata();
		assertEquals(previousMetadata.getFile(), extractedMetadata.getFile());
		assertArrayEquals(previousMetadata.getFileHash(), extractedMetadata.getFileHash());
		assertEquals(previousMetadata.getPath(), extractedMetadata.getPath());
		assertEquals(previousMetadata.getPermissions(), extractedMetadata.getPermissions());
		assertEquals(previousMetadata.getDateCreated(), extractedMetadata.getDateCreated());
		assertEquals(previousMetadata.getDateModified(), extractedMetadata.getDateModified());
		assertEquals(previousMetadata.getProductUUID(), extractedMetadata.getProductUUID());
		assertFalse(extractedMetadata.isMetadataUpdate());
		
		
		File partFile = fileContents.getExtractedFile();
		File assembled = new File(extractionFolder.getAbsolutePath() + "/" +
				extractedMetadata.getFile().getName());
		PartAssembler.assemble(extractionFolder, assembled);
		assertTrue(ByteConversion.bytesEqual(Hashing.hash(assembled), Hashing.hash(testFile)));
		
		assertEquals(assembled.getParentFile(), extractionFolder);
		assertEquals(extractionFolder.listFiles().length, 1);
	}
	
	private void reset()
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		TestFileTrees.reset(homeFolder, 1);
	}
	
	private void clearFolder(File folder)
	{
		FileSystemUtil.deleteDir(folder);
		folder.mkdir();
	}
}
