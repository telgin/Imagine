package testing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.google.common.io.Files;

import util.FileSystemUtil;

public class TestFileTrees {
	private static HashMap<Integer, FileTree> trees;
	private static File bank = new File("testing/bank/");
	
	static
	{
		//trees:
		trees = new HashMap<Integer, FileTree>();
		trees.put(1, getFileTree1());
	}
	
	public static void clear(File parent, int i)
	{
		clearFolder(trees.get(i).getRoot(parent));
	}

	private static FileTree getFileTree1() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/testFiles1/");
			}

			@Override
			public void create(File parent) {
				addFile(new File(bank.getPath() + "/message.txt"), getRoot(parent));				
			}
			
		};
		
		return tree;
	}

	public static void reset(File parent, int i)
	{
		clear(parent, i);
		create(parent, i);
	}
	
	public static void create(File parent, int i)
	{
		trees.get(i).create(parent);
	}
	
	private static void clearFolder(File folder)
	{
		//folder.delete();
		FileSystemUtil.deleteDir(folder);
		folder.mkdir();
	}

	public static File getRoot(File parent, int i) {
		return trees.get(i).getRoot(parent);
	}
	
	private static void addFile(File target, File newParent)
	{
		File copyTo = new File(newParent.getPath() + "/" + target.getName());
		try {
			Files.copy(target, copyTo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
