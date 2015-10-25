package algorithms.stealthpng.patterns;

public class Pattern2 extends Pattern{

	@Override
	public void getNeighbors(int[] pv, int w, int h) {
		int[] t = toColor(pv[0], w);
		int[] a1 = new int[]{-1, -1, t[2]};
		int[] a2 = new int[]{-1, -1, t[2]};
		
		boolean found1 = false;
		int[] xdir = new int[]{1,1,1,0,-1,-1,-1,0};
		int[] ydir = new int[]{-1,0,1,1,1,0,-1,-1};
		for (int i=0; i<8; ++i)
		{
			int x = t[0] + xdir[i];
			int y = t[1] + ydir[i];
			if (inBounds(x, y, w, h) && !columnOpen(x, w) && !rowOpen(y, h))
			{
				if (!found1)
				{
					a1[0] = x;
					a1[1] = y;
					found1 = true;
				}
				else
				{
					a2[0] = x;
					a2[1] = y;
					break;
				}
			}
		}
		
		pv[1] = toIndex(a1, w);
		pv[2] = toIndex(a2, w);
		
		//assert(!isOpen(pv[1], w, h));
		//assert(!isOpen(pv[2], w, h));
	}
	


	@Override
	public boolean isOpen(int i, int w, int h) {
		int[] p = toColor(i, w);
		
		return columnOpen(p[0], w) ? true : rowOpen(p[1], h);
	}
	
	private final boolean columnOpen(int x, int w)
	{
		return x % 3 != 1 && 
			!(w % 3 == 1 && x == w - 1);
	}
	
	private final boolean rowOpen(int y, int h)
	{
		return y % 3 == 2 &&
			!(h % 3 == 0 && y == h - 1) &&
			!(h % 3 == 1 && y == h - 2);
	}
	
	public static void main(String[] args)
	{
		Pattern2 p2 = new Pattern2();
		int w = 16;
		int h = 14;
		for (int y=0; y<h; ++y)
		{
			for (int x=0; x<w; ++x)
			{
				int i = toIndex(new int[]{x, y, 1}, w);
				if (p2.isOpen(i, w, h))
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
