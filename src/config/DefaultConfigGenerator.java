package config;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import util.ConfigUtil;

public abstract class DefaultConfigGenerator
{
	private static Document doc;

	public static void main(String[] args)
	{
		//creates a config file at current location
		create(Constants.CONFIG_FILE);
	}
	
	public static void create(File configFile)
	{
		// save new default config to default location
		ConfigUtil.saveConfig(makeBasicConfig(), configFile);
		
		//reload the config
		Configuration.reloadConfig();
		
		//add algorithm presets
		for (String algoName : AlgorithmRegistry.getAlgorithmNames())
		{
			for (Algorithm preset : AlgorithmRegistry.getAlgorithmPresets(algoName))
			{
				Configuration.addAlgorithmPreset(preset);
			}
		}
		
		//save configuration
		Configuration.saveConfig();
	}

	private static Document makeBasicConfig()
	{
		// create new doc
		doc = ConfigUtil.getNewDocument();

		//setup basic nodes
		Element root = mkElement("Configuration");

		root.appendChild(mkElement("AlgorithmPresets"));
		root.appendChild(mkElement("TrackingGroups"));
		root.appendChild(mkSystemNode());

		doc.appendChild(root);

		return doc;
	}

	private static Element mkElement(String tagName)
	{
		return doc.createElement(tagName);
	}

	private static Element mkSystemNode()
	{
		Element system = mkElement("System");
		
		//folders
		system.appendChild(mkPathNode("LogFolder", "logs"));
		system.appendChild(mkPathNode("DatabaseFolder", "databases"));
		
		//installation uuid
		Element installationUUID = mkElement("InstallationUUID");
		String uuid = Long.toString(System.currentTimeMillis());
		installationUUID.setAttribute("value", uuid);
		system.appendChild(installationUUID);
		
		return system;
	}

	private static Node mkPathNode(String name, String value)
	{
		Element element = mkElement("Path");
		if (name != null)
			element.setAttribute("name", name);
		element.setAttribute("value", value);
		return element;
	}

}
