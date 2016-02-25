package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
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
	 * @update_comment
	 * @return
	 */
	public static List<String> getAlgorithmNames()
	{
		List<String> names = new ArrayList<String>(s_definitions.keySet());
		names.sort(null);
		
		return names;
	}
	
	/**
	 * @update_comment
	 * @param p_name
	 * @return
	 */
	public static Algorithm getDefaultAlgorithm(String p_name)
	{
		return s_definitions.get(p_name).constructDefaultAlgorithm();
	}
	
	/**
	 * @update_comment
	 * @param p_name
	 * @return
	 */
	public static List<Algorithm> getAlgorithmPresets(String p_name)
	{
		return s_definitions.get(p_name).getAlgorithmPresets();
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public static ProductReaderFactory<? extends ProductReader> 
		getProductReaderFactory(Algorithm p_algo, Key p_key)
	{
		if (!s_definitions.containsKey(p_algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + p_algo.getName());
		
		return s_definitions.get(p_algo.getName()).getProductFactoryCreation()
			.createReader(p_algo, p_key);
	}
	
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public static ProductWriterFactory<? extends ProductWriter> 
		getProductWriterFactory(Algorithm p_algo, Key p_key)
	{
		if (!s_definitions.containsKey(p_algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + p_algo.getName());
		
		return s_definitions.get(p_algo.getName()).getProductFactoryCreation()
			.createWriter(p_algo, p_key);
	}
}
