package config;

import data.TrackingGroup
import groovy.util.Node;
import util.ConfigUtil;
import util.Constants;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException

import logging.Logger;
import logging.LogLevel;

import org.xml.sax.SAXException

public class Configuration {
	private static Node root;
	private static List<TrackingGroup> trackingGroups;
	
	static
	{
		root = ConfigUtil.loadConfig(Constants.configFile);
	}
	
	public static File getDatabaseFile()
	{
		return new File(root.Database[0].Path[0].'@value');
	}
	
	public static File getProductStagingFolder()
	{
		return new File(root.FileSystem[0].Path[0].'@value');
	}
	
	public static File getExtractionFolder()
	{
		return new File(root.FileSystem[0].Path[1].'@value');
	}
	
	public static File getLogFolder()
	{
		return new File(root.FileSystem[0].Path[2].'@value');
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
		for (Node group:root.TrackingGroups[0].children())
		{
			TrackingGroup tg = new TrackingGroup(group.'@name');
			
			//add key
			if (group.'@isSecured' == 'true')
			{
				Node key = group.Key[0];
				if (key == null)
				{
					Logger.log(LogLevel.k_error, "There was no key for the secured group: " + tg.getName());
				}
				else
				{
					tg.setKeyName(key.'@name');
					Node keyPath = key.Path[0];
					if (keyPath != null)
					{
						tg.setKeyLocation(keyPath.'@value');
					}
				}
			}
			
			//add algorithm
			Node algoNode = group.Algorithm[0];
			new XmlNodePrinter().print(algoNode);
			if (algoNode == null)
			{
				Logger.log(LogLevel.k_error, "No algorithm node found for group: " + tg.getName());
			}
			else
			{
				tg.setAlgorithmName(algoNode.'@name');
			}
			
			//using database
			String bool = group.'@usesDatabase';
			println(bool);
			tg.setUsingDatabase(bool.equals("true"));
			
			//add paths
			for (Node path:group.Path)
			{
				new XmlNodePrinter().print(path);
				tg.addPath(path.'@value');
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
	
	private static Node getAlgorithm(String name)
	{
		Node algo = root.Algorithms[0].children().find {it.'@name' == name};
		if (algo == null)
			System.err.println("Could not find algorithm: " + name);
			
		return algo;
	}
	
	private static String getParameter(Node config, String name)
	{
		Node param = config.Parameters[0].children().find {it.'@name' == name};
		
		if (param == null)
			System.err.println("Could not find parameter: " + name);
			
		return param.'@value';
	}

	public static Integer getTextBlockBlockSize() {
		String value = getParameter(getAlgorithm("TextBlock"), "blockSize");
		return Integer.parseInt(value);
	}
	
}
