package algorithms.imageoverlay;

import java.util.LinkedList;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import api.UsageException;
import archive.ArchiveFactoryCreation;
import archive.ArchiveReader;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriter;
import archive.ArchiveWriterFactory;
import key.Key;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The definition of the image overlay algorithm, which defines defaults, parameters, and descriptions.
 */
public class Definition implements algorithms.Definition
{
	private static final String NAME = "ImageOverlay";
	private static final int VERSION_NUMBER = 1;
	private static final String DESCRIPTION = "Data is encoded in the slightly "
		+ "modified pixels of an existing image file such that it is hard "
		+ "or impossible to perceive the difference with human eyes.";
	public static final String INSERTION_DENSITY_PARAM = "InsertionDensity";
	public static final String IMAGE_FOLDER_PARAM = "ImageFolder";
	public static final String IMAGE_CONSUMPTION_MODE_PARAM = "ImageConsumptionMode";
	public static final String IMAGE_TYPE_PARAM = "ImageType";
	
	private static Definition s_self;

	/**
	 * Create a definition instance
	 */
	private Definition()
	{
	}

	/**
	 * Gets an instance of this definition
	 * @return The definition instance
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
			// data insertion density (use of 4x4 (25%) or 2x16 (50%))
			Parameter param = new Parameter(INSERTION_DENSITY_PARAM, Parameter.STRING_TYPE, false, true);
			param.setDescription("The percentage of data in the input image to be overwritten with embedded data. At "
				+ "25%, the visual difference with be unnoticeable for most images. At 50%, some artifacts "
				+ "will be noticeable in smaller images or when looking at an image zoomed in.");

			param.addOption(new Option("25%"));
			param.addOption(new Option("50%"));
			
			param.setValue("25%");

			algo.addParameter(param);
		}

		{
			// input image folder
			Parameter param = new Parameter(IMAGE_FOLDER_PARAM, Parameter.FILE_TYPE, false, true);
			param.setDescription("A folder of images to apply the overlay to. Supported input types include"
				+ " png, jpg, jpeg, gif, bmp, and wbmp.");

			param.addOption(new Option("*"));
			param.addOption(Option.PROMPT_OPTION);
			
			param.setValue(Option.PROMPT_OPTION.getValue());

			algo.addParameter(param);
		}
		
		{
			// input image consumption mode
			Parameter param = new Parameter(IMAGE_CONSUMPTION_MODE_PARAM, Parameter.STRING_TYPE, false, true);
			param.setDescription("How to deal with used input images once an overlay is applied.\n\nCycle:  "
				+ "Cycle through input images in the input folder. Start from the beginning once "
				+ "the last one is used.\n\nMove:  Move used images to a subfolder within the image "
				+ "input folder.\n\nDelete:  Delete images from the input folder once an overlay is "
				+ "applied.");

			param.addOption(new Option("cycle"));
			param.addOption(new Option("move"));
			param.addOption(new Option("delete"));
			
			param.setValue("cycle");

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
				return new ImageOverlayFactory(p_algo, p_key);
			}

			/* (non-Javadoc)
			 * @see archive.ArchiveFactoryCreation#createWriter(algorithms.Algorithm, key.Key)
			 */
			@Override
			public ArchiveWriterFactory<? extends ArchiveWriter> createWriter(
							Algorithm p_algo, Key p_key)
			{
				return new ImageOverlayFactory(p_algo, p_key);
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
		
		try
		{
			//light
			Algorithm light = constructDefaultAlgorithm();
			light.setPresetName("image_overlay_light");
			presets.add(light);
			
			//heavy
			Algorithm heavy = constructDefaultAlgorithm();
			heavy.setPresetName("image_overlay_heavy");
			heavy.setParameter(INSERTION_DENSITY_PARAM, "50%");
			presets.add(heavy);
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_debug,  e, false);
		}
		
		return presets;
	}
}
