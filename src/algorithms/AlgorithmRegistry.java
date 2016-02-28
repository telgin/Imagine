package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archive.ArchiveReader;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriter;
import archive.ArchiveWriterFactory;
import key.Key;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This class maintains a central place where all algorithm definitions can be found.
 */
public class AlgorithmRegistry
{
	private static Map<String, Definition> s_definitions;	
	
	static
	{
		s_definitions = new HashMap<String, Definition>();
		
		s_definitions.put(algorithms.text.Definition.getInstance().getName(),
			algorithms.text.Definition.getInstance());
		
		s_definitions.put(algorithms.image.Definition.getInstance().getName(),
			algorithms.image.Definition.getInstance());
		
		s_definitions.put(algorithms.imageoverlay.Definition.getInstance().getName(),
			algorithms.imageoverlay.Definition.getInstance());
	}
	
	/**
	 * No need for instance
	 */
	private AlgorithmRegistry(){}
	
	/**
	 * Gets the list of algorithm definition names
	 * @return The list of names
	 */
	public static List<String> getAlgorithmNames()
	{
		List<String> names = new ArrayList<String>(s_definitions.keySet());
		names.sort(null);
		
		return names;
	}
	
	/**
	 * Gets the default algorithm by the algorithm definition name specified
	 * @param p_name The algorithm definition name
	 * @return The default algorithm
	 */
	public static Algorithm getDefaultAlgorithm(String p_name)
	{
		return s_definitions.get(p_name).constructDefaultAlgorithm();
	}
	
	/**
	 * Gets the default presets defined by the algorithm definition
	 * @param p_name The algorithm definition name
	 * @return A list of all default presets
	 */
	public static List<Algorithm> getAlgorithmPresets(String p_name)
	{
		return s_definitions.get(p_name).getAlgorithmPresets();
	}
	
	/**
	 * Creates an archive reader factory for the given algorithm / key
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @return The archive reader factory for this algorithm
	 */
	public static ArchiveReaderFactory<? extends ArchiveReader> 
		getArchiveReaderFactory(Algorithm p_algo, Key p_key)
	{
		if (!s_definitions.containsKey(p_algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + p_algo.getName());
		
		return s_definitions.get(p_algo.getName()).getArchiveFactoryCreator()
			.createReader(p_algo, p_key);
	}
	
	/**
	 * Creates an archive writer factory for the given algorithm / key
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @return The archive writer factory for this algorithm
	 */
	public static ArchiveWriterFactory<? extends ArchiveWriter> 
		getArchiveWriterFactory(Algorithm p_algo, Key p_key)
	{
		if (!s_definitions.containsKey(p_algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + p_algo.getName());
		
		return s_definitions.get(p_algo.getName()).getArchiveFactoryCreator()
			.createWriter(p_algo, p_key);
	}
}
