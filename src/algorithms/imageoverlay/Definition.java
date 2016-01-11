package algorithms.imageoverlay;

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
	private static final String NAME = "ImageOverlay";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	private String description;

	private Definition()
	{
		description = "Data is encoded in the slightly "
						+ "modified pixels of another image file.";
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
			// data insertion density (use of 4x4 or 2x16)
			Parameter param = new Parameter("InsertionDensity", "string", "25%", false);
			if (includeOptions)
			{
				param.addOption(new Option("25%"));
				param.addOption(new Option("50%"));
			}
			algo.addParameter(param);
		}

		{
			// input image folder
			Parameter param = new Parameter("ImageFolder", "string", "testing/input_images",
							false);
			if (includeOptions)
			{
				param.addOption(new Option("*"));
			}
			algo.addParameter(param);
		}
		
		{
			// input image consumption mode
			Parameter param = new Parameter("ImageConsumptionMode", "string", "cycle",
							false);
			if (includeOptions)
			{
				param.addOption(new Option("cycle"));
				param.addOption(new Option("move"));
				param.addOption(new Option("delete"));
			}
			algo.addParameter(param);
		}

		{
			// working folder
			Parameter param = new Parameter("WorkingFolder", "string",
							".image_overlay_working", false);
			if (includeOptions)
			{
				param.addOption(new Option("*"));
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
				return new ImageOverlayFactory(algo, key);
			}

			@Override
			public ProductWriterFactory<? extends ProductWriter> createWriter(
							Algorithm algo, Key key)
			{
				return new ImageOverlayFactory(algo, key);
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
		Algorithm imageOverlayNormal = construct(false);
		imageOverlayNormal.setPresetName("image_overlay_basic");
		presets.add(imageOverlayNormal);
		
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
