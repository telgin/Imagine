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
	/**
	 * @update_comment
	 */
	public static void install()
	{
		//create a config file
		DefaultConfigGenerator.create(Constants.CONFIG_FILE);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @throws UsageException
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
	 * @update_comment
	 * @param p_presetName
	 * @throws UsageException
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
	 * @update_comment
	 * @param p_presetName
	 * @return
	 * @throws UsageException
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
	 * @update_comment
	 * @return
	 */
	public static List<String> getAlgorithmPresetNames()
	{
		return Configuration.getAlgorithmPresetNames();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public static List<String> getAlgorithmDefinitionNames()
	{
		return AlgorithmRegistry.getAlgorithmNames();
	}
	
	/**
	 * @update_comment
	 * @param p_algoDefName
	 * @return
	 * @throws UsageException
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
