package product;

import algorithms.Algorithm;
import data.Key;

public interface ProductFactoryCreation {
	public ProductFactory<? extends Product> create(Algorithm algo, Key key);
}
