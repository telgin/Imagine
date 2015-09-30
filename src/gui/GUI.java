package gui;

import java.io.File;

import data.TrackingGroup;
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
	
	public void runBackup()
	{
		runner.runBackup();
	}

	public abstract void showControlPanel();
	
	public abstract void showBackupPanel();

	public abstract String promptTrackingGroup();

	public abstract File promptKeyFileLocation(TrackingGroup trackingGroup);

	public abstract String promptKey(TrackingGroup trackingGroup);
}
