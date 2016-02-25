package config;

import java.io.File;
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
 * @update_comment
 */
public class Configuration {
	private static Document s_document;
	private static Element s_root;
	private static File s_logFolder;

	static
	{
		reloadConfig();
	}
	
	/**
	 * @update_comment
	 */
	public static void reloadConfig()
	{
		s_document = ConfigUtil.loadConfig(Constants.CONFIG_FILE);
		s_document.getDocumentElement().normalize();
		
		s_root = s_document.getDocumentElement();
		
		//everything will be reloaded when needed
		s_logFolder = null;
	}
	
	/**
	 * @update_comment
	 * @param p_document
	 */
	public static void loadConfig(Document p_document)
	{
		s_document = p_document;
		s_root = s_document.getDocumentElement();
	}
	
	/**
	 * @update_comment
	 */
	public static void saveConfig()
	{
		ConfigUtil.saveConfig(s_document, Constants.CONFIG_FILE);
		Logger.log(LogLevel.k_info, "Configuration saved to file: " + Constants.CONFIG_FILE.getAbsolutePath());
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public static File getLogFolder()
	{
		if (s_logFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(s_root, "System"));
			if (fileSystemNode != null)
			{
				Element logFoldNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(fileSystemNode, "Path"),
								"name", "LogFolder"));
				
				s_logFolder = new File(logFoldNode.getAttribute("value"));	
			}
		}

		return s_logFolder;
	}
	
	/**
	 * @update_comment
	 * @param p_presetName
	 * @return
	 */
	public static Algorithm getAlgorithmPreset(String p_presetName)
	{
		return new Algorithm(getAlgorithmPresetElement(p_presetName));
	}
	
	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @param p_presetName
	 * @return
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
	 * @update_comment
	 * @param p_algo
	 */
	public static void addAlgorithmPreset(Algorithm p_algo)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		algoPresetsNode.appendChild(p_algo.toElement(s_document));
	}
	
	/**
	 * @update_comment
	 * @param p_presetName
	 */
	public static void deleteAlgorithmPreset(String p_presetName)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(s_root, "AlgorithmPresets"));
		Element toRemove = getAlgorithmPresetElement(p_presetName);
		algoPresetsNode.removeChild(toRemove);
	}
}
