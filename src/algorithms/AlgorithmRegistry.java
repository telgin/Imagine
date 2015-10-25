package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.Key;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class AlgorithmRegistry {
	private static HashMap<String, Definition> definitions;	
	
	static
	{
		definitions = new HashMap<String, Definition>();
		
		definitions.put(algorithms.textblock.Definition.getInstance().getName(),
				algorithms.textblock.Definition.getInstance());
		
		definitions.put(algorithms.fullpng.Definition.getInstance().getName(),
				algorithms.fullpng.Definition.getInstance());
		
		definitions.put(algorithms.stealthpng.Definition.getInstance().getName(),
				algorithms.stealthpng.Definition.getInstance());
	}
	
	/**
	 * No need for instance
	 */
	private AlgorithmRegistry(){}
	
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
