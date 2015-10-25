package algorithms.textblock;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import data.Key;
import product.ProductFactoryCreation;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class Definition implements algorithms.Definition{
	private static final String NAME = "TextBlock";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	
	private Definition(){}

	public static Definition getInstance()
	{
		if (self == null)
			self = new Definition();
		
		return self;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public Algorithm getDefaultAlgorithm() {
		return construct(false);
	}

	@Override
	public Algorithm getAlgorithmSpec() {
		return construct(true);
	}
	
	private Algorithm construct(boolean includeOptions)
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);
		
		{
			//product mode
			Parameter param = new Parameter("ProductMode", "string", "Normal", false);
			if (includeOptions)
			{
				param.addOption(new Option("Normal"));
			}
			algo.addParameter(param);
		}

		{
			//blockSize
			Parameter param = new Parameter("blockSize", "int", "102400", false);
			if (includeOptions)
			{
				param.addOption(new Option("500", Integer.toString(Integer.MAX_VALUE)));
			}
			algo.addParameter(param);
		}
		
		return algo;
	}

	@Override
	public ProductFactoryCreation getProductFactoryCreation()
	{
		return new ProductFactoryCreation()
		{
			@Override
			public ProductReaderFactory<? extends ProductReader>
				createReader(Algorithm algo, Key key)
			{
				return new TextBlockFactory(algo);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter>
				createWriter(Algorithm algo, Key key)
			{
				return new TextBlockFactory(algo);
			}
		};
	}

}
