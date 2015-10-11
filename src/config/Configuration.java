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
import java.util.List;
import logging.Logger;
import product.ProductMode;
import logging.LogLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		doc = ConfigUtil.loadConfig(Constants.configFile);
		doc.getDocumentElement().normalize();
		
		root = doc.getDocumentElement();
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

	public static List<TrackingGroup> getTrackingGroups() {
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
			Element algoNode = ConfigUtil.first(ConfigUtil.children(groupNode, "Algorithm"));
			if (algoNode == null)
			{
				Logger.log(LogLevel.k_fatal, "No algorithm node found for group: " + groupName);
			}
			
			Algorithm groupAlgo = new Algorithm(algoNode);
			
			//add key
			Key groupKey;
			if (groupAlgo.getProductSecurityLevel().equals(ProductMode.NORMAL))
			{
				groupKey = new NullKey();
			}
			else
			{
				Element keyNode = ConfigUtil.first(ConfigUtil.children(groupNode, "Key"));
				if (keyNode == null)
				{
					Logger.log(LogLevel.k_error, "There was no key for the secured group: " + groupName);
					Logger.log(LogLevel.k_warning, "Using default password key for group: " + groupName);
					groupKey = new PasswordKey("default", groupName);
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
						groupKey = new PasswordKey(keyNode.getAttribute("name"), groupName);
					}
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

	public static boolean groupUsingDatabase(String groupName) {
		return findTrackingGroup(groupName).isUsingDatabase();
	}
	
	private static Element getAlgorithm(String name)
	{
		Element supportedAlgosNode = ConfigUtil.first(ConfigUtil.children(root, "SupportedAlgorithms"));
		Element algoNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(
						ConfigUtil.children(supportedAlgosNode, "Algorithm"), "name", name));
		
		if (algoNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find algorithm: " + name);
			return null;
		}
			
		return algoNode;
	}
	
	private static String getParameter(Element config, String name)
	{
		Element paramNode = ConfigUtil.first(
				ConfigUtil.filterByAttribute(ConfigUtil.children(config, "Parameter"), "name", name));
		
		if (paramNode == null)
		{
			Logger.log(LogLevel.k_error, "Could not find parameter: " + name);
			return null;
		}
			
		return paramNode.getAttribute("value");
	}

	public static Integer getTextBlockBlockSize() {
		String value = getParameter(getAlgorithm("TextBlock"), "blockSize");
		return Integer.parseInt(value);
	}
}
