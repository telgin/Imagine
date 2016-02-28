package api;

import java.util.List;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Configuration;
import config.Constants;
import config.DefaultConfigGenerator;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Contains functions which allow for a standard means to change the configuration.
 */
public abstract class ConfigurationAPI
{
	/**
	 * Installs the software. Currently, just generates a default configuration.
	 */
	public static void install()
	{
		//create a config file
		DefaultConfigGenerator.create(Constants.CONFIG_FILE);
	}
	
	/**
	 * Adds a new algorithm preset to the configuration
	 * @param p_algo The algorithm preset
	 * @throws UsageException If the algorithm could not be added
	 */
	public static void addNewAlgorithmPreset(Algorithm p_algo) throws UsageException
	{
		String presetName = p_algo.getPresetName();
		
		if (presetName == null || presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (Configuration.getAlgorithmPresetNames().contains(presetName))
			throw new UsageException("The preset name must be unique.");
			
		Configuration.addAlgorithmPreset(p_algo);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	/**
	 * Deletes an algorithm preset from the configuration
	 * @param p_presetName The preset name to delete
	 * @throws UsageException If the algorithm could not be deleted
	 */
	public static void deleteAlgorithmPreset(String p_presetName) throws UsageException
	{	
		if (p_presetName == null || p_presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (!Configuration.getAlgorithmPresetNames().contains(p_presetName))
			throw new UsageException("An algorithm by the preset name of '" 
				+ p_presetName + "' does not exist.");
		
		Configuration.deleteAlgorithmPreset(p_presetName);
		Configuration.saveConfig();
		Configuration.reloadConfig();
	}
	
	/**
	 * Gets the algorithm preset by the specified preset name.
	 * @param p_presetName The preset name to search for
	 * @return The algorithm preset
	 * @throws UsageException If the algorithm could not be found
	 */
	public static Algorithm getAlgorithmPreset(String p_presetName) throws UsageException
	{
		if (p_presetName == null || p_presetName.length() == 0)
			throw new UsageException("The preset name must be defined.");
		
		if (!Configuration.getAlgorithmPresetNames().contains(p_presetName))
			throw new UsageException("An algorithm by the preset name of '" 
				+ p_presetName + "' does not exist.");
		
		return Configuration.getAlgorithmPreset(p_presetName);
	}
	
	/**
	 * Gets the list of preset names from the configuration
	 * @return The list of preset names
	 */
	public static List<String> getAlgorithmPresetNames()
	{
		return Configuration.getAlgorithmPresetNames();
	}
	
	/**
	 * Gets the list of algorithm definition names.
	 * @return The list of algorithm definition names
	 */
	public static List<String> getAlgorithmDefinitionNames()
	{
		return AlgorithmRegistry.getAlgorithmNames();
	}
	
	/**
	 * Gets the defined default algorithm, which is like a preset with all default values.
	 * @param p_algoDefName The algorithm definition name
	 * @return The default algorithm
	 * @throws UsageException If the default algorithm could not be found
	 */
	public static Algorithm getDefaultAlgorithm(String p_algoDefName) throws UsageException
	{
		if (p_algoDefName == null || p_algoDefName.length() == 0)
			throw new UsageException("The algorithm definition name must be defined.");
		
		if (!AlgorithmRegistry.getAlgorithmNames().contains(p_algoDefName))
			throw new UsageException("An algorithm definition by the name of '"
				+ p_algoDefName + "' does not exist.");
		
		return AlgorithmRegistry.getDefaultAlgorithm(p_algoDefName);
	}
}
