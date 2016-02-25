package system;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import logging.LogLevel;
import logging.Logger;
import ui.ArgParseResult;
import ui.UIContext;
import ui.cmd.CmdUI;
import ui.graphical.GUI;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
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
	 * @param p_args
	 */
	public static void main(String[] p_args)
	{
		//using extra function step for easier testing automation
		run(p_args);
	}
	
	/**
	 * The main entry point, separated for automated
	 * testing purposes.
	 * @param p_args
	 */
	public static void run(String[] p_args)
	{
		//process args as gui vs. command line interface
		
		List<String> argList = new ArrayList<String>(Arrays.asList(p_args));
		ArgParseResult result = processArgs(argList);
		try
		{
			if (result == null)
			{
				usage("Could not parse arguments.");
			}
			else if (result.isGuiMode())
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
	
	
	/**
	 * @update_comment
	 * @param p_args
	 * @return
	 */
	private static ArgParseResult processArgs(List<String> p_args)
	{
		ArgParseResult result = new ArgParseResult();
		
		//gui mode (no arguments -> gui mode)
		result.setGuiMode(p_args.contains("--gui") || p_args.isEmpty());

		//actions
		if (p_args.contains("--help"))
			result.setAction(CmdAction.k_help);
		else if (p_args.contains("--open"))
			result.setAction(CmdAction.k_open);
		else if (p_args.contains("--embed"))
			result.setAction(CmdAction.k_embed);
		else if (p_args.contains("--extract"))
			result.setAction(CmdAction.k_extract);
		else if (p_args.contains("--install"))
			result.setAction(CmdAction.k_install);
		else
			result.setAction(CmdAction.k_unspecified);
		
		try
		{
			//algorithm preset name
			if (p_args.contains("-a"))
				result.setPresetName(p_args.get(p_args.indexOf("-a")+1));
			
			//manually specified input files
			while (p_args.contains("-i"))
			{
				int flagIndex = p_args.indexOf("-i");
				result.addInputFile(new File(p_args.get(flagIndex+1)));
				p_args.remove(flagIndex+1);
				p_args.remove(flagIndex);
			}
			
			//input files specified in a input list
			if (p_args.contains("-I"))
			{
				for (String path : myUtilities.readListFromFile(new File(p_args.get(p_args.indexOf("-I")+1))))
				{
					path = path.trim();
					if (path.length() > 0)
						result.addInputFile(new File(path));
				}
			}
			
			//output folder
			if (p_args.contains("-o"))
				result.setOutputFolder(new File(p_args.get(p_args.indexOf("-o")+1)));
			
			//key file
			if (p_args.contains("-k"))
				result.setKeyFile(new File(p_args.get(p_args.indexOf("-k")+1)));

			//use password
			result.setUsingPassword(p_args.contains("-p"));
			
			//result file
			if (p_args.contains("-r"))
				result.setResultFile(new File(p_args.get(p_args.indexOf("-r")+1)));
			
			//parameter
			while (p_args.contains("-P"))
			{
				int flagIndex = p_args.indexOf("-P");
				String pair = p_args.get(flagIndex+1);
				
				//remove quotes
				if (pair.charAt(0) == '"' && pair.charAt(pair.length()-1) == '"')
					pair = pair.substring(1, pair.length()-1);
				
				//split the pair
				if (pair.contains("="))
				{
					String name = pair.substring(0, pair.indexOf('='));
					String value = pair.substring(pair.indexOf('=')+1);
					result.addParameter(new String[]{name, value});
				}
				
				//remove this parameter
				p_args.remove(flagIndex+1);
				p_args.remove(flagIndex);
			}
			
			return result;
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			return null;
		}
	}

	/**
	 * @update_comment
	 * @param p_message
	 */
	public static void usage(String p_message)
	{
		if (p_message != null)
			System.err.println(p_message);
		
		System.err.println("Example Usage: (See 'imagine --help' for more details.)");
		System.err.println("imagine --gui\n");
		System.err.println("imagine --open -a <algorithm> -i <file> [-o <folder>] [-p]\n");
		System.err.println("imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-p]\n");
		System.err.println("imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-p]");
	}
}
