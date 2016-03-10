package config;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import system.Imagine;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Generates a default configuration file which includes default presets.
 */
public abstract class DefaultConfigGenerator
{
	private static Document s_document;

	/**
	 * An entry point for creating the default config file. Used for testing.
	 * @param p_args The command line arguments
	 */
	public static void main(String[] p_args)
	{
		//regenerate default config
		Imagine.run(new String[]{"--install"});
	}
	
	/**
	 * Saves a default configuration to the file specified.
	 * @param p_configFile The file to save the default config in
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
	 * Generates a blank default configuration without any listed presets
	 * @return The xml document containing the blank config
	 */
	private static Document makeBasicConfig()
	{
		// create new doc
		s_document = ConfigUtil.getNewDocument();

		//setup basic nodes
		Element root = mkElement("Configuration");

		root.appendChild(mkElement("AlgorithmPresets"));

		s_document.appendChild(root);

		return s_document;
	}

	/**
	 * Utility function which creates an element with the specified tag name.
	 * @param p_tagName The tag name
	 * @return The element that was created
	 */
	private static Element mkElement(String p_tagName)
	{
		return s_document.createElement(p_tagName);
	}
}
