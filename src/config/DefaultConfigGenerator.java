package config;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import system.Imagine;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class DefaultConfigGenerator
{
	private static Document s_document;

	/**
	 * @update_comment
	 * @param p_args
	 */
	public static void main(String[] p_args)
	{
		//regenerate default config
		Imagine.run(new String[]{"--install"});
	}
	
	/**
	 * @update_comment
	 * @param p_configFile
	 */
	public static void create(File p_configFile)
	{
		// save new default config to default location
		ConfigUtil.saveConfig(makeBasicConfig(), p_configFile);
		
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

	/**
	 * @update_comment
	 * @return
	 */
	private static Document makeBasicConfig()
	{
		// create new doc
		s_document = ConfigUtil.getNewDocument();

		//setup basic nodes
		Element root = mkElement("Configuration");

		root.appendChild(mkElement("AlgorithmPresets"));
		root.appendChild(mkSystemNode());

		s_document.appendChild(root);

		return s_document;
	}

	/**
	 * @update_comment
	 * @param p_tagName
	 * @return
	 */
	private static Element mkElement(String p_tagName)
	{
		return s_document.createElement(p_tagName);
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static Element mkSystemNode()
	{
		Element system = mkElement("System");
		
		//folders
		system.appendChild(mkPathNode("LogFolder", "logs"));
		
		return system;
	}

	/**
	 * @update_comment
	 * @param p_name
	 * @param p_value
	 * @return
	 */
	private static Node mkPathNode(String p_name, String p_value)
	{
		Element element = mkElement("Path");
		if (p_name != null)
			element.setAttribute("name", p_name);
		element.setAttribute("value", p_value);
		return element;
	}

}
