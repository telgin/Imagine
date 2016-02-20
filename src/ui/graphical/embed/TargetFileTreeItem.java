package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import product.ConversionJobFileStatus;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TargetFileTreeItem extends TreeItem<String>
{
	private File file;
	private ConversionJobFileStatus status;
	
	private static final TargetFileTreeItem tempExpandableChildItem = new TargetFileTreeItem("Loading...");
	
	private static final Map<ConversionJobFileStatus, String> fxStatusColors = 
					new HashMap<ConversionJobFileStatus, String>();
	
	private static final String textFillColor = "-fx-text-fill: black; ";
	
	static
	{
		fxStatusColors.put(ConversionJobFileStatus.NOT_STARTED, "-fx-accent: #0093ff; ");
		fxStatusColors.put(ConversionJobFileStatus.WRITING, "-fx-background-color: rgba(11, 156, 0, .7); ");
		fxStatusColors.put(ConversionJobFileStatus.PAUSED, "-fx-background-color: rgba(156, 146, 0, .7); ");
		fxStatusColors.put(ConversionJobFileStatus.FINISHED, "-fx-background-color: rgba(11, 156, 0, .9); ");
		fxStatusColors.put(ConversionJobFileStatus.ERRORED, "-fx-background-color: rgba(244, 20, 0, .75); ");
	}
	
	

	/**
	 * @update_comment
	 * @param string
	 */
	public TargetFileTreeItem(File file)
	{
		super(file.getName());
		this.file = file;
		status = ConversionJobFileStatus.NOT_STARTED;
		
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
	
	public TargetFileTreeItem(String display)
	{
		super(display);
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
	public ConversionJobFileStatus getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ConversionJobFileStatus status)
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
	
	public void setCellStyle(TreeCell<?> cell)
	{
		cell.setStyle(textFillColor + fxStatusColors.get(status));
	}
}
