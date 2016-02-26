package ui.graphical;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A convenient pairing of commonly used gui elements. Simplifies gui code significantly.
 * Specifically, this is a label and a text field with a browse button.
 */
public class FileModule extends GUIModule
{
	private String f_name;
	private Button f_browseButton;
	private TextField f_pathField;
	private Consumer<Void> f_browseCallback;
	
	/**
	 * Creates a file module
	 * @param p_name The name used as the label
	 * @param p_browseCallback The callback for when the browse button is clicked
	 */
	public FileModule(String p_name, Consumer<Void> p_browseCallback)
	{
		setName(p_name);
		f_browseCallback = p_browseCallback;
		setLabel(new Label(p_name));
	}
	
	/**
	 * Sets the path in the text area
	 * @param p_path The path string
	 */
	public void setPath(String p_path)
	{
		f_pathField.setText(p_path);
	}
	
	/**
	 * Gets the path, which is whatever is set in the text field
	 * @return The contents of the text field
	 */
	public String getPath()
	{
		return f_pathField.getText();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox p_container)
	{
		p_container.getChildren().add(getLabel());
		
		f_pathField = new TextField();
		f_pathField.setEditable(false);
		
		f_browseButton = new Button();
		f_browseButton.setText("Browse");
		f_browseButton.setOnAction(e -> f_browseCallback.accept(null));
		
		HBox pathRow = indentElement(1, f_pathField);
		pathRow.getChildren().add(f_browseButton);
		
		p_container.getChildren().add(pathRow);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean p_enabled)
	{
		f_pathField.disableProperty().set(!p_enabled);
		f_browseButton.disableProperty().set(!p_enabled);
		getLabel().disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_pathField.setStyle("-fx-opacity: .75");
			f_browseButton.setStyle("-fx-opacity: .75");
			getLabel().setStyle("-fx-opacity: .5");
		}
		else
		{
			f_pathField.setStyle("-fx-opacity: 1");
			f_browseButton.setStyle("-fx-opacity: 1");
			getLabel().setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean p_error)
	{
		if (p_error)
		{
			f_pathField.setStyle("-fx-text-inner-color: red; "
							+ "-fx-text-box-border: red; "
							+ "-fx-focus-color: red; "
							+ "-fx-border-width: 2px;");
		}
		else
		{
			f_pathField.setStyle("");
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
		f_name = p_name;
	}

}
