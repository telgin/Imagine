package algorithms;

public interface ProductFactoryCreation {
	public ProductFactory<? extends Product> create(ProductMode mode, byte[] keyHash);
}
