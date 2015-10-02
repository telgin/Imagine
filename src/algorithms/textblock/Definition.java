package algorithms.textblock;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import algorithms.Option;
import algorithms.Parameter;

public class Definition implements algorithms.Definition{
	private static final String NAME = "TextBlock";
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
			Parameter param = new Parameter("ProductMode", "Normal", false);
			if (includeOptions)
			{
				param.addOption(new Option("Text", "Normal"));
			}
		}
		
		{
			//usesDatabase //TODO this was a tracking group field, not an algo field
			Parameter param = new Parameter("usesDatabase", "true", false);
			if (includeOptions)
			{
				param.addOption(new Option("boolean", "t/f"));
			}
		}
		
		return algo;
	}

}
