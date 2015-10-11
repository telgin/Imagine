package algorithms.textblock;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import data.Key;
import product.ProductFactoryCreation;

public class Definition implements algorithms.Definition{
	private static final String NAME = "TextBlock";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	
	private Definition(){}

	public static Definition getInstance()
	{
		if (self == null)
			self = new Definition();
		
		return self;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public Algorithm getDefaultAlgorithm() {
		return construct(false);
	}

	@Override
	public Algorithm getAlgorithmSpec() {
		return construct(true);
	}
	
	private Algorithm construct(boolean includeOptions)
	{
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);
		
		{
			//product mode
			Parameter param = new Parameter("ProductMode", "string", "Normal", false);
			if (includeOptions)
			{
				param.addOption(new Option("Normal"));
			}
			algo.addParameter(param);
		}

		{
			//blockSize
			Parameter param = new Parameter("blockSize", "int", "102400", false);
			if (includeOptions)
			{
				param.addOption(new Option("500", Integer.toString(Integer.MAX_VALUE)));
			}
			algo.addParameter(param);
		}
		
		return algo;
	}

	@Override
	public ProductFactoryCreation getProductFactoryCreation() {
		return new ProductFactoryCreation() {
		    @Override
		    public TextBlockFactory create(Algorithm algo, Key key) {
		        return new TextBlockFactory(algo);
		    }
		};
	}

}
