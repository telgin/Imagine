package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import util.myUtilities;

public class HashCat {
	static HashMap<String,HashSet<String>> hashes = new HashMap<String,HashSet<String>>();
	static int totalDuplicates = 0;
	static int totalFiles = 0;
	static long totalFileBytes = 0;
	static long totalDuplicateBytes = 0;
	public static void main(String[] args) {
		File hashFolder = new File("G:\\Buffer\\Hashes\\Everything\\hashes\\");//specify
		
		//parse all files in folder
		for(File hashFile:hashFolder.listFiles()){
			ArrayList<String> lines = myUtilities.readListFromFile(hashFile);
			for(String line:lines){
				String[] parts = line.split("\t");
				String hash = parts[0];
				HashSet<String> matches = new HashSet<String>(Arrays.asList(parts[1].split(";")));
				
				addHashes(hash, matches);
			}
		}
		
		//map --> list
		ArrayList<String> outLines = new ArrayList<String>();
		File out = new File("G:\\Buffer\\Hashes\\Everything\\hash_everything.txt");//specify
		for(String hash:hashes.keySet()){
			ArrayList<String> matches = new ArrayList<String>(hashes.get(hash));
			String line = hash + "\t";
			line += matches.get(0);
			
			for(int x=1; x<matches.size(); x++)
				line += ";" + matches.get(x);
			outLines.add(line);
			
			//get stats
			//try {
				totalFiles += matches.size();
				totalDuplicates += (matches.size() - 1);
				//totalFileBytes += Files.size(Paths.get(matches.get(0)));
				//for(int x=1; x<matches.size(); x++){
				//	long size = Files.size(Paths.get(matches.get(x)));
					//totalFileBytes += size;
				//	totalDuplicateBytes += size;
				//	System.out.println(totalDuplicateBytes);
				//}	
			//} catch (Exception e) {
			//	//e.printStackTrace();
			//}
			//
				
		}
		
		//write list to file
		myUtilities.writeListToFile(out, outLines);
		
		//print stats
		System.out.println("Total Files: " + totalFiles);
		System.out.println("Total File Bytes: " + totalFileBytes);
		System.out.println("Total Duplicate Files: " + totalDuplicates);
		System.out.println("Total Duplicate File Bytes: " + totalDuplicateBytes);
	}

	private static void addHashes(String hash, HashSet<String> matches) {
		if(hashes.containsKey(hash))
			hashes.get(hash).addAll(matches);
		else
			hashes.put(hash, matches);
	}

}
