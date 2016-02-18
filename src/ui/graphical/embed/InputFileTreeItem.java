package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.beans.value.ObservableValue;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class InputFileTreeItem extends CheckBoxTreeItem<String>
{
	private double progress;
	private File file;
	private int status;
	
	private static final InputFileTreeItem tempExpandableChildItem = new InputFileTreeItem("Loading...");
	
	public static final int NOT_STARTED = 0;
	public static final int OK = 1;
	public static final int PAUSED = 2;
	public static final int FINISHED = 3;
	public static final int ERRORED = 4;
	
	private static final Map<Integer, String> fxStatusColors = 
					new HashMap<Integer, String>();
	
	static
	{
		fxStatusColors.put(NOT_STARTED, "");
		fxStatusColors.put(OK, "-fx-background-color: rgba(11, 156, 0, .7); ");
		fxStatusColors.put(PAUSED, "-fx-background-color: rgba(156, 146, 0, .7); ");
		fxStatusColors.put(FINISHED, "-fx-background-color: rgba(11, 156, 0, .9); ");
		fxStatusColors.put(ERRORED, "-fx-background-color: rgba(244, 20, 0, .75); ");
	}
	

	/**
	 * @update_comment
	 * @param string
	 */
	public InputFileTreeItem(File file)
	{
		super(file.getName());
		this.file = file;
		status = NOT_STARTED;
		
		if (file.isDirectory() && file.listFiles().length > 0)
		{
			//load actual children once expanded
			expandedProperty().addListener(
				(ObservableValue<? extends Boolean> value,
					Boolean oldValue, Boolean newValue) ->
						folderEntryExpanded(newValue.booleanValue()));
			
			//add a temporary entry so the input file entry will show up as expandable
			//this will be replaced when it is expanded
			getChildren().add(tempExpandableChildItem);
		}
	}
	
	public InputFileTreeItem(String display)
	{
		super(display);
	}

	/**
	 * @return the progress
	 */
	public double getProgress()
	{
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(double progress)
	{
		this.progress = progress;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file)
	{
		this.file = file;
		setValue(file.getName());
	}

	/**
	 * @return the status
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	/**
	 * @param item 
	 * @update_comment
	 * @param booleanValue
	 * @param inputFile
	 * @return
	 */
	private void folderEntryExpanded(boolean expanded)
	{
		//only load children if they haven't been loaded yet
		if (expanded && getChildren().get(0).equals(tempExpandableChildItem))
		{
			List<InputFileTreeItem> loadedItems = new ArrayList<InputFileTreeItem>();
			
			//load entries
			for (File child : getFile().listFiles())
			{
				InputFileTreeItem item = new InputFileTreeItem(child);
				item.setStatus(new Random().nextInt(5));//TODO remove
				item.setSelected(isSelected());
				loadedItems.add(item);
			}
			
			//clear now that all are loaded
			getChildren().clear();
			
			//add loaded items
			getChildren().addAll(loadedItems);
		}
	}
	
	public void setCellStyle(TreeCell<?> cell)
	{
		int width = (int) cell.getWidth();
		int rightInset = width - (int) (width * this.progress);
		System.out.println("Progress: " + getProgress() + ", Right inset: " + rightInset + ", Status: " + status);
		String bar = status == ERRORED ? "" : " -fx-background-insets: 0 " + rightInset + " 0 0";
		cell.setStyle(fxStatusColors.get(status) + bar);
	}
}
