package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ByteConversionTest {

	@Test
	public void testLongToBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToLongByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToLongByteArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testByteToInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntToByte() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntToBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToIntByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToIntByteArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToIntByteByteByteByte() {
		fail("Not yet implemented");
	}

	@Test
	public void testShortToBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToShortByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToShortByteArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testConcat() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeepcopy() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesEqual() {
		fail("Not yet implemented");
	}

	@Test
	public void testBytesToHex() {
		fail("Not yet implemented");
	}

	@Test
	public void testHexToBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testBooleanToByte() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStreamUUID() {
		{
			long struuid = 1234567;
			int sequenceNum = 1;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(struuid, ByteConversion.getStreamUUID(uuid));
		}
		
		{
			long struuid = 0;
			int sequenceNum = 1;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(struuid, ByteConversion.getStreamUUID(uuid));
		}
		
		{
			long struuid = 1445743370056l;
			int sequenceNum = 5;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(struuid, ByteConversion.getStreamUUID(uuid));
		}
		
		{
			long struuid = 144l;
			int sequenceNum = 545674;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(struuid, ByteConversion.getStreamUUID(uuid));
		}
	}

	@Test
	public void testGetProductSequenceNumber() {
		{
			long struuid = 1234567;
			int sequenceNum = 1;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(sequenceNum, ByteConversion.getProductSequenceNumber(uuid));
		}
		
		{
			long struuid = 0;
			int sequenceNum = 1;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(sequenceNum, ByteConversion.getProductSequenceNumber(uuid));
		}
		
		{
			long struuid = 1445744247061l;
			int sequenceNum = 1;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(sequenceNum, ByteConversion.getProductSequenceNumber(uuid));
		}
		
		{
			long struuid = 144l;
			int sequenceNum = 545674;
			byte[] streamUUID = ByteConversion.longToBytes(struuid);
			byte[] productSequenceNumber = ByteConversion.intToBytes(sequenceNum);
			
			byte[] uuid = ByteConversion.concat(streamUUID, productSequenceNumber);
			
			assertEquals(sequenceNum, ByteConversion.getProductSequenceNumber(uuid));
		}
	}

	@Test
	public void testSubArray() {
		fail("Not yet implemented");
	}

}
