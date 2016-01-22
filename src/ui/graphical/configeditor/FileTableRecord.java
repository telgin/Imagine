package ui.graphical.configeditor;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileTableRecord
{
	private final SimpleStringProperty path;
	private File file;
	
	public FileTableRecord(File file)
	{
		this.file = file;
		this.path = new SimpleStringProperty(file.getAbsolutePath());
	}

	/**
	 * @return the path
	 */
	public SimpleStringProperty getPath()
	{
		return path;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}
}
