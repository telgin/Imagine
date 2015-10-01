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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
			Element databaseNode = getFirstElement(getElementsByTagName(root, "Database"));
			if (databaseNode != null)
			{
				Element databaseFileNode = getFirstElement(
						filterByAttribute(getElementsByTagName(databaseNode, "Path"),
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
			Element fileSystemNode = getFirstElement(getElementsByTagName(root, "FileSystem"));
			if (fileSystemNode != null)
			{
				Element prodStagFoldNode = getFirstElement(
						filterByAttribute(getElementsByTagName(fileSystemNode, "Path"),
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
			Element fileSystemNode = getFirstElement(getElementsByTagName(root, "FileSystem"));
			if (fileSystemNode != null)
			{
				Element extrFoldNode = getFirstElement(
						filterByAttribute(getElementsByTagName(fileSystemNode, "Path"),
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
			Element fileSystemNode = getFirstElement(getElementsByTagName(root, "FileSystem"));
			if (fileSystemNode != null)
			{
				Element logFoldNode = getFirstElement(
						filterByAttribute(getElementsByTagName(fileSystemNode, "Path"),
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
		Element trackingGroupsNode = getFirstElement(getElementsByTagName(root, "TrackingGroups"));
		for (Element group:getElementsByTagName(trackingGroupsNode, "Group"))
		{
			TrackingGroup tg = new TrackingGroup(group.getAttribute("name"));
			
			//add key
			if (!group.getAttribute("SecurityLevel").equals("Normal"))
			{
				Element key = getFirstElement(getElementsByTagName(group, "Key"));
				if (key == null)
				{
					Logger.log(LogLevel.k_error, "There was no key for the secured group: " + tg.getName());
				}
				else
				{
					tg.setKeyName(key.getAttribute("name"));
					Element keyPath = getFirstElement(getElementsByTagName(key, "Path"));
					if (keyPath != null)
					{
						tg.setKeyLocation(new File(keyPath.getAttribute("value")));
					}
				}
			}
			
			//add algorithm
			Element algoNode = getFirstElement(getElementsByTagName(group, "Algorithm"));
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
			for (Element path:getElementsByTagName(group, "Path"))
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
			Logger.log(LogLevel.k_debug, "Trying to get key of group which isn't secured: " + groupName);
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
		Element supportedAlgosNode = getFirstElement(getElementsByTagName(root, "SupportedAlgorithms"));
		Element algoNode = getFirstElement(
				filterByAttribute(
						getElementsByTagName(supportedAlgosNode, "Algorithm"), "name", name));
		
		if (algoNode == null)
		{
			Logger.log(LogLevel.k_debug, "Could not find algorithm: " + name);
			return null;
		}
			
		return algoNode;
	}
	
	private static String getParameter(Element config, String name)
	{
		Element paramNode = getFirstElement(
				filterByAttribute(getElementsByTagName(config, "Parameter"), "name", name));
		
		if (paramNode == null)
		{
			Logger.log(LogLevel.k_debug, "Could not find parameter: " + name);
			return null;
		}
			
		return paramNode.getAttribute("value");
	}

	public static Integer getTextBlockBlockSize() {
		String value = getParameter(getAlgorithm("TextBlock"), "blockSize");
		return Integer.parseInt(value);
	}
	
	
	
	//TODO: move to config util ----------------------------------------------------------
	private static Element getFirstElement(ArrayList<Element> elements)
	{
		if (elements.isEmpty())
			return null;
		return elements.get(0);
	}
	
	private static ArrayList<Element> filterByAttribute(ArrayList<Element> elements, String name, String value)
	{
		ArrayList<Element> filtered = new ArrayList<Element>();
		for (Element e:elements)
			if (e.hasAttribute(name) && e.getAttribute(name).equals(value))
				filtered.add(e);
				
		return filtered;
				
	}
	
	private static ArrayList<Element> getElementsByTagName(Element parent, String tag)
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = parent.getElementsByTagName(tag);
		
		for (int i=0; i<nodes.getLength(); ++i)
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				elements.add((Element) nodes.item(i));
		
		return elements;
	}
	
	
}
