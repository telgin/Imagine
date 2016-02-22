package system;

import ui.ArgParseResult;
import ui.UIContext;
import ui.cmd.CmdUI;
import ui.graphical.GUI;
import util.myUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logging.LogLevel;
import logging.Logger;

public class Imagine
{
	static
	{
		//add hook so ctrl+C shuts down the system properly
		//this is also called during a normal exit
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				SystemManager.shutdown();
			}
		});
	}
	
	
	/**
	 * Args:
	 * 
	 * [nothing] <-- implies cmd main menu
	 * 
	 * --help
	 * 
	 * --gui
	 * --gui --treeview <treefile|treefolder>
	 * --gui --open -i <file>
	 * --gui --embed -i <file/folder>
	 * --gui --extract -i <file/folder>
	 * 
	 * --open -g <groupname> -i <file>
	 * --open -a <presetname> -i <file> [-k <keyfile>]
	 * 
	 * --embed -g <groupname> -i <file/folder> -o <folder>
	 * --embed -a <presetname> -i <file/folder> -o <folder> [-k <keyfile>]
	 * 
	 * --extract -g <groupname> -i <file/folder> -o <folder>
	 * --extract -a <presetname> -i <file/folder> -o <folder> [-k <keyfile>]
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		//using extra function step for easier testing automation
		run(args);
	}
	
	/**
	 * The main entry point, separated for automated
	 * testing purposes.
	 * @param args
	 */
	public static void run(String[] args)
	{
		//process args as gui vs. command line interface
		
		List<String> argList = new ArrayList<String>(Arrays.asList(args));
		ArgParseResult result = processArgs(argList);
		try
		{
			if (result == null)
			{
				usage("Could not parse arguments.");
			}
			else if (result.guiMode)
			{
				GUI gui = new GUI(result);
				UIContext.setUI(gui);
			}
			else
			{
				CmdUI cmd = new CmdUI(result);
				UIContext.setUI(cmd);
			}
			
			//process the args
			UIContext.getUI().init();
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_fatal, e.getMessage());
		}
	}
	
	
	private static ArgParseResult processArgs(List<String> args)
	{
		ArgParseResult result = new ArgParseResult();
		
		//gui mode
		result.guiMode = args.contains("--gui") || args.isEmpty();

		//actions
		if (args.contains("--help"))
			result.action = CmdAction.k_help;
		else if (args.contains("--open"))
			result.action = CmdAction.k_open;
		else if (args.contains("--embed"))
			result.action = CmdAction.k_embed;
		else if (args.contains("--extract"))
			result.action = CmdAction.k_extract;
		else if (args.contains("--install"))
			result.action = CmdAction.k_install;
		
		try
		{
			//algorithm preset name
			if (args.contains("-a"))
				result.presetName = args.get(args.indexOf("-a")+1);
			
			//manually specified input files
			while (args.contains("-i"))
			{
				int flagIndex = args.indexOf("-i");
				result.inputFiles.add(new File(args.get(flagIndex+1)));
				args.remove(flagIndex+1);
				args.remove(flagIndex);
			}
			
			//input files specified in a input list
			if (args.contains("-I"))
			{
				for (String path : myUtilities.readListFromFile(new File(args.get(args.indexOf("-I")+1))))
				{
					path = path.trim();
					if (path.length() > 0)
						result.inputFiles.add(new File(path));
				}
			}
			
			//output folder
			if (args.contains("-o"))
				result.outputFolder = new File(args.get(args.indexOf("-o")+1));
			
			//key file
			if (args.contains("-k"))
				result.keyFile = new File(args.get(args.indexOf("-k")+1));

			//use password
			result.usePassword = args.contains("-p");
			
			//result file
			if (args.contains("-r"))
				result.resultFile = new File(args.get(args.indexOf("-r")+1));
			
			//parameter
			while (args.contains("-P"))
			{
				int flagIndex = args.indexOf("-P");
				String pair = args.get(flagIndex+1);
				
				//remove quotes
				if (pair.charAt(0) == '"' && pair.charAt(pair.length()-1) == '"')
					pair = pair.substring(1, pair.length()-1);
				
				//split the pair
				if (pair.contains("="))
				{
					String name = pair.substring(0, pair.indexOf('='));
					String value = pair.substring(pair.indexOf('=')+1);
					result.parameters.add(new String[]{name, value});
				}
				
				//remove this parameter
				args.remove(flagIndex+1);
				args.remove(flagIndex);
			}
			
			return result;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			return null;
		}
	}

	public static void usage(String message)
	{
		if (message != null)
			System.err.println(message);
		
		System.err.println("Usage:");
		System.err.println("(See 'imagine --help' for more details.)");
		System.err.println("imagine --gui\n");
		System.err.println("imagine --open -a <algorithm> -i <file> [-o <folder>] [-k [keyfile]]\n");
		System.err.println("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]\n");
		System.err.println("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]");
	}
}
