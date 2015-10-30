package algorithms.textblock;

import algorithms.Algorithm;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class TextBlockFactory implements 
	ProductReaderFactory<TextBlockReader>, ProductWriterFactory<TextBlockWriter>
{

	private Algorithm algo;
	private Key key;
	
	public TextBlockFactory(Algorithm algo, Key key)
	{
		this.algo = algo;
		this.key = key;
	}

	@Override
	public TextBlockWriter createWriter() {
		return new TextBlockWriter(algo, key);
	}

	@Override
	public TextBlockReader createReader() {
		return new TextBlockReader(algo, key);
	}

}
