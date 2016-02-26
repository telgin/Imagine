package ui.graphical.archiveviewer;

import java.io.File;

import data.FileType;
import javafx.beans.property.SimpleStringProperty;
import util.StandardUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileContentsTableRecord
{
	private final SimpleStringProperty f_index;
	private final SimpleStringProperty f_type;
	private final SimpleStringProperty f_name;
	private final SimpleStringProperty f_dateCreated;
	private final SimpleStringProperty f_dateModified;
	private final long f_fragmentNumber;
	
	/**
	 * @update_comment
	 * @param p_index
	 * @param p_type
	 * @param f_name
	 * @param p_dateCreated
	 * @param p_dateModified
	 * @param p_fragmentNumber
	 */
	public FileContentsTableRecord(int p_index, FileType p_type, long p_fragmentNumber,
					boolean p_isFragment, File p_file, long p_dateCreated, long p_dateModified)
	{
		f_index = new SimpleStringProperty(Integer.toString(p_index));
		
		String displayType = "";
		if (p_type.equals(FileType.k_file))
		{
			displayType = "File";
		}
		else //k_folder
		{
			displayType = "Folder";
		}
		
		if (p_fragmentNumber > 1 || p_isFragment)
			displayType += " (Fragment #" + Long.toString(p_fragmentNumber) + ")";
		
		f_fragmentNumber = p_fragmentNumber;
		f_type = new SimpleStringProperty(displayType);
		f_name = new SimpleStringProperty(p_file.getName());
		f_dateCreated = new SimpleStringProperty(StandardUtil.formatDate(p_dateCreated));
		f_dateModified = new SimpleStringProperty(StandardUtil.formatDate(p_dateModified));
	}

	/**
	 * @return the index
	 */
	public String getIndex()
	{
		return f_index.get();
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return f_type.get();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return f_name.get();
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated()
	{
		return f_dateCreated.get();
	}

	/**
	 * @return the dateModified
	 */
	public String getDateModified()
	{
		return f_dateModified.get();
	}

	/**
	 * @return the fragmentNumber
	 */
	public long getFragmentNumber()
	{
		return f_fragmentNumber;
	}

}
