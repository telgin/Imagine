package algorithms;

import java.util.HashMap;

public class AlgorithmRegistry {
	//static storage variables
	private static HashMap<String, Algorithm> defaults;
	private static HashMap<String, Definition> definitions;
	
	private AlgorithmRegistry(){}
	
	public static AlgorithmRegistry getInstance()
	{
		return new AlgorithmRegistry();
	}
	
	
	//define algorithm defaults
	static
	{	
		definitions = new HashMap<String, Definition>();
	}	

	public static void registerDefinition(String name, Definition def)
	{
		definitions.put(name, def);
	}
	
	public static Algorithm getDefaultAlgorithm(String name)
	{
		return definitions.get(name).getDefaultAlgorithm();
	}
	
	public static Algorithm getAlgorithmSpec(String name)
	{
		return definitions.get(name).getAlgorithmSpec();
	}
	

}
