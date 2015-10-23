package algorithms.stealthpng;

public class Patterns {

	public static void eval(int pattern, int[] pv, int width, int height) {
		pattern1(pv, width, height);
	}
	
	private static void pattern1(int[] pv, int w, int h)
	{
		int[] t = toColor(pv[0], w);
		int[] a1;
		int[] a2;
		
		if (t[0] == w-1)
		{
			if (t[1] == h-1)
			{
				a1 = new int[]{t[0]-1,t[1],t[2]};
				a2 = new int[]{t[0],t[1]-1,t[2]};
			}
			else
			{
				a1 = new int[]{t[0]-1,t[1],t[2]};
				a2 = new int[]{t[0],t[1]+1,t[2]};
			}
		}
		else
		{
			if (t[1] == h-1)
			{
				a1 = new int[]{t[0],t[1]-1,t[2]};
				a2 = new int[]{t[0]+1,t[1],t[2]};
			}
			else
			{
				a1 = new int[]{t[0],t[1]+1,t[2]};
				a2 = new int[]{t[0]+1,t[1],t[2]};
			}
		}
		
		pv[1] = toIndex(a1, w);
		pv[2] = toIndex(a2, w);
	}
	
	private static int[] toColor(int index, int w)
	{
		int rgb = index % 3;
		int pixel = index / 3;
		int y = pixel / w;
		int x = pixel % w;
		
		if (index == 15933)
			System.out.println("magic: " + x + ", " + y + ", " + rgb);

		return new int[]{x, y, rgb};
	}
	
	private static int toIndex(int[] coords, int w)
	{
		return (((coords[1] * w) + coords[0]) * 3) + coords[2];
	}

	public static boolean validIndex(int pattern, int i, int w) {
		return pattern1(i, w);
	}

	private static boolean pattern1(int i, int w) {
		int[] point = toColor(i, w);
		boolean cond1 = point[0] % 2 == 1;
		boolean cond2 = point[1] % 2 == 1;
		return cond1 ^ cond2;
	}

}
