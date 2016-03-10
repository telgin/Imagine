package config;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles reading and writing to the configuration file which mainly holds
 * information about algorithm presets.
 */
public class Configuration {
	private static Document s_document;
	private static Element s_root;

	static
	{
		reloadConfig();
	}
	
	/**
	 * Reloads the xml document from the config file
	 */
	public static void reloadConfig()
	{
		s_document = ConfigUtil.loadConfig(Constants.CONFIG_FILE);
		s_document.getDocumentElement().normalize();
		
		s_root = s_document.getDocumentElement();
	}
	
	/**
	 * Loads the specified xml document
	 * @param p_document The xml document to load
	 */
	public static void loadConfig(Document p_document)
	{
		s_document = p_document;
		s_root = s_document.getDocumentElement();
	}
	
	/**
	 * Saves the xml document to the config file
	 */
	public static void saveConfig()
	{
		ConfigUtil.saveConfig(s_document, Constants.CONFIG_FILE);
		Logger.log(LogLevel.k_info, "Configuration saved to file: " + Constants.CONFIG_FILE.getAbsolutePath());
	}
	
	/**
	 * Gets the algorithm preset with the given name
	 * @param p_presetName The preset name to look up
	 * @return The algorithm preset of the given name
	 */
	public static Algorithm getAlgorithmPreset(String p_presetName)
	{
		return new Algorithm(getAlgorithmPresetElement(p_presetName));
	}
	
	/**
	 * Gets the list of algorithm preset names stored in the configuration
	 * @return The list of preset names
	 */
	public static List<String> getAlgorithmPresetNames()
	{
		List<String> presetNames = new LinkedList<String>();
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		for (Element algoPreset : ConfigUtil.children(algoPresetsNode, "Algorithm"))
		{
			String presetName = algoPreset.getAttribute("presetName");
			if (presetName.length() != 0)
			{
				presetNames.add(presetName);
			}
		}
		
		presetNames.sort(null);
		
		return presetNames;
	}
	
	/**
	 * Gets the algorithm preset element associated with the given name
	 * @param p_presetName The preset name to search for
	 * @return The algorithm element
	 */
	private static Element getAlgorithmPresetElement(String p_presetName)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		Element algoNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(
						ConfigUtil.children(algoPresetsNode, "Algorithm"), "presetName", p_presetName));
		
		if (algoNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find algorithm: " + p_presetName);
			return null;
		}
		
		return algoNode;
	}
	
	/**
	 * Adds an algorithm preset to the configuration. Changes will not be saved unless
	 * saving is explicitly called.
	 * @param p_algo The algorithm to add
	 */
	public static void addAlgorithmPreset(Algorithm p_algo)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		algoPresetsNode.appendChild(p_algo.toElement(s_document));
	}
	
	/**
	 * Deletes an algorithm preset from the configuration. Changes will not be saved unless
	 * saving is explicitly called.
	 * @param p_presetName The name of the preset to delete
	 */
	public static void deleteAlgorithmPreset(String p_presetName)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		Element toRemove = getAlgorithmPresetElement(p_presetName);
		algoPresetsNode.removeChild(toRemove);
	}
}
