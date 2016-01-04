package algorithms.text;

import algorithms.Algorithm;
import data.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class TextFactory implements ProductReaderFactory<TextReader>,
				ProductWriterFactory<TextWriter>
{

	private Algorithm algo;
	private Key key;

	public TextFactory(Algorithm algo, Key key)
	{
		this.algo = algo;
		this.key = key;
	}

	@Override
	public TextWriter createWriter()
	{
		return new TextWriter(algo, key);
	}

	@Override
	public TextReader createReader()
	{
		return new TextReader(algo, key);
	}

}
