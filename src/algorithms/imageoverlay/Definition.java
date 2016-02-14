package algorithms.imageoverlay;

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
	private static final String NAME = "ImageOverlay";
	private static final int VERSION_NUMBER = 1;
	private static final String DESCRIPTION = "Data is encoded in the slightly "
						+ "modified pixels of another image file.";
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
	public Algorithm constructDefaultAlgorithm()
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER, DESCRIPTION);

		{
			// data insertion density (use of 4x4 (25%) or 2x16 (50%))
			Parameter param = new Parameter("InsertionDensity", Parameter.STRING_TYPE, false, true);
			param.setDescription("The precentage of data in the input image to be overwritten with embedded data.");

			param.addOption(new Option("25%", "Use 25% of the information about each color value to store the embedded data."));
			param.addOption(new Option("50%", "Use 50% of the information about each color value to store the embedded data."));
			
			param.setValue("25%");

			algo.addParameter(param);
		}

		{
			// input image folder
			Parameter param = new Parameter("ImageFolder", Parameter.FILE_TYPE, false, true);
			param.setDescription("A folder of images to apply the overlay to.");

			param.addOption(new Option("*", "Path to a folder."));
			
			param.setValue(Option.PROMPT_OPTION.getValue());

			algo.addParameter(param);
		}
		
		{
			// input image consumption mode
			Parameter param = new Parameter("ImageConsumptionMode", Parameter.STRING_TYPE, false, true);
			param.setDescription("How to deal with used input images once an overlay is applied.");

			param.addOption(new Option("cycle", "Cycle through input images in the input folder. Start from the beginning once the last one is used."));
			param.addOption(new Option("move", "Move used images to a subfolder within the image input folder."));
			param.addOption(new Option("delete", "Delete images from the input folder once an overlay is applied."));
			
			param.setValue("cycle");

			algo.addParameter(param);
		}
		
		{
			// image type
			Parameter param = new Parameter("ImageType", Parameter.STRING_TYPE, false, true);
			param.setDescription("The file format to output images in.");

			param.addOption(new Option("png", "Output png images."));
			
			param.setValue("png");

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
		Algorithm basic = constructDefaultAlgorithm();
		basic.setPresetName("image_overlay_basic");
		basic.setParameter("ImageFolder", "testing/input_images");//TODO remove this
		presets.add(basic);
		
		//secure
		Algorithm secure = constructDefaultAlgorithm();
		secure.setPresetName("image_overlay_secure");
		secure.setParameter("ImageFolder", "testing/input_images");//TODO remove this
		presets.add(secure);
		
		return presets;
	}
}
