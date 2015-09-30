package algorithms.fullpng;

import algorithms.ProductFactory;
import algorithms.ProductMode;

public class FullPNGFactory implements ProductFactory<FullPNG> {
	
	private byte[] keyHash;
	private ProductMode mode;
	
	public FullPNGFactory(ProductMode mode, byte[] keyHash)
	{
		this.keyHash = keyHash;
		this.mode = mode;
	}
	
	@Override
	public FullPNG create(){
		return new FullPNG(mode, keyHash);
	}
}
