package algorithms.fullpng;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import algorithms.Option;
import algorithms.Parameter;

public class Definition implements algorithms.Definition{
	private static final String NAME = "FullPNG";
	private static final int VERSION_NUMBER = 1;
	private static Definition self;
	
	private Definition(){}
	
	public static void init()
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
		Algorithm algo = new Algorithm(NAME, VERSION_NUMBER);
		
		{
			//product mode
			Parameter param = new Parameter("ProductMode", "string", "Secure", false);
			if (includeOptions)
			{
				param.addOption(new Option("Secure"));
			}
			algo.addParameter(param);
		}
		
		{
			//colors
			Parameter param = new Parameter("colors", "string", "rgb", false);
			if (includeOptions)
			{
				param.addOption(new Option("rgb"));
				param.addOption(new Option("rgba"));
			}
			algo.addParameter(param);
		}

		{
			//width
			Parameter param = new Parameter("width", "int", "1820", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
			}
			algo.addParameter(param);
		}

		{
			//height
			Parameter param = new Parameter("height", "int", "980", false);
			if (includeOptions)
			{
				param.addOption(new Option("0", "10000"));
			}
			algo.addParameter(param);
		}
		
		return algo;
	}

}
