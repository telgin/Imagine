package product;

import algorithms.Algorithm;

public interface ProductFactoryCreation {
	public ProductFactory<? extends Product> create(Algorithm algo, byte[] keyHash);
}
