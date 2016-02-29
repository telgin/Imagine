package ui.graphical;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A convenience class for representing commonly paired gui elements. For instance,
 * the grouping of labels to input fields. 
 */
public abstract class GUIModule
{
	private Label f_label;
	
	/**
	 * Sets up the module in a vbox
	 * @param p_container The vbox container to add this module to
	 */
	public abstract void setup(VBox p_container);
	
	/**
	 * Sets the enabled state of this module
	 * @param p_enabled The enabled state
	 */
	public abstract void setEnabled(boolean p_enabled);
	
	/**
	 * Sets the error state of this module
	 * @param p_error The error state
	 */
	public abstract void setErrorState(boolean p_error);
	
	/**
	 * Indents an element by wrapping it in a hbox off center
	 * @param p_indent The indentation to use
	 * @param p_element The element to indent
	 * @return The hbox containing the indented element
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
