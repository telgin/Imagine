package algorithms.fullpng;

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
	private static final String NAME = "FullPNG";
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
			Parameter param = new Parameter("ProductMode", "string", "Secure", false);
			if (includeOptions)
			{
				param.addOption(new Option("Secure"));
			}
			algo.addParameter(param);
		}
		
		{
			//colors
			Parameter param = new Parameter("Colors", "string", "rgb", false);
			if (includeOptions)
			{
				param.addOption(new Option("rgb"));
				param.addOption(new Option("rgba"));
			}
			algo.addParameter(param);
		}

		{
			//width
			Parameter param = new Parameter("Width", "int", "1820", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
			}
			algo.addParameter(param);
		}

		{
			//height
			Parameter param = new Parameter("Height", "int", "980", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
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
			public ProductReaderFactory<? extends ProductReader> createReader(Algorithm algo, Key key) {
				return new FullPNGFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(Algorithm algo, Key key) {
				return new FullPNGFactory(algo, key);
			}
		};
	}

}
