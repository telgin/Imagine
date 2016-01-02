package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import logging.LogLevel;
import logging.Logger;
import stats.ProgressMonitor;
import stats.StateStat;
import config.Configuration;
import data.TrackingGroup;

public class ConversionRunner extends Runner
{
	private ControlPanelRunner controlPanelRunner;
	private HashMap<TrackingGroup, ConversionJob> backupJobs;
	private List<Thread> jobThreads;

	public ConversionRunner()
	{
		backupJobs = new HashMap<TrackingGroup, ConversionJob>();
		jobThreads = new ArrayList<Thread>();

		ProgressMonitor.addStat(new StateStat("filesProcessed", 0.0));
		ProgressMonitor.addStat(new StateStat("productsCreated", 0.0));
	}

	public void setControlPanelRunner(ControlPanelRunner controlPanel)
	{
		controlPanelRunner = controlPanel;
	}

	public boolean isRunning()
	{
		for (Thread thread : jobThreads)
		{
			if (thread.isAlive())
				return true;
		}

		return false;
	}

	public void runAllBackups()
	{
		Logger.log(LogLevel.k_debug, "Running Backup...?");

		List<TrackingGroup> groups = Configuration.getTrackingGroups();

		Logger.log(LogLevel.k_debug, "Found " + groups.size() + " groups.");

		// run the backups
		for (TrackingGroup group : groups)
			runBackup(group);
	}

	public void runBackup(TrackingGroup group)
	{
		Logger.log(LogLevel.k_general, "Running backup for group: " + group.getName());
		if (backupJobs.containsKey(group))
		{
			assert(backupJobs.get(group) == null || backupJobs.get(group).isFinished());
		}

		ConversionJob job = new ConversionJob(group, 5);
		Thread jobThread = new Thread(job);
		backupJobs.put(group, job);
		jobThreads.add(jobThread);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
	}

	@Override
	public void shutdown()
	{
		Logger.log(LogLevel.k_debug, "Backup shutdown");

		for (TrackingGroup group : backupJobs.keySet())
		{
			backupJobs.get(group).shutdown();
		}

		for (Thread thread : jobThreads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		if (controlPanelRunner == null)
			controlPanelRunner = new ControlPanelRunner();

		getActiveGUI().setRunner(controlPanelRunner);
		getActiveGUI().showControlPanel();
	}
}
