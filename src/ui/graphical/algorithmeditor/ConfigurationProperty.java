package ui.graphical.algorithmeditor;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class ConfigurationProperty
{
	private Label label;
	
	
	
	public abstract void setup(VBox container);
	
	public abstract void setEnabled(boolean enabled);
	
	protected HBox indentElement(int indent, Node element)
	{
		String indentation = "    ";
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(10, 0, 10, 0));
		hbox.setSpacing(10);
		Label space = new Label(new String(new char[indent]).replace("\0", indentation));
		hbox.getChildren().addAll(space, element);
		return hbox;
	}

	/**
	 * @return the label
	 */
	public Label getLabel()
	{
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(Label label)
	{
		this.label = label;
	}
}
