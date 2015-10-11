package runner;


import gui.background.BackgroundGUI;
import gui.cmd.CmdGUI;
import gui.controlpanel.ControlPanelGUI;

import java.io.File;

import config.Configuration;

public class BackupManager {

	public static void main(String[] args) {
		
		//for testing
		args = new String[]{"cmd"};
		
		//load config
		File dbFile = Configuration.getDatabaseFile();
		
		if(args.length != 1)
			usage("Incorrect number of arguments.");
		
		//enter mode specified
		runMode(args[0]);
	}

	private static void runMode(String mode) {
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
			new BackupRunner().runBackup();
		}
		else
			usage("Unknown mode: " + mode);
	}

	private static void usage(String message) {
		if(message != null)
			System.err.println(message);
		System.err.println("Usage: java -jar Backup.jar <mode>");
		System.err.println("(See help for a list of modes)");
	}

}
