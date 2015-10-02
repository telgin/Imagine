package algorithms.fullpng;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import algorithms.Option;
import algorithms.Parameter;

public class Definition implements algorithms.Definition{
	private static final String NAME = "FullPNG";
	private static Definition self;
	
	private Definition(){}
	
	static
	{
		self = new Definition();
		AlgorithmRegistry.registerDefinition(NAME, self);
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
		Algorithm algo = new Algorithm(NAME);
		
		{
			//product mode
			Parameter param = new Parameter("ProductMode", "Secure", false);
			if (includeOptions)
			{
				param.addOption(new Option("Text", "Secure"));
			}
		}
		
		{
			//usesDatabase
			Parameter param = new Parameter("usesDatabase", "Secure", false);
			if (includeOptions)
			{
				param.addOption(new Option("boolean", "t/f"));
			}
		}
		
		return algo;
	}

}
