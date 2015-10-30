package algorithms;

import product.ProductFactoryCreation;

public interface Definition
{
	public String getName();

	public Algorithm getDefaultAlgorithm();

	public Algorithm getAlgorithmSpec();

	public ProductFactoryCreation getProductFactoryCreation();
}
