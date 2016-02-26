package ui.graphical;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A convenient pairing of commonly used gui elements. Simplifies gui code significantly.
 * Specifically, this is a label and a text field.
 */
public class StringModule extends GUIModule
{
	private String f_name;
	private TextField f_field;
	
	/**
	 * Creates a string module
	 * @param p_name The name used as the label
	 */
	public StringModule(String p_name)
	{
		setName(p_name);
		setLabel(new Label(p_name));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	public void setup(VBox p_container)
	{
		p_container.getChildren().add(getLabel());
		
		setField(new TextField());
		p_container.getChildren().add(indentElement(1, f_field));
	}
	
	/**
	 * Sets the text of the text field
	 * @param p_text The text to set
	 */
	public void setText(String p_text)
	{
		f_field.setText(p_text);
	}
	
	/**
	 * Gets the text in the text field
	 * @return The contents of the text field
	 */
	public String getText()
	{
		return f_field.getText();
	}

	/**
	 * @return the field
	 */
	public TextField getField()
	{
		return f_field;
	}

	/**
	 * @param p_field the field to set
	 */
	public void setField(TextField p_field)
	{
		this.f_field = p_field;
	}
	
	/**
	 * Sets the callback function for when the text field is edited (textProperty listener)
	 * @param p_callback The callback function
	 */
	public void setEditedCallback(Consumer<String> p_callback)
	{
		f_field.textProperty().addListener((obsv, oldValue, newValue) -> p_callback.accept(newValue)) ;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean p_enabled)
	{
		getLabel().disableProperty().set(!p_enabled);
		f_field.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			f_field.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			f_field.setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean p_error)
	{
		if (p_error)
		{
			f_field.setStyle("-fx-text-inner-color: red; "
							+ "-fx-text-box-border: red; "
							+ "-fx-focus-color: red; "
							+ "-fx-border-width: 2px;");
		}
		else
		{
			f_field.setStyle("");
		}
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * @param p_name the name to set
	 */
	public void setName(String p_name)
	{
		this.f_name = p_name;
	}
}
