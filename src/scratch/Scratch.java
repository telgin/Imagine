package scratch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import org.apache.derby.tools.ij;

import util.ByteConversion;
import util.FileSystemUtil;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.FileKey;
import data.IndexWorker;
import data.Key;
import data.Metadata;
import data.ProductWorker;
import data.TrackingGroup;
import product.ProductContents;
import product.ProductLoader;
import system.Imagine;
import treegenerator.TreeGenerator;
import product.ProductExtractor;
import product.ProductIOException;
import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import algorithms.image.Image;
import algorithms.image.ImageFactory;

public class Scratch {
	public static int x = 0;
	public static double y = 0;
	public static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	private static boolean b;
	public static void main(String args[]) throws IOException, InterruptedException, SQLException
	{
		
		
		
		
		//args = new String[]{"--open", "-a", "image_basic", "-i", "testing/output/imagine_reserved_temp_1452209731750_0.png"};
		
		//args = new String[]{"--help"};
		
		//Imagine.run(args);
		
		
		
		
		System.out.println(b);
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
//		File originalRoot = new File("/home/tom/temp/original_root");
//		File extractedRoot = new File("/home/tom/temp/extracted_root");
//		
//		listFiles(originalRoot);
//		listFiles(extractedRoot);
//		
//		File run1 = new File("/home/tom/temp/original_root/1/run1");
//		File run2 = new File("/home/tom/temp/original_root/1/run2");
//		
//		System.out.println("absolutePaths == false");
//		File r1_1 = new File(run1.toURI().relativize(originalRoot.toURI()));
//		String r1_2 = originalRoot.toURI().relativize(run1.toURI()).getPath();
//		File f_r1_2 = new File(r1_2);
//		File expectedRel = new File(extractedRoot, r1_2);
//		System.out.println(expectedRel.getPath());
//		
//		File expectedAbs = new File(extractedRoot, run1.getPath());
//		System.out.println(expectedAbs.getPath());
//		
//	}
	
	
	
	
	private static void listFiles(File root)
	{
		System.out.println("Contents of: " + root.getAbsolutePath());
		int count = 0;
		
		//bfs through folders
		Queue<File> folders = new LinkedList<File>();
		folders.add(root);
		
		while (folders.size() > 0)
		{
			File folder = folders.poll();
			for (File sub : folder.listFiles())
			{
				if (sub.isDirectory())
				{
					folders.add(sub);
				}
				else
				{
					++count;
				}
				
				System.out.println(sub.getPath());
			}
		}
		
		System.out.println("total files = " + count + "\n");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
//		//tracking group setup
//		Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("TextBlock");
//		String keyName = "testKeyName";
//		String groupName = "Cheese and other Cheese";
//		Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));
//		TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
//		group.addTrackedPath("testing/scratch/bigTree");
//		
//		
//		File treeFile = new File("testing/scratch/tree3.xml");
//		TreeGenerator tg = new TreeGenerator(group);
//		tg.generateTree();
//		tg.save(treeFile);
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
//		EmbeddedDB db = new EmbeddedDB();
//		
//		
//		Algorithm algorithm = AlgorithmRegistry.getDefaultAlgorithm("TextBlock");
//		
//		//tracking group setup
//		String keyName = "testKeyName";
//		String groupName = "Cheese and other Cheese";
//		Key key = new FileKey(keyName, groupName, new File("testing/keys/key1.txt"));
//
//		TrackingGroup group = new TrackingGroup(groupName, true, algorithm, key);
//		
//		Metadata fileMetadata = FileSystemUtil.loadMetadataFromFile(new File("testing/keys/key1.txt"));
//		db.saveFileHash(fileMetadata, group);
//		
//		fileMetadata.setProductUUID(new byte[64]);
//		db.saveProductUUID(fileMetadata, group);
//		
//		//System.out.println();
//		
//		
//		db.display();
//		
//		
//		db.shutdown();
		
		
		
		
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//test2();
		//test3();
		//test4();
		
		//trackingGroupTest();
		//indexWorkerTest();
		
		//loadBallanceTest();

		
		
		//11 mult is minimum average percent ?!?!?!?!?
		
		
		
		
		
//		int i = 125;
//		System.out.println(i);
//		
//		int t = i / 16;
//		int u = i % 16;
//		System.out.println(t + ", " + u);
//		
//		int a = t / 4;
//		int b = t % 4;
//		int c = u / 4;
//		int d = u % 4;
//		System.out.println(a + ", " + b + ", " + c + ", " + d);
//		
//		int z = (((a * 4) + b) * 16) + ((c * 4) + d);
//		System.out.println(z);
//		BufferedImage img = new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
//		boolean yes = true;
//		for (int yy = 0; yy<512; ++yy)
//		{
//			for (int zz = 0; zz<512; ++zz)
//			{
//				if (yes)
//					img.setRGB(zz, yy, Color.BLACK.getRGB());
//				else
//					img.setRGB(zz, yy, Color.WHITE.getRGB());
//				yes = !yes;
//			}
//			yes = !yes;
//		}
//		
//		ImageIO.write(img, "png", new File("/home/tom/testimg.png"));
//		
//		for (int yy = 0; yy<256; ++yy)
//		{
//			for (int zz = 0; zz<256; ++zz)
//			{
//				int[] colors = new int[]{yy, 255-zz, yy, zz, 255-yy, zz, 255-yy, 255-zz};
//
//
//				for (int x=0; x<256; ++x)
//				{
//					
//					
//					int in = x;
//					int[] written = new int[]{-1,-1,-1,-1};
//					int read = -1;
//					
//					{
//						int val = in;
//						int div16 = val / 16;
//						int mod16 = val % 16;
//			
//						int[] fourVals = new int[]{div16 / 4, div16 % 4, mod16 / 4, mod16 % 4};
//						//System.out.println(fourVals[0] + ", " + fourVals[1] + ", " + fourVals[2] + ", " + fourVals[3]);
//						
//						for (int i=0; i<4; ++i)
//						{
//							int c1 = colors[i];
//							int c2 = colors[i+1];
//			
//							int min = Math.min(c1, c2);
//							if (min > 2)
//							{
//								written[i] = (min - 3) + fourVals[i];
//							}
//							else
//							{
//								written[i] = (min + 3) - fourVals[i];
//							}
//						}
//					
//					}
//					
//					{
//						int[] fourVals = new int[]{0,0,0,0};
//			
//						for (int i=0; i<4; ++i)
//						{
//							int c0 = written[i];
//							int c1 = colors[i];
//							int c2 = colors[i+1];
//							
//							int min = Math.min(c1, c2);
//							if (min > 2)
//							{
//								fourVals[i] = c0 - (min - 3);
//							}
//							else
//							{
//								fourVals[i] = (min + 3) - c0;
//							}
//						}
//						//System.out.println(fourVals[0] + ", " + fourVals[1] + ", " + fourVals[2] + ", " + fourVals[3]);
//						int val = (((fourVals[0] * 4) + fourVals[1]) * 16) + ((fourVals[2] * 4) + fourVals[3]);
//						read = val;
//					}
//					
//					if (in != read)
//						System.out.println(x + ", " + in + ", " + read);
//					
//					
//				}
//			}
//			
//		}
//		
//		
//		System.out.println("Done");
//		
		
		
		
//		
//		 Set<String> set = new HashSet<String>();
//		 
//		  
//		 
//		         // Get list of all informal format names understood by the current set of registered readers
//		 
//		         String[] formatNames = ImageIO.getReaderFormatNames();
//		 
//		  
//		 
//		         for (int i = 0; i < formatNames.length; i++) {
//		 
//		             set.add(formatNames[i].toLowerCase());
//		 
//		         }
//		 
//		         System.out.println("Supported read formats: " + set);
//		
//		  
//		 
//		         set.clear();
//		 
//		  
//		
//		         // Get list of all informal format names understood by the current set of registered writers
//		
//		         formatNames = ImageIO.getWriterFormatNames();
//		 
//		  
//		
//		         for (int i = 0; i < formatNames.length; i++) {
//		 
//		             set.add(formatNames[i].toLowerCase());
//		
//		         }
//		 
//		         System.out.println("Supported write formats: " + set);
//		
//		  
//		
//		         set.clear();
//		
//		  
//
//		         // Get list of all MIME types understood by the current set of registered readers
//		 
//		         formatNames = ImageIO.getReaderMIMETypes();
//		 
//		  
//		 
//		         for (int i = 0; i < formatNames.length; i++) {
//		
//		             set.add(formatNames[i].toLowerCase());
//		 
//		         }
//		 
//		         System.out.println("Supported read MIME types: " + set);
//		
//		  
//		
//		         set.clear();
//		 
//		  
//		 
//		         // Get list of all MIME types understood by the current set of registered writers
//		
//		         formatNames = ImageIO.getWriterMIMETypes();
//		
//		  
//		 
//		         for (int i = 0; i < formatNames.length; i++) {
//		
//		             set.add(formatNames[i].toLowerCase());
//		 
//		         }
//		 
//		         System.out.println("Supported write MIME types: " + set);
//		 
//		  
//		 
//		     }
		
		
		
//		
//		
//		
//		HashRandom random = new HashRandom(1234l);
//		UniqueRandomRange urr = new UniqueRandomRange(random, 10);
//		int[] array = new int[]{1,2,3,4,5,6,7,8,9,10,11};
//		int[] array2 = new int[]{1,2,3,4,5,6,7,8,9,10,11};
//		int index = 10;
//		int temp = -1;
//		int[] store = new int[1];
//		
//		for (int i = 0; i < 13; ++i)
//		{
//			
//			try
//			{
//			store[0] = urr.next();
//			System.out.println(store[0]);
////				int swapIndex = random.nextInt(index);
////				temp = array[swapIndex];
////				array[swapIndex] = array[index - 1];
////				array[index - 1] = temp;
////				
////				--index;
////				
////				store[0] = temp;
////				System.out.println(temp);
////				
//			}
//			catch (ProductIOException | ArrayIndexOutOfBoundsException e)
//			{
//				e.printStackTrace();
//			}
//		
//		}
//		System.out.println("store: " + store[0]);
//		System.out.println("array: ");
//		for (int a = 0; a<array.length; ++a)
//		{
//			System.out.println(array[a]);
//		}
//		
//	}
		
		
		
		
		
	
	
//	{
//		
//		for (int i = 1; i <= 256; ++i)
//		{
//			int pow = (int) Math.floor(Math.log10(i) / Math.log10(2));
//			int base = (int) Math.pow(2, pow);
//			int add = i - base;
//			System.out.println(i + " --> " + pow + " (" + base + ") + " + add + " = " + (base+add)
//					+ " *" + (((float)(pow+add))/i) + "*");
//		}
//		
//		
//		for (int mult = 1; mult < 256; ++mult)
//		{
//			float average = 0;
//			for (int i = 1; i <= 256; ++i)
//			{
//				int base = i / mult;
//				int add = i % mult;
//				float percent = (((float)(base+add))/i);
//				//System.out.println(i + " --> " + base + "*16 + " + add + " = " + ((base*16)+add)
//				//		+ " *" + percent + "*");
//				
//				average += percent;
//				assert ((base*mult)+add == i);
//			}
//			average = average / 256;
//			
//			
//			System.out.println(mult + " --> " + average);
//		}
//		
//		for (int mult = 1; mult < 16; ++mult)
//		{
//			float average = 0;
//			for (int i = 1; i <= 16; ++i)
//			{
//				int base = i / mult;
//				int add = i % mult;
//				float percent = (((float)(base+add))/i);
//				//System.out.println(i + " --> " + base + "*16 + " + add + " = " + ((base*16)+add)
//				//		+ " *" + percent + "*");
//				
//				average += percent;
//				assert ((base*mult)+add == i);
//			}
//			average = average / 26;
//			
//			
//			System.out.println(mult + " --> " + average);
//	}
	
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
