package algorithms.imageoverlay.patterns;

public class Pattern1 extends Pattern
{

	@Override
	public void getNeighbors(int[] pv, int w, int h)
	{
		if (pv[0] == w - 1)
		{
			if (pv[1] == h - 1)
			{
				pv[2] = pv[0] - 1;
				pv[3] = pv[1];
				pv[4] = pv[0];
				pv[5] = pv[1] - 1;
			}
			else
			{
				pv[2] = pv[0] - 1;
				pv[3] = pv[1];
				pv[4] = pv[0];
				pv[5] = pv[1] + 1;
			}
		}
		else
		{
			if (pv[1] == h - 1)
			{
				pv[2] = pv[0];
				pv[3] = pv[1] - 1;
				pv[4] = pv[0] + 1;
				pv[5] = pv[1];
			}
			else
			{
				pv[2] = pv[0];
				pv[3] = pv[1] + 1;
				pv[4] = pv[0] + 1;
				pv[5] = pv[1];
			}
		}
	}

	@Override
	public boolean isOpen(int x, int y, int w, int h)
	{
		return (x % 2 == 1) ^ (y % 2 == 1);
	}

	public static void main(String[] args)
	{
		Pattern1 p1 = new Pattern1();
		int w = 15;
		int h = 14;
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				if (p1.isOpen(x, y, w, h))
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
