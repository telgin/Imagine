package algorithms.text;

import java.util.LinkedList;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import data.Key;
import product.ProductFactoryCreation;
import product.ProductMode;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class Definition implements algorithms.Definition
{
	private static final String NAME = "Text";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	
	public static final String base64Encoding = "Base64";
	public static final String hexEncoding = "Hex";

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
			Parameter param = new Parameter("ProductMode", "string", 
							ProductMode.NORMAL.toString(), false);
			if (includeOptions)
			{
				param.addOption(new Option(ProductMode.NORMAL.toString()));
				param.addOption(new Option(ProductMode.SECURE.toString()));
				param.addOption(new Option(ProductMode.STEALTH.toString()));
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
		
		{
			// encoding
			Parameter param = new Parameter("encoding", "string", base64Encoding, false);
			if (includeOptions)
			{
				param.addOption(new Option(base64Encoding));
				param.addOption(new Option(hexEncoding));
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
				return new TextFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(
							Algorithm algo, Key key)
			{
				return new TextFactory(algo, key);
			}
		};
	}

	/* (non-Javadoc)
	 * @see algorithms.Definition#getAlgorithmPresets()
	 */
	@Override
	public List<Algorithm> getAlgorithmPresets()
	{
		List<Algorithm> presets = new LinkedList<Algorithm>();
		
		//plain default
		Algorithm textblockNormal = construct(false);
		textblockNormal.setPresetName("textblock_basic");
		presets.add(textblockNormal);
		
		return presets;
	}

}
