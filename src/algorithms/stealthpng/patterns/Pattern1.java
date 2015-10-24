package algorithms.stealthpng.patterns;

public class Pattern1 extends Pattern{

	@Override
	public void getNeighbors(int[] pv, int w, int h)
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

	@Override
	public boolean isOpen(int i, int w, int h) {
		int[] point = toColor(i, w);
		boolean cond1 = point[0] % 2 == 1;
		boolean cond2 = point[1] % 2 == 1;
		return cond1 ^ cond2;
	}
	
	public static void main(String[] args)
	{
		Pattern1 p1 = new Pattern1();
		int w = 15;
		int h = 14;
		for (int y=0; y<h; ++y)
		{
			for (int x=0; x<w; ++x)
			{
				int i = toIndex(new int[]{x, y, 1}, w);
				if (p1.isOpen(i, w, h))
				{
					System.out.print(" . ");
				}
				else
				{
					System.out.print(" x ");
				}
			}
			System.out.println();
		}		
	}
}
