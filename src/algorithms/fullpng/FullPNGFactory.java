package algorithms.fullpng;

import algorithms.Algorithm;
import data.Key;
import product.ProductFactory;

public class FullPNGFactory implements ProductFactory<FullPNG> {
	
	private Key key;
	private Algorithm algo;
	
	public FullPNGFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}
	
	@Override
	public FullPNG create(){
		return new FullPNG(algo, key);
	}
}
