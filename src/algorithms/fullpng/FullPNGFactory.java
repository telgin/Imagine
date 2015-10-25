package algorithms.fullpng;

import algorithms.Algorithm;
import algorithms.textblock.TextBlockReader;
import algorithms.textblock.TextBlockWriter;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class FullPNGFactory implements 
	ProductReaderFactory<FullPNGReader>, ProductWriterFactory<FullPNGWriter>
{
	
	private Key key;
	private Algorithm algo;
	
	public FullPNGFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}

	@Override
	public FullPNGWriter createWriter() {
		return new FullPNGWriter(algo, key);
	}

	@Override
	public FullPNGReader createReader() {
		return new FullPNGReader(algo, key);
	}
}
