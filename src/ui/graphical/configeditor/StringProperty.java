package ui.graphical.configeditor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class StringProperty extends ConfigurationProperty
{
	private String name;
	private TextField field;
	
	public StringProperty(String name)
	{
		this.name = name;
		setLabel(new Label(name));
	}
	
	public void setup(VBox container)
	{
		container.getChildren().add(getLabel());
		
		setField(new TextField());
		container.getChildren().add(indentElement(1, field));
	}
	
	public void setText(String text)
	{
		field.setText(text);
	}
	
	public String getText()
	{
		return field.getText();
	}

	/**
	 * @return the field
	 */
	public TextField getField()
	{
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(TextField field)
	{
		this.field = field;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		System.out.println(name + " enabled: " + enabled);
		
		getLabel().disableProperty().set(!enabled);
		field.disableProperty().set(!enabled);
		
		
		if (!enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			field.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			field.setStyle("-fx-opacity: 1");
		}
	}
}
