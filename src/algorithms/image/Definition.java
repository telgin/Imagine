package algorithms.image;

import java.util.LinkedList;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import archive.ArchiveFactoryCreation;
import archive.ArchiveReader;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriter;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Definition implements algorithms.Definition
{
	private static final String NAME = "Image";
	private static final int VERSION_NUMBER = 1;
	private static final String DESCRIPTION = "Data is encoded in the pixels of an image file. "
		+ "(Very large or small images may result in excessivly long conversion times. If the "
		+ "image is not large enough to contain the file header, the conversion "
		+ "will fail. If the image file is too large, a lot of space may be wasted depending "
		+ "on how fully the embedded data fills the last output image.)";
	
	public static final String IMAGE_TYPE_PARAM = "ImageType";
	public static final String WIDTH_PARAM = "Width";
	public static final String HEIGHT_PARAM = "Height";
	public static final String COLORS_PARAM = "Colors";
	
	private static Definition s_self;

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
		if (s_self == null)
			s_self = new Definition();

		return s_self;
	}

	/* (non-Javadoc)
	 * @see algorithms.Definition#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}

	/* (non-Javadoc)
	 * @see algorithms.Definition#constructDefaultAlgorithm()
	 */
	@Override
	public Algorithm constructDefaultAlgorithm()
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER, DESCRIPTION);

		{
			// colors
			Parameter param = new Parameter(COLORS_PARAM, Parameter.STRING_TYPE, false, true);
			param.setDescription("The way to represent colors in the output image.");
			
			param.addOption(new Option("rgb"));

			param.setValue("rgb");
			
			algo.addParameter(param);
		}

		{
			// width
			Parameter param = new Parameter(WIDTH_PARAM, Parameter.INT_TYPE, false, true);
			param.setDescription("The width of the output image.");

			param.addOption(new Option("1", "10000"));
			
			param.setValue("1820");

			algo.addParameter(param);
		}

		{
			// height
			Parameter param = new Parameter(HEIGHT_PARAM, Parameter.INT_TYPE, false, true);
			param.setDescription("The height of the output image.");

			param.addOption(new Option("1", "10000"));
			
			param.setValue("980");

			algo.addParameter(param);
		}
		
		{
			// image type
			Parameter param = new Parameter(IMAGE_TYPE_PARAM, Parameter.STRING_TYPE, false, true);
			param.setDescription("The file format to output images in.");

			param.addOption(new Option("png"));
			
			param.setValue("png");

			algo.addParameter(param);
		}

		return algo;
	}

	/* (non-Javadoc)
	 * @see algorithms.Definition#getArchiveFactoryCreation()
	 */
	@Override
	public ArchiveFactoryCreation getArchiveFactoryCreation()
	{
		return new ArchiveFactoryCreation()
		{
			/* (non-Javadoc)
			 * @see archive.ArchiveFactoryCreation#createReader(algorithms.Algorithm, key.Key)
			 */
			@Override
			public ArchiveReaderFactory<? extends ArchiveReader> createReader(
							Algorithm p_algo, Key p_key)
			{
				return new ImageFactory(p_algo, p_key);
			}

			/* (non-Javadoc)
			 * @see archive.ArchiveFactoryCreation#createWriter(algorithms.Algorithm, key.Key)
			 */
			@Override
			public ArchiveWriterFactory<? extends ArchiveWriter> createWriter(
							Algorithm p_algo, Key p_key)
			{
				return new ImageFactory(p_algo, p_key);
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
		Algorithm defaultAlgo = constructDefaultAlgorithm();
		defaultAlgo.setPresetName("image_default");
		presets.add(defaultAlgo);
		
		return presets;
	}
}
