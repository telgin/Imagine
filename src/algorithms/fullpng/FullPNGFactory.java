package algorithms.fullpng;

import algorithms.Algorithm;
import product.ProductFactory;
import product.ProductMode;

public class FullPNGFactory implements ProductFactory<FullPNG> {
	
	private byte[] keyHash;
	private Algorithm algo;
	
	public FullPNGFactory(Algorithm algo, byte[] keyHash)
	{
		this.keyHash = keyHash;
		this.algo = algo;
	}
	
	@Override
	public FullPNG create(){
		return new FullPNG(algo, keyHash);
	}
}
