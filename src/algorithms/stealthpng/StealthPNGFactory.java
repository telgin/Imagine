package algorithms.stealthpng;

import algorithms.Algorithm;
import algorithms.stealthpng.StealthPNGReader;
import algorithms.stealthpng.StealthPNGWriter;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class StealthPNGFactory implements ProductReaderFactory<StealthPNGReader>,
				ProductWriterFactory<StealthPNGWriter>
{
	private Key key;
	private Algorithm algo;

	public StealthPNGFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}

	@Override
	public StealthPNGWriter createWriter()
	{
		return new StealthPNGWriter(algo, key);
	}

	@Override
	public StealthPNGReader createReader()
	{
		return new StealthPNGReader(algo, key);
	}
}
