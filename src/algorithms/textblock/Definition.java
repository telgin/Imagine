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

public class Definition implements algorithms.Definition
{
	private static final String NAME = "TextBlock";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;

	/**
	 * @update_comment
	 */
	private Definition()
	{
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static Definition getInstance()
	{
		if (self == null)
			self = new Definition();

		return self;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.Definition#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.Definition#getDefaultAlgorithm()
	 */
	@Override
	public Algorithm getDefaultAlgorithm()
	{
		return construct(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.Definition#getAlgorithmSpec()
	 */
	@Override
	public Algorithm getAlgorithmSpec()
	{
		return construct(true);
	}

	/**
	 * @update_comment
	 * @param includeOptions
	 * @return
	 */
	private Algorithm construct(boolean includeOptions)
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);

		{
			// product mode
			Parameter param = new Parameter("ProductMode", "string", "Normal", false);
			if (includeOptions)
			{
				param.addOption(new Option("Normal"));
			}
			algo.addParameter(param);
		}

		{
			// blockSize
			Parameter param = new Parameter("blockSize", "int", "102400", false);
			if (includeOptions)
			{
				param.addOption(new Option("500", Integer.toString(Integer.MAX_VALUE)));
			}
			algo.addParameter(param);
		}

		return algo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.Definition#getProductFactoryCreation()
	 */
	@Override
	public ProductFactoryCreation getProductFactoryCreation()
	{
		return new ProductFactoryCreation()
		{
			@Override
			public ProductReaderFactory<? extends ProductReader> createReader(
							Algorithm algo, Key key)
			{
				return new TextBlockFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(
							Algorithm algo, Key key)
			{
				return new TextBlockFactory(algo, key);
			}
		};
	}

}
