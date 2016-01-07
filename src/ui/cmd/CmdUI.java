package ui.cmd;

import java.io.File;
import java.util.List;
import java.util.Set;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.UsageException;
import config.Configuration;
import data.FileKey;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import product.ProductMode;
import ui.UI;

public class CmdUI extends UI
{
	private List<String> args;
	
	//menu definitions
	private static CallbackMenu mainMenu;
	private static CallbackMenu manageTrackingGroupsMenu;
	private static CallbackMenu manageAlgorithmsMenu;
	private static CallbackMenu openArchiveMenu;
	private static Menu nextMenu;
	
	//menu display functions
//	private static void mainMenu(Void v) { mainMenu.display(); }
//	private static void manageTrackingGroupsMenu(Void v) { manageTrackingGroupsMenu.display(); }
//	private static void manageAlgorithmsMenu(Void v) { manageAlgorithmsMenu.display(); }
//	private static void openArchiveMenu(Void v) { openArchiveMenu.display(); }
//	private static void embedDataMenu(Void v) { embedDataMenu.display(); }
//	private static void extractDataMenu(Void v) { extractDataMenu.display(); }
	
	//menu options
	static
	{
		mainMenu = new CallbackMenu("Main Menu");
		manageTrackingGroupsMenu = new CallbackMenu("Manage Tracking Groups Menu");
		manageAlgorithmsMenu = new CallbackMenu("Manage Algorithms Menu");
		openArchiveMenu = new CallbackMenu("Open Archive Menu");
		
		mainMenu.addOption("Manage Tracking Groups", manageTrackingGroupsMenu);
		mainMenu.addOption("Manage Algorithm Presets", manageAlgorithmsMenu);
		mainMenu.addOption("Embed Data", CmdUI::embedDataPrompts);
		mainMenu.addOption("Open Archive (View / Extract Data)", openArchiveMenu);
		mainMenu.addOption("Exit", CmdUI::shutdown);
		
		manageTrackingGroupsMenu.addOption("View Tracking Group", CmdUI::viewTrackingGroupPrompts);
		manageTrackingGroupsMenu.addOption("Create Tracking Group", CmdUI::createTrackingGroupPrompts);
		manageTrackingGroupsMenu.addOption("Edit Tracking Group", CmdUI::editTrackingGroupPrompts);
		manageTrackingGroupsMenu.addOption("Delete Tracking Group", CmdUI::deleteTrackingGroupPrompts);
		manageTrackingGroupsMenu.addOption("Back To Previous Menu", mainMenu);
		
		manageAlgorithmsMenu.addOption("View Algorithm", CmdUI::viewAlgorithmPrompts);
		manageAlgorithmsMenu.addOption("Create Algorithm", CmdUI::createAlgorithmPrompts);
		manageAlgorithmsMenu.addOption("Edit Algorithm", CmdUI::editAlgorithmPrompts);
		manageAlgorithmsMenu.addOption("Delete Algorithm", CmdUI::deleteAlgorithmPrompts);
		manageAlgorithmsMenu.addOption("Back To Previous Menu", mainMenu);
		
		openArchiveMenu.addOption("View Product File Contents and Selectivly Extract Data", CmdUI::viewProductFilePrompts);
		openArchiveMenu.addOption("Extract All Data From One or More Product Files", CmdUI::extractAllPrompts);
		openArchiveMenu.addOption("Back To Previous Menu", mainMenu);
		
		
		
		
		
		
		
		
		
	}
	
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
			nextMenu = mainMenu;
		
		
		while (nextMenu != null)
		{
			Menu menu = nextMenu;
			nextMenu = null;
			menu.display();
		}
	}

	/**
	 * @update_comment
	 */
	private static void viewTrackingGroupPrompts(Void v)
	{
		CallbackMenu viewTrackingGroupsMenu = new CallbackMenu("Select A Name For More Details");
		//viewTrackingGroupsMenu.setSubtext("Select a number or type in the name of a tracking group:");
		
		List<String> groupNames = ConfigurationAPI.getTrackingGroupNames();
		for (String name : groupNames)
			viewTrackingGroupsMenu.addOption(name);
		
		String back = "Back to Tracking Group Menu";
		viewTrackingGroupsMenu.addOption(back, manageTrackingGroupsMenu);
		viewTrackingGroupsMenu.display();
		
		
		int index = viewTrackingGroupsMenu.getChosenIndex();
		
		if (viewTrackingGroupsMenu.getIndexOfOption(back) == index)
		{
			nextMenu = manageTrackingGroupsMenu;
		}
		else
		{
			try
			{
				TrackingGroup selected = ConfigurationAPI.getTrackingGroup(groupNames.get(index));
				
				System.out.println(selected.toString());
				nextMenu = manageTrackingGroupsMenu;
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e, false);
				nextMenu = manageTrackingGroupsMenu;
			}
		}
		
	}

	/**
	 * @update_comment
	 */
	private static void editTrackingGroupPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void deleteTrackingGroupPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void viewAlgorithmPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void createAlgorithmPrompts(Void v)
	{
		
	}

	/**
	 * @update_comment
	 */
	private static void editAlgorithmPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void deleteAlgorithmPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void extractAllPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 */
	private static void embedDataPrompts(Void v)
	{
		// TODO Auto-generated method stub
		
	}

	private static void createTrackingGroupPrompts(Void v)
	{

		// name
		String groupName = promptInput("Enter a new name for the tracking group: ");
		
		// algorithm
		Algorithm algorithm = chooseAlgorithmPrompts();
		
		// key
		Key key = chooseKeyPrompts(groupName, algorithm.getProductSecurityLevel());

		// database
		p("Enabling tracking will cause file records from previous runs to be cached,");
		p("meaning that only unique/new files will be added to products.");
		boolean usesDatabase =
						promptInput("Should files belonging to this group be tracked within the file system? [y/n]: ")
										.toLowerCase().equals("y");

		

		boolean chooseFiles = promptInput("Would you like to specify "
						+ "paths/files to add to the tracking group now? [y/n]: ").toLowerCase().equals("y");
		
		
		TrackingGroup created = new TrackingGroup(groupName, usesDatabase, algorithm, key);
		
		//hashdb file

		// tracked/untracked files
		if (chooseFiles)
		{
			addTrackedFilesPrompts(created);
			addUrackedFilesPrompts(created);
		}
		
		try
		{
			ConfigurationAPI.addNewTrackingGroup(created);
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_error, e, false);
			p("An error occured and the tracking group could not be saved:");
			p(e.getMessage());
		}
		
		nextMenu = manageTrackingGroupsMenu;
	}

	/**
	 * @update_comment
	 * @param created
	 */
	private static void addTrackedFilesPrompts(TrackingGroup created)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @param created
	 */
	private static void addUrackedFilesPrompts(TrackingGroup created)
	{
		// TODO Auto-generated method stub
		
	}

	private static Key chooseKeyPrompts(String groupName, ProductMode mode)
	{
		Key key;
		
		if (mode.equals(ProductMode.NORMAL))
		{
			key = new NullKey();
		}
		else
		{
			Menu securityMenu = new Menu("Security Menu");
			securityMenu.setSubtext("How would you like to secure product files generated by this group?");
			securityMenu.addOption("Use Password");
			securityMenu.addOption("Use Key File");
			securityMenu.display();
	
			String keyName = promptInput(
						"Enter a name for your password or key file (like a hint): ");
			
			if (securityMenu.getChosenIndex() == 0)
			{
				p("The system will prompt you for your password when it is needed.");
				key = new PasswordKey(keyName, groupName);
			}
			else
			{
				File keyFile = null;
				
				while (keyFile == null)
				{
					String input = promptInput(
									"Enter key file location (or return to have the "
									+ "system prompt for it when needed): ");
					keyFile = new File(input);
					if (!keyFile.exists())
					{
						p("Sorry, a file by that name could not be located.");
						keyFile = null;
					}
				}
				
				key = new FileKey(keyName, groupName, keyFile);
			}
		}

		return key;
	}

	private static Algorithm chooseAlgorithmPrompts()
	{
		List<String> presetNames = ConfigurationAPI.getAlgorithmPresetNames();
		
		CallbackMenu algoMenu = new CallbackMenu("Choose an algorithm preset");
		for (String presetName : presetNames)
		{
			try
			{
				ProductMode mode = ConfigurationAPI.getAlgorithmPreset(presetName)
								.getProductSecurityLevel();
				
				String security = null;
				if (mode.equals(ProductMode.NORMAL))
					security = "not secured";
				else if (mode.equals(ProductMode.SECURE))
					security = "clear headers";
				else
					security = "secured";
				
				algoMenu.addOption(presetName + " (" + security + ")");
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e, false);
			}
		}
		
		algoMenu.display();
		
		try
		{
			Algorithm chosen = ConfigurationAPI.getAlgorithmPreset(
							presetNames.get(algoMenu.getChosenIndex()));
			return chosen;
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_error, e, false);
			return null;
		}
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

	private static void viewProductFilePrompts(Void v)
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
