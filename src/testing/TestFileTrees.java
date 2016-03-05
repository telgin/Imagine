package testing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Defines/creates the file trees to be used during testing
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
	 * Clears root of the file tree in the parent folder
	 * @param p_parent The parent folder
	 * @param p_name The name of the tree
	 */
	public static void clear(File p_parent, String p_name)
	{
		clearFolder(s_trees.get(p_name.toLowerCase()).getRoot(p_parent));
	}

	/**
	 * Creates the empty folder tree
	 * @return The empty folder tree
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
	 * Creates the small file tree
	 * @return The small file tree
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
	 * Creates the small tree tree
	 * @return The small tree tree
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
	 * Creates the big file tree
	 * @return The big file tree
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
	 * Creates the big tree tree
	 * @return The big tree tree
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
				addFile(new File(s_bank, "testGroupInput/"), getRoot(parent));
			}
		};

		return tree;
	}
	
	/**
	 * Creates the input images tree
	 * @return The input images tree
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
	 * Resets the tree in the given parent folder
	 * @param p_parent The parent folder
	 * @param p_name The tree name
	 */
	public static void reset(File p_parent, String p_name)
	{
		clear(p_parent, p_name.toLowerCase());
		create(p_parent, p_name.toLowerCase());
	}

	/**
	 * Creates a file tree in the specified parent folder
	 * @param p_parent The folder to copy to
	 * @param p_name The name of the file tree
	 */
	public static void create(File p_parent, String p_name)
	{
		s_trees.get(p_name.toLowerCase()).create(p_parent);
	}

	/**
	 * Clears the existing folder of all files
	 * @param p_folder The folder to clear
	 */
	private static void clearFolder(File p_folder)
	{
		FileSystemUtil.deleteDir(p_folder);
		p_folder.mkdir();
	}

	/**
	 * Gets the root of a file tree in a specified location
	 * @param p_parent The name of the folder to have the file copied to
	 * @param p_name The file tree name
	 * @return The new file tree root
	 */
	public static File getRoot(File p_parent, String p_name)
	{
		return s_trees.get(p_name.toLowerCase()).getRoot(p_parent);
	}

	/**
	 * Copies a file or folder to a new parent directory
	 * @param p_target The file/folder to be copied
	 * @param p_newParent The new folder to copy the file/folder to
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
