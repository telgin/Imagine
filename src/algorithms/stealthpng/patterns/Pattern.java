package algorithms.stealthpng.patterns;

import java.util.HashMap;

public abstract class Pattern {
	private static HashMap<Integer, Pattern> patterns;
	
	static
	{
		patterns = new HashMap<Integer, Pattern>();
		patterns.put(1, new Pattern1());
		patterns.put(2, new Pattern2());
	}
	
	public static void eval(int pattern, int[] pv, int width, int height) {
		patterns.get(pattern).getNeighbors(pv, width, height);
	}
	
	public static boolean validIndex(int pattern, int i, int w, int h) {
		return patterns.get(pattern).isOpen(i, w, h);
	}
	
	public abstract void getNeighbors(int[] pv, int w, int h);
	
	public abstract boolean isOpen(int i, int w, int h);
	
	protected static int[] toColor(int index, int w)
	{
		int rgb = index % 3;
		int pixel = index / 3;
		int y = pixel / w;
		int x = pixel % w;

		return new int[]{x, y, rgb};
	}
	
	protected static int toIndex(int[] coords, int w)
	{
		return (((coords[1] * w) + coords[0]) * 3) + coords[2];
	}



	

}
