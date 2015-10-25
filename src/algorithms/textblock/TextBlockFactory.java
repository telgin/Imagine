package algorithms.textblock;

import algorithms.Algorithm;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

public class TextBlockFactory implements 
	ProductReaderFactory<TextBlockReader>, ProductWriterFactory<TextBlockWriter>
{

	private Algorithm algo;
	
	public TextBlockFactory(Algorithm algo)
	{
		this.algo = algo;
	}

	@Override
	public TextBlockWriter createWriter() {
		return new TextBlockWriter(algo);
	}

	@Override
	public TextBlockReader createReader() {
		return new TextBlockReader(algo);
	}

}
