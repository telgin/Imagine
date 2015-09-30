package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import algorithms.fullpng.HeartRandom;

public class CryptImage {
	private static final int LENGTH_LENGTH = 4;
	
	public static BufferedImage encrypt(int height, int width, byte[] data, String password) throws IOException{
		if((height * width) < (data.length + LENGTH_LENGTH))
			throw new IOException("For and image of height: " + height + ", and width: " + width + 
					", there can be no more than " + ((height * width) - LENGTH_LENGTH) + " bytes of input.");
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		HeartRandom rand = new HeartRandom(password);
		password = null;
		Random badRand = new Random(System.currentTimeMillis());
		System.out.println("Length in: " + data.length);
		
		//make an choices
		List<short[]> choices = new ArrayList<short[]>();
		for(short x=0; x<width; x++){
			for(short y=0; y<height; y++){
				for(short argb=0; argb<4; argb++){
					choices.add(new short[]{x,y,argb});
				}
			}
		}
		
		//make a byte matrix
		byte[][][] imgData = new byte[width][height][4];
		
		//set the length
		byte[] lengthBytes = intToBytes(data.length);
		for(int i=0; i<LENGTH_LENGTH; i++){
			short[] c = choices.remove(rand.nextInt(choices.size()));
			imgData[c[0]][c[1]][c[2]] = lengthBytes[i];
			System.out.println(c[0] + ", " + c[1] + ", " + c[2]);
			System.out.println(lengthBytes[i]);
		}
		
		//set the data
		for(int i=0; i<data.length; i++){
			short[] c = choices.remove(rand.nextInt(choices.size()));
			imgData[c[0]][c[1]][c[2]] = data[i];
		}
		
		//set the rest
		int stop = choices.size();
		for(int i=0; i<stop; i++){
			short[] c = choices.remove(rand.nextInt(choices.size()));
			imgData[c[0]][c[1]][c[2]] = (byte) badRand.nextInt(256);
		}
		
		//set the image
		for(int x=0; x<width; x++){
			for(int y=0; y<height; y++){
				img.setRGB(x, y, bytesToInt(imgData[x][y]));
			}
		}
		
		return img;
	}
	
	public static byte[] decrypt(BufferedImage img, String password){
		HeartRandom rand = new HeartRandom(password);
		password = null;
		
		//make an choices
		List<short[]> choices = new ArrayList<short[]>();
		for(short x=0; x<img.getWidth(); x++){
			for(short y=0; y<img.getHeight(); y++){
				for(short argb=0; argb<4; argb++){
					choices.add(new short[]{x,y,argb});
				}
			}
		}
		
		//make a byte matrix
		byte[][][] imgData = new byte[img.getWidth()][img.getHeight()][4];
		
		//fill the matrix
		for(short x=0; x<img.getWidth(); x++){
			for(short y=0; y<img.getHeight(); y++){
				imgData[x][y] = intToBytes(img.getRGB(x,y));
			}
		}
		
		//get length
		byte[] lengthBytes = new byte[LENGTH_LENGTH];
		for(int i=0; i<LENGTH_LENGTH; i++){
			short[] c = choices.remove(rand.nextInt(choices.size()));
			lengthBytes[i] = imgData[c[0]][c[1]][c[2]];
			System.out.println(c[0] + ", " + c[1] + ", " + c[2]);
			System.out.println(lengthBytes[i]);
		}
		int length = bytesToInt(lengthBytes);
		System.out.println("Length out: " + length);
		
		//get data
		byte[] data = new byte[length];
		for(int i=0; i<data.length; i++){
			short[] c = choices.remove(rand.nextInt(choices.size()));
			data[i] = imgData[c[0]][c[1]][c[2]];
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
