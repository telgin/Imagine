package algorithms.image;

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
	private static final String NAME = "Image";
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
			// colors
			Parameter param = new Parameter("Colors", "string", "rgb", false);
			if (includeOptions)
			{
				param.addOption(new Option("rgb"));
				param.addOption(new Option("rgba"));
			}
			algo.addParameter(param);
		}

		{
			// width
			Parameter param = new Parameter("Width", "int", "1820", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
			}
			algo.addParameter(param);
		}

		{
			// height
			Parameter param = new Parameter("Height", "int", "980", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
			}
			algo.addParameter(param);
		}
		
		{
			// image type
			Parameter param = new Parameter("ImageType", "string", "png", false);
			if (includeOptions)
			{
				param.addOption(new Option("png"));
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
				return new ImageFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(
							Algorithm algo, Key key)
			{
				return new ImageFactory(algo, key);
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
		Algorithm imageNormal = construct(false);
		imageNormal.setPresetName("image_basic");
		presets.add(imageNormal);
		
		return presets;
	}

}
