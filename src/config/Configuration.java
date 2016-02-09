package config;

import util.ConfigUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import logging.LogLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import algorithms.Algorithm;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.StaticKey;

public class Configuration {
	private static Document doc;
	private static Element root;
	
	private static File logFolder;
	private static String installationUUID;

	static
	{
		reloadConfig();
	}
	
	public static void reloadConfig()
	{
		doc = ConfigUtil.loadConfig(Constants.CONFIG_FILE);
		doc.getDocumentElement().normalize();
		
		root = doc.getDocumentElement();
		
		//everything will be reloaded when needed
		logFolder = null;
	}
	
	public static void loadConfig(Document document)
	{
		doc = document;
		root = doc.getDocumentElement();
	}
	
	public static void saveConfig()
	{
		ConfigUtil.saveConfig(doc, Constants.CONFIG_FILE);
	}
	
	public static void deleteTrackingGroup(String groupName)
	{
		Element trackingGroupsNode = ConfigUtil.first(
						ConfigUtil.children(root, "TrackingGroups"));
		Element toRemove = getTrackingGroupElement(groupName);
		trackingGroupsNode.removeChild(toRemove);
	}
	
	public static File getLogFolder()
	{
		if (logFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(root, "System"));
			if (fileSystemNode != null)
			{
				Element logFoldNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(fileSystemNode, "Path"),
								"name", "LogFolder"));
				
				logFolder = new File(logFoldNode.getAttribute("value"));	
			}
		}

		return logFolder;
	}
	
	public static Algorithm getAlgorithmPreset(String presetName)
	{
		return new Algorithm(getAlgorithmPresetElement(presetName));
	}
	
	public static List<String> getAlgorithmPresetNames()
	{
		List<String> presetNames = new LinkedList<String>();
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(root, "AlgorithmPresets"));
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
	
	private static Element getAlgorithmPresetElement(String presetName)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(root, "AlgorithmPresets"));
		Element algoNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(
						ConfigUtil.children(algoPresetsNode, "Algorithm"), "presetName", presetName));
		
		if (algoNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find algorithm: " + presetName);
			return null;
		}
		
		return algoNode;
	}
	
	private static Element getTrackingGroupElement(String groupName)
	{
		Element trackingGroupsNode = ConfigUtil.first(
						ConfigUtil.children(root, "TrackingGroups"));
		Element groupNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(
						ConfigUtil.children(trackingGroupsNode, "Group"), "name", groupName));
		
		if (groupNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find tracking group element: " + groupName);
			return null;
		}
		
		return groupNode;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static String getInstallationUUID()
	{
		if (installationUUID == null)
		{
			Element systemNode = ConfigUtil.first(ConfigUtil.children(root, "System"));
			if (systemNode != null)
			{
				Element uuidNode = ConfigUtil.first(ConfigUtil.children(systemNode, "InstallationUUID"));
				
				installationUUID = uuidNode.getAttribute("value");	
			}
		}

		return installationUUID;
	}
	
	public static void addAlgorithmPreset(Algorithm algo)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(root, "AlgorithmPresets"));
		algoPresetsNode.appendChild(algo.toElement(doc));
	}
	
	public static void deleteAlgorithmPreset(String presetName)
	{
		Element algoPresetsNode = ConfigUtil.first(
						ConfigUtil.children(root, "AlgorithmPresets"));
		Element toRemove = getAlgorithmPresetElement(presetName);
		algoPresetsNode.removeChild(toRemove);
	}
	
	private static Element mkPathNode(String name, String value)
	{
		Element element = doc.createElement("Path");
		if (name != null)
			element.setAttribute("name", name);
		element.setAttribute("value", value);
		return element;
	}
}
