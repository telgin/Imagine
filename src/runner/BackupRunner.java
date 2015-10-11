package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import logging.LogLevel;
import logging.Logger;
import config.Configuration;
import data.TrackingGroup;

public class BackupRunner extends Runner {
	private ControlPanelRunner controlPanelRunner;
	private HashMap<TrackingGroup, BackupJob> backupJobs;
	private List<Thread> jobThreads;
	
	public BackupRunner()
	{
	}

	public void setControlPanelRunner(ControlPanelRunner controlPanel)
	{
		controlPanelRunner = controlPanel;
	}
	
	@Override
	public void runBackup() {
		Logger.log(LogLevel.k_debug, "Running Backup...?");
		
		backupJobs = new HashMap<TrackingGroup, BackupJob>();
		jobThreads = new ArrayList<Thread>();
		
		List<TrackingGroup> groups = Configuration.getTrackingGroups();
		
		Logger.log(LogLevel.k_debug, "Found " + groups.size() + " groups.");
		
		//run the backups
		for (TrackingGroup group: groups)
			if (!group.getName().equals("Untracked"))
				runBackup(group);
		
		//show progress of the backup jobs
		//pass in 'ProgressMonitor' to things, or just static, add stat and such?
			//thread safety
		
		//wait so something can happen in testing:
		System.out.println("sleeping");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//for now, shutdown and block here
		shutdown();
		
		Logger.log(LogLevel.k_debug, "Backup done.");
	}
	
	private void runBackup(TrackingGroup group)
	{
		Logger.log(LogLevel.k_general, "Running backup for group: " + group.getName());
		if (backupJobs.containsKey(group))
		{
			assert (backupJobs.get(group) == null || backupJobs.get(group).isFinished());
		}
		
		BackupJob job = new BackupJob(group, 5, 5);
		Thread jobThread = new Thread(job);
		backupJobs.put(group, job);
		jobThreads.add(jobThread);
		
		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
	}

	@Override
	public void shutdown() {
		Logger.log(LogLevel.k_debug, "Backup shutdown");
		
		for (TrackingGroup group:backupJobs.keySet())
		{
			backupJobs.get(group).shutdown();
		}
		
		for(Thread thread:jobThreads)
		{
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//TODO remove this, put it where it should be
		Logger.shutdown();
		
		if (controlPanelRunner == null)
			controlPanelRunner = new ControlPanelRunner();
		
		getActiveGUI().setRunner(controlPanelRunner);
		getActiveGUI().showControlPanel();
	}
}
