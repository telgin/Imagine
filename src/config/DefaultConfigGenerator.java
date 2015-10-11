package config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import algorithms.AlgorithmRegistry;
import util.ConfigUtil;
import util.Constants;

class DefaultConfigGenerator {
	private static Document doc;
	public static void main(String[] args) {
		//save new default config to default location
		ConfigUtil.saveConfig(makeDefaultConfig(), Constants.configFile);
	}
	
	public static Document makeDefaultConfig()
	{
		//create new doc
		doc = ConfigUtil.getNewDocument();
		
		Element root = mkElement("Configuration");
		
		root.appendChild(mkSupportedAlgorithmsNode());
		root.appendChild(mkDatabaseNode());
		root.appendChild(mkFileSystemNode());
		root.appendChild(mkTrackingGroupsNode()); 
		
		doc.appendChild(root);
		
		return doc;
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
		if (name != null)
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
	
	private static Element mkParameterNode(String name, String value, boolean optional, boolean enabled)
	{
		Element element = mkElement("Parameter");
		element.setAttribute("name", name);
		element.setAttribute("value", value);
		element.setAttribute("optional", Boolean.toString(optional));
		if (optional)
			element.setAttribute("enabled", Boolean.toString(enabled));
		return element;
	}
	
	private static Element mkTrackingGroupsNode()
	{
		Element trackingGroups = mkElement("TrackingGroups");

		//Create Temporary Test Group
		trackingGroups.appendChild(mkTestTrackingGroup());
		
		return trackingGroups;
	}
	
	private static Element mkTestTrackingGroup()
	{
		//test tracking group:
		Element testGroup = mkTrackingGroup("Test", "Normal", false);
		
		//tracked paths
		Element tracked = mkElement("Tracked");
		tracked.appendChild(mkPathNode(null, "testGroupInput"));
		testGroup.appendChild(tracked);
		
		//untracked paths
		Element untracked = mkElement("Untracked");
		untracked.appendChild(mkPathNode(null, "testGroupInput/folder/untracked.txt"));
		untracked.appendChild(mkPathNode(null, "testGroupInput/folder/untracked"));
		testGroup.appendChild(untracked);
		
		Element key = mkElement("Key");
		key.setAttribute("name", "potatoes");
		key.appendChild(mkParameterNode("Key File", "keys/key1.txt", true, true));
		testGroup.appendChild(key);
		
		testGroup.appendChild(AlgorithmRegistry.getDefaultAlgorithm("TextBlock").toElement(doc));
		
		return testGroup;
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
