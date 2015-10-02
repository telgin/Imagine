package product;

import java.util.HashMap;

import logging.LogLevel;
import logging.Logger;
import algorithms.Algorithm;
import algorithms.fullpng.FullPNGFactory;
import algorithms.textblock.TextBlockFactory;
import data.TrackingGroup;

public class ProductFactoryRegistry {
	private static HashMap<String, ProductFactoryCreation> factories;
	
	static
	{
		factories = new HashMap<String, ProductFactoryCreation>();
		
		factories.put("FullPNG", new ProductFactoryCreation() {
		    @Override
		    public FullPNGFactory create(Algorithm algo, byte[] keyHash) {
		        return new FullPNGFactory(algo, keyHash);
		    }
		});
		
		factories.put("TextBlock", new ProductFactoryCreation() {
		    @Override
		    public TextBlockFactory create(Algorithm algo, byte[] keyHash) {
		        return new TextBlockFactory(algo);
		    }
		});

	}
	
	public static ProductFactory<? extends Product> getProductFactory(TrackingGroup group)
	{
		String algorithmName = group.getAlgorithm().getName();
		if (!factories.containsKey(algorithmName))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + algorithmName);
		
		return factories.get(algorithmName).create(group.getAlgorithm(), group.getKeyHash());
	}
}
