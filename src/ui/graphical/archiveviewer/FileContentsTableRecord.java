package ui.graphical.archiveviewer;

import java.io.File;

import data.FileType;
import javafx.beans.property.SimpleStringProperty;
import util.myUtilities;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileContentsTableRecord
{

	private final SimpleStringProperty index;
	private final SimpleStringProperty type;
	private final SimpleStringProperty name;
	private final SimpleStringProperty dateCreated;
	private final SimpleStringProperty dateModified;
	
	/**
	 * @update_comment
	 * @param i
	 * @param type
	 * @param name
	 * @param dateCreated
	 * @param dateModified
	 * @param fragmentNumber
	 */
	public FileContentsTableRecord(int index, FileType type, long fragmentNumber,
					boolean isFragment, File file, long dateCreated, long dateModified)
	{
		this.index = new SimpleStringProperty(Integer.toString(index));
		
		String displayType = "";
		if (type.equals(FileType.k_file))
		{
			displayType = "File";
		}
		else //k_folder
		{
			displayType = "Folder";
		}
		
		if (fragmentNumber > 1 || isFragment)
			displayType += " (Fragment #" + Long.toString(fragmentNumber) + ")";
		
		this.type = new SimpleStringProperty(displayType);
		this.name = new SimpleStringProperty(file.getName());
		this.dateCreated = new SimpleStringProperty(myUtilities.formatDate(dateCreated));
		this.dateModified = new SimpleStringProperty(myUtilities.formatDate(dateModified));
	}

	/**
	 * @return the index
	 */
	public String getIndex()
	{
		return index.get();
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type.get();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name.get();
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated()
	{
		return dateCreated.get();
	}

	/**
	 * @return the dateModified
	 */
	public String getDateModified()
	{
		return dateModified.get();
	}

}
