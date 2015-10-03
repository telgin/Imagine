package config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import algorithms.AlgorithmRegistry;
import runner.Initialization;
import util.ConfigUtil;
import util.Constants;

class DefaultConfigGenerator {
	private static Document doc;
	public static void main(String[] args) {
		Initialization.init();
		
		//create new doc
		doc = ConfigUtil.getNewDocument();
		Element root = mkElement("Configuration");
		
		root.appendChild(mkSupportedAlgorithmsNode());
		root.appendChild(mkDatabaseNode());
		root.appendChild(mkFileSystemNode());
		root.appendChild(mkTrackingGroupsNode()); 
		
		doc.appendChild(root);
		
		//save doc
		ConfigUtil.saveConfig(doc, Constants.configFile);
		
		//TODO: use json or sax to preserve attribute order for readability
	}
	
	private static Element mkElement(String tagName)
	{
		return doc.createElement(tagName);
	}
	
	private static Element mkFileSystemNode()
	{
		Element fileSystem = mkElement("FileSystem");
		fileSystem.appendChild(mkPathNode("ProductStagingFolder", "products"));
		fileSystem.appendChild(mkPathNode("ExtractionFolder", "extractions"));
		fileSystem.appendChild(mkPathNode("LogFolder", "logs"));
		return fileSystem;
	}
	private static Node mkPathNode(String name, String value) {
		Element element = mkElement("Path");
		element.setAttribute("name", name);
		element.setAttribute("value", value);
		return element;
	}

	private static Element mkDatabaseNode() {
		Element database = mkElement("Database");
		database.appendChild(mkPathNode("DatabaseFile", "FileIndex.sqlite"));
		return database;
	}
	
	private static Element mkSupportedAlgorithmsNode()
	{
		Element algorithms = mkElement("SupportedAlgorithms");
		for (String algoName : AlgorithmRegistry.getAlgorithmNames())
			algorithms.appendChild(AlgorithmRegistry.getAlgorithmSpec(algoName).toElement(doc));
		return algorithms;
	}
	
	private static Element mkAlgorithmNode(String name)
	{
		Element algorithm = mkElement("Algorithm");
		algorithm.setAttribute("name", name);
		return algorithm;
	}
	
	private static Element mkParameterNode(String name, String value, boolean optional)
	{
		Element element = mkElement("Parameter");
		element.setAttribute("name", name);
		element.setAttribute("value", value);
		element.setAttribute("optional", Boolean.toString(optional));
		if (optional)
			element.setAttribute("enabled", Boolean.toString(false));
		return element;
	}
	
	private static Element mkFullPNGAlgorithm()
	{
		Element algorithm = mkAlgorithmNode("FullPNG");
		algorithm.appendChild(mkParameterNode("colors", "rgb", false));
		algorithm.appendChild(mkParameterNode("maxWidth", "1820", false));
		algorithm.appendChild(mkParameterNode("maxHeight", "980", false));
		
		return algorithm;
	}
	
	private static Element mkTextBlockAlgorithm()
	{
		Element algorithm = mkAlgorithmNode("TextBlock");
		algorithm.appendChild(mkParameterNode("blockSize", "102400", false));
		return algorithm;
	}
	
	private static Element mkTrackingGroupsNode()
	{
		String tgPathName = "owned location";
		
		Element trackingGroups = mkElement("TrackingGroups");
		
		//untracked group
		Element untrackedGroup = mkTrackingGroup("Untracked", "Normal", false);
		
		//test untracked paths:
		untrackedGroup.appendChild(mkPathNode(tgPathName, "testGroupInput\\folder\\untracked.txt"));
		untrackedGroup.appendChild(mkPathNode(tgPathName, "testGroupInput\\folder\\untracked"));
		
		trackingGroups.appendChild(untrackedGroup);
		
		//test tracking group:
		Element testGroup = mkTrackingGroup("Test", "Normal", false);
		testGroup.appendChild(mkPathNode(tgPathName, "testGroupInput"));
		
		Element key = mkElement("Key");
		key.setAttribute("name", "potatoes");
		key.appendChild(mkParameterNode("Key File", "keys\\key1.txt", true));
		testGroup.appendChild(key);
		
		testGroup.appendChild(AlgorithmRegistry.getDefaultAlgorithm("TextBlock").toElement(doc));
		
		trackingGroups.appendChild(testGroup);
		return trackingGroups;
	}
	
	/**
	 * TODO: add params for working dir for temp files, special output dir that otherwise defaults to filesystem one
	 * @param groupName
	 * @param isSecured
	 * @param usesDatabase
	 * @return
	 */
	private static Element mkTrackingGroup(String groupName, String securityLevel, boolean usesDatabase)
	{
		Element trackingGroup = mkElement("Group");
		trackingGroup.setAttribute("name", groupName);
		trackingGroup.setAttribute("securityLevel", securityLevel);
		trackingGroup.setAttribute("usesDatabase", Boolean.toString(usesDatabase));
		return trackingGroup;
	}

}
