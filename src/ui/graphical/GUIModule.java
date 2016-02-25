package ui.graphical;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class GUIModule
{
	private Label f_label;
	
	/**
	 * @update_comment
	 * @param p_container
	 */
	public abstract void setup(VBox p_container);
	
	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public abstract void setEnabled(boolean p_enabled);
	
	/**
	 * @update_comment
	 * @param p_error
	 */
	public abstract void setErrorState(boolean p_error);
	
	/**
	 * @update_comment
	 * @param p_indent
	 * @param p_element
	 * @return
	 */
	protected HBox indentElement(int p_indent, Node p_element)
	{
		String indentation = "    ";
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(10, 0, 10, 0));
		hbox.setSpacing(10);
		Label space = new Label(new String(new char[p_indent]).replace("\0", indentation));
		hbox.getChildren().addAll(space, p_element);
		return hbox;
	}

	/**
	 * @return the label
	 */
	public Label getLabel()
	{
		return f_label;
	}

	/**
	 * @param p_label the label to set
	 */
	public void setLabel(Label p_label)
	{
		f_label = p_label;
	}
}
