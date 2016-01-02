package gui.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import algorithms.Algorithm;
import config.Configuration;
import data.FileKey;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;

import gui.GUI;
import logging.LogLevel;
import logging.Logger;
import product.ProductMode;
import runner.ConversionRunner;
import stats.ProgressMonitor;

public class CmdGUI extends GUI
{
	private BufferedReader cin;
	private ConversionRunner backupRunner;

	public CmdGUI()
	{
		cin = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void runnerStartupMessage()
	{
		p("The system is starting up...");
		mainMenu();
	}

	private void mainMenu()
	{

		p("Main Menu:");
		p("\t1\trun backup");
		p("\t2\tget file information");
		p("\t3\tcreate new tracking group");
		p("\t4\texit");

		int choice = 0;
		while (choice == 0)
		{
			try
			{
				choice = Integer.parseInt(getInput(""));
				if (choice == 1)
					runAllBackups();
				else if (choice == 2)
					fileInfoMenu();
				else if (choice == 3)
					createTrackingGroupPrompts();
				else if (choice == 4)
					shutdown();
				else
				{
					incorrectInput("Enter one of the numbers in the menu.");
					choice = 0;
				}
			}
			catch (Exception e)
			{
				incorrectInput("Enter one of the numbers in the menu.");
				choice = 0;
			}
		}
	}

	private void createTrackingGroupPrompts()
	{

		// name
		String groupName = getInput("Enter a new name for the tracking group: ");

		// mode
		ProductMode mode = ProductMode.getMode(getInput("Enter the security level: "));

		// database
		boolean usesDatabase =
						getInput("Should files belonging to this group be tracked within the file system? [y/n]: ")
										.toLowerCase().equals("y");

		// agorithm
		Algorithm algorithm = chooseAlgorithmPrompts();

		// key
		Key key = chooseKeyPrompts(mode, groupName);

		// TODO: add files here???

		// review and save?

		Configuration.addTrackingGroup(
						new TrackingGroup(groupName, usesDatabase, algorithm, key));

		Configuration.saveConfig();
	}

	private Key chooseKeyPrompts(ProductMode mode, String groupName)
	{
		Key key;

		// key name
		String keyName = getInput(
						"Enter a name for your password or key file (like a hint): ");

		if (mode.equals(ProductMode.NORMAL))
		{
			key = new NullKey();
		}
		else
		{
			if (getInput("Would you like to use a key file? [y/n]: ").toLowerCase()
							.equals("y"))
			{
				// key file
				File keyFile = null;
				String input = getInput(
								"Enter key file location (or return to have the system prompt for it when needed): ");
				if (input.length() > 0)
				{
					keyFile = new File(input);
				}
				key = new FileKey(keyName, groupName, keyFile);
			}
			else
			{
				p("The system will prompt you for your password when it is needed.");
				key = new PasswordKey(keyName, groupName);
			}
		}

		return key;
	}

	private Algorithm chooseAlgorithmPrompts()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void runBackup(TrackingGroup group)
	{
		try
		{
			if (backupRunner == null)
				backupRunner = new ConversionRunner();

			setRunner(backupRunner);
			backupRunner.runBackup(group);
			waitForBackup();
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, e, false);
		}
	}

	private void runAllBackups()
	{
		try
		{
			if (backupRunner == null)
				backupRunner = new ConversionRunner();

			setRunner(backupRunner);
			backupRunner.runAllBackups();
			waitForBackup();
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, e, false);
		}
	}

	private void waitForBackup() throws InterruptedException
	{
		while (backupRunner.isRunning())
		{
			Thread.sleep(1000);
			int filesProcessed = (int) ProgressMonitor.getStat("filesProcessed")
							.getNumericProgress().doubleValue();
			int productsCreated = (int) ProgressMonitor.getStat("productsCreated")
							.getNumericProgress().doubleValue();
			Logger.log(LogLevel.k_info, "Files Processed: " + filesProcessed
							+ ", Products Created: " + productsCreated);
		}
	}

	private void fileInfoMenu()
	{
		p("File Info Menu:");
		p("\t1\tget status of file");
		p("\t2\tlist the contents of an image");
		p("\t3\tback to main menu");

		int choice = 0;
		while (choice == 0)
		{
			try
			{
				choice = Integer.parseInt(getInput(""));
				if (choice == 1)
					fileStatusPrompt();
				else if (choice == 2)
					imageContentsPrompt();
				else if (choice == 3)
					mainMenu();
				else
				{
					incorrectInput("Enter one of the numbers in the menu.");
					choice = 0;
				}
			}
			catch (Exception e)
			{
				incorrectInput("Enter one of the numbers in the menu.");
				choice = 0;
			}
		}
	}

	private void fileStatusPrompt()
	{
		String path = "";
		while (true)
		{
			path = getInput("Enter the path of a file: ");
			File file = new File(path);
			if (file.exists())
			{
				p("File status???");
				break;
			}
			else if (path.equals("exit"))
			{
				break;
			}
			else
			{
				p("The file '" + file.getAbsolutePath() + "' does not exist.");
				p("Re-enter a file name, or type 'exit' to exit.");
			}
		}
	}

	private void imageContentsPrompt()
	{
		String imagePath = "";
		while (true)
		{
			imagePath = getInput("Enter the path of an image product: ");
			File imageFile = new File(imagePath);
			if (imageFile.exists())
			{
				p("Reading image???");
				break;
			}
			else if (imagePath.equals("exit"))
			{
				break;
			}
			else
			{
				p("The file '" + imageFile.getAbsolutePath() + "' does not exist.");
				p("Re-enter a file name, or type 'exit' to exit.");
			}
		}

	}

	private void incorrectInput(String what)
	{
		p("Incorrect Input: " + what);
	}

	private String getInput(String prompt)
	{
		System.out.print(prompt);
		try
		{
			return cin.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	private final void p(String print)
	{
		System.out.println(print);
	}

	@Override
	public void showControlPanel()
	{
		mainMenu();
	}

	@Override
	public void showBackupPanel()
	{
		p("Starting backup...");
	}

	@Override
	public String promptTrackingGroup()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File promptKeyFileLocation(String keyName, String groupName)
	{
		return new File(getInput("Enter key (" + keyName
						+ ") file location for tracking group '" + groupName + "': "));
	}

	@Override
	public String promptKey(String keyName, String groupName)
	{
		return getInput("Enter key (" + keyName + ") for tracking group '" + groupName
						+ "': ");
	}
}
