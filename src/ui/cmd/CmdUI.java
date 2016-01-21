package ui.cmd;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.List;
import algorithms.Algorithm;
import algorithms.Parameter;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import config.Constants;
import data.FileKey;
import data.FileType;
import data.Key;
import data.PasswordKey;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.FileContents;
import product.ProductContents;
import product.ProductMode;
import stats.ProgressMonitor;
import stats.Stat;
import ui.ArgParseResult;
import ui.UI;

public class CmdUI extends UI
{
	private List<String> args;
	private boolean outputPaused;

	public CmdUI(List<String> args)
	{
		this.args = args;
		outputPaused = false;
	}
	
	private void usage(String message)
	{
		if (message != null)
			err(message);
		
		err("Usage:");
		err("(See 'imagine --help' for more details.)");
		err("imagine --gui\n");
		err("imagine --open -p <profile> -i <file> [-o <folder>]");
		err("imagine --open -a <algorithm> -i <file> [-o <folder>] [-k <keyfile>]\n");
		err("imagine --embed -p <profile> [-o <folder>]");
		err("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]\n");
		err("imagine --extract -p <profile> -i <file/folder> [-o <folder>]");
		err("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]");
	}
	
	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void processArgs()
	{
		if (args.isEmpty())
		{
			usage("tmp empty");
		}
		else if (args.contains("--help"))
		{
			helpSection();
		}
		else if (args.contains("--open"))
		{
			args.remove("--open");
			ArgParseResult result = cmdParse(CmdAction.k_open, args);
			if (result != null)
				openArchive(result);
		}
		else if (args.contains("--embed"))
		{
			args.remove("--embed");
			ArgParseResult result = cmdParse(CmdAction.k_embed, args);
			if (result != null)
				embed(result);
		}
		else if (args.contains("--extract"))
		{
			args.remove("--extract");
			ArgParseResult result = cmdParse(CmdAction.k_extract, args);
			if (result != null)
				extract(result);
		}
		else
		{
			usage("tmp else");
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
		p("imagine --open -p <profile> -i <file> [-o <folder>]");
		p("imagine --open -a <algorithm> -i <file> [-o <folder>] [-k <keyfile>]\n");
		p("imagine --embed -p <profile> [-o <folder>]");
		p("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]\n");
		p("imagine --extract -p <profile> -i <file/folder> [-o <folder>]");
		p("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k <keyfile>]\n");
		
		p("--open     ");
		p("    open an archive and selectively extract its contents");
		p("--embed    ");
		p("    embed data into a supported format");
		p("--extract  ");
		p("    extract all data from an archive file or folder or multiple archives\n");
		
		p("-a         algorithm preset name");
		p("-p         profile name");
		p("-i         input file or folder");
		p("-o         output folder");
		p("-k         key file (optional)");
		p("-P         specify additional algorithm parameter");
	}

	/**
	 * @update_comment
	 * @param subargs
	 */
	private ArgParseResult cmdParse(CmdAction action, List<String> subargs)
	{
		if (!subargs.contains("-p") && !subargs.contains("-a"))
		{
			usage("Either a profile name (-p) or an algorithm preset name (-a) must be specified.");
		}
		else if (action.equals(CmdAction.k_embed) && subargs.contains("-p") && subargs.contains("-i"))
		{
			usage("Profiles have a locked set of data input locations. Seperate input cannot be specified.");
		}
		else if (!subargs.contains("-i"))
		{
			usage("The input file (-i) must be specified.");
		}
		else if (subargs.contains("-p") && subargs.contains("-a"))
		{
			usage("Either specify an algorithm preset name or a profile name, not both.");
		}
		else if (subargs.contains("-p") && subargs.contains("-k"))
		{
			usage("You cannot specify both a profile and a key. Keys are contained within profiles.");
		}
		else
		{
			try
			{
				ArgParseResult result = new ArgParseResult();
				
				if (subargs.contains("-p"))
					result.profileName = subargs.get(subargs.indexOf("-p")+1);
				
				if (subargs.contains("-a"))
					result.presetName = subargs.get(subargs.indexOf("-a")+1);
				
				if (subargs.contains("-i"))
					result.inputFile = new File(subargs.get(subargs.indexOf("-i")+1));
				
				if (subargs.contains("-o"))
					result.outputFolder = new File(subargs.get(subargs.indexOf("-o")+1));
					
				if (subargs.contains("-k"))
					result.keyFile = new File(subargs.get(subargs.indexOf("-k")+1));
				
				return result;
			}
			catch (Exception e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				usage(null);
			}
		}
		
		return null;
	}
	
	private void openArchive(ArgParseResult result)
	{
		try
		{
			TrackingGroup group = getTrackingGroup(result);
			
			ProductContents productContents = ConversionAPI.openArchive(group, result.inputFile);
			
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
			
			//try static output folder first
			if (result.outputFolder == null)
				result.outputFolder = group.getStaticOutputFolder();
			
			//otherwise use local dir
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			ConversionAPI.extractFile(group, result.inputFile, result.outputFolder, choice);
		}
		catch (IOException | UsageException e)
		{
			usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			usage(null);
			Logger.log(LogLevel.k_debug, e, false);
		}
		
	}

	/**
	 * @update_comment
	 */
	private void embed(ArgParseResult result)
	{
		try
		{
			TrackingGroup group = getTrackingGroup(result);
			
			group.addTrackedPath(result.inputFile);
			
			//try static output folder first
			if (result.outputFolder == null)
				result.outputFolder = group.getStaticOutputFolder();
			
			//otherwise use local dir
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			group.setStaticOutputFolder(result.outputFolder);
			
			ConversionJob job = ConversionAPI.runConversion(group, Constants.DEFAULT_THREAD_COUNT);
			
			showStats(job);
		}
		catch (UsageException e)
		{
			usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			usage("A failure occurred during conversion.");
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
			TrackingGroup group = getTrackingGroup(result);
			
			//try static output folder first
			if (result.outputFolder == null)
				result.outputFolder = group.getStaticOutputFolder();
			
			//otherwise use local dir
			if (result.outputFolder == null)
				result.outputFolder = new File(".");
			
			ConversionAPI.extractAll(group, result.inputFile, result.outputFolder);
		}
		catch (UsageException | IOException e)
		{
			usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		catch (Exception e)
		{
			usage(null);
			Logger.log(LogLevel.k_debug, e, false);
		}
	}
	
	private TrackingGroup getTrackingGroup(ArgParseResult result) throws UsageException
	{
		TrackingGroup group = null;
		if (result.profileName != null)
		{
			group = ConfigurationAPI.getTrackingGroup(result.profileName);
		}
		else
		{
			Algorithm preset = ConfigurationAPI.getAlgorithmPreset(result.presetName);
			
			if (preset.getProductSecurityLevel().equals(ProductMode.k_basic))
			{
				group = ConversionAPI.createTemporaryTrackingGroup(result.presetName);
			}
			else if (result.keyFile == null)
			{
				Key passKey = new PasswordKey(Constants.TEMP_KEY_NAME, Constants.TEMP_RESERVED_GROUP_NAME);
				group = ConversionAPI.createTemporaryTrackingGroup(result.presetName, passKey);
			}
			else
			{
				Key fileKey = new FileKey(Constants.TEMP_KEY_NAME,
								Constants.TEMP_RESERVED_GROUP_NAME, result.keyFile);
				group = ConversionAPI.createTemporaryTrackingGroup(result.presetName, fileKey);
			}
			
			//temporary groups should not need/want file system tracking
			group.setUsesAbsolutePaths(false);
			group.setUsingIndexFiles(false);
		}
		
		return group;
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

	private void showStats(ConversionJob job) throws InterruptedException
	{
		while (!job.isFinished())
		{
			while (!outputPaused && !job.isFinished())
			{
				String statOutput = "";
				Stat stat = ProgressMonitor.getStat("filesProcessed");
				if (stat != null)
				{
					int filesProcessed = (int) stat.getNumericProgress().doubleValue();
					statOutput += "Files Processed: " + filesProcessed;
				}
				
				stat = ProgressMonitor.getStat("productsCreated");
				if (stat != null)
				{
					int productsCreated = (int) stat.getNumericProgress().doubleValue();
					if (!statOutput.isEmpty())
						statOutput += ", ";
					statOutput += ", Products Created: " + productsCreated;
				}
				
				if (!statOutput.isEmpty())
					Logger.log(LogLevel.k_info, statOutput);
				
				Thread.sleep(1000);
			}
			Thread.sleep(1000);
		}
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
	public File promptKeyFileLocation(String keyName, String groupName)
	{
		outputPaused = true;
		File location = new File(promptInput("Enter key (" + keyName
						+ ") file location for tracking group '" + groupName + "': "));
		outputPaused = false;
		return location;
	}

	@Override
	public String promptKey(String keyName, String groupName)
	{
		outputPaused = true;
		
		p("Enter password (" + keyName + ") for tracking group '" + groupName
						+ "': ");
		
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
	
	private enum CmdAction
	{
		k_open,
		k_embed,
		k_extract;
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
