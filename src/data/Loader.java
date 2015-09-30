package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loader extends Thread{
	private List<File> toProcess;
	private File outputFolder;
	private static final int DATA_MAX = (1920*1080*4)-4;
	private byte[] data;
	private int index = 0;
	
	public Loader(File outputFolder){
		toProcess = new ArrayList<File>();
		this.outputFolder = outputFolder;
		data = new byte[DATA_MAX];
	}
	
	public void addFile(File f){
		
	}
	
	public void addFiles(List<File> files){
		
	}
	
	public void start(){
		
	}
	
	@Override
	public void run(){
		while(toProcess.size() > 0){
			File f = toProcess.remove(0);
			if(f.canRead()){
				File[] children = f.listFiles();
				if(children != null){
					toProcess.addAll(Arrays.asList(children));
				}else{
					try {
						concatFile(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	private void concatFile(File f) throws IOException {
		long length = Files.size(f.toPath());
		FileInputStream fis = new FileInputStream(f);
		
		//put the whole file in, take as many images as needed
		while(DATA_MAX-index > length){
			
			int partLength = DATA_MAX-index;
			//add the part length as data
			index += 4;
			fis.read(data, index, partLength-4);
			//log the thing
			//send the file to the place
			length -= DATA_MAX-index;
		}
		
		//put part of a file in
		if(length > 0){
			
		}
	}

}
