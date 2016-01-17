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
			Parameter param = new Parameter("ProductMode",
							Definition.PARAM_STRING_TYPE, ProductMode.k_basic.toString(), false);
			if (includeOptions)
			{
				param.addOption(new Option(ProductMode.k_basic.toString()));
				param.addOption(new Option(ProductMode.k_trackable.toString()));
				param.addOption(new Option(ProductMode.k_secure.toString()));
			}
			algo.addParameter(param);
		}

		{
			// data insertion density (use of 4x4 (25%) or 2x16 (50%))
			Parameter param = new Parameter("InsertionDensity", 
							Definition.PARAM_STRING_TYPE, "25%", false);
			if (includeOptions)
			{
				param.addOption(new Option("25%"));
				param.addOption(new Option("50%"));
			}
			algo.addParameter(param);
		}

		{
			// input image folder
			Parameter param = new Parameter("ImageFolder",
							Definition.PARAM_FILE_TYPE, Option.PROMPT_OPTION.getValue(), false);
			param.setDescription("A folder of images to apply the overlay to.");
			if (includeOptions)
			{
				param.addOption(new Option("*"));
			}
			algo.addParameter(param);
		}
		
		{
			// input image consumption mode
			Parameter param = new Parameter("ImageConsumptionMode",
							Definition.PARAM_STRING_TYPE, "cycle", false);
			if (includeOptions)
			{
				param.addOption(new Option("cycle"));
				param.addOption(new Option("move"));
				param.addOption(new Option("delete"));
			}
			algo.addParameter(param);
		}
		
		{
			// image type
			Parameter param = new Parameter("ImageType",
							Definition.PARAM_STRING_TYPE, "png", false);
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
		
		//basic
		Algorithm basic = construct(false);
		basic.setPresetName("image_overlay_basic");
		basic.setParameter("ImageFolder", "testing/input_images");//TODO remove this
		presets.add(basic);
		
		//secure
		Algorithm secure = construct(false);
		secure.setPresetName("image_overlay_secure");
		secure.setParameter("ProductMode", "secure");
		secure.setParameter("ImageFolder", "testing/input_images");//TODO remove this
		presets.add(secure);
		
		//test trackable
		Algorithm trackable = construct(false);
		trackable.setPresetName("test_image_overlay_trackable");
		trackable.setParameter("ProductMode", "trackable");
		trackable.setParameter("ImageFolder", "testing/input_images");
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
