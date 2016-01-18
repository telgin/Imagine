package api;

import java.util.List;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Configuration;
import config.Constants;
import config.DefaultConfigGenerator;
import data.TrackingGroup;

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
			throw new UsageException("An algorithm by the preset name of '" + presetName + "' does not exist.");
		
		Configuration.deleteAlgorithmPreset(presetName);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	public static Algorithm getAlgorithmPreset(String presetName) throws UsageException
	{
		if (presetName == null || presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (!Configuration.getAlgorithmPresetNames().contains(presetName))
			throw new UsageException("An algorithm by the preset name of '" + presetName + "' does not exist.");
		
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
			throw new UsageException("A tracking group by the name of '" + groupName + "' does not exist.");
		
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
			throw new UsageException("A tracking group by the name of '" + groupName + "' does not exist.");
		
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
			throw new UsageException("A tracking group by the name of '" + groupName + "' does not exist.");
		
		return Configuration.getTrackingGroup(groupName);
	}
	
	public static List<String> getAlgorithmDefinitionNames()
	{
		return AlgorithmRegistry.getAlgorithmNames();
	}
	
	public static String getAlgorithmDefinitionDescription(String algoDefName) throws UsageException
	{
		if (algoDefName == null || algoDefName.length() == 0)
			throw new UsageException("The algorithm definition name must be defined.");
		
		if (!AlgorithmRegistry.getAlgorithmNames().contains(algoDefName))
			throw new UsageException("An algorithm definition by the name of '" + algoDefName + "' does not exist.");
		
		return AlgorithmRegistry.getAlgorithmDefinitionDescription(algoDefName);
	}
	
	public static Algorithm getAlgorithmDefinition(String algoDefName) throws UsageException
	{
		if (algoDefName == null || algoDefName.length() == 0)
			throw new UsageException("The algorithm definition name must be defined.");
		
		if (!AlgorithmRegistry.getAlgorithmNames().contains(algoDefName))
			throw new UsageException("An algorithm definition by the name of '" + algoDefName + "' does not exist.");
		
		return AlgorithmRegistry.getAlgorithmSpec(algoDefName);
	}
}
