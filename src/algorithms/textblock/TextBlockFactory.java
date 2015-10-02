package algorithms.textblock;

import algorithms.Algorithm;
import product.ProductFactory;
import product.ProductMode;

public class TextBlockFactory implements ProductFactory<TextBlock>{

	private Algorithm algo;
	
	public TextBlockFactory(Algorithm algo)
	{
		this.algo = algo;
	}
	
	@Override
	public TextBlock create() {
		return new TextBlock(algo);
	}

}
