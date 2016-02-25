package testing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TestFileTrees
{
	private static Map<String, FileTree> s_trees;
	private static File s_bank = new File(new File("testing"), "bank");

	static
	{
		// trees:
		s_trees = new HashMap<String, FileTree>();
		s_trees.put("emptyfolder", getEmptyFolderTree());
		s_trees.put("smallfile", getSmallFileTree());
		s_trees.put("smalltree", getSmallTree());
		s_trees.put("bigfile", getBigFileTree());
		s_trees.put("bigtree", getBigTree());
		s_trees.put("inputimages", getInputImages());
	}

	/**
	 * @update_comment
	 * @param p_parent
	 * @param p_name
	 */
	public static void clear(File p_parent, String p_name)
	{
		clearFolder(s_trees.get(p_name.toLowerCase()).getRoot(p_parent));
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
				addFile(new File(s_bank, "message.txt"), getRoot(parent));
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
				addFile(new File(s_bank, "tracked_topfolder_r/"),
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
				addFile(new File(s_bank, "Computer Art.zip"), getRoot(parent));
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
				addFile(new File(s_bank, "eclipse-installer/"), getRoot(parent));
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
				addFile(new File(s_bank, "input_images/"), getRoot(parent));
			}

		};

		return tree;
	}

	/**
	 * @update_comment
	 * @param p_parent
	 * @param p_name
	 */
	public static void reset(File p_parent, String p_name)
	{
		clear(p_parent, p_name.toLowerCase());
		create(p_parent, p_name.toLowerCase());
	}

	/**
	 * @update_comment
	 * @param p_parent
	 * @param p_name
	 */
	public static void create(File p_parent, String p_name)
	{
		s_trees.get(p_name.toLowerCase()).create(p_parent);
	}

	/**
	 * @update_comment
	 * @param p_folder
	 */
	private static void clearFolder(File p_folder)
	{
		FileSystemUtil.deleteDir(p_folder);
		p_folder.mkdir();
	}

	/**
	 * @update_comment
	 * @param p_parent
	 * @param p_name
	 * @return
	 */
	public static File getRoot(File p_parent, String p_name)
	{
		return s_trees.get(p_name.toLowerCase()).getRoot(p_parent);
	}

	/**
	 * @update_comment
	 * @param p_target
	 * @param p_newParent
	 */
	private static void addFile(File p_target, File p_newParent)
	{
		File copyTo = new File(p_newParent, p_target.getName());
		try
		{
			if (p_target.isDirectory())
				FileSystemUtil.copyDir2(p_target, copyTo);
			else
				Files.copy(p_target.toPath(), copyTo.toPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
