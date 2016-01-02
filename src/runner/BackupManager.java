package runner;

import gui.background.BackgroundGUI;
import gui.cmd.CmdGUI;
import gui.controlpanel.ControlPanelGUI;
import logging.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Configuration;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;

public class BackupManager
{

	public static void main(String[] args)
	{

		// --help
		// --gui
		// --backup
		// --interactive (-> cmdgui)
		// -a "algoName"
		// -p "pName"="pValue"
		// -d (use defaults not specified)
		// -i "file/folder"
		// -o "folder"
		// -k "keyFile"
		args = new String[]{
						"-a", "TextBlock",
						"-d",
						"-i", "testGroupInput/folder/text yo.txt",
						"-o", "output",
						"-k", "testGroupInput/untracked.txt"};

		if (args.length == 1)
			runMode(args[0]);

		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		processArgs(argList);

		Logger.shutdown();
	}

	private static void processArgs(ArrayList<String> argList)
	{
		if (argList.contains("--help"))
		{
			// be nice for --help
			runMode("help");
		}
		else if (argList.contains("--gui") || argList.contains("--backup")
						|| argList.contains("--interactive"))
		{
			usage("If a mode is specified, it must be the only argument.");
		}
		else
		{
			// TODO make this actually work
			Algorithm defaultAlgo = AlgorithmRegistry.getDefaultAlgorithm("FullPNG");
			Key key = new PasswordKey("chickens", "temp");
			TrackingGroup group = new TrackingGroup("temp", false, defaultAlgo, key);
			group.addTrackedPath("/home/tom/Downloads/");
			CmdGUI gui = new CmdGUI();
			Runner.setActiveGUI(gui);
			gui.runBackup(group);
			gui.shutdown();
		}
	}

	private static void runMode(String mode)
	{
		mode = mode.replaceAll("-", "");
		if (mode.equals("help"))
			new HelpRunner();
		else if (mode.equals("cmd"))
		{
			Runner.setActiveGUI(new CmdGUI());
			new ControlPanelRunner().start();
		}
		else if (mode.equals("gui"))
		{
			Runner.setActiveGUI(new ControlPanelGUI());
			new ControlPanelRunner().start();
		}
		else if (mode.equals("silent_backup"))
		{
			Runner.setActiveGUI(new BackgroundGUI());
			new ConversionRunner().runAllBackups();
		}
		else
			usage("Unknown mode: " + mode);
	}

	private static void usage(String message)
	{
		if (message != null)
			System.err.println(message);
		System.err.println("Usage: java -jar Backup.jar <mode>");
		System.err.println("(See help for a list of modes)");
	}

}
