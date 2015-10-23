package util.algorithms;


public class UniqueRandomRange
{
	private int index;
	private int[] array;
	private HashRandom random;
	
	public UniqueRandomRange(HashRandom random, int range)
	{
		this.random = random;
		
		index = range;
		array = new int[range];
		
		for(int i=0; i<range; ++i)
			array[i] = i;
	}
	
	public void reseed(byte[] seed)
	{
		random = new HashRandom(seed);
	}
	
	public int remainingNumbers()
	{
		return index;
	}
	
	public boolean hasRemainingNumbers()
	{
		return index > 0;
	}
	
	public int next()
	{
		int swapIndex = random.nextInt(index);
		int temp = array[swapIndex];
		array[swapIndex] = array[index - 1];
		array[index - 1] = temp;
		
		--index;
		
		//System.out.println(temp);
		return temp;
	}
}
