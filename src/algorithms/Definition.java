package algorithms;

import java.util.List;

import product.ProductFactoryCreation;

public interface Definition
{
	public String getName();
	
	public String getDescription();

	public Algorithm getDefaultAlgorithm();

	public Algorithm getAlgorithmSpec();
	
	public List<Algorithm> getAlgorithmPresets();

	public ProductFactoryCreation getProductFactoryCreation();
}
