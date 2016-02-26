package util;

import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import config.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Utility class for byte conversion between common primitives.
 */
public class ByteConversion
{

	/**
	 * Converts a long to an 8 byte array.
	 * @param p_long The long to convert
	 * @return The long as an 8 byte array.
	 */
	public static final byte[] longToBytes(long p_long)
	{
		ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		longBuffer.putLong(0, p_long);
		return longBuffer.array();
	}

	/**
	 * Converts an 8 byte array to a long
	 * @param p_bytes The 8 byte array
	 * @return The long value
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
	 * Converts an 8 bytes to a long
	 * @param p_bytes The byte array
	 * @param p_offset The index to start from
	 * @return The long value
	 */
	public static final long bytesToLong(byte[] p_bytes, int p_offset)
	{
		ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		longBuffer.put(p_bytes, p_offset, Long.BYTES);
		longBuffer.flip();
		return longBuffer.getLong();
	}

	/**
	 * Converts one byte to its integer value
	 * @param p_byte The input byte
	 * @return The int value
	 */
	public static final int byteToInt(byte p_byte)
	{
		return p_byte & 0xff;
	}

	/**
	 * Converts an int to a byte value. Assumes the integer is
	 * in the range [0, 255]
	 * @param p_int The input int
	 * @return The int as a byte
	 */
	public static final byte intToByte(int p_int)
	{
		assert(((byte) p_int & 0xff) == p_int);
		return (byte) p_int;
	}

	/**
	 * Converts an int to its 4 byte representation
	 * @param p_int The input int
	 * @return A 4 byte array representing the int
	 */
	public static final byte[] intToBytes(int p_int)
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
		intBuffer.putInt(0, p_int);
		assert(intBuffer.array().length == 4);
		return intBuffer.array();
	}

	/**
	 * Converts a 4 byte array to an int
	 * @param p_bytes A 4 byte array
	 * @return The int value
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
	 * Parses an int from a byte array
	 * @param p_bytes The byte array
	 * @param p_offset The index to start at. Must leave at least 4 bytes to parse.
	 * @return The int value
	 */
	public static final int bytesToInt(byte[] p_bytes, int p_offset)
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
		intBuffer.put(p_bytes, p_offset, Integer.BYTES);
		intBuffer.flip();
		return intBuffer.getInt();
	}

	/**
	 * Converts 4 bytes to an int
	 * @param p_a The first byte
	 * @param p_b The second byte
	 * @param p_c The third byte
	 * @param p_d The fourth byte
	 * @return The int value
	 */
	public static final int bytesToInt(byte p_a, byte p_b, byte p_c, byte p_d)
	{
		return (p_a << 24 | (p_b & 0xFF) << 16 | (p_c & 0xFF) << 8 | (p_d & 0xFF));
	}

	/**
	 * Converts a short to a two byte array
	 * @param p_short The short value
	 * @return A two byte array of the short
	 */
	public static final byte[] shortToBytes(short p_short)
	{
		ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
		shortBuffer.putShort(0, p_short);
		return shortBuffer.array();
	}

	/**
	 * Converts a 2 byte array to a short
	 * @param p_bytes The byte array
	 * @return The short value
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
	 * Parses a short from a byte array
	 * @param p_bytes The byte array
	 * @param p_offset The index to start at. Must leave at least two bytes.
	 * @return The short value
	 */
	public static final short bytesToShort(byte[] p_bytes, int p_offset)
	{
		ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
		shortBuffer.put(p_bytes, p_offset, Short.BYTES);
		shortBuffer.flip();
		return shortBuffer.getShort();
	}

	/**
	 * Concatenates two byte arrays into one
	 * @param p_a The first byte array
	 * @param p_b The second byte array
	 * @return The concatenated byte array
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
	 * Copies the values in an array of bytes to another array
	 * @param p_orig The array of bytes
	 * @return The copy array of bytes.
	 */
	public static final byte[] deepcopy(byte[] p_orig)
	{
		byte[] copy = new byte[p_orig.length];
		System.arraycopy(p_orig, 0, copy, 0, p_orig.length);
		return copy;
	}

	/**
	 * Converts a byte array to a hex string
	 * @param p_bytes The byte array
	 * @return The hex encoded string
	 */
	public static final String bytesToHex(byte[] p_bytes)
	{
		return DatatypeConverter.printHexBinary(p_bytes);
	}

	/**
	 * Converts a hex encoded string to a byte array
	 * @param p_string The hex encoded string
	 * @return The byte array
	 */
	public static final byte[] hexToBytes(String p_string)
	{
		return DatatypeConverter.parseHexBinary(p_string);
	}

	/**
	 * Converts a byte array to a base64 encoded string
	 * @param p_bytes The byte array
	 * @return The base64 encoded string
	 */
	public static final String bytesToBase64(byte[] p_bytes)
	{
		return DatatypeConverter.printBase64Binary(p_bytes);
	}

	/**
	 * Converts a base64 encoded string to a byte array
	 * @param p_string The base64 encoded string
	 * @return The byte array
	 */
	public static final byte[] base64ToBytes(String p_string)
	{
		return DatatypeConverter.parseBase64Binary(p_string);
	}

	/**
	 * Converts a boolean to a byte
	 * @param p_boolean The boolean value
	 * @return The byte representation
	 */
	public static final byte booleanToByte(boolean p_boolean)
	{
		return p_boolean ? intToByte(1) : intToByte(0);
	}

	/**
	 * Standard function to parse the stream uuid from an archive uuid
	 * @param p_archiveUUID The archive uuid
	 * @return The stream uuid
	 */
	public static final long getStreamUUID(byte[] p_archiveUUID)
	{
		return ByteConversion.bytesToLong(p_archiveUUID, 0);
	}

	/**
	 * Standard function to parse the archive sequence number from an archive uuid
	 * @param p_archiveUUID The archive uuid
	 * @return The sequence number
	 */
	public static final int getArchiveSequenceNumber(byte[] p_archiveUUID)
	{
		return ByteConversion.bytesToInt(p_archiveUUID, Constants.STREAM_UUID_SIZE);
	}

	/**
	 * Creates a subarray from a given array of bytes
	 * @param p_data The byte array
	 * @param p_offset The index to start copying from
	 * @param p_length The length of bytes to copy
	 * @return The new subarray
	 */
	public static final byte[] subArray(byte[] p_data, int p_offset, int p_length)
	{
		byte[] sub = new byte[p_length];
		System.arraycopy(p_data, p_offset, sub, 0, p_length);
		return sub;
	}
}
