package testing;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface FileTree
{
	/**
	 * @update_comment
	 * @param parent
	 * @return
	 */
	public File getRoot(File parent);

	/**
	 * @update_comment
	 * @param parent
	 */
	public void create(File parent);
}
