package algorithms;

import java.util.HashMap;

import logging.LogLevel;
import logging.Logger;

import algorithms.fullpng.FullPNGFactory;
import algorithms.textblock.TextBlockFactory;

public class ProductFactoryRegistry {
	private static HashMap<String, ProductFactoryCreation> factories;
	
	static
	{
		factories = new HashMap<String, ProductFactoryCreation>();
		
		factories.put("FullPNG", new ProductFactoryCreation() {
		    @Override
		    public FullPNGFactory create(ProductMode mode, byte[] keyHash) {
		        return new FullPNGFactory(mode, keyHash);
		    }
		});
		
		factories.put("TextBlock", new ProductFactoryCreation() {
		    @Override
		    public TextBlockFactory create(ProductMode mode, byte[] keyHash) {
		        return new TextBlockFactory(mode);
		    }
		});

	}
	
	public static ProductFactory<? extends Product> getProductFactory(
			String algorithmName, ProductMode mode, byte[] keyHash)
	{
		if (!factories.containsKey(algorithmName))
			Logger.log(LogLevel.k_fatal, "There is no factory by the name of: " + algorithmName);
		
		return factories.get(algorithmName).create(mode, keyHash);
	}
}
