package data;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This class allows the absolute path of a file to be kept track of
 * while still using the path the user specified when writing an archive.
 */
public class ArchiveFile extends File
{
	private static final long serialVersionUID = 982302354356341952L;
	private File f_relativeFile;

	/**
	 * Constructs an archive file from a path string. This is equivalent to
	 * the same constructor in File
	 * @param p_pathString The path
	 */
	public ArchiveFile(String p_pathString)
	{
		super(p_pathString);
	}
	
	/**
	 * Constructs an archive file from a parent archive file
	 * and a child file.
	 * @param p_parent The parent archive file
	 * @param p_child The child file
	 */
	public ArchiveFile(ArchiveFile p_parent, File p_child)
	{
		super(p_child.getPath());
		
		if (p_parent.getRelativeFile() != null)
			setRelativeFile(new File(p_parent.getRelativeFile(), getName()));
	}

	/**
	 * Gets the path to use in an archive file
	 * @return the path
	 */
	public String getArchivePath()
	{
		return f_relativeFile == null ? getPath() : f_relativeFile.getPath();
	}

	/**
	 * @param p_relativePath the relativePath to set
	 */
	public void setRelativePath(String p_relativePath)
	{
		f_relativeFile = new File(p_relativePath);
	}
	
	/**
	 * @return the relativeFile
	 */
	public File getRelativeFile()
	{
		return f_relativeFile;
	}

	/**
	 * @param p_relativeFile the relativeFile to set
	 */
	public void setRelativeFile(File p_relativeFile)
	{
		this.f_relativeFile = p_relativeFile;
	}
	
	/**
	 * Appends a parent file to this file's relative path.
	 * For instance, if the relative path was "file.txt" and the new parent file
	 * path was "folder", the relative path would be changed to "folder/file.txt".
	 * @param p_parent The parent file to append
	 */
	public void appendRelativeParent(File p_parent)
	{
		f_relativeFile = new File(p_parent, f_relativeFile.getPath());
	}

}
