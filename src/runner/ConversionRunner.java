package runner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import stats.ProgressMonitor;
import stats.StateStat;
import util.Constants;
import config.Configuration;
import data.Key;
import data.NullKey;
import data.TrackingGroup;

public abstract class ConversionRunner
{

	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName, Key key, File selection)
	{
		String groupName = Constants.TEMP_RESERVED_GROUP_NAME;
		Algorithm algorithm = Configuration.getAlgorithmPreset(algoPresetName);
		TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
		group.addTrackedPath(selection);
		
		return group;
	}
	
	public static TrackingGroup createTemporaryTrackingGroup(String algoPresetName, File selection)
	{
		return createTemporaryTrackingGroup(algoPresetName, new NullKey(), selection);
	}

	public static ConversionJob runConversion(TrackingGroup group, int threads)
	{
		Logger.log(LogLevel.k_general, "Running conversion for group: " + group.getName());
		
		ConversionJob job = new ConversionJob(group, threads);
		Thread jobThread = new Thread(job);

		Logger.log(LogLevel.k_debug, "Starting job thread...");
		jobThread.start();
		return job;
	}
}
