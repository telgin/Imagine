package testing;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This describes a grouping of files which are used as a standard
 * set of input data for archive creation.
 */
public interface FileTree
{
	/**
	 * Gets the root folder for this tree once it is copied
	 * @param parent The parent folder to add the files to
	 * @return The root folder of this file group
	 */
	public File getRoot(File parent);

	/**
	 * Copies the files into the given parent folder
	 * @param parent The folder to copy the files into
	 */
	public void create(File parent);
}
