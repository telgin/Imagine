package system;

import ui.UIContext;
import ui.cmd.CmdUI;
import ui.graphical.GUI;

import java.util.Arrays;
import java.util.List;

public class Imagine
{

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
		//add hook so ctrl+C shuts down the system properly
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				SystemManager.shutdown();
			}
		});
		
		//process args as gui vs. command line interface
		String guiCode = "--gui";
		List<String> argList = Arrays.asList(args);
		
		if (argList.contains(guiCode))
		{
			argList.remove(guiCode);
			GUI gui = new GUI(argList);
			UIContext.setUI(gui);
		}
		else
		{
			CmdUI cmd = new CmdUI(argList);
			UIContext.setUI(cmd);
		}
		
		//process the args
		UIContext.getUI().processArgs();
	}
}
