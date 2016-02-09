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
	private static Definition self;
	private String description;
	
	public static final String encodingParam = "encoding";
	public static final String blockSizeParam = "blockSize";
	
	public static final String base64Encoding = "Base64";
	public static final String hexEncoding = "Hex";
	

	/**
	 * @update_comment
	 */
	private Definition()
	{
		description = "Data is encoded into text files such "
						+ "that all characters are ascii.";
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
	public Algorithm getDefaultAlgorithm()
	{
		return construct(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.Definition#getAlgorithmSpec()
	 */
	@Override
	public Algorithm getAlgorithmSpec()
	{
		return construct(true);
	}

	/**
	 * @update_comment
	 * @param includeOptions
	 * @return
	 */
	private Algorithm construct(boolean includeOptions)
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);

		{
			// blockSize
			Parameter param = new Parameter(blockSizeParam, "int", "102400", false);
			if (includeOptions)
			{
				param.addOption(new Option("500", Integer.toString(Integer.MAX_VALUE)));
			}
			algo.addParameter(param);
		}
		
		{
			// encoding
			Parameter param = new Parameter(encodingParam, "string", base64Encoding, false);
			if (includeOptions)
			{
				param.addOption(new Option(base64Encoding));
				param.addOption(new Option(hexEncoding));
			}
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
		
		//basic
		Algorithm basic = construct(false);
		basic.setPresetName("text_basic");
		presets.add(basic);
		
		//secure
		Algorithm secure = construct(false);
		secure.setPresetName("text_secure");
		presets.add(secure);

		
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
