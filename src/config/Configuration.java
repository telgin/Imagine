package config;

import data.TrackingGroup;
import util.ConfigUtil;
import util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import logging.Logger;
import logging.LogLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	public static int getFullPNGMaxWidth()
	{
		String value = getParameter(getAlgorithm("FullPNG"), "maxWidth");
		return Integer.parseInt(value);
	}
	
	public static int getFullPNGMaxHeight()
	{
		String value = getParameter(getAlgorithm("FullPNG"), "maxHeight");
		return Integer.parseInt(value);
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
		for (Element group:ConfigUtil.children(trackingGroupsNode, "Group"))
		{
			TrackingGroup tg = new TrackingGroup(group.getAttribute("name"));
			
			//add key
			if (!group.getAttribute("SecurityLevel").equals("Normal"))
			{
				Element key = ConfigUtil.first(ConfigUtil.children(group, "Key"));
				if (key == null)
				{
					Logger.log(LogLevel.k_error, "There was no key for the secured group: " + tg.getName());
				}
				else
				{
					tg.setKeyName(key.getAttribute("name"));
					Element keyPath = ConfigUtil.first(ConfigUtil.children(key, "Path"));
					if (keyPath != null)
					{
						tg.setKeyLocation(new File(keyPath.getAttribute("value")));
					}
				}
			}
			
			//add algorithm
			Element algoNode = ConfigUtil.first(ConfigUtil.children(group, "Algorithm"));
			if (algoNode == null)
			{
				Logger.log(LogLevel.k_error, "No algorithm node found for group: " + tg.getName());
			}
			else
			{
				tg.setAlgorithmName(algoNode.getAttribute("name"));
			}
			
			//using database
			tg.setUsingDatabase(Boolean.parseBoolean(group.getAttribute("usesDatabase")));
			
			//add paths
			for (Element path:ConfigUtil.children(group, "Path"))
			{
				tg.addPath(path.getAttribute("value"));
			}
			trackingGroups.add(tg);
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

	public static String getGroupKeyName(String groupName) {
		TrackingGroup tg = findTrackingGroup(groupName);
		if (!tg.isSecure())
		{
			Logger.log(LogLevel.k_error, "Trying to get key of group which isn't secured: " + groupName);
			return null;
		}
		else
		{
			return tg.getKeyName();
		}
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
