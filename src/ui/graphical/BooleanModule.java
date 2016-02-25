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
public class BooleanModule extends GUIModule
{
	private String f_name;
	private CheckBox f_checkBox;
	private Consumer<Boolean> f_callback;
	private HBox f_propertyRow;
	
	/**
	 * @update_comment
	 * @param p_name
	 * @param p_callback
	 */
	public BooleanModule(String p_name, Consumer<Boolean> p_callback)
	{
		setName(p_name);
		f_callback = p_callback;
		setLabel(new Label(p_name));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox p_container)
	{
		f_propertyRow = new HBox();
		f_propertyRow.setSpacing(10);
		f_propertyRow.getChildren().add(getLabel());
		
		f_checkBox = new CheckBox();
		f_checkBox.selectedProperty().addListener(
			(ObservableValue<? extends Boolean> value,
				Boolean oldValue, Boolean newValue) ->
					f_callback.accept(newValue));
		f_propertyRow.getChildren().add(f_checkBox);
		
		p_container.getChildren().add(f_propertyRow);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean p_enabled)
	{
		getLabel().disableProperty().set(!p_enabled);
		f_checkBox.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			f_checkBox.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			f_checkBox.setStyle("-fx-opacity: 1");
		}
	}

	/**
	 * @update_comment
	 * @param enabled
	 */
	public void setChecked(boolean p_checked)
	{
		f_checkBox.setSelected(p_checked);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean p_error)
	{
		if (p_error)
			f_checkBox.setStyle("-fx-highlight-fill: red");
		else
			f_checkBox.setStyle("-fx-highlight-fill: white");
	}

	/**
	 * @update_comment
	 * @param p_insets
	 */
	public void setPadding(Insets p_insets)
	{
		f_propertyRow.setPadding(p_insets);
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
