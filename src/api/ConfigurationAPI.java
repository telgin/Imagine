package api;

import java.util.List;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Configuration;
import config.Constants;
import config.DefaultConfigGenerator;

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
	
	public static List<String> getAlgorithmDefinitionNames()
	{
		return AlgorithmRegistry.getAlgorithmNames();
	}
	
	public static Algorithm getDefaultAlgorithm(String algoDefName) throws UsageException
	{
		if (algoDefName == null || algoDefName.length() == 0)
			throw new UsageException("The algorithm definition name must be defined.");
		
		if (!AlgorithmRegistry.getAlgorithmNames().contains(algoDefName))
			throw new UsageException("An algorithm definition by the name of '" + algoDefName + "' does not exist.");
		
		return AlgorithmRegistry.getDefaultAlgorithm(algoDefName);
	}
}
