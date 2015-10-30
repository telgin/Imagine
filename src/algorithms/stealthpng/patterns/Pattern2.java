package algorithms.stealthpng.patterns;

public class Pattern2 extends Pattern
{

	@Override
	public void getNeighbors(int[] pv, int w, int h)
	{
		boolean found1 = false;
		int[] xdir = new int[] { 1, 1, 1, 0, -1, -1, -1, 0 }; // TODO optimize
																// based on x %
																// 3
		int[] ydir = new int[] { -1, 0, 1, 1, 1, 0, -1, -1 };
		for (int i = 0; i < 8; ++i)
		{
			int x = pv[0] + xdir[i];
			int y = pv[1] + ydir[i];
			if (inBounds(x, y, w, h) && !columnOpen(x, w) && !rowOpen(y, h))
			{
				if (!found1)
				{
					pv[2] = x;
					pv[3] = y;
					found1 = true;
				}
				else
				{
					pv[4] = x;
					pv[5] = y;
					break;
				}
			}
		}

		// assert(!isOpen(pv[1], w, h));
		// assert(!isOpen(pv[2], w, h));
	}

	@Override
	public boolean isOpen(int x, int y, int w, int h)
	{
		return columnOpen(x, w) ? true : rowOpen(y, h);
	}

	private final boolean columnOpen(int x, int w)
	{
		return x % 3 != 1 && !(w % 3 == 1 && x == w - 1);
	}

	private final boolean rowOpen(int y, int h)
	{
		return y % 3 == 2 && !(h % 3 == 0 && y == h - 1) && !(h % 3 == 1 && y == h - 2);
	}

	public static void main(String[] args)
	{
		Pattern2 p2 = new Pattern2();
		int w = 16;
		int h = 14;
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				if (p2.isOpen(x, y, w, h))
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
