package testing.highlevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.Test;

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
	private static File hashdbFile = new File("testing/resources/hashdb.db");
	private static boolean shutdownCalled = true;
	private static ArrayList<ConversionJob> jobs = new ArrayList<ConversionJob>();

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
	
	
	//@Test(timeout = 60000)
	public void imageOverlayBasicEmptyFolder()
	{
		cmdTempGroup("image_overlay_basic", "emptyFolder");
	}

	@Test(timeout = 60000)
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
	
	//@Test(timeout = 60000)
	public void imageOverlayBasicBigTree()
	{
		cmdTempGroup("image_overlay_basic", "bigTree");
	}
	
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
	
	private static File setup(String treeName)
	{
		if (!shutdownCalled)
			shutdown();
		shutdownCalled = false;

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
		shutdownCalled = true;
	}
	private static void reset(String treeName)
	{
		clearFolder(outputFolder);
		clearFolder(extractionFolder);
		clearFolder(imagesFolder);
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
}
