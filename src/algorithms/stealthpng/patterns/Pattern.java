package algorithms.stealthpng.patterns;

import java.util.HashMap;

public abstract class Pattern
{
	private static HashMap<Integer, Pattern> patterns;

	static
	{
		patterns = new HashMap<Integer, Pattern>();
		patterns.put(1, new Pattern1());
		patterns.put(2, new Pattern2());
	}

	public static void eval(int pattern, int[] pv, int width, int height)
	{
		patterns.get(pattern).getNeighbors(pv, width, height);
	}

	public static boolean validIndex(int pattern, int x, int y, int w, int h)
	{
		return patterns.get(pattern).isOpen(x, y, w, h);
	}

	public abstract void getNeighbors(int[] pv, int w, int h);

	public abstract boolean isOpen(int x, int y, int w, int h);

	protected final boolean inBounds(int x, int y, int w, int h)
	{
		return x >= 0 && x < w && y >= 0 && y < h;
	}

}
