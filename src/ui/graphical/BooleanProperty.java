package ui.graphical;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class BooleanProperty extends ConfigurationProperty
{
	private String name;
	private CheckBox checkBox;
	private Consumer<Boolean> callback;
	private HBox propertyRow;
	
	public BooleanProperty(String name, Consumer<Boolean> callback)
	{
		this.name = name;
		this.callback = callback;
		setLabel(new Label(name));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox container)
	{
		propertyRow = new HBox();
		propertyRow.setSpacing(10);
		propertyRow.getChildren().add(getLabel());
		
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(
						(ObservableValue<? extends Boolean> value,
							Boolean oldValue, Boolean newValue) ->
								checked(value, oldValue, newValue));
		propertyRow.getChildren().add(checkBox);
		
		container.getChildren().add(propertyRow);
	}

	/**
	 * @update_comment
	 * @param value
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private void checked(ObservableValue<? extends Boolean> value, Boolean oldValue,
					Boolean newValue)
	{
		callback.accept(newValue);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		System.out.println(name + " enabled: " + enabled);
		
		getLabel().disableProperty().set(!enabled);
		checkBox.disableProperty().set(!enabled);
		
		
		if (!enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			checkBox.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			checkBox.setStyle("-fx-opacity: 1");
		}
	}

	/**
	 * @update_comment
	 * @param enabled
	 */
	public void setChecked(boolean checked)
	{
		checkBox.setSelected(checked);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean error)
	{
		if (error)
			checkBox.setStyle("-fx-highlight-fill: red");
		else
			checkBox.setStyle("-fx-highlight-fill: white");
	}

	/**
	 * @update_comment
	 * @param insets
	 */
	public void setPadding(Insets insets)
	{
		propertyRow.setPadding(insets);
	}

}
