package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archive.CreationJobFileState;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A tree item class for an input item. These are checkbox tree items to
 * allow for dynamic selection of inputs with minimal clicks.
 */
public class InputFileTreeItem extends CheckBoxTreeItem<String>
{
	private double f_progress;
	private File f_file;
	private CreationJobFileState f_status;
	private boolean f_focused;
	
	private static final InputFileTreeItem s_tempExpandableChildItem =
		new InputFileTreeItem("Loading...");
	private static final Map<CreationJobFileState, String> s_fxStatusColors = 
		new HashMap<CreationJobFileState, String>();
	private static final String s_textFillColor = "-fx-text-fill: black; ";
	
	static
	{
		s_fxStatusColors.put(CreationJobFileState.NOT_STARTED,
			"-fx-accent: rgba(200, 200, 200, 1); ");
		s_fxStatusColors.put(CreationJobFileState.WRITING,
			"-fx-background-color: rgba(11, 156, 0, .5); ");
		s_fxStatusColors.put(CreationJobFileState.PAUSED,
			"-fx-background-color: rgba(156, 146, 0, .5); ");
		s_fxStatusColors.put(CreationJobFileState.FINISHED,
			"-fx-background-color: rgba(11, 156, 0, .7); ");
		s_fxStatusColors.put(CreationJobFileState.ERRORED,
			"-fx-background-color: rgba(244, 20, 0, .75); ");
	}
	
	/**
	 * Constructs an input file tree item for a given input file
	 * @param p_file The input file
	 */
	public InputFileTreeItem(File p_file)
	{
		super(p_file.getName());
		this.f_file = p_file;
		f_status = CreationJobFileState.NOT_STARTED;
		
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
	 * Creates an input file tree item with the given display text
	 * @param p_display The display text
	 */
	public InputFileTreeItem(String p_display)
	{
		super(p_display);
	}

	/**
	 * @return the progress
	 */
	public double getProgress()
	{
		return f_progress;
	}

	/**
	 * @param p_progress the progress to set
	 */
	public void setProgress(double p_progress)
	{
		this.f_progress = p_progress;
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
	public CreationJobFileState getStatus()
	{
		return f_status;
	}

	/**
	 * @param p_status the status to set
	 */
	public void setStatus(CreationJobFileState p_status)
	{
		this.f_status = p_status;
	}

	/**
	 * Adds children items to the tree when an item is expanded. This is done
	 * so all the items don't need to be loaded at the beginning.
	 * @param p_expanded The expanded state of the item
	 */
	private void folderEntryExpanded(boolean p_expanded)
	{
		//only load children if they haven't been loaded yet
		if (p_expanded && getChildren().get(0).equals(s_tempExpandableChildItem))
		{
			List<InputFileTreeItem> loadedItems = new ArrayList<InputFileTreeItem>();
			
			//load entries
			for (File child : getFile().listFiles())
			{
				InputFileTreeItem item = new InputFileTreeItem(child);
				item.setSelected(isSelected());
				loadedItems.add(item);
			}
			
			//clear now that all are loaded
			getChildren().clear();
			
			//add loaded items
			getChildren().addAll(loadedItems);
		}
	}
	
	/**
	 * Sets the style of a cell according to the state/progress of this item
	 * @param p_cell The cell to set the style of
	 */
	public void setCellStyle(TreeCell<?> p_cell)
	{
		int width = (int) p_cell.getWidth();
		int rightInset = width - (int) (width * this.f_progress);

		//create a progress bar out of the background color by setting the insets
		//according to the progress of the item
		String bar = "";
		if ((f_status.equals(CreationJobFileState.WRITING) || 
						f_status.equals(CreationJobFileState.PAUSED)))
		{
			bar = " -fx-background-insets: 0 " + rightInset + " 0 0";
		}
		
		String focusedOpacity = f_focused ? "-fx-opacity: 1; " : "-fx-opacity: .9; ";
		
		p_cell.setStyle(focusedOpacity + s_textFillColor + s_fxStatusColors.get(f_status) + bar);
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
