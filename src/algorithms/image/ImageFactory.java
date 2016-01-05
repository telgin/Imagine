package algorithms.image;

import algorithms.Algorithm;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class ImageFactory implements ProductReaderFactory<ImageReader>,
				ProductWriterFactory<ImageWriter>
{

	private Key key;
	private Algorithm algo;

	public ImageFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}

	@Override
	public ImageWriter createWriter()
	{
		return new ImageWriter(algo, key);
	}

	@Override
	public ImageReader createReader()
	{
		return new ImageReader(algo, key);
	}
}
