package gui.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import data.TrackingGroup;

import gui.GUI;

public class CmdGUI extends GUI {
	private BufferedReader cin;
	public CmdGUI()
	{
		cin = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void runnerStartupMessage() {
		p("The system is starting up...");
		mainMenu();
	}
	
	private void mainMenu() {
		
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
					try
					{
						runBackup();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
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

	private void createTrackingGroupPrompts() {
		// TODO Auto-generated method stub
		
	}
	

	private void fileInfoMenu() {
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

	private void fileStatusPrompt() {
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

	private void imageContentsPrompt() {
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
		try {
			return cin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private final void p(String print)
	{
		System.out.println(print);
	}

	@Override
	public void showControlPanel() {
		mainMenu();
	}

	@Override
	public void showBackupPanel() {
		p("Starting backup...");
	}

	@Override
	public String promptTrackingGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File promptKeyFileLocation(String keyName, String groupName) {
		return new File(getInput("Enter key (" + keyName + 
				") file location for tracking group " + groupName + ": "));
	}

	@Override
	public String promptKey(String keyName, String groupName) {
		return getInput("Enter key (" + keyName + ") for tracking group: " + groupName);
	}
}
