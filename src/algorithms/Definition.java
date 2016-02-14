package algorithms;

import java.util.List;

import product.ProductFactoryCreation;

public interface Definition
{
	public String getName();

	public Algorithm constructDefaultAlgorithm();
	
	public List<Algorithm> getAlgorithmPresets();

	public ProductFactoryCreation getProductFactoryCreation();
}
