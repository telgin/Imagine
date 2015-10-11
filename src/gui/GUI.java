package gui;

import java.io.File;

import runner.Runner;

public abstract class GUI {

	private Runner runner;
	
	public void setRunner(Runner runner) {
		this.runner = runner;
	}

	public abstract void runnerStartupMessage();
	
	public void shutdown()
	{
		runner.shutdown();
	}

	public abstract void showControlPanel();
	
	public abstract void showBackupPanel();

	public abstract String promptTrackingGroup();

	public abstract File promptKeyFileLocation(String keyName, String groupName);

	public abstract String promptKey(String keyName, String groupName);
}
