package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import algorithms.fullpng.HeartRandom;

public class SecImage {
	private static final int LENGTH_LENGTH = 4;
	private static final int DIFFICULTY = 100;
	
	public static BufferedImage secure(int width, int height, byte[] data, String password) throws IOException{
		if((height * width * 4) < (data.length + LENGTH_LENGTH))
			throw new IOException("For and image of height: " + height + ", and width: " + width + 
					", there can be no more than " + ((height * width) - LENGTH_LENGTH) + " bytes of input.");
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		HeartRandom rand = new HeartRandom(password);
		password = null;
		System.out.println("Length in: " + data.length);
		
		//list choices
		List<short[]> choices = new LinkedList<short[]>();
		for(short x=0; x<width; x++){
			for(short y=0; y<height; y++){
				choices.add(0, new short[]{x,y});
			}
		}
		
		//set the length
		byte[] lengthBytes = intToBytes(data.length);
		short[] lengthChoice = choices.remove(rand.nextInt(choices.size()));
		img.setRGB(lengthChoice[0], lengthChoice[1], bytesToInt(new byte[]{lengthBytes[0],
																		   lengthBytes[1],
																		   lengthBytes[2],
																		   lengthBytes[3]})); 
		
		//set the image
		int dataIndex = 0;
		while(choices.size() > 0){
			short[] c = choices.remove(rand.nextInt( choices.size() >= DIFFICULTY ? DIFFICULTY : choices.size()));
			img.setRGB(c[0], c[1], bytesToInt(new byte[]{data[(dataIndex) % data.length],
														 data[(dataIndex+1) % data.length],
														 data[(dataIndex+2) % data.length],
														 data[(dataIndex+3) % data.length]})); 
			dataIndex += 4;
		}
		
		return img;
	}
	
	public static byte[] extract(BufferedImage img, String password) throws IOException{
		if(img.getType() != BufferedImage.TYPE_INT_ARGB)
			throw new IOException("Img is not of expected type: int_argb");
		
		HeartRandom rand = new HeartRandom(password);
		password = null;
		
		//list choices
		List<short[]> choices = new LinkedList<short[]>();
		for(short x=0; x<img.getWidth(); x++){
			for(short y=0; y<img.getHeight(); y++){
				choices.add(0, new short[]{x,y});
			}
		}
		
		//get the length
		short[] lengthChoice = choices.remove(rand.nextInt(choices.size()));
		int length = img.getRGB(lengthChoice[0], lengthChoice[1]);
		if((img.getWidth() * img.getHeight() * 4) < (length + LENGTH_LENGTH))
			throw new IOException("Length of: " + length + " is incorrect for the given image.");
		
		//get the data
		byte[] data = new byte[length];
		int dataIndex = 0;
		while(dataIndex < length){
			short[] c = choices.remove(rand.nextInt( choices.size() >= DIFFICULTY ? DIFFICULTY : choices.size()));
			byte[] pix = intToBytes(img.getRGB(c[0], c[1]));
			if(dataIndex < length)
				data[dataIndex++] = pix[0];
			if(dataIndex < length)
				data[dataIndex++] = pix[1];
			if(dataIndex < length)
				data[dataIndex++] = pix[2];
			if(dataIndex < length)
				data[dataIndex++] = pix[3];
		}

		return data;
	}
	
	public static int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getInt();
	}
	
	public static byte[] intToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.putInt(x);
	    return buffer.array();
	}
}
