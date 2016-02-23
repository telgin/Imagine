package ui.cmd;

import java.io.Console;
import java.io.File;
import java.io.IOException;

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
import system.Imagine;
import system.SystemManager;
import ui.ArgParseResult;
import ui.UI;

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
		if (args.presetName == null)
		{
			Logger.log(LogLevel.k_fatal, "An algorithm preset name must be specified.");
		}
		else if (args.inputFiles.isEmpty())
		{
			Logger.log(LogLevel.k_fatal, "Input files must be specified.");
		}
		else if (args.keyFile != null && args.usingPassword)
		{
			Logger.log(LogLevel.k_fatal, "You cannot use both a key file (-k) and a password (-p).");
		}
		else
		{
			//reset static components
			SystemManager.reset();
			
			//execute correct action
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
	}

	/**
	 * @update_comment
	 */
	private void helpSection()
	{
		p("Currently, the command line interface supports some common operations for");
		p("viewing, embedding, and extracting data. Changes to algorithms must be ");
		p("done through the GUI.\n");
		
		p("Command Syntax:");
		p("imagine --open -a <algorithm> -i <file> [-o <folder>] [-k [keyfile]]\n");
		p("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]\n");
		p("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]\n");
		
		p("--open");
		p("    open an archive and selectively extract its contents");
		p("--embed");
		p("    embed data into a supported format (create archives)");
		p("--extract");
		p("    extract all data from an archive file or folder of multiple archives\n");
		p("--gui");
		p("    open the gui (default) (not all flags supported)");
		
		p("-a <algorithm>");
		p("    algorithm preset name");
		p("-i <file/folder>");
		p("    input file or folder (multiple flags supported)");
		p("-I <file>");
		p("    a file containing a list of input files (1 per line)");
		p("-o <folder>");
		p("    output folder");
		p("-k [file]");
		p("    key file or empty for password (optional)");
		p("-p");
		p("    prompt for a password");
		p("-P <\"name=value\">");
		p("    override algorithm parameter (quotes optional)");
		p("-r <file>");
		p("    create a report file of which archive each file was added to");
	}

	private Algorithm getAlgorithm(ArgParseResult result) throws UsageException
	{
		Algorithm algo = ConfigurationAPI.getAlgorithmPreset(result.presetName);
		
		for (String[] pair : result.parameters)
		{
			String name = pair[0];
			String value = pair[1];
			
			algo.setParameter(name, value);
		}
		
		return algo;
	}
	
	private void openArchive(ArgParseResult result)
	{
		try
		{
			Algorithm algo = getAlgorithm(result);
			Key key = getKey(result);
			
			ProductContents productContents = ConversionAPI.openArchive(algo, key, result.inputFiles.get(0));
			
			Menu contentsMenu = new Menu("File Contents");
			contentsMenu.setSubtext("Select a file to extract it.");
			
			for (FileContents fileContents : productContents.getFileContents())
			{
				String folder =      "(folder)        ";
				String file =        "(file)          ";
				String path = fileContents.getMetadata().getFile().getPath();
				
				if (fileContents.getFragmentNumber() > 1)
				{
					String fragment = "(fragment " + fileContents.getFragmentNumber() + ")";
					while (fragment.length() < folder.length())
						fragment += " "; //not efficient, but this is rare
					contentsMenu.addOption(fragment + path);
				}
				else if (fileContents.getMetadata().getType().equals(FileType.k_file))
					contentsMenu.addOption(file + path);
				else
					contentsMenu.addOption(folder + path);
			}
			
			contentsMenu.display();
			
			int choice = contentsMenu.getChosenIndex();
			
			if (choice == 0 && productContents.getFileContents().get(0).getFragmentNumber() > 1)
			{
				Logger.log(LogLevel.k_warning, "This fragment is not the first fragment. Only the first fragment may start an extraction chain.");
				Logger.log(LogLevel.k_warning, "If you extract this fragment, your result will only contain a portion of the original data.");
				p("Select the fragment again to continue, otherwise you may choose something else.");
				contentsMenu.inputChoice();
				choice = contentsMenu.getChosenIndex();
			}
			
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
			Algorithm algo = getAlgorithm(result);
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
			}
			
			//do not track file status in cmd
			Settings.setTrackFileStatus(false);

			//run the job thread
			ConversionJob job = ConversionAPI.runConversion(result.inputFiles, algo, key, Constants.DEFAULT_THREAD_COUNT);
			
			String previousStat = "";
			while (!job.isFinished())
			{
				String currentStat = "Files Processed: " + JobStatus.getInputFilesProcessed() + 
								", Products Created: " + JobStatus.getProductsCreated();
				if (!outputPaused && !previousStat.equals(currentStat))
				{
					Logger.log(LogLevel.k_info, currentStat);
					previousStat = currentStat;
				}
				
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
			Algorithm algo = getAlgorithm(result);
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
