package algorithms.text;

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
	private static final String NAME = "Text";
	private static final int VERSION_NUMBER = 1;
	private static final String DESCRIPTION = "Data is encoded into text files such "
						+ "that all characters are ascii.";
	private static Definition self;
	
	public static final String ENCODING_PARAM = "encoding";
	public static final String BLOCK_SIZE_PARAM = "blockSize";
	
	public static final String BASE64_ENCODING = "Base64";
	public static final String HEX_ENCODING = "Hex";
	

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
	public Algorithm constructDefaultAlgorithm()
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER, DESCRIPTION);

		{
			// blockSize
			Parameter param = new Parameter(BLOCK_SIZE_PARAM, Parameter.INT_TYPE, false, true);
			param.setDescription("The number of bytes of input data to put in each output file.");

			param.addOption(new Option("500", Integer.toString(Integer.MAX_VALUE)));
			
			param.setValue("102400");

			algo.addParameter(param);
		}
		
		{
			// encoding
			Parameter param = new Parameter(ENCODING_PARAM, Parameter.STRING_TYPE, false, true);
			param.setDescription("The encoding of bytes to output text as.");
			
			param.addOption(new Option(BASE64_ENCODING));
			param.addOption(new Option(HEX_ENCODING));
			
			param.setValue(BASE64_ENCODING);
			
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
		Algorithm defalutAlgo = constructDefaultAlgorithm();
		defalutAlgo.setPresetName("text_default");
		presets.add(defalutAlgo);
		
		return presets;
	}
}
