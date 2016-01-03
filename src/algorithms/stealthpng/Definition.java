package algorithms.stealthpng;

import java.util.LinkedList;
import java.util.List;

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
	private static final String NAME = "StealthPNG";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;

	private Definition()
	{
	}

	public static Definition getInstance()
	{
		if (self == null)
			self = new Definition();

		return self;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public Algorithm getDefaultAlgorithm()
	{
		return construct(false);
	}

	@Override
	public Algorithm getAlgorithmSpec()
	{
		return construct(true);
	}

	private Algorithm construct(boolean includeOptions)
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);

		{
			// product mode
			Parameter param = new Parameter("ProductMode", "string", "Normal", false);
			if (includeOptions)
			{
				param.addOption(new Option("Normal"));
				param.addOption(new Option("Secure"));
				param.addOption(new Option("Stealth"));
			}
			algo.addParameter(param);
		}

		{
			// pattern
			Parameter param = new Parameter("Pattern", "string", "1", false);
			if (includeOptions)
			{
				param.addOption(new Option("1"));
				param.addOption(new Option("2"));
			}
			algo.addParameter(param);
		}

		{
			// input image folder
			Parameter param = new Parameter("ImageFolder", "string", "./inputImages/",
							false);
			if (includeOptions)
			{
				param.addOption(new Option("*"));
			}
			algo.addParameter(param);
		}

		{
			// working folder
			Parameter param = new Parameter("ImageFolder", "string",
							"./.StealthPNG_working/", false);
			if (includeOptions)
			{
				param.addOption(new Option("*"));
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
			public ProductReaderFactory<? extends ProductReader> createReader(
							Algorithm algo, Key key)
			{
				return new StealthPNGFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(
							Algorithm algo, Key key)
			{
				return new StealthPNGFactory(algo, key);
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
		Algorithm stealthpngNormal = construct(false);
		stealthpngNormal.setPresetName("stealthpng_basic");
		presets.add(stealthpngNormal);
		
		return presets;
	}

}
