package testing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.google.common.io.Files;

import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TestFileTrees
{
	private static HashMap<String, FileTree> trees;
	private static File bank = new File("testing/bank/");

	static
	{
		// trees:
		trees = new HashMap<String, FileTree>();
		trees.put("emptyfolder", getEmptyFolderTree());
		trees.put("smallfile", getSmallFileTree());
		trees.put("smalltree", getSmallTree());
		trees.put("bigfile", getBigFileTree());
		trees.put("bigtree", getBigTree());
		trees.put("inputimages", getInputImages());
	}

	/**
	 * @update_comment
	 * @param parent
	 * @param name
	 */
	public static void clear(File parent, String name)
	{
		clearFolder(trees.get(name.toLowerCase()).getRoot(parent));
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getEmptyFolderTree()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "emptyFolder/");
			}

			@Override
			public void create(File parent)
			{
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getSmallFileTree()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "smallFile/");
			}

			@Override
			public void create(File parent)
			{
				addFile(new File(bank, "message.txt"), getRoot(parent));
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getSmallTree()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "smallTree/");
			}

			@Override
			public void create(File parent)
			{
				addFile(new File(bank, "tracked_topfolder_r/"),
								getRoot(parent));
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getBigFileTree()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "bigFile/");
			}

			@Override
			public void create(File parent)
			{
				addFile(new File(bank, "Computer Art.zip"), getRoot(parent));
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getBigTree()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "bigTree/");
			}

			@Override
			public void create(File parent)
			{
				addFile(new File(bank, "eclipse-installer/"), getRoot(parent));
			}

		};

		return tree;
	}
	
	
	/**
	 * @update_comment
	 * @return
	 */
	private static FileTree getInputImages()
	{
		FileTree tree = new FileTree()
		{

			@Override
			public File getRoot(File parent)
			{
				return new File(parent, "inputImages/");
			}

			@Override
			public void create(File parent)
			{
				addFile(new File(bank, "input_images/"), getRoot(parent));
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @param parent
	 * @param name
	 */
	public static void reset(File parent, String name)
	{
		clear(parent, name.toLowerCase());
		create(parent, name.toLowerCase());
	}

	/**
	 * @update_comment
	 * @param parent
	 * @param name
	 */
	public static void create(File parent, String name)
	{
		trees.get(name.toLowerCase()).create(parent);
	}

	/**
	 * @update_comment
	 * @param folder
	 */
	private static void clearFolder(File folder)
	{
		FileSystemUtil.deleteDir(folder);
		folder.mkdir();
	}

	/**
	 * @update_comment
	 * @param parent
	 * @param name
	 * @return
	 */
	public static File getRoot(File parent, String name)
	{
		return trees.get(name.toLowerCase()).getRoot(parent);
	}

	/**
	 * @update_comment
	 * @param target
	 * @param newParent
	 */
	private static void addFile(File target, File newParent)
	{
		File copyTo = new File(newParent, target.getName());
		try
		{
			if (target.isDirectory())
				FileSystemUtil.copyDir2(target, copyTo);
			else
				Files.copy(target, copyTo);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
