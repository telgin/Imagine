package ui;

import java.io.File;

import runner.SystemManager;

public abstract class UI
{
	public abstract void runnerStartupMessage();

	public void shutdown()
	{
		SystemManager.shutdown();
	}

	public abstract void showControlPanel();

	public abstract void showBackupPanel();

	public abstract String promptTrackingGroup();

	public abstract File promptKeyFileLocation(String keyName, String groupName);

	public abstract String promptKey(String keyName, String groupName);

	/**
	 * Starts the execution of the command line arguments
	 */
	public abstract void processArgs();
}
