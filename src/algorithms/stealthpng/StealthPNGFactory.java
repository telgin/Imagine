package algorithms.stealthpng;

import algorithms.Algorithm;
import data.Key;
import product.ProductFactory;

public class StealthPNGFactory implements ProductFactory<StealthPNG> {
	
	private Key key;
	private Algorithm algo;
	
	public StealthPNGFactory(Algorithm algo, Key key)
	{
		this.key = key;
		this.algo = algo;
	}
	
	@Override
	public StealthPNG create(){
		return new StealthPNG(algo, key);
	}
}
