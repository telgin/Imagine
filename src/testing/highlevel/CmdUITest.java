package testing.highlevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.Test;

import database.Database;
import product.ConversionJob;
import system.Imagine;
import system.SystemManager;
import testing.Comparisons;
import testing.TestFileTrees;
import util.FileSystemUtil;

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
	private static boolean shutdownCalled = false;
	private static ArrayList<ConversionJob> jobs = new ArrayList<ConversionJob>();

	
	//----------------------------------------
	//Basic Tests
	//----------------------------------------
	
	//Image
	
	//@Test(timeout = 60000)
	public void imageBasicEmptyFolder()
	{
		cmdTempGroup("image_basic", "emptyFolder");
	}

	//@Test(timeout = 60000)
	public void imageBasicSmallFile()
	{
		cmdTempGroup("image_basic", "smallFile");
	}
	
	//@Test(timeout = 60000)
	public void imageBasicSmallTree()
	{
		cmdTempGroup("image_basic", "smallTree");
	}
	
	//@Test(timeout = 60000)
	public void imageBasicBigFile()
	{
		cmdTempGroup("image_basic", "bigFile");
	}
	
	//@Test(timeout = 60000)
	public void imageBasicBigTree()
	{
		cmdTempGroup("image_basic", "bigTree");
	}

	//Image Overlay
	
	//@Test(timeout = 60000)
	public void imageOverlayBasicEmptyFolder()
	{
		cmdTempGroup("image_overlay_basic", "emptyFolder");
	}

	//@Test(timeout = 60000)
	public void imageOverlayBasicSmallFile()
	{
		cmdTempGroup("image_overlay_basic", "smallFile");
	}
	
	//@Test(timeout = 60000)
	public void imageOverlayBasicSmallTree()
	{
		cmdTempGroup("image_overlay_basic", "smallTree");
	}
	
	//@Test(timeout = 120000)
	public void imageOverlayBasicBigFile()
	{
		cmdTempGroup("image_overlay_basic", "bigFile");
	}
	
	//@Test//(timeout = 60000)
	public void imageOverlayBasicBigTree()
	{
		cmdTempGroup("image_overlay_basic", "bigTree");
	}
	
	//Text
	
	//@Test(timeout = 60000)
	public void textBasicEmptyFolder()
	{
		cmdTempGroup("text_basic", "emptyFolder");
	}

	//@Test(timeout = 60000)
	public void textBasicSmallFile()
	{
		cmdTempGroup("text_basic", "smallFile");
	}
	
	//@Test(timeout = 60000)
	public void textBasicSmallTree()
	{
		cmdTempGroup("text_basic", "smallTree");
	}
	
	//@Test(timeout = 60000)
	public void textBasicBigFile()
	{
		cmdTempGroup("text_basic", "bigFile");
	}
	
	//@Test(timeout = 60000)
	public void textBasicBigTree()
	{
		cmdTempGroup("text_basic", "bigTree");
	}

	
	//----------------------------------------
	//Trackable Tests
	//----------------------------------------
	
	//Image
	
	//@Test(timeout = 60000)
	public void imageTrackableBigTree()
	{
		cmdTempGroup("test_image_trackable", "bigTree", keyFile.getPath());
	}
	
	//ImageOverlay
	
	//@Test//(timeout = 60000)
	public void imageOverlayTrackableBigTree()
	{
		cmdTempGroup("test_image_overlay_trackable", "bigTree", keyFile.getPath());
	}
		
	//Text
		
	//@Test(timeout = 60000)
	public void textTrackableEmptyFolder()
	{
		cmdTempGroup("test_text_trackable", "emptyFolder", keyFile.getPath());
	}

	//@Test(timeout = 60000)
	public void textTrackableSmallFile()
	{
		cmdTempGroup("test_text_trackable", "smallFile", keyFile.getPath());
	}
	
	//@Test(timeout = 60000)
	public void textTrackableSmallTree()
	{
		cmdTempGroup("test_text_trackable", "smallTree", keyFile.getPath());
	}
	
	//@Test(timeout = 60000)
	public void textTrackableBigFile()
	{
		cmdTempGroup("test_text_trackable", "bigFile", keyFile.getPath());
	}
	
	//@Test
	public void textTrackableBigTree()
	{
		cmdTempGroup("test_text_trackable", "bigTree", keyFile.getPath());
	}
	
	//----------------------------------------
	//Secure Key Tests
	//----------------------------------------
	
	//Image
	
	//@Test(timeout = 60000)
	public void imageSecureBigTree()
	{
		cmdTempGroup("image_secure", "bigTree", keyFile.getPath());
	}
	
	//ImageOverlay
	
	//@Test//(timeout = 60000)
	public void imageOverlaySecureBigTree()
	{
		cmdTempGroup("image_overlay_secure", "bigTree", keyFile.getPath());
	}
		
	//Text
		
	//@Test(timeout = 60000)
	public void textSecureEmptyFolder()
	{
		cmdTempGroup("text_secure", "emptyFolder", keyFile.getPath());
	}

	//@Test(timeout = 60000)
	public void textSecureSmallFile()
	{
		cmdTempGroup("text_secure", "smallFile", keyFile.getPath());
	}
	
	//@Test(timeout = 60000)
	public void textSecureSmallTree()
	{
		cmdTempGroup("text_secure", "smallTree", keyFile.getPath());
	}
	
	//@Test(timeout = 60000)
	public void textSecureBigFile()
	{
		cmdTempGroup("text_secure", "bigFile", keyFile.getPath());
	}
	
	//@Test
	public void textSecureBigTree()
	{
		cmdTempGroup("text_secure", "bigTree", keyFile.getPath());
	}
	
	
	//----------------------------------------
	//Secure Password Tests
	//----------------------------------------
	
	@Test
	public void textSecurePasswordBigTree()
	{
		cmdTempGroup("text_secure", "bigTree");
	}
	
		
	
	
	
	
	
	
	private static void cmdTempGroup(String presetName, String testFileTreeName)
	{
		File inputFolder = setup(testFileTreeName);
		setupInputImages();
		
		//embed
		String[] args = new String[]{"--embed", "-i", inputFolder.getPath(),
						"-a", presetName, "-o", outputFolder.getPath()};
		Imagine.run(args);
		
		//extract
		args = new String[]{"--extract", "-i", outputFolder.getPath(),
						"-a", presetName, "-o", extractionFolder.getPath()};
		Imagine.run(args);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	private static void cmdTempGroup(String presetName, String testFileTreeName, String keyFilePath)
	{
		File inputFolder = setup(testFileTreeName);
		setupInputImages();
		
		//embed
		String[] args = new String[]{"--embed", "-i", inputFolder.getPath(),
						"-a", presetName, "-o", outputFolder.getPath(),
						"-k", keyFilePath};
		Imagine.run(args);
		
		//extract
		args = new String[]{"--extract", "-i", outputFolder.getPath(),
						"-a", presetName, "-o", extractionFolder.getPath(),
						"-k", keyFilePath};
		Imagine.run(args);
		
		//compare
		Comparisons.compareExtractedFileStructure(inputFolder, extractionFolder, false);
	}
	
	private static File setup(String treeName)
	{
		//shutdown from previous run
		shutdown();
		Database.reset();

		// setup
		File inputFolder = TestFileTrees.getRoot(homeFolder, treeName);
		reset(treeName);
		
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
		
		shutdownCalled = true;
	}
	private static void reset(String treeName)
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		clearFolder(imagesFolder);
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
