package ui;

import java.io.File;

import system.SystemManager;

public abstract class UI
{
	public static void shutdown(Void v)
	{
		SystemManager.shutdown();
	}

	public abstract File promptKeyFileLocation(String keyName, String groupName);

	public abstract String promptKey(String keyName, String groupName);

	/**
	 * Starts the execution of the command line arguments
	 */
	public abstract void processArgs();

	/**
	 * @update_comment
	 * @param enclosingFolder
	 * @param curProductFolder
	 * @param productSearchName
	 * @return
	 */
	public abstract File promptEnclosingFolder(File curEnclosingFolder, File curProductFolder,
					String productSearchName);
}
