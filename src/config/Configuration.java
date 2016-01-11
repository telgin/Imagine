package config;

import data.FileKey;
import data.Key;
import data.NullKey;
import data.PasswordKey;
import data.TrackingGroup;
import util.ConfigUtil;
import util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import logging.LogLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import algorithms.Algorithm;

public class Configuration {
	private static Document doc;
	private static Element root;
	
	private static File databaseFolder;
	private static File logFolder;
	private static String installationUUID;
	private static List<TrackingGroup> trackingGroups;

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
		trackingGroups = null;
		databaseFolder = null;
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
	
	public static void addTrackingGroup(TrackingGroup group)
	{
		Element groupNode = doc.createElement("Group");
		
		//group attributes
		groupNode.setAttribute("algorithmPresetName", group.getAlgorithm().getPresetName());
		groupNode.setAttribute("name", group.getName());
		groupNode.setAttribute("securityLevel", group.getAlgorithm().getProductSecurityLevel().toString());
		groupNode.setAttribute("usesDatabase", Boolean.toString(group.isUsingIndexFiles()));
		
		//tracked files
		Element trackedNode = doc.createElement("Tracked");
		for (File tracked : group.getTrackedFiles())
		{
			Element pathNode = doc.createElement("Path");
			
			if (group.usesAbsolutePaths())
				pathNode.setAttribute("value", tracked.getAbsolutePath());
			else
				pathNode.setAttribute("value", tracked.getPath());
			
			trackedNode.appendChild(pathNode);
		}
		groupNode.appendChild(trackedNode);
		
		//untracked files
		Element untrackedNode = doc.createElement("Untracked");
		for (File untracked : group.getUntrackedFiles())
		{
			Element pathNode = doc.createElement("Path");
			
			if (group.usesAbsolutePaths())
				pathNode.setAttribute("value", untracked.getAbsolutePath());
			else
				pathNode.setAttribute("value", untracked.getPath());
			
			untrackedNode.appendChild(pathNode);
		}
		groupNode.appendChild(untrackedNode);
		
		//key
		Element keyNode = doc.createElement("Key");
		keyNode.setAttribute("name", group.getKey().getName());
		keyNode.setAttribute("type", group.getKey().getType());
		
		if (group.getKey().getType().equals("FileKey"))
		{
			File keyFile = ((FileKey)group.getKey()).getKeyFile();
			Element pathNode = doc.createElement("Path");
			pathNode.setAttribute("value", keyFile.getAbsolutePath());
			keyNode.appendChild(pathNode);
		}
		groupNode.appendChild(keyNode);
		
		//config locations
		Element locations = doc.createElement("Locations");
		locations.appendChild(mkPathNode("hashdb", group.getHashDBFile().getAbsolutePath()));
		if (group.getStaticOutputFolder() != null)
			locations.appendChild(mkPathNode("output", group.getStaticOutputFolder().getAbsolutePath()));
		groupNode.appendChild(locations);
		
		//append new tracking group to the list
		Element trackingGroupsNode = ConfigUtil.first(ConfigUtil.children(root, "TrackingGroups"));
		trackingGroupsNode.appendChild(groupNode);
	}
	
	public static File getDatabaseFolder()
	{
		if (databaseFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(root, "System"));
			if (fileSystemNode != null)
			{
				Element prodStagFoldNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(fileSystemNode, "Path"),
								"name", "DatabaseFolder"));
				
				databaseFolder = new File(prodStagFoldNode.getAttribute("value"));	
			}
		}

		return databaseFolder;
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

	public static List<TrackingGroup> getTrackingGroups()
	{
		if (trackingGroups == null)
			parseTrackingGroups();
			
		return trackingGroups;
	}
	
	private static void parseTrackingGroups()
	{
		trackingGroups = new ArrayList<TrackingGroup>();
		Element trackingGroupsNode = ConfigUtil.first(ConfigUtil.children(root, "TrackingGroups"));
		for (Element groupNode:ConfigUtil.children(trackingGroupsNode, "Group"))
		{
			String groupName = groupNode.getAttribute("name");

			//add algorithm
			String presetName = groupNode.getAttribute("algorithmPresetName");
			Algorithm groupAlgo = getAlgorithmPreset(presetName);
			if (groupAlgo == null)
			{
				Logger.log(LogLevel.k_fatal, "No algorithm node found for group: " + groupName);
			}
			
			//add key
			Element keyNode = ConfigUtil.first(ConfigUtil.children(groupNode, "Key"));
			Key groupKey = null;
			if (keyNode.getAttribute("type").equals("NullKey"))
			{
				groupKey = new NullKey();
			}
			else if (keyNode.getAttribute("type").equals("PasswordKey"))
			{
				groupKey = new PasswordKey(keyNode.getAttribute("name"), groupName);
			}
			else
			{
				Element keyPath = ConfigUtil.first(ConfigUtil.children(keyNode, "Path"));
				if (keyPath != null)
				{
					groupKey = new FileKey(keyNode.getAttribute("name"), groupName,
							new File(keyPath.getAttribute("value")));
				}
				else
				{
					Logger.log(LogLevel.k_fatal, "The file key does not define a path in tracking group: " + groupName);
				}
			}
			
			
			//using database
			boolean usesDatabase = Boolean.parseBoolean(groupNode.getAttribute("usesDatabase"));
			
			TrackingGroup group = new TrackingGroup(groupName, usesDatabase, groupAlgo, groupKey);
			
			//tracked paths
			for (Element pathNode : ConfigUtil.children(
										ConfigUtil.first(
											ConfigUtil.children(
													groupNode,
													"Tracked")),
										"Path"))
			{
				group.addTrackedPath(pathNode.getAttribute("value"));
			}
			
			//untracked paths
			for (Element pathNode : ConfigUtil.children(
										ConfigUtil.first(
											ConfigUtil.children(
													groupNode,
													"Untracked")),
										"Path"))
			{
				group.addUntrackedPath(pathNode.getAttribute("value"));
			}
			
			//locations
			Element locationsNode = ConfigUtil.first(ConfigUtil.children(groupNode, "Locations"));
			String hashDBPath = ConfigUtil.first(
							ConfigUtil.filterByAttribute(
								ConfigUtil.children(locationsNode, "Path"), "name", "hashdb"))
									.getAttribute("value");
			if (hashDBPath.length() == 0)
				hashDBPath = "hashdb.db";
			group.setHashDBFile(new File(hashDBPath));
			
			Element staticOutputElement = ConfigUtil.first(
							ConfigUtil.filterByAttribute(
								ConfigUtil.children(locationsNode, "Path"), "name", "output"));
			
			if (staticOutputElement != null)
				group.setStaticOutputFolder(new File(staticOutputElement.getAttribute("value")));
			
			
			trackingGroups.add(group);
		}
	}
	
	public static TrackingGroup getTrackingGroup(String groupName)
	{
		for (TrackingGroup tg:getTrackingGroups())
			if (tg.getName().equals(groupName))
				return tg;
				
		Logger.log(LogLevel.k_error, "Could not find tracking group: " + groupName);
		return null;
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
	
	public static List<String> getTrackingGroupNames()
	{
		//easy way to load if null
		List<TrackingGroup> tempList = getTrackingGroups();
		
		List<String> names = new LinkedList<String>();
		for (TrackingGroup group : tempList)
			names.add(group.getName());
		
		names.sort(null);
		
		return names;
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
