package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlgorithmRegistry {
	private static HashMap<String, Definition> definitions = 
			new HashMap<String, Definition>();
	
	/**
	 * No need for instance
	 */
	private AlgorithmRegistry(){}

	public static void registerDefinition(String name, Definition def)
	{
		definitions.put(name, def);
	}
	
	public static List<String> getAlgorithmNames()
	{
		return new ArrayList<String>(definitions.keySet());
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
