package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import product.ConversionJobFileState;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TargetFileTreeItem extends TreeItem<String>
{
	private File f_file;
	private ConversionJobFileState f_status;
	private boolean f_focused;
	
	private static final TargetFileTreeItem s_tempExpandableChildItem = 
		new TargetFileTreeItem("Loading...");
	private static final Map<ConversionJobFileState, String> s_fxStatusColors = 
		new HashMap<ConversionJobFileState, String>();
	private static final String s_textFillColor = "-fx-text-fill: black; ";
	
	static
	{
		s_fxStatusColors.put(ConversionJobFileState.NOT_STARTED,
			"-fx-accent: rgba(200, 200, 200, 1); ");
		s_fxStatusColors.put(ConversionJobFileState.WRITING,
			"-fx-background-color: rgba(11, 156, 0, .5); ");
		s_fxStatusColors.put(ConversionJobFileState.PAUSED,
			"-fx-background-color: rgba(156, 146, 0, .5); ");
		s_fxStatusColors.put(ConversionJobFileState.FINISHED,
			"-fx-background-color: rgba(11, 156, 0, .7); ");
		s_fxStatusColors.put(ConversionJobFileState.ERRORED,
			"-fx-background-color: rgba(244, 20, 0, .75); ");
	}

	/**
	 * @update_comment
	 * @param string
	 */
	public TargetFileTreeItem(File p_file)
	{
		super(p_file.getName());
		this.f_file = p_file;
		f_status = ConversionJobFileState.NOT_STARTED;
		
		if (p_file.isDirectory() && p_file.listFiles().length > 0)
		{
			//load actual children once expanded
			expandedProperty().addListener(
				(ObservableValue<? extends Boolean> value,
					Boolean oldValue, Boolean newValue) ->
						folderEntryExpanded(newValue.booleanValue()));
			
			//add a temporary entry so the input file entry will show up as expandable
			//this will be replaced when it is expanded
			getChildren().add(s_tempExpandableChildItem);
		}
	}
	
	/**
	 * @update_comment
	 * @param p_display
	 */
	public TargetFileTreeItem(String p_display)
	{
		super(p_display);
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return f_file;
	}

	/**
	 * @param p_file the file to set
	 */
	public void setFile(File p_file)
	{
		this.f_file = p_file;
		setValue(p_file.getName());
	}

	/**
	 * @return the status
	 */
	public ConversionJobFileState getStatus()
	{
		return f_status;
	}

	/**
	 * @param p_status the status to set
	 */
	public void setStatus(ConversionJobFileState p_status)
	{
		this.f_status = p_status;
	}
	
	/**
	 * @param item 
	 * @update_comment
	 * @param booleanValue
	 * @param inputFile
	 * @return
	 */
	private void folderEntryExpanded(boolean p_expanded)
	{
		//only load children if they haven't been loaded yet
		if (p_expanded && getChildren().get(0).equals(s_tempExpandableChildItem))
		{
			List<TargetFileTreeItem> loadedItems = new ArrayList<TargetFileTreeItem>();
			
			//load entries
			for (File child : getFile().listFiles())
			{
				TargetFileTreeItem item = new TargetFileTreeItem(child);
				loadedItems.add(item);
			}
			
			//clear now that all are loaded
			getChildren().clear();
			
			//add loaded items
			getChildren().addAll(loadedItems);
		}
	}
	
	/**
	 * @update_comment
	 * @param p_cell
	 */
	public void setCellStyle(TreeCell<?> p_cell)
	{
		String focusedOpacity = f_focused ? "-fx-opacity: 1; " : "-fx-opacity: .9; ";
		
		p_cell.setStyle(focusedOpacity + s_textFillColor + s_fxStatusColors.get(f_status));
	}

	/**
	 * @return the focused
	 */
	public boolean isFocused()
	{
		return f_focused;
	}

	/**
	 * @param p_focused the focused to set
	 */
	public void setFocused(boolean p_focused)
	{
		this.f_focused = p_focused;
	}
}
