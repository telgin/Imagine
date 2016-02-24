package util;

import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import config.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 *
 */
public class ByteConversion
{

	/**
	 * @update_comment
	 * @param p_long
	 * @return
	 */
	public static final byte[] longToBytes(long p_long)
	{
		ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		longBuffer.putLong(0, p_long);
		return longBuffer.array();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public static final long bytesToLong(byte[] p_bytes)
	{
		ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		assert(p_bytes.length == 8);
		longBuffer.put(p_bytes, 0, p_bytes.length);
		longBuffer.flip();
		return longBuffer.getLong();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @param p_offset
	 * @return
	 */
	public static final long bytesToLong(byte[] p_bytes, int p_offset)
	{
		ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		longBuffer.put(p_bytes, p_offset, Long.BYTES);
		longBuffer.flip();
		return longBuffer.getLong();
	}

	/**
	 * @update_comment
	 * @param p_byte
	 * @return
	 */
	public static final int byteToInt(byte p_byte)
	{
		return p_byte & 0xff;
	}

	/**
	 * @update_comment
	 * @param p_int
	 * @return
	 */
	public static final byte intToByte(int p_int)
	{
		assert(((byte) p_int & 0xff) == p_int);
		return (byte) p_int;
	}

	/**
	 * @update_comment
	 * @param p_int
	 * @return
	 */
	public static final byte[] intToBytes(int p_int)
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
		intBuffer.putInt(0, p_int);
		assert(intBuffer.array().length == 4);
		return intBuffer.array();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public static final int bytesToInt(byte[] p_bytes)
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
		assert(p_bytes.length == 4);
		intBuffer.put(p_bytes, 0, p_bytes.length);
		intBuffer.flip();
		return intBuffer.getInt();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @param p_offset
	 * @return
	 */
	public static final int bytesToInt(byte[] p_bytes, int p_offset)
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
		intBuffer.put(p_bytes, p_offset, Integer.BYTES);
		intBuffer.flip();
		return intBuffer.getInt();
	}

	/**
	 * @update_comment
	 * @param p_a
	 * @param p_b
	 * @param p_c
	 * @param p_d
	 * @return
	 */
	public static final int bytesToInt(byte p_a, byte p_b, byte p_c, byte p_d)
	{
		return (p_a << 24 | (p_b & 0xFF) << 16 | (p_c & 0xFF) << 8 | (p_d & 0xFF));
	}

	/**
	 * @update_comment
	 * @param p_short
	 * @return
	 */
	public static final byte[] shortToBytes(short p_short)
	{
		ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
		shortBuffer.putShort(0, p_short);
		return shortBuffer.array();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public static final short bytesToShort(byte[] p_bytes)
	{
		ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
		assert(p_bytes.length == 2);
		shortBuffer.put(p_bytes, 0, p_bytes.length);
		shortBuffer.flip();
		return shortBuffer.getShort();
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @param p_offset
	 * @return
	 */
	public static final short bytesToShort(byte[] p_bytes, int p_offset)
	{
		ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
		shortBuffer.put(p_bytes, p_offset, Short.BYTES);
		shortBuffer.flip();
		return shortBuffer.getShort();
	}

	/**
	 * @update_comment
	 * @param p_a
	 * @param p_b
	 * @return
	 */
	public static final byte[] concat(byte[] p_a, byte[] p_b)
	{
		int len1 = p_a.length;
		int len2 = p_b.length;
		byte[] c = new byte[len1 + len2];
		System.arraycopy(p_a, 0, c, 0, len1);
		System.arraycopy(p_b, 0, c, len1, len2);
		return c;
	}

	/**
	 * @update_comment
	 * @param p_orig
	 * @return
	 */
	public static final byte[] deepcopy(byte[] p_orig)
	{
		byte[] copy = new byte[p_orig.length];
		System.arraycopy(p_orig, 0, copy, 0, p_orig.length);
		return copy;
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public static final String bytesToHex(byte[] p_bytes)
	{
		return DatatypeConverter.printHexBinary(p_bytes);
	}

	/**
	 * @update_comment
	 * @param p_string
	 * @return
	 */
	public static final byte[] hexToBytes(String p_string)
	{
		return DatatypeConverter.parseHexBinary(p_string);
	}

	/**
	 * @update_comment
	 * @param p_bytes
	 * @return
	 */
	public static final String bytesToBase64(byte[] p_bytes)
	{
		return DatatypeConverter.printBase64Binary(p_bytes);
	}

	/**
	 * @update_comment
	 * @param p_string
	 * @return
	 */
	public static final byte[] base64ToBytes(String p_string)
	{
		return DatatypeConverter.parseBase64Binary(p_string);
	}

	/**
	 * @update_comment
	 * @param p_boolean
	 * @return
	 */
	public static final byte booleanToByte(boolean p_boolean)
	{
		return p_boolean ? intToByte(1) : intToByte(0);
	}

	/**
	 * @update_comment
	 * @param p_productUUID
	 * @return
	 */
	public static final long getStreamUUID(byte[] p_productUUID)
	{
		return ByteConversion.bytesToLong(p_productUUID, 0);
	}

	/**
	 * @update_comment
	 * @param p_productUUID
	 * @return
	 */
	public static final int getProductSequenceNumber(byte[] p_productUUID)
	{
		return ByteConversion.bytesToInt(p_productUUID, Constants.STREAM_UUID_SIZE);
	}

	/**
	 * @update_comment
	 * @param p_data
	 * @param p_offset
	 * @param p_length
	 * @return
	 */
	public static final byte[] subArray(byte[] p_data, int p_offset, int p_length)
	{
		byte[] sub = new byte[p_length];
		System.arraycopy(p_data, p_offset, sub, 0, p_length);
		return sub;
	}
}
