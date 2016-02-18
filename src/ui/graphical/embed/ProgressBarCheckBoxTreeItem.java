package ui.graphical.embed;

import javafx.scene.control.CheckBoxTreeItem;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ProgressBarCheckBoxTreeItem<T> extends CheckBoxTreeItem<T>
{
	private double progress;

	/**
	 * @update_comment
	 * @param string
	 */
	public ProgressBarCheckBoxTreeItem(T t)
	{
		super(t);
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

	
}
