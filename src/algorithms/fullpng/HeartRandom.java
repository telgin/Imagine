package algorithms.fullpng;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import test.CodeTimer;
import util.ByteConversion;

/**
 * @author Tom
 * The random of my heart.
 */
public class HeartRandom {
	private byte[] curHash;
	private MessageDigest md;
	private final int STOP = ((512/8) / 2); //half way
	private int index = 0;
	
	public HeartRandom(byte[] seed){
		init(seed);
	}
	
	public HeartRandom(String seed){
		init(seed.getBytes());
	}
	
	public HeartRandom(Long seed){
		init(ByteConversion.longToBytes(seed));
	}
	
	private void init(byte[] seed){
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		curHash = md.digest(seed);
	}
	
	public short nextShort(short to){
		if(index >= STOP-1)
			refresh();
		
		short next = (short) ((curHash[index] << 8) | (curHash[index+1]));
		index += 2;
		return (short) Math.abs(next % to);
	}

	private void refresh() {
		index = 0;
		curHash = md.digest(curHash);
	}
	
	public byte nextByte()
	{
		if(index >= STOP)
			refresh();
		
		return curHash[index++];
	}

	public int nextInt(int to) {
		if(to == 0)
			return 0;
		
		if(index >= STOP-3)
			refresh();
		
		//int next = ((curHash[index] << 24) | (curHash[index+1] << 16) | (curHash[index+2] << 8) | (curHash[index+3]));
		//int next2 = (  ((val >> 24) & 0xff) | ((val >> 8) & 0xff00) | ((val << 8) & 0xff0000) | ((val << 24) & 0xff000000) );
		//int next3 = (curHash[index] << 24 | (curHash[index+1] & 0xFF) << 16 | (curHash[index+2] & 0xFF) << 8 | (curHash[index+3] & 0xFF));
		//int next = ((~curHash[index] & 0xff) << 24) | ((~curHash[index+1] & 0xff) << 16) | ((~curHash[index+2] & 0xff) << 8) | (~curHash[index+3] & 0xff);
		//byte[] test = new byte[]{curHash[index],curHash[index+1],curHash[index+2],curHash[index+3]};
		//Byte
		//next = ByteConversion.bytesToInt(test);
		 
		 
		
		index += 4;
		//System.out.println(next);
		//System.out.println(Math.abs(next % to));
		//return  Math.abs(Integer.reverseBytes(next3) % to);
		return  Math.abs((curHash[index] << 24 | (curHash[index+1] & 0xFF) << 16 | (curHash[index+2] & 0xFF) << 8 | (curHash[index+3] & 0xFF)) % to);
	}
	
	public static void main(String args[])
	{
		
		profileIntIterative();
		profileIntPreGeneration();
		
	}
	
	private static void profileIntIterative()
	{
		int averageImageSize = 6022800;
		int images = 16;
		int rotations = averageImageSize * images;
		
		
		CodeTimer ct = new CodeTimer();
		ct.start();
		
		HeartRandom random = new HeartRandom("seed");
		long dump = 0;
		for(int i=0; i<rotations; ++i)
		{
			dump += random.nextInt(averageImageSize); //this number is not important for profiling
			dump += random.nextByte();
		}
		
		ct.end();
		System.out.println("Dump: " + dump);
		System.out.println(rotations + " rotations in " + ct.getElapsedTime() + " milliseconds.");
		System.out.println("(~" + (ct.getElapsedTime() / images) + " milliseconds/image)");
	}
	
	private static void profileIntPreGeneration()
	{
		int averageImageSize = 6022800;
		int maxApplicationSize = 8000000;
		int images = 100000000 / averageImageSize; // the number of images close to iterative profiling
		
		CodeTimer ct = new CodeTimer();
		ct.start();
		
		HeartRandom random = new HeartRandom("seed");
		int[] ibuffer = new int[maxApplicationSize];
		byte[] bbuffer = new byte[maxApplicationSize];
		for(int i=0; i<maxApplicationSize; ++i)
		{
			ibuffer[i] = random.nextInt(averageImageSize);
			bbuffer[i] = random.nextByte();
		}
		
		long dump = 0;
		for (int x=0; x<images; ++x)
		{
			for (int y=0; y<averageImageSize; ++y)
			{
				dump += ibuffer[y];
				dump += bbuffer[y];
			}
		}
		
		ct.end();
		System.out.println("Dump: " + dump);
		System.out.println("Generated " + images + " images in " + ct.getElapsedTime() + " milliseconds.");
		System.out.println("(~" + (ct.getElapsedTime() / images) + " milliseconds/image)");
	}
}
