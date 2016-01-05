package ui.cmd;

import java.io.File;
import java.util.List;

import algorithms.Algorithm;
import config.Configuration;
import data.FileKey;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;
import product.ProductMode;
import ui.UI;

public class CmdUI extends UI
{
	private List<String> args;
	
	public CmdUI(List<String> args)
	{
		this.args = args;
	}
	
	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void processArgs()
	{
		if (args.isEmpty())
			mainMenu();
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
		p("\t1\tmanage tracking groups");
		p("\t2\tmanage algorithm presets");
		p("\t3\topen archive to view contents");
		p("\t4\tembed data");
		p("\t5\textract data");
		p("\t6\texit");

		int choice = 0;
		while (choice == 0)
		{
			try
			{
				choice = Integer.parseInt(promptInput(""));
				if (choice == 1)
					manageTrackingGroupsMenu();
				else if (choice == 2)
					manageAlgorithmsMenu();
				else if (choice == 3)
					openArchiveMenu();
				else if (choice == 4)
					embedDataMenu();
				else if (choice == 5)
					extractDataMenu();
				else if (choice == 6)
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

	/**
	 * @update_comment
	 */
	private void extractDataMenu()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private void embedDataMenu()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private void openArchiveMenu()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private void manageAlgorithmsMenu()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private void manageTrackingGroupsMenu()
	{
		// TODO Auto-generated method stub
		
	}

	private void createTrackingGroupPrompts()
	{

		// name
		String groupName = promptInput("Enter a new name for the tracking group: ");

		// mode
		ProductMode mode = ProductMode.getMode(promptInput("Enter the security level: "));

		// database
		boolean usesDatabase =
						promptInput("Should files belonging to this group be tracked within the file system? [y/n]: ")
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
		String keyName = promptInput(
						"Enter a name for your password or key file (like a hint): ");

		if (mode.equals(ProductMode.NORMAL))
		{
			key = new NullKey();
		}
		else
		{
			if (promptInput("Would you like to use a key file? [y/n]: ").toLowerCase()
							.equals("y"))
			{
				// key file
				File keyFile = null;
				String input = promptInput(
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

//	private void waitForBackup() throws InterruptedException
//	{
//		while (backupRunner.isRunning())
//		{
//			Thread.sleep(1000);
//			int filesProcessed = (int) ProgressMonitor.getStat("filesProcessed")
//							.getNumericProgress().doubleValue();
//			int productsCreated = (int) ProgressMonitor.getStat("productsCreated")
//							.getNumericProgress().doubleValue();
//			Logger.log(LogLevel.k_info, "Files Processed: " + filesProcessed
//							+ ", Products Created: " + productsCreated);
//		}
//	}

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
				choice = Integer.parseInt(promptInput(""));
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
			path = promptInput("Enter the path of a file: ");
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
			imagePath = promptInput("Enter the path of an image product: ");
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

	private String promptInput(String prompt)
	{
		System.out.print(prompt);
		return CMDInput.getLine();
	}

	/**
	 * typing this sucks
	 * @param print
	 */
	private final void p(String print)
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
		return new File(promptInput("Enter key (" + keyName
						+ ") file location for tracking group '" + groupName + "': "));
	}

	@Override
	public String promptKey(String keyName, String groupName)
	{
		return promptInput("Enter key (" + keyName + ") for tracking group '" + groupName
						+ "': ");
	}
	
	private void usage(String message)
	{
		if (message != null)
			err(message);
		
		err("Usage:");
		err("(See 'imagine --help' for more details.)");
		err("imagine --gui\n");
		err("imagine --open -g <groupname> -i <file>");
		err("imagine --open -a <presetname> -i <file> [-k <keyfile>]\n");
		err("imagine --embed -g <groupname> -i <file/folder> -o <folder>");
		err("imagine --embed -a <presetname> -i <file/folder> -o <folder> [-k <keyfile>]\n");
		err("imagine --extract -g <groupname> -i <file/folder> -o <folder>");
		err("imagine --extract -a <presetname> -i <file/folder> -o <folder> [-k <keyfile>]");
		
	}


}
