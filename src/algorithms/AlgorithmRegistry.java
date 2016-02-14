package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class AlgorithmRegistry
{
	private static HashMap<String, Definition> definitions;	
	
	static
	{
		definitions = new HashMap<String, Definition>();
		
		definitions.put(algorithms.text.Definition.getInstance().getName(),
				algorithms.text.Definition.getInstance());
		
		definitions.put(algorithms.image.Definition.getInstance().getName(),
				algorithms.image.Definition.getInstance());
		
		definitions.put(algorithms.imageoverlay.Definition.getInstance().getName(),
				algorithms.imageoverlay.Definition.getInstance());
	}
	
	/**
	 * No need for instance
	 */
	private AlgorithmRegistry(){}
	
	public static List<String> getAlgorithmNames()
	{
		List<String> names = new ArrayList<String>(definitions.keySet());
		names.sort(null);
		
		return names;
	}
	
	public static Algorithm getDefaultAlgorithm(String name)
	{
		return definitions.get(name).constructDefaultAlgorithm();
	}
	
	/**
	 * @update_comment
	 * @param algoName
	 * @return
	 */
	public static List<Algorithm> getAlgorithmPresets(String name)
	{
		return definitions.get(name).getAlgorithmPresets();
	}
	
	public static ProductReaderFactory<? extends ProductReader> getProductReaderFactory(Algorithm algo, Key key) //make this a part of the algorithm class?
	{
		if (!definitions.containsKey(algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + algo.getName());
		
		return definitions.get(algo.getName()).getProductFactoryCreation()
				.createReader(algo, key);
	}
	
	public static ProductWriterFactory<? extends ProductWriter> getProductWriterFactory(Algorithm algo, Key key)
	{
		if (!definitions.containsKey(algo.getName()))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + algo.getName());
		
		return definitions.get(algo.getName()).getProductFactoryCreation()
				.createWriter(algo, key);
	}
}
