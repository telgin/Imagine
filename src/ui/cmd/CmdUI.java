package ui.cmd;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import algorithms.Algorithm;
import algorithms.Parameter;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import config.Constants;
import config.Settings;
import data.FileType;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.StaticKey;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.FileContents;
import product.JobStatus;
import product.ProductContents;
import report.Report;
import system.CmdAction;
import system.Imagine;
import ui.ArgParseResult;
import ui.UI;
import util.myUtilities;

public class CmdUI extends UI
{
	private boolean outputPaused;
	private ArgParseResult args;

	public CmdUI(ArgParseResult args)
	{
		this.args = args;
		outputPaused = false;
	}
	
	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void init()
	{
		switch (args.action)
		{
			case k_open:
				openArchive(args);
				break;
				
			case k_embed:
				embed(args);
				break;
				
			case k_extract:
				extract(args);
				break;
				
			case k_install:
				ConfigurationAPI.install();
				break;
				
			case k_help:
				helpSection();
				break;
		
			case k_editor:
				Imagine.usage("Algorithm editor is not supported in command line mode.");
				break;
				
			default:
				Imagine.usage("A valid action must be specified.");
		}
	}

	/**
	 * @update_comment
	 */
	private void helpSection() //TODO spelling
	{
		p("Currently, the command line interface supports some common operations for");
		p("viewing, embedding, and extracting data. However, at this time, specific");
		p("configuration edits (editing profiles or algorithms) must be done in the GUI.\n");
		
		p("Command Syntax:");
		p("imagine --open -a <algorithm> -i <file> [-o <folder>] [-k [keyfile]]\n");
		p("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]\n");
		p("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]\n");
		
		p("--open     ");
		p("    open an archive and selectively extract its contents");
		p("--embed    ");
		p("    embed data into a supported format");
		p("--extract  ");
		p("    extract all data from an archive file or folder or multiple archives\n");
		
		p("-a         algorithm preset name");
		p("-i         input file or folder");
		p("-o         output folder");
		p("-k         key file or empty for password (optional)");
		p("-p         use a password");
		p("-P         specify additional algorithm parameter");
	}

//		if (!subargs.contains("-p") && !subargs.contains("-a"))
//		{
//			Imagine.usage("Either a profile name (-p) or an algorithm preset name (-a) must be specified.");
//		}
//		else if (action.equals(CmdAction.k_embed) && subargs.contains("-p") && subargs.contains("-i"))
//		{
//			Imagine.usage("Profiles have a locked set of data input locations. Seperate input cannot be specified.");
//		}
//		else if (!subargs.contains("-i") || (action.equals(CmdAction.k_embed) && !subargs.contains("-i") && !subargs.contains("-I")))
//		{
//			Imagine.usage("Input files must be specified.");
//		}
//		else if (subargs.contains("-p") && subargs.contains("-a"))
//		{
//			Imagine.usage("Either specify an algorithm preset name or a profile name, not both.");
//		}
//		else if (subargs.contains("-p") && subargs.contains("-k"))
//		{
//			Imagine.usage("You cannot specify both a profile and a key. Keys are contained within profiles.");
//		}

	
	private void openArchive(ArgParseResult result)
	{
		try
		{
			Algorithm algo = ConfigurationAPI.getAlgorithmPreset(result.presetName);
			Key key = getKey(result);
			
			ProductContents productContents = ConversionAPI.openArchive(algo, key, result.inputFiles.get(0));
			
			Menu contentsMenu = new Menu("File Contents");
			contentsMenu.setSubtext("Select a file to extract it.");
			
			for (FileContents fileContents : productContents.getFileContents())
			{
				String ref =    "(reference) ";
				String folder = "(folder)    ";
				String file =   "(file)      ";
				String path = fileContents.getMetadata().getFile().getPath();
				
				if (fileContents.getMetadata().getType().equals(FileType.k_file))
					contentsMenu.addOption(file + path);
				else if (fileContents.getMetadata().getType().equals(FileType.k_folder))
					contentsMenu.addOption(folder + path);
				else
					contentsMenu.addOption(ref + path);
			}
			
			contentsMenu.display();
			
			int choice = contentsMenu.getChosenIndex();
			
			//use local dir if no output folder set
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			ConversionAPI.extractFile(algo, key, result.inputFiles.get(0), result.outputFolder, choice);
		}
		catch (IOException | UsageException e)
		{
			Imagine.usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			Imagine.usage(null);
			Logger.log(LogLevel.k_debug, e, false);
		}
		
	}

	/**
	 * @update_comment
	 * @param result
	 * @return
	 */
	private Key getKey(ArgParseResult result)
	{
		Key key = null;
		
		if (result.keyFile != null)
		{
			key = new FileKey(result.keyFile);
		}
		else if (result.usingPassword)
		{
			key = new PasswordKey();
		}
		else
		{
			key = new StaticKey();
		}
		
		return key;
	}

	/**
	 * @update_comment
	 */
	private void embed(ArgParseResult result)
	{
		try
		{
			Algorithm algo = ConfigurationAPI.getAlgorithmPreset(result.presetName);
			Key key = getKey(result);
			
			//must have input files
			if (result.inputFiles.size() == 0)
				throw new UsageException("No input files could be found.");
			
			//use local dir
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			Settings.setOutputFolder(result.outputFolder);
			
			//make report if requested
			if (result.resultFile != null)
			{
				Settings.setGenerateReport(true);
				Report.reset();
			}
			
			//do not track file status in cmd
			Settings.setTrackFileStatus(false);

			//run the job thread
			ConversionJob job = ConversionAPI.runConversion(result.inputFiles, algo, key, Constants.DEFAULT_THREAD_COUNT);
			
			while (!job.isFinished())
			{
				if (!outputPaused)
					showStats();
				
				Thread.sleep(1000);
			}
			
			//write report
			if (Settings.generateReport())
				Report.writeReport(result.resultFile);
			
		}
		catch (UsageException e)
		{
			Imagine.usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			Imagine.usage("A failure occurred during conversion.");
			Logger.log(LogLevel.k_debug, e, false);
		}
	}

	/**
	 * @update_comment
	 * @param subargs
	 */
	private void extract(ArgParseResult result)
	{
		try
		{
			Algorithm algo = ConfigurationAPI.getAlgorithmPreset(result.presetName);
			Key key = getKey(result);
			
			//otherwise use local dir
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			ConversionAPI.extractAll(algo, key, result.inputFiles.get(0), result.outputFolder);
		}
		catch (UsageException | IOException e)
		{
			Imagine.usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			Imagine.usage(null);
			Logger.log(LogLevel.k_debug, e, false);
		}
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter param)
	{
		p("The algorithm parameter '" + param.getName() + "' must be set.");
		p("Parameter description: " + param.getDescription());
		return promptInput("Please enter the parameter value: ");
	}

	private void showStats()
	{
		Logger.log(LogLevel.k_info, "Files Processed: " + JobStatus.getInputFilesProcessed() + 
			", Products Created: " + JobStatus.getProductsCreated());
	}

	private static String promptInput(String prompt)
	{
		System.out.print(prompt);
		return CMDInput.getLine();
	}

	/**
	 * typing this sucks
	 * @param print
	 */
	private final static void p(String print)
	{
		System.out.println(print);
	}
	
	/**
	 * typing this sucks
	 * @param print
	 */
	private final void err(String print)
	{
		System.err.println(print);
	}

	@Override
	public File promptKeyFileLocation()
	{
		outputPaused = true;
		File location = new File(promptInput("Enter key file location: "));
		outputPaused = false;
		return location;
	}

	@Override
	public String promptKey()
	{
		outputPaused = true;
		
		p("Enter password: ");
		
		Console console = System.console();
		String password = null;
		if (console == null)
		{
			//for testing w/ eclipse, password will not be hidden
			password = CMDInput.getLine();
		}
		else
		{
			password = new String(console.readPassword(""));
		}
		
		outputPaused = false;
		return password;
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptEnclosingFolder(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public File promptEnclosingFolder(File curEnclosingFolder, File curProductFolder,
					String productSearchName)
	{
		outputPaused = true;
		
		p("The file, " + productSearchName + ", was not found after searching in ");
		p("The current extraction file location: " + curProductFolder.getPath());
		
		String path = null;
		if (curEnclosingFolder.getAbsolutePath().equals(curProductFolder.getAbsolutePath()))
		{
			
			p("Enter the name of the enclosing folder where this file may be found,");
			path = promptInput("or hit enter to skip: ");
		}
		else
		{
			p("Or the current enclosing folder: " + curEnclosingFolder.getPath());
			p("Enter the name of the enclosing folder where this file may be found,");
			path = promptInput("or hit enter to skip: ");
		}
		
		if (path ==  null || path.isEmpty())
		{
			outputPaused = false;
			return null;
		}
		else
		{
			File newEnclosingFolder = new File(path);
			
			while ((!newEnclosingFolder.exists() || !newEnclosingFolder.isDirectory()) && !path.isEmpty())
			{
				p("The folder: " + newEnclosingFolder.getPath() + " could not be found.");
				p("Enter the name of the enclosing folder where this file may be found,");
				path = promptInput("or hit enter to skip: ");
			}
			
			if (path ==  null || path.isEmpty())
			{
				outputPaused = false;
				return null;
			}
			else
			{
				newEnclosingFolder = new File(path);
			}
			
			outputPaused = false;
			return newEnclosingFolder;
		}
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportError(java.lang.String)
	 */
	@Override
	public void reportError(String message)
	{
		err(message);
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportMessage(java.lang.String)
	 */
	@Override
	public void reportMessage(String message)
	{
		p(message);
	}

}
