package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import util.ByteConversion;
import util.FileSystemUtil;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.IndexWorker;
import data.ProductWorker;
import data.TrackingGroup;
import product.ProductContents;
import product.ProductLoader;
import product.ProductExtractor;
import algorithms.fullpng.FullPNG;
import algorithms.fullpng.FullPNGFactory;

public class Scratch {
	public static void main(String args[]) throws IOException, InterruptedException{
		
		//test2();
		//test3();
		//test4();
		
		//trackingGroupTest();
		//indexWorkerTest();
		
		//loadBallanceTest();

		
		
	}
	
	/**
	private static void loadBallanceTest() throws InterruptedException
	{
		List<TrackingGroup> groups = Configuration.getTrackingGroups();
		HashMap<String, LinkedBlockingQueue<File>> queues = new HashMap<String, LinkedBlockingQueue<File>>();
		HashMap<String, IndexWorker> indexWorkers = new HashMap<String, IndexWorker>();
		HashMap<String, ProductWorker> productWorkers = new HashMap<String, ProductWorker>();
		HashSet<Thread> pworkerThreads = new HashSet<Thread>();
		for(TrackingGroup group:groups)
		{
			LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<File>();
			queues.put(group.getName(), queue);
			IndexWorker iworker = new IndexWorker(queue, group.getFileSet(), group.getName());
			indexWorkers.put(group.getName(), iworker);
			
			//hook up other thing
			ProductWorker pworker = new ProductWorker(queue, group.getName(), new FullPNGFactory("asdf".getBytes()));
			productWorkers.put(group.getName(), pworker);
			Thread.sleep(1);
			
			Thread pthread = new Thread(pworker);
			
			//didn't need this
			pworkerThreads.add(pthread);
			pthread.start();
			
			new Thread(iworker).start();
		}
		
		Thread.sleep(3000);
		printQueueContents(queues.get("Untracked"));
		Thread.sleep(30000);
		productWorkers.get("Test").shutdown();
		productWorkers.get("Untracked").shutdown();
	}
	
	private static void indexWorkerTest() throws InterruptedException
	{
		//FileQueue fq = new FileQueue();
		LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<File>();
		List<TrackingGroup> groups = Configuration.getTrackingGroups();
		TrackingGroup group = groups.get(1);
		System.out.println(group);
		IndexWorker worker = new IndexWorker(queue, group.getFileSet(), group.getName());
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		printQueueContents(queue);
	}
	
	private static void printQueueContents(LinkedBlockingQueue<File> queue) throws InterruptedException
	{
		System.out.println("Queue Contents:");
		while (!queue.isEmpty())
			System.out.println("\t" + queue.take().getAbsolutePath());
	}
	
	private static void trackingGroupTest()
	{
		System.out.println(FileSystemUtil.notExplicitlyTrackedByOther(new File("C:\\TestArea\\File System Test"), "Test"));
		for (TrackingGroup tg:Configuration.getTrackingGroups())
			System.out.println(tg.toString());
	}
	
	private static void test4() throws IOException
	{
		CodeTimer ct = new CodeTimer();
		ct.start();
		
		String productPath = Configuration.getProductStagingFolder().getAbsolutePath();
		
		ProductReader<FullPNG> pr = new ProductReader<FullPNG>(new FullPNGFactory("asdf".getBytes()));
		ProductContents contents = pr.getProductContents(new File(productPath + "\\Untracked_1436587228413_4.png"));
		
		if (contents != null)
			System.out.println(contents.toString());

		pr.shutdown();
		
		

		ct.end();
		
		System.out.println("Total time: " + ct.getElapsedTime());
		
		//ImageFrame frame = new ImageFrame(ImageIO.read(new File("group_0.png")));
		
		//frame.setVisible(true);
	}
	
	private static void test3() throws IOException
	{
		CodeTimer ct = new CodeTimer();
		ct.start();
		
		
		ProductLoader pl = new ProductLoader(new FullPNGFactory("lol".getBytes()), "group");
		//pl.writeFile(new File("C:\\TestArea\\Flag Flash Cards_v1.zip"));
		pl.writeFile(new File("C:\\TestArea\\diruse.exe"));
		pl.writeFile(new File("C:\\TestArea\\diruse.exe"));
		pl.shutdown();
		
		

		ct.end();
		
		System.out.println("Total time: " + ct.getElapsedTime());
		
		ImageFrame frame = new ImageFrame(ImageIO.read(new File("products\\group_0.png")));
		
		frame.setVisible(true);
	}
	*//*
	private static void test2()
	{
		CodeTimer ct = new CodeTimer();
		ct.start();
		
		int width = 1920;
		int height = 1080;
		int range = width * height * 3;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//System.out.println("number of bytes: " + range);
		
		HashRandom ht = new HashRandom("hey");

		UniqueRandomRange urr = new UniqueRandomRange(ht, range);
		
		for(int i=0; i<range; i++)
		{
			//int temp = urr.next();
			//setImageByte(img, urr.next(), intToByte((int)ht.nextShort((short) 255)));
			//setImageByte(img, urr.next(), intToByte(150));
			setImageByte(img, urr.next(), ht.nextByte());
		}
		
		
		
		byte red = intToByte(5);
		byte green = intToByte(55);
		byte blue = intToByte(66);
		int rgb = 0;
		
		System.out.println("Green: " + green);
		System.out.println("Green: " + byteToInt(green));
		System.out.println(Integer.toHexString(rgb));
		rgb = setRed(rgb, red);
		System.out.println(Integer.toHexString(rgb));
		rgb = setGreen(rgb, green);
		System.out.println(Integer.toHexString(rgb));
		rgb = setBlue(rgb, blue);
		System.out.println(Integer.toHexString(rgb));
		

		System.out.println("Green: " + getGreen(rgb));
		System.out.println("Green: " + byteToInt(getGreen(rgb)));
		
		
		ct.end();
		
		System.out.println("Total time: " + ct.getElapsedTime());
		
		ImageFrame frame = new ImageFrame(img);
		
		frame.setVisible(true);
	}
	*/
	public static int byteToInt(byte b)
	{
		return b & 0xff;
	}
	
	public static byte intToByte(int i)
	{
		return (byte)i;
	}
	
	public static byte getRed(int rgb)
	{
		return (byte) ((rgb >> 16) & 0xFF);
	}
	public static int setRed(int rgb, byte red)
	{
		return 0xFF000000 | ((red << 16) & 0x00FF0000) | ((getGreen(rgb) << 8) & 0x0000FF00) | (getBlue(rgb) & 0x000000FF);
	}
	
	public static byte getGreen(int rgb)
	{
		return (byte) ((rgb >> 8) & 0xFF);
	}
	public static int setGreen(int rgb, byte green)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000) | ((green << 8) & 0x0000FF00) | (getBlue(rgb) & 0x000000FF);
	}
	
	public static byte getBlue(int rgb)
	{
		return (byte) (rgb & 0xFF);
	}
	public static int setBlue(int rgb, byte blue)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000) | ((getGreen(rgb) << 8) & 0x0000FF00) | (blue & 0x000000FF);
	}
	
	public static String where(int width, int height, int index)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / width;
		int x = pixel % width;
		
		
		
		String text = "(" + x + "," + y + ")";
		if (color == 0)
		{
			text += ", Red";
		}
		else if (color == 1)
		{
			text += ", Green";
		}
		else
		{
			text += ", Blue";
		}
		
		if (color >= 3 || color < 0)
			System.out.println("Color out of range: " + color);
		
		if (x < 0 || x >= width)
			System.out.println("Width out of range: " + x);		
		
		if (y < 0 || y >= height)
			System.out.println("Height out of range: " + y);
		

		
		return text;
		
	}
	
	private static void setImageByte(BufferedImage img, int index, byte data)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();

		if (color == 0)
		{
			img.setRGB(x, y, setRed(img.getRGB(x, y), data));
		}
		else if (color == 1)
		{
			img.setRGB(x, y, setGreen(img.getRGB(x, y), data));
		}
		else
		{
			img.setRGB(x, y, setBlue(img.getRGB(x, y), data));
		}
	}
}
