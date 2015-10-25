package util;

import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

public class ByteConversion {
	
    public static byte[] longToBytes(long x) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    	longBuffer.clear();
    	longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    	assert (bytes.length == 8);
    	longBuffer.clear();
    	longBuffer.put(bytes, 0, bytes.length);
    	longBuffer.flip();
        return longBuffer.getLong();
    }
    
    public static long bytesToLong(byte[] bytes, int offset) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    	longBuffer.clear(); //clears probably aren't necessary anymore
    	longBuffer.put(bytes, offset, Long.BYTES);
    	longBuffer.flip();
        return longBuffer.getLong();
    }
    
	public static int byteToInt(byte b)
	{
		return b & 0xff;
	}
	
	public static byte intToByte(int i)
	{
		assert(((byte)i & 0xff) == i);
		return (byte)i;
	}

    public static byte[] intToBytes(int x) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
    	intBuffer.clear();
    	intBuffer.putInt(0, x);
    	assert(intBuffer.array().length == 4);
        return intBuffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
    	assert (bytes.length == 4);
    	intBuffer.clear();
    	intBuffer.put(bytes, 0, bytes.length);
    	intBuffer.flip();
        return intBuffer.getInt();
    }

	public static int bytesToInt(byte[] bytes, int offset) {
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
    	intBuffer.clear();
    	intBuffer.put(bytes, offset, Integer.BYTES); //need source, 3rd field is bytes to read?
    	intBuffer.flip();
        return intBuffer.getInt();
	}
    
    public static int bytesToInt(byte a, byte b, byte c, byte d)
    {
    	return (a << 24 | (b & 0xFF) << 16 | (c & 0xFF) << 8 | (d & 0xFF));
    }
    
    public static byte[] shortToBytes(short x) {
    	ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    	shortBuffer.clear();
    	shortBuffer.putShort(0, x);
        return shortBuffer.array();
    }

    public static short bytesToShort(byte[] bytes) {
    	ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    	assert (bytes.length == 2);
    	shortBuffer.clear();
    	shortBuffer.put(bytes, 0, bytes.length);
    	shortBuffer.flip();
        return shortBuffer.getShort();
    }
    
    public static short bytesToShort(byte[] bytes, int offset) {
    	ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    	shortBuffer.clear(); //clears probably aren't necessary anymore
    	shortBuffer.put(bytes, offset, Short.BYTES);
    	shortBuffer.flip();
        return shortBuffer.getShort();
    }
    
    public static byte[] concat(byte[] a, byte[] b) {
    	   int len1 = a.length;
    	   int len2 = b.length;
    	   byte[] c = new byte[len1+len2];
    	   System.arraycopy(a, 0, c, 0, len1);
    	   System.arraycopy(b, 0, c, len1, len2);
    	   return c;
	}
    
    public static byte[] deepcopy(byte[] orig) {
 	   byte[] copy = new byte[orig.length];
 	   System.arraycopy(orig, 0, copy, 0, orig.length);
 	   return copy;
	}

	public static boolean bytesEqual(byte[] ar1, byte[] ar2) {
		if (ar1.length != ar2.length)
			return false;
		
		for (int x=0; x<ar1.length; ++x)
		{
			if (ar1[x] != ar2[x])
				return false;
		}
		
		return true;
	}

	public static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}

	public static byte[] hexToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}

	public static byte booleanToByte(boolean metadataUpdate) {
		return metadataUpdate ? intToByte(1) : intToByte(0);
	}
	
	public static long getStreamUUID(byte[] productUUID)
	{
		return ByteConversion.bytesToLong(productUUID, 0);
	}
	
	public static int getProductSequenceNumber(byte[] productUUID)
	{
		return ByteConversion.bytesToInt(productUUID, Constants.STREAM_UUID_SIZE);
	}
	
	public static byte[] subArray(byte[] data, int offset, int length)
	{
		byte[] sub = new byte[length];
		System.arraycopy(data, offset, sub, 0, length);
		return sub;
	}
}
