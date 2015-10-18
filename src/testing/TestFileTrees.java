package testing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.google.common.io.Files;

import util.FileSystemUtil;

public class TestFileTrees {
	private static HashMap<String, FileTree> trees;
	private static File bank = new File("testing/bank/");
	
	static
	{
		//trees:
		trees = new HashMap<String, FileTree>();
		trees.put("nofiles", getNoFilesTree());
		trees.put("smallfile", getSmallFileTree());
		trees.put("smallfiles", getSmallFilesTree());
		trees.put("bigfile", getBigFileTree());
		trees.put("dynamictree", getDynamicTree());
	}
	
	public static void clear(File parent, String name)
	{
		clearFolder(trees.get(name.toLowerCase()).getRoot(parent));
	}



	private static FileTree getNoFilesTree() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/noFiles/");
			}

			@Override
			public void create(File parent) {}
			
		};
		
		return tree;
	}

	private static FileTree getSmallFileTree() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/smallFile/");
			}

			@Override
			public void create(File parent) {
				addFile(new File(bank.getPath() + "/message.txt"), getRoot(parent));				
			}
			
		};
		
		return tree;
	}
	
	private static FileTree getSmallFilesTree() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/smallFiles/");
			}

			@Override
			public void create(File parent) {
				addFile(new File(bank.getPath() + "/message.txt"), getRoot(parent));				
			}
			
		};
		
		return tree;
	}

	private static FileTree getBigFileTree() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/bigFile/");
			}

			@Override
			public void create(File parent) {
				addFile(new File(bank.getPath() + "/Computer Art.zip"), getRoot(parent));				
			}
			
		};
		
		return tree;
	}

	private static FileTree getDynamicTree() {
		FileTree tree = new FileTree(){

			@Override
			public File getRoot(File parent) {
				return new File(parent.getPath() + "/dynamicTree/");
			}

			@Override
			public void create(File parent) {
				addFile(new File(bank.getPath() + "/eclipse-installer"), getRoot(parent));				
			}
			
		};
		
		return tree;
	}

	public static void reset(File parent, String name)
	{
		clear(parent, name.toLowerCase());
		create(parent, name.toLowerCase());
	}
	
	public static void create(File parent, String name)
	{
		trees.get(name.toLowerCase()).create(parent);
	}
	
	private static void clearFolder(File folder)
	{
		//folder.delete();
		FileSystemUtil.deleteDir(folder);
		folder.mkdir();
	}

	public static File getRoot(File parent, String name) {
		return trees.get(name.toLowerCase()).getRoot(parent);
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
