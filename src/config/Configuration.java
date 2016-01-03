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
import product.ProductMode;
import logging.LogLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import algorithms.Algorithm;

public class Configuration {
	private static Document doc;
	private static Element root;
	
	private static File databaseFile;
	private static File productStagingFolder;
	private static File extractionFolder;
	private static File logFolder;
	private static List<TrackingGroup> trackingGroups;

	static
	{
		reloadConfig();
	}
	
	public static void reloadConfig()
	{
		doc = ConfigUtil.loadConfig(Constants.configFile);
		doc.getDocumentElement().normalize();
		
		root = doc.getDocumentElement();
	}
	
	public static void loadConfig(Document document)
	{
		doc = document;
		root = doc.getDocumentElement();
	}
	
	public static void saveConfig()
	{
		ConfigUtil.saveConfig(doc, Constants.configFile);
	}
	
	//TODO add setters
	
	public static void addTrackingGroup(TrackingGroup group)
	{
		Element groupNode = doc.createElement("Group");
		
		//group attributes
		groupNode.setAttribute("algorithmPresetName", group.getAlgorithm().getPresetName());
		groupNode.setAttribute("name", group.getName());
		groupNode.setAttribute("securityLevel", group.getAlgorithm().getProductSecurityLevel().toString());
		groupNode.setAttribute("usesDatabase", Boolean.toString(group.isUsingDatabase()));
		
		//tracked files
		Element trackedNode = doc.createElement("Tracked");
		for (File tracked : group.getTrackedFiles())
		{
			Element pathNode = doc.createElement("Path");
			pathNode.setAttribute("value", tracked.getAbsolutePath());
			trackedNode.appendChild(pathNode);
		}
		groupNode.appendChild(trackedNode);
		
		//untracked files
		Element untrackedNode = doc.createElement("Untracked");
		for (File untracked : group.getUntrackedFiles())
		{
			Element pathNode = doc.createElement("Path");
			pathNode.setAttribute("value", untracked.getAbsolutePath());
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
		locations.appendChild(mkPathNode("extraction", group.getExtractionFolder().getAbsolutePath()));
		locations.appendChild(mkPathNode("staging", group.getProductStagingFolder().getAbsolutePath()));
		groupNode.appendChild(locations);
		
		//append new tracking group to the list
		Element trackingGroupsNode = ConfigUtil.first(ConfigUtil.children(root, "TrackingGroups"));
		trackingGroupsNode.appendChild(groupNode);
	}
	
	public static File getDatabaseFile()
	{
		if (databaseFile == null)
		{
			Element databaseNode = ConfigUtil.first(ConfigUtil.children(root, "Database"));
			if (databaseNode != null)
			{
				Element databaseFileNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(databaseNode, "Path"),
								"name", "DatabaseFile"));
				
				databaseFile = new File(databaseFileNode.getAttribute("value"));	
			}
		}

		return databaseFile;
	}
	
	public static File getProductStagingFolder()
	{
		if (productStagingFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(root, "FileSystem"));
			if (fileSystemNode != null)
			{
				Element prodStagFoldNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(fileSystemNode, "Path"),
								"name", "ProductStagingFolder"));
				
				productStagingFolder = new File(prodStagFoldNode.getAttribute("value"));	
			}
		}

		return productStagingFolder;
	}
	
	public static File getExtractionFolder()
	{
		if (extractionFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(root, "FileSystem"));
			if (fileSystemNode != null)
			{
				Element extrFoldNode = ConfigUtil.first(
						ConfigUtil.filterByAttribute(ConfigUtil.children(fileSystemNode, "Path"),
								"name", "ExtractionFolder"));
				
				extractionFolder = new File(extrFoldNode.getAttribute("value"));	
			}
		}

		return extractionFolder;
	}
	
	public static File getLogFolder()
	{
		if (logFolder == null)
		{
			Element fileSystemNode = ConfigUtil.first(ConfigUtil.children(root, "FileSystem"));
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
			
			String extractionPath = ConfigUtil.first(
							ConfigUtil.filterByAttribute(
								ConfigUtil.children(locationsNode, "Path"), "name", "extraction"))
									.getAttribute("value");
			if (extractionPath.length() == 0)
				extractionPath = "extraction";
			group.setHashDBFile(new File(extractionPath));
			
			String stagingPath = ConfigUtil.first(
							ConfigUtil.filterByAttribute(
								ConfigUtil.children(locationsNode, "Path"), "name", "staging"))
									.getAttribute("value");
			if (stagingPath.length() == 0)
				stagingPath = "staging";
			group.setHashDBFile(new File(stagingPath));
			
			
			trackingGroups.add(group);
		}
	}
	
	public static TrackingGroup findTrackingGroup(String groupName)
	{
		for (TrackingGroup tg:trackingGroups)
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
	
	private static String getParameter(Element config, String name)
	{
		Element paramNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(
						ConfigUtil.children(config, "Parameter"), "name", name));
		
		if (paramNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find parameter: " + name);
			return null;
		}
			
		return paramNode.getAttribute("value");
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static String getInstallationUUID()
	{
		// TODO Set this at installation
		return "default";
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
