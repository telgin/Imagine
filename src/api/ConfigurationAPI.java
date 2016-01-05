package api;

import java.util.List;

import algorithms.Algorithm;
import config.Configuration;
import config.DefaultConfigGenerator;
import data.TrackingGroup;
import runner.UsageException;
import util.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class ConfigurationAPI
{
	public static void install()
	{
		//create a config file
		DefaultConfigGenerator.create(Constants.CONFIG_FILE);
		
		//create these folders now, because it's better to show
		//they exist and are empty as opposed to creating them
		//when they're needed
		Configuration.getDatabaseFolder().mkdir();
		Configuration.getLogFolder().mkdir();
	}
	
	//Algorithm Preset Operations------------------------
	public static void addNewAlgorithmPreset(Algorithm algo) throws UsageException
	{
		String presetName = algo.getPresetName();
		
		if (presetName == null || presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (Configuration.getAlgorithmPresetNames().contains(presetName))
			throw new UsageException("The preset name must be unique.");
			
		Configuration.addAlgorithmPreset(algo);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static void deleteAlgorithmPreset(String presetName) throws UsageException
	{	
		if (presetName == null || presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (!Configuration.getAlgorithmPresetNames().contains(presetName))
			throw new UsageException("An algorithm by the given preset name does not exist.");
		
		Configuration.deleteAlgorithmPreset(presetName);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static Algorithm getAlgorithmPreset(String presetName) throws UsageException
	{
		if (presetName == null || presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (!Configuration.getAlgorithmPresetNames().contains(presetName))
			throw new UsageException("An algorithm by the given preset name does not exist.");
		
		return Configuration.getAlgorithmPreset(presetName);
	}
	
	public static List<String> getAlgorithmPresetNames()
	{
		return Configuration.getAlgorithmPresetNames();
	}
	
	//Tracking Group Operations------------------------
	public static void addNewTrackingGroup(TrackingGroup group) throws UsageException
	{
		String groupName = group.getName();

		if (groupName == null || groupName.length() == 0)
			throw new UsageException("The group name must be defined.");
		
		if (Configuration.getTrackingGroupNames().contains(groupName))
			throw new UsageException("The group name must be unique.");
		
		if (groupName.equals(Constants.TEMP_RESERVED_GROUP_NAME))
			throw new UsageException("'" + Constants.TEMP_RESERVED_GROUP_NAME + "' is "
							+ "a reserved group name and can not be used.");
		
		Configuration.addTrackingGroup(group);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static void updateTrackingGroup(TrackingGroup group) throws UsageException
	{
		String groupName = group.getName();
		
		if (groupName == null || groupName.length() == 0)
			throw new UsageException("The group name must be defined.");
		
		if (!Configuration.getTrackingGroupNames().contains(groupName))
			throw new UsageException("An tracking group by the given name does not exist.");
		
		Configuration.deleteTrackingGroup(groupName);
		Configuration.addTrackingGroup(group);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static void deleteTrackingGroup(String groupName) throws UsageException
	{
		if (groupName == null || groupName.length() == 0)
			throw new UsageException("The group name must be defined.");
		
		if (!Configuration.getTrackingGroupNames().contains(groupName))
			throw new UsageException("An tracking group by the given name does not exist.");
		
		Configuration.deleteTrackingGroup(groupName);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static List<String> getTrackingGroupNames()
	{
		return Configuration.getTrackingGroupNames();
	}
	
	public static TrackingGroup getTrackingGroup(String groupName) throws UsageException
	{
		if (groupName == null || groupName.length() == 0)
			throw new UsageException("The group name must be defined.");
		
		if (!Configuration.getTrackingGroupNames().contains(groupName))
			throw new UsageException("An tracking group by the given name does not exist.");
		
		return Configuration.getTrackingGroup(groupName);
	}
}
