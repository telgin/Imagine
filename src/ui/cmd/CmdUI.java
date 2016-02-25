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
import key.DefaultKey;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.FileContents;
import product.ProductContents;
import report.JobStatus;
import report.Report;
import system.Imagine;
import system.SystemManager;
import ui.ArgParseResult;
import ui.UI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class CmdUI extends UI
{
	private boolean f_outputPaused;
	private ArgParseResult f_args;

	/**
	 * @update_comment
	 * @param p_args
	 */
	public CmdUI(ArgParseResult p_args)
	{
		f_args = p_args;
		f_outputPaused = false;
	}
	
	/* (non-Javadoc)
	 * @see ui.UI#init()
	 */
	@Override
	public void init()
	{
		if (f_args.getPresetName() == null)
		{
			Logger.log(LogLevel.k_fatal, "An algorithm preset name must be specified.");
		}
		else if (f_args.getInputFiles().isEmpty())
		{
			Logger.log(LogLevel.k_fatal, "Input files must be specified.");
		}
		else if (f_args.getKeyFile() != null && f_args.isUsingPassword())
		{
			Logger.log(LogLevel.k_fatal, "You cannot use both a key file (-k) and a password (-p).");
		}
		else
		{
			//reset static components
			SystemManager.reset();
			
			//execute correct action
			switch (f_args.getAction())
			{
				case k_open:
					openArchive();
					break;
					
				case k_embed:
					embed();
					break;
					
				case k_extract:
					extract();
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

	/**
	 * @update_comment
	 * @return
	 * @throws UsageException
	 */
	private Algorithm getAlgorithm() throws UsageException
	{
		Algorithm algo = ConfigurationAPI.getAlgorithmPreset(f_args.getPresetName());
		
		for (String[] pair : f_args.getParameters())
		{
			String name = pair[0];
			String value = pair[1];
			
			algo.setParameter(name, value);
		}
		
		return algo;
	}

	/**
	 * @update_comment
	 */
	private void openArchive()
	{
		try
		{
			Algorithm algo = getAlgorithm();
			Key key = getKey();
			
			ProductContents productContents = ConversionAPI.openArchive(algo, key, f_args.getInputFiles().get(0));
			
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
			if (f_args.getOutputFolder() == null)
				f_args.setOutputFolder(new File("."));
			
			ConversionAPI.extractFile(algo, key, f_args.getInputFiles().get(0), f_args.getOutputFolder(), choice);
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
	 * @return
	 */
	private Key getKey()
	{
		Key key = null;

		if (f_args.getKeyFile() != null)
		{
			key = new FileKey(f_args.getKeyFile());
		}
		else if (f_args.isUsingPassword())
		{
			key = new PasswordKey();
		}
		else
		{
			key = new DefaultKey();
		}
		
		return key;
	}

	/**
	 * @update_comment
	 */
	private void embed()
	{
		try
		{
			Algorithm algo = getAlgorithm();
			Key key = getKey();

			//must have input files
			if (f_args.getInputFiles().size() == 0)
				throw new UsageException("No input files could be found.");
			
			//use local dir
			if (f_args.getOutputFolder() == null)
				f_args.setOutputFolder(new File("."));
			
			Settings.setOutputFolder(f_args.getOutputFolder());
			
			//make report if requested
			if (f_args.getResultFile() != null)
			{
				Settings.setGenerateReport(true);
			}
			
			//do not track file status in cmd
			Settings.setTrackFileStatus(false);

			//run the job thread
			ConversionJob job = ConversionAPI.runConversion(f_args.getInputFiles(), algo, key, Constants.DEFAULT_THREAD_COUNT);
			
			String previousStat = "";
			while (!job.isFinished())
			{
				String currentStat = "Files Processed: " + JobStatus.getInputFilesProcessed() + 
								", Products Created: " + JobStatus.getProductsCreated();
				if (!f_outputPaused && !previousStat.equals(currentStat))
				{
					Logger.log(LogLevel.k_info, currentStat);
					previousStat = currentStat;
				}
				
				Thread.sleep(1000);
			}
			
			//write report
			if (Settings.generateReport())
				Report.writeReport(f_args.getResultFile());
			
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
	 */
	private void extract()
	{
		try
		{
			Algorithm algo = getAlgorithm();
			Key key = getKey();
			
			//otherwise use local dir
			if (f_args.getOutputFolder() == null)
				f_args.setOutputFolder(new File("."));
			
			ConversionAPI.extractAll(algo, key, f_args.getInputFiles().get(0), f_args.getOutputFolder());
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
	public String promptParameterValue(Parameter p_param)
	{
		p("The algorithm parameter '" + p_param.getName() + "' must be set.");
		p("Parameter description: " + p_param.getDescription());
		return promptInput("Please enter the parameter value: ");
	}

	/**
	 * @update_comment
	 * @param p_prompt
	 * @return
	 */
	private static String promptInput(String p_prompt)
	{
		System.out.print(p_prompt);
		return CMDInput.getLine();
	}

	/**
	 * typing this sucks
	 * @param p_print
	 * TODO hhhhhnnnnnnngggg
	 */
	private final static void p(String p_print)
	{
		System.out.println(p_print);
	}
	
	/**
	 * typing this sucks
	 * @param p_print
	 * TODO hhhhhnnnnnnngggg
	 */
	private final void err(String p_print)
	{
		System.err.println(p_print);
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptKeyFileLocation()
	 */
	@Override
	public File promptKeyFileLocation()
	{
		f_outputPaused = true;
		File location = new File(promptInput("Enter key file location: "));
		f_outputPaused = false;
		return location;
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptKey()
	 */
	@Override
	public String promptKey()
	{
		f_outputPaused = true;
		
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
		
		f_outputPaused = false;
		return password;
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptEnclosingFolder(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public File promptEnclosingFolder(File p_curEnclosingFolder, File p_curProductFolder,
					String p_productSearchName)
	{
		f_outputPaused = true;
		
		p("The file, " + p_productSearchName + ", was not found after searching in ");
		p("The current extraction file location: " + p_curProductFolder.getPath());
		
		String path = null;
		if (p_curEnclosingFolder.getAbsolutePath().equals(p_curProductFolder.getAbsolutePath()))
		{
			
			p("Enter the name of the enclosing folder where this file may be found,");
			path = promptInput("or hit enter to skip: ");
		}
		else
		{
			p("Or the current enclosing folder: " + p_curEnclosingFolder.getPath());
			p("Enter the name of the enclosing folder where this file may be found,");
			path = promptInput("or hit enter to skip: ");
		}
		
		if (path ==  null || path.isEmpty())
		{
			f_outputPaused = false;
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
				f_outputPaused = false;
				return null;
			}
			else
			{
				newEnclosingFolder = new File(path);
			}
			
			f_outputPaused = false;
			return newEnclosingFolder;
		}
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportError(java.lang.String)
	 */
	@Override
	public void reportError(String p_message)
	{
		err(p_message);
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportMessage(java.lang.String)
	 */
	@Override
	public void reportMessage(String p_message)
	{
		p(p_message);
	}
}
