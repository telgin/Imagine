package algorithms.image;

import java.util.LinkedList;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import key.Key;
import product.ProductFactoryCreation;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class Definition implements algorithms.Definition
{
	private static final String NAME = "Image";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	private String description;

	private Definition()
	{
		description = "Data is encoded in the pixels of an image file.";
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
		
		//basic
		Algorithm basic = construct(false);
		basic.setPresetName("image_basic");
		presets.add(basic);
		
		//secure
		Algorithm secure = construct(false);
		secure.setPresetName("image_secure");
		secure.setParameter("ProductMode", "secure");
		presets.add(secure);
		
		//test trackable
		Algorithm trackable = construct(false);
		trackable.setPresetName("test_image_trackable");
		trackable.setParameter("ProductMode", "trackable");
		presets.add(trackable);
		
		return presets;
	}

	/* (non-Javadoc)
	 * @see algorithms.Definition#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

}
