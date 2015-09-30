package config

import groovy.util.Node;
import util.ConfigUtil;
import util.Constants;

class DefaultConfigGenerator {

	static main(args) {
		Node root = ConfigUtil.getNewRootNode();
		
		
		addAlgorithmsNode(root);
		addDatabaseNode(root);
		addFileSystemNode(root);
		addTrackingGroupsNode(root); //add params for zipping, implementation supported encryption, working dir for temp files, special output dir that otherwise defaults to filesystem one
		
		ConfigUtil.saveConfig(root, Constants.configFile);
	}
	
	private static void addFileSystemNode(Node config)
	{
		Node fileSystem = new Node(config, "FileSystem");
		new Node(fileSystem, "Path", ["name":"ProductStagingFolder", "value":"products"]);
		new Node(fileSystem, "Path", ["name":"ExtractionFolder", "value":"extractions"]);
		new Node(fileSystem, "Path", ["name":"LogFolder", "value":"logs"]);
	}
	private static void addDatabaseNode(Node config) {
		Node database = new Node(config,"Database");
		new Node(database,"Path", ["name":"DatabaseFile", value:"FileIndex.sqlite"]);
	}
	
	private static void addAlgorithmsNode(Node config)
	{
		Node algorithms = new Node(config, "Algorithms");
		addFullPNGAlgorithm(algorithms);
		addTextBlockAlgorithm(algorithms);
	}
	
	private static Node addAlgorithmNode(Node config, String name)
	{
		Node algorithm = new Node(config, "Algorithm", ["name":name]);
		Node parameters = new Node(algorithm, "Parameters");
		return algorithm;
	}
	
	private static Node addParameterNode(Node algorithmNode, String name, String value)
	{
		Node param = new Node(algorithmNode.Parameters[0], "Parameter", ["name":name, "value":value]);
		return param;
	}
	
	private static void addFullPNGAlgorithm(Node config)
	{
		Node algorithm = addAlgorithmNode(config, "FullPNG");
		addParameterNode(algorithm, "colors", "rgb");
		addParameterNode(algorithm, "maxWidth", "1820");
		addParameterNode(algorithm, "maxHeight", "980");
	}
	
	private static void addTextBlockAlgorithm(Node config)
	{
		Node algorithm = addAlgorithmNode(config, "TextBlock");
		addParameterNode(algorithm, "blockSize", "102400");
	}
	
	private static void addTrackingGroupsNode(Node config)
	{
		Node trackingGroups = new Node(config, "TrackingGroups");
		Node untrackedGroup = addTrackingGroup(trackingGroups, "Untracked", false, false);
		
		//test untracked paths:
		addPath(untrackedGroup, "testGroupInput\\folder\\untracked.txt");
		addPath(untrackedGroup, "testGroupInput\\folder\\untracked");
		
		//test tracking group:
		Node testGroup = addTrackingGroup(trackingGroups, "Test", false, false);
		addPath(testGroup, "testGroupInput");
		
		Node key = addKey(testGroup, "potatoes");
		addPath(key, "keys\\key1.txt");
		new Node(testGroup, "Algorithm", ["name":"TextBlock"]);
	}
	
	private static Node addPath(Node config, String path)
	{
		Node pathNode = new Node(config, "Path", ["value":path]);
		return pathNode;
	}
	
	private static Node addKey(Node config, String keyName)
	{
		Node key = new Node(config, "Key", ["name":keyName]);
		return key;
	}
	
	private static Node addTrackingGroup(Node config, String groupName, boolean isSecured, boolean usesDatabase)
	{
		Node trackingGroup = new Node(config, "Group", ["name":groupName, "isSecured":isSecured, "usesDatabase":usesDatabase]);
		return trackingGroup;
	}

}
