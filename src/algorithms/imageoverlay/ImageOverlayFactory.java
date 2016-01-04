package algorithms.imageoverlay;

import algorithms.Algorithm;
import algorithms.imageoverlay.ImageOverlayReader;
import algorithms.imageoverlay.ImageOverlayWriter;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class ImageOverlayFactory implements ProductReaderFactory<ImageOverlayReader>,
				ProductWriterFactory<ImageOverlayWriter>
{
	private Key key;
	private Algorithm algo;

	public ImageOverlayFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}

	@Override
	public ImageOverlayWriter createWriter()
	{
		return new ImageOverlayWriter(algo, key);
	}

	@Override
	public ImageOverlayReader createReader()
	{
		return new ImageOverlayReader(algo, key);
	}
}
