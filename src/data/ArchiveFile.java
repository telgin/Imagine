package data;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ArchiveFile extends File
{
	private static final long serialVersionUID = 982302354356341952L;
	
	private File relativeFile;

	/**
	 * @update_comment
	 * @param pathname
	 */
	public ArchiveFile(String pathname)
	{
		super(pathname);
	}
	
	public ArchiveFile(ArchiveFile parent, File child)
	{
		super(child.getPath());
		
		if (parent.getRelativeFile() != null)
			setRelativeFile(new File(parent.getRelativeFile(), getName()));
	}

	/**
	 * @return the relativePath
	 */
	public String getArchivePath()
	{
		return relativeFile == null ? getPath() : relativeFile.getPath();
	}

	/**
	 * @param relativePath the relativePath to set
	 */
	public void setRelativePath(String relativePath)
	{
		relativeFile = new File(relativePath);
	}
	
	/**
	 * @return the relativeFile
	 */
	public File getRelativeFile()
	{
		return relativeFile;
	}

	/**
	 * @param relativeFile the relativeFile to set
	 */
	public void setRelativeFile(File relativeFile)
	{
		this.relativeFile = relativeFile;
	}
	
	/**
	 * @update_comment
	 * @param parent
	 */
	public void appendRelativeParent(File parent)
	{
		relativeFile = new File(parent, relativeFile.getPath());
	}

}
