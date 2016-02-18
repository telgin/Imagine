package ui.graphical;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileProperty extends ConfigurationProperty
{
	private String name;
	private Button browse;
	private TextField path;
	private Consumer<Void> callback;
	
	public FileProperty(String name, Consumer<Void> callback)
	{
		this.name = name;
		this.callback = callback;
		setLabel(new Label(name));
	}
	
	public void setPath(String path)
	{
		this.path.setText(path);
	}
	
	public String getPath()
	{
		return path.getText();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox container)
	{
		container.getChildren().add(getLabel());
		
		path = new TextField();
		path.setEditable(false);
		
		browse = new Button();
		browse.setText("Browse");
		browse.setOnAction(e -> callback.accept(null));
		
		HBox pathRow = indentElement(1, path);
		pathRow.getChildren().add(browse);
		
		container.getChildren().add(pathRow);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		System.out.println(name + " enabled: " + enabled);
		
		path.disableProperty().set(!enabled);
		browse.disableProperty().set(!enabled);
		getLabel().disableProperty().set(!enabled);
		
		if (!enabled)
		{
			path.setStyle("-fx-opacity: .75");
			browse.setStyle("-fx-opacity: .75");
			getLabel().setStyle("-fx-opacity: .5");
		}
		else
		{
			path.setStyle("-fx-opacity: 1");
			browse.setStyle("-fx-opacity: 1");
			getLabel().setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean error)
	{
		if (error)
		{
			path.setStyle("-fx-text-inner-color: red; "
							+ "-fx-text-box-border: red; "
							+ "-fx-focus-color: red; "
							+ "-fx-border-width: 2px;");
		}
		else
		{
			path.setStyle("");
		}
	}

}
