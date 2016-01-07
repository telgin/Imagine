package ui.cmd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import config.Configuration;
import data.FileKey;
import data.FileType;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import product.FileContents;
import product.ProductContents;
import product.ProductMode;
import ui.UI;
import util.Constants;

public class CmdUI extends UI
{
	private List<String> args;

	public CmdUI(List<String> args)
	{
		this.args = args;
	}
	
	private void usage(String message)
	{
		if (message != null)
			err(message);
		
		err("Usage:");
		err("(See 'imagine --help' for more details.)");
		err("imagine --gui\n");
		err("imagine --open -p <profilename> -i <file>");
		err("imagine --open -a <algo-pre-name> -i <file> [-k <keyfile>]\n");
		err("imagine --embed -p <profilename> -i <file/folder> -o <folder>");
		err("imagine --embed -a <algo-pre-name> -i <file/folder> -o <folder> [-k <keyfile>]\n");
		err("imagine --extract -p <profilename> -i <file/folder> -o <folder>");
		err("imagine --extract -a <algo-pre-name> -i <file/folder> -o <folder> [-k <keyfile>]");
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
			openArchiveParse(args);
		}
		else if (args.contains("--embed"))
		{
			args.remove("--embed");
			embedSection();
		}
		else if (args.contains("--extract"))
		{
			args.remove("--extract");
			extractSection(args);
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
		p("imagine --open -p <profilename> -i <file>");
		p("imagine --open -a <algo-pre-name> -i <file> [-k <keyfile>]\n");
		p("imagine --embed -p <profilename> -i <file/folder> -o <folder>");
		p("imagine --embed -a <algo-pre-name> -i <file/folder> -o <folder> [-k <keyfile>]\n");
		p("imagine --extract -p <profilename> -i <file/folder> -o <folder>");
		p("imagine --extract -a <algo-pre-name> -i <file/folder> -o <folder> [-k <keyfile>]\n");
		
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
	}

	/**
	 * @update_comment
	 * @param subargs
	 */
	private void openArchiveParse(List<String> subargs)
	{
		if (!subargs.contains("-p") && !subargs.contains("-a"))
		{
			usage("Either a profile name (-p) or an algorithm preset name (-a) must be specified.");
		}
		else if (!subargs.contains("-i"))
		{
			usage("The input file (-i) must be specified.");
		}
		else if (subargs.contains("-p") && subargs.contains("-a"))
		{
			usage("Either specify an algorithm preset name or a profile name, not both.");
		}
		else
		{
			String profileName = null;
			String presetName = null;
			File inputFile = null;
			File keyFile = null;
			
			try
			{
				if (subargs.contains("-p"))
					profileName = subargs.get(subargs.indexOf("-p")+1);
				
				if (subargs.contains("-a"))
					presetName = subargs.get(subargs.indexOf("-a")+1);
				
				if (subargs.contains("-i"))
					inputFile = new File(subargs.get(subargs.indexOf("-i")+1));
				
				if (subargs.contains("-k"))
					keyFile = new File(subargs.get(subargs.indexOf("-k")+1));
				
				openArchive(profileName, presetName, inputFile, keyFile);
			}
			catch (Exception e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				usage(null);
			}
		}
	}
	
	private void openArchive(String profileName, String presetName, File inputFile, File keyFile)
	{
		try
		{
			TrackingGroup group = null;
			if (profileName != null)
			{
				group = ConfigurationAPI.getTrackingGroup(profileName);
				
			}
			else
			{
				Algorithm preset = ConfigurationAPI.getAlgorithmPreset(presetName);
				
				if (preset.getProductSecurityLevel().equals(ProductMode.NORMAL))
				{
					group = ConversionAPI.createTemporaryTrackingGroup(presetName);
				}
				else if (keyFile == null)
				{
					Key passKey = new PasswordKey(Constants.TEMP_KEY_NAME, Constants.TEMP_RESERVED_GROUP_NAME);
					group = ConversionAPI.createTemporaryTrackingGroup(presetName, passKey);
				}
				else
				{
					Key fileKey = new FileKey(Constants.TEMP_KEY_NAME,
									Constants.TEMP_RESERVED_GROUP_NAME, keyFile);
					group = ConversionAPI.createTemporaryTrackingGroup(presetName, fileKey);
				}
			}
			
			
			ProductContents productContents = ConversionAPI.openArchive(group, inputFile);
			
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
			
			File outputFile = group.getStaticOutputFolder();
			if (outputFile == null)
				outputFile = new File(".");
			
			ConversionAPI.extractFile(group, inputFile, outputFile, choice);
		}
		catch (IOException | UsageException e)
		{
			usage(e.getMessage());
			Logger.log(LogLevel.k_debug, e, false);
		}
		
	}

	/**
	 * @update_comment
	 */
	private void embedSection()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @param subargs
	 */
	private void extractSection(List<String> subargs)
	{
		// TODO Auto-generated method stub
		
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
		
		int index = viewTrackingGroupsMenu.getChosenIndex();


		try
		{
			TrackingGroup selected = ConfigurationAPI.getTrackingGroup(groupNames.get(index));
			System.out.println(selected.toString());
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_error, e, false);
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
	
	


}
