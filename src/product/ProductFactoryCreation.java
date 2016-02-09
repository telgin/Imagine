package product;

import algorithms.Algorithm;
import key.Key;

public interface ProductFactoryCreation
{
	public ProductReaderFactory<? extends ProductReader> createReader(Algorithm algo,
					Key key);

	public ProductWriterFactory<? extends ProductWriter> createWriter(Algorithm algo,
					Key key);
}
