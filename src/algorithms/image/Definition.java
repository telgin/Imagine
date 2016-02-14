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
	private static final String DESCRIPTION = "Data is encoded in the pixels of an image file. "
					+ "(Very large or small images may result in excessivly long conversion times. If the "
					+ "image is not large enough to contain the file header, the conversion "
					+ "will fail. If the image file is too large, a lot of space may be wasted depending "
					+ "on how fully the embedded data fills the last output image.)";
	private static Definition self;
	
	
	public static final String IMAGE_TYPE_PROPERTY = "ImageType";

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
			// colors
			Parameter param = new Parameter("Colors", Parameter.STRING_TYPE, false, true);
			param.setDescription("The way to represent colors in the output image.");
			
			param.addOption(new Option("rgb", "Use one byte each for red, green, and blue to represent each pixel color."));

			param.setValue("rgb");
			
			algo.addParameter(param);
		}

		{
			// width
			Parameter param = new Parameter("Width", Parameter.INT_TYPE, false, true);
			param.setDescription("The width of the output image.");

			param.addOption(new Option("0", "10000", "The width in pixels."));
			
			param.setValue("1820");

			algo.addParameter(param);
		}

		{
			// height
			Parameter param = new Parameter("Height", Parameter.INT_TYPE, false, true);
			param.setDescription("The height of the output image.");

			param.addOption(new Option("0", "10000", "The height in pixels."));
			
			param.setValue("980");

			algo.addParameter(param);
		}
		
		{
			// image type
			Parameter param = new Parameter(IMAGE_TYPE_PROPERTY, Parameter.STRING_TYPE, false, true);
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
		Algorithm basic = constructDefaultAlgorithm();
		basic.setPresetName("image_basic");
		presets.add(basic);
		
		//secure
		Algorithm secure = constructDefaultAlgorithm();
		secure.setPresetName("image_secure");
		presets.add(secure);
		
		return presets;
	}
}
