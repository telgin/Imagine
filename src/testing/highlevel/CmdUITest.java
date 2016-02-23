package testing.highlevel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Test;

import product.ConversionJob;
import system.Imagine;
import system.SystemManager;
import testing.Comparisons;
import testing.TestFileTrees;
import util.FileSystemUtil;
import util.myUtilities;

/**
 * Currently, the command line interface supports some common operations for
	viewing, embedding, and extracting data. However, at this time, specific
	configuration edits (editing profiles or algorithms) must be done in the GUI.
	
	Command Syntax:
	imagine --open -p <profile> -i <file> [-o <folder>]
	imagine --open -a <algorithm> -i <file> [-o <folder>] [-k <keyfile>]
	
	imagine --embed -p <profile> [-o <folder>]
	imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]
	
	imagine --extract -p <profile> -i <file/folder> [-o <folder>]
	imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]
	
	--open     
	    open an archive and selectively extract its contents
	--embed    
	    embed data into a supported format
	--extract  
	    extract all data from an archive file or folder or multiple archives
	
	-a         algorithm preset name
	-p         profile name
	-i         input file or folder
	-o         output folder
	-k         key file (optional)

 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */

public class CmdUITest
{
	static File homeFolder = new File("testing/highlevel/");
	private static File outputFolder = new File("testing/output/");
	private static File extractionFolder = new File("testing/extraction/");
	private static File imagesFolder = new File("testing/input_images/");
	private static File keyFile = new File("testing/keys/key1.txt");
	private static File reportFolder = new File("testing/reports/");
	private static File reportFile = new File(reportFolder, "report.txt");
	private static File bigTreeFileList = new File("testing/file_lists/bigTree.txt");
	private static ArrayList<ConversionJob> jobs = new ArrayList<ConversionJob>();
	
	
	private static final String EMPTY_FOLDER = "emptyFolder";
	private static final String SMALL_FILE = "smallFile";
	private static final String SMALL_TREE = "smallTree";
	private static final String BIG_FILE = "bigFile";
	private static final String BIG_TREE = "bigTree";

	
	//----------------------------------------
	//Image Tests
	//----------------------------------------
	
	/**
	 * Simple test without key
	 */
	public void image_1(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//test all for start
	//@Test
	public void image_1_et() { image_1(EMPTY_FOLDER); }
	
	//@Test
	public void image_1_sf() { image_1(SMALL_FILE); }
	
	@Test
	public void image_1_st() { image_1(SMALL_TREE); }
	
	//@Test
	public void image_1_bf() { image_1(BIG_FILE); }
	
	//@Test
	public void image_1_bt() { image_1(BIG_TREE); }
	
	
	/**
	 * Simple test with key file
	 */
	public void image_2(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	@Test
	public void image_2_bf() { image_2(BIG_FILE); }
	
	//@Test
	public void image_2_bt() { image_2(BIG_TREE); }
	
	/**
	 * Test for report output
	 * @throws IOException 
	 */
	public void image_3(String testFileName) throws IOException
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath(),
						"-r", reportFile.getPath()};
		Imagine.run(embed);

		assertTrue(reportFile.exists());
		
		int expectedLines = FileSystemUtil.countEligableFiles(inputFolder);
		int actualLines = myUtilities.readListFromFile(reportFile).size();
		
		assertEquals(expectedLines, actualLines);
	}
	
	//@Test
	public void image_3_bt() throws IOException { image_3(BIG_TREE); }
	
	/**
	 * Input file list
	 */
	public void image_4(String testFileName, File fileList)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-I", fileList.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, true);
	}
	
	//@Test
	public void image_4_bt() throws IOException { image_4(BIG_TREE, bigTreeFileList); }
	
	/**
	 * Parameter test (image size)
	 * @throws IOException 
	 */
	public void image_5(String testFileName) throws IOException
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath(),
						"-P", algorithms.image.Definition.WIDTH_PARAM + "=500",
						"-P", algorithms.image.Definition.HEIGHT_PARAM + "=500"};
		Imagine.run(embed);
		
		
		BufferedImage img = ImageIO.read(outputFolder.listFiles()[0]);
		assertEquals(500, img.getWidth());
		assertEquals(500, img.getHeight());
	}
	
	//@Test
	public void image_5_st() throws IOException { image_5(SMALL_TREE); }
	
	/**
	 * Multiple input flags
	 */
	public void image_6()
	{
		//setup
		File inputFolder = setup(SMALL_TREE);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/u7.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/tracked_subfolder3_r/u8_r.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/tracked_subfolder3_r/t12.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/tracked_subfolder3_r/t11.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/u5.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/untracked_subfolder1/u6.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/redundant_untracked1_subfolder_1_r/u10.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/redundant_untracked1_subfolder_1_r/utracked_empty_subfolder1",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/u3.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/u4.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/t10_r.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/tracked_subfolder2_r/t14.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/tracked_subfolder2_r/u9_r.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/untracked1_r/tracked_subfolder2_r/t13.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/t7.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/tracked_subfolder2/t9.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/tracked_subfolder2/t8.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/tracked_subfolder2/u2_r.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/t5.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/u1_r.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/redundant_tracked1_subfolder_1_r/t15.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/redundant_tracked1_subfolder_1_r/tracked_empty_subfolder1",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/tracked_subfolder1/t6.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/t3.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/t4.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/t2.txt",
						"-i", "/home/tom/git/Imagine/testing/highlevel/smallTree/tracked_topfolder_r/t1.txt",
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, true);
	}
	
	//@Test
	public void image_6_st() throws IOException { image_6(); }
	
	/**
	 * Wrong key file
	 */
	public void image_7(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath(),
						"-k", imagesFolder.list()[0]};
		Imagine.run(extract);
		
		//verify nothing exists
		assertEquals(0, extractionFolder.listFiles().length);
	}
	
	//@Test
	public void image_7_bt() { image_7(BIG_TREE); }
	
	//----------------------------------------
	//Image Overlay Tests
	//----------------------------------------
	/**
	 * Simple test without key (25)
	 */
	public void image_overlay_1(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_light", 
						"-o", extractionFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//test all for start
	//@Test
	public void image_overlay_1_et() { image_overlay_1(EMPTY_FOLDER); }
	
	//@Test
	public void image_overlay_1_sf() { image_overlay_1(SMALL_FILE); }
	
	//@Test
	public void image_overlay_1_st() { image_overlay_1(SMALL_TREE); }
	
	//@Test
	public void image_overlay_1_bf() { image_overlay_1(BIG_FILE); }
	
	//@Test
	public void image_overlay_1_bt() { image_overlay_1(BIG_TREE); }
	
	
	/**
	 * Simple test with key file (25)
	 */
	public void image_overlay_2(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_light", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void image_overlay_2_bf() { image_overlay_2(BIG_FILE); }
	
	//@Test
	public void image_overlay_2_bt() { image_overlay_2(BIG_TREE); }
	
	/**
	 * Simple test with key file (50)
	 */
	public void image_overlay_3(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_heavy",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_heavy", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void image_overlay_3_bf() { image_overlay_3(BIG_FILE); }
	
	//@Test
	public void image_overlay_3_bt() { image_overlay_3(BIG_TREE); }
	
	/**
	 * quotes on parameters
	 */
	public void image_overlay_4(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-P", "\"" + algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath() + "\""};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_light", 
						"-o", extractionFolder.getPath(),
						"-P", "\"" + algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath() + "\""};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void image_overlay_4_st() { image_overlay_4(SMALL_TREE); }
	
	/**
	 * no input images available
	 */
	public void image_overlay_5(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//remove images
		clearFolder(imagesFolder);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath()};
		Imagine.run(embed);
		
		//verify nothing exists
		assertEquals(0, outputFolder.listFiles().length);
	}
	
	//@Test
	public void image_overlay_5_st() { image_overlay_5(SMALL_TREE); }
	
	/**
	 * image folder move mode
	 */
	public void image_overlay_6(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_CONSUMPTION_MODE_PARAM + "=" + 
										"move"};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_light", 
						"-o", extractionFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
		
		//make sure there's an image that's done
		File doneFolder = new File(imagesFolder, "done");
		
		assertTrue(doneFolder.exists());
		
		assertEquals(1, doneFolder.listFiles().length);
	}
	
	//@Test
	public void image_overlay_6_st() { image_overlay_6(SMALL_TREE); }
	
	/**
	 * image folder delete mode
	 */
	public void image_overlay_7(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_light",
						"-o", outputFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_FOLDER_PARAM + "=" + imagesFolder.getPath(),
						"-P", algorithms.imageoverlay.Definition.IMAGE_CONSUMPTION_MODE_PARAM + "=" + 
										"delete"};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_light", 
						"-o", extractionFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
		
		//make sure there are not files in the input image folder
		assertEquals(0, imagesFolder.listFiles().length);
	}
	
	//@Test
	public void image_overlay_7_st() { image_overlay_7(SMALL_TREE); }

	//----------------------------------------
	//Text Tests
	//----------------------------------------
	
	/**
	 * Simple test without key
	 */
	public void text_1(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "text_default",
						"-o", outputFolder.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "text_default", 
						"-o", extractionFolder.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//test all for start
	//@Test
	public void text_1_et() { text_1(EMPTY_FOLDER); }
	
	//@Test
	public void text_1_sf() { text_1(SMALL_FILE); }
	
	//@Test
	public void text_1_st() { text_1(SMALL_TREE); }
	
	//@Test
	public void text_1_bf() { text_1(BIG_FILE); }
	
	//@Test
	public void text_1_bt() { text_1(BIG_TREE); }
	
	
	/**
	 * Simple test with key file
	 */
	public void text_2(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "text_default",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "text_default", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void text_2_bf() { text_2(BIG_FILE); }
	
	//@Test
	public void text_2_bt() { text_2(BIG_TREE); }
		

	//----------------------------------------
	//Manual Tests
	//----------------------------------------
	
	/**
	 * manual password
	 */
	public void text_3(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "text_default",
						"-o", outputFolder.getPath(),
						"-p"};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "text_default", 
						"-o", extractionFolder.getPath(),
						"-p"};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void text_3_bt() { text_3(BIG_TREE); }

	/**
	 * manual image folder (50)
	 */
	public void image_overlay_8(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_overlay_heavy",
						"-o", outputFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(embed);
		
		//extract
		String[] extract = new String[]{"--extract",
						"-i", outputFolder.getPath(),
						"-a", "image_overlay_heavy", 
						"-o", extractionFolder.getPath(),
						"-k", keyFile.getPath()};
		Imagine.run(extract);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	//@Test
	public void image_overlay_8_bt() { image_overlay_8(BIG_TREE); }
	
	/**
	 * manual open
	 */
	public void image_6(String testFileName)
	{
		//setup
		File inputFolder = setup(testFileName);
		
		//embed
		String[] embed = new String[]{"--embed",
						"-i", inputFolder.getPath(),
						"-a", "image_default",
						"-o", outputFolder.getPath()};
		Imagine.run(embed);
		
		//open
		String[] open = new String[]{"--open",
						"-i", outputFolder.listFiles()[0].getPath(),
						"-a", "image_default", 
						"-o", extractionFolder.getPath()};
		Imagine.run(open);
	}

	//@Test
	public void image_6_bt() { image_6(BIG_TREE); }
	
	//----------------------------------------
	//Support Functions (end test cases)
	//----------------------------------------
	
	private static File setup(String treeName)
	{
		//shutdown from previous run
		if (!SystemManager.isShutdown())
			shutdown();

		// setup
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		
		setupInputImages();
		
		return inputFolder;
	}
	
	private static void setupInputImages()
	{
		File root = TestFileTrees.getRoot(homeFolder, "inputImages");
		reset("inputImages");
		
		for (File imageFile : root.listFiles()[0].listFiles())
		{
			try
			{
				Files.copy(imageFile.toPath(), new File(imagesFolder, imageFile.getName()).toPath());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void shutdown()
	{
		SystemManager.shutdown();
		for (ConversionJob job : jobs)
			job.shutdown();
		jobs = new ArrayList<ConversionJob>();
		
		while (!SystemManager.isShutdown())
		{
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e){}
		}
		
		SystemManager.reset();
	}
	private static void reset(String treeName)
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		clearFolder(imagesFolder);
		clearFolder(reportFolder);
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
}
