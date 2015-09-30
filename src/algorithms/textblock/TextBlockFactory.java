package algorithms.textblock;

import algorithms.ProductFactory;
import algorithms.ProductMode;

public class TextBlockFactory implements ProductFactory<TextBlock>{

	private ProductMode mode;
	
	public TextBlockFactory(ProductMode mode)
	{
		this.mode = mode;
	}
	
	@Override
	public TextBlock create() {
		return new TextBlock(mode);
	}

}
