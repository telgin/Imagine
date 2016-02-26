package ui.graphical;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A convenient pairing of commonly used gui elements. Simplifies gui code significantly.
 * Specifically, this is a label and a choice box.
 */
public class ChoiceModule extends GUIModule
{
	private String f_name;
	private ChoiceBox<String> f_choiceBox;
	private List<String> f_choices;
	private Consumer<Integer> f_callback;
	
	/**
	 * Creates a choice module
	 * @param p_name The name used as the label
	 * @param p_choices The list of choices
	 * @param p_callback The callback function to call when a selection changes
	 */
	public ChoiceModule(String p_name, List<String> p_choices, Consumer<Integer> p_callback)
	{
		setName(p_name);
		f_choices = p_choices;
		f_callback = p_callback;
		setLabel(new Label(p_name));
	}
	
	/**
	 * Sets the list of choices 
	 * @param p_choices The list of choices to set
	 */
	public void setChoices(List<String> p_choices)
	{
		f_choices = p_choices;
	}
	
	/**
	 * Gets the list of choices
	 * @return The list of choices
	 */
	public List<String> getChoices()
	{
		return f_choices;
	}
	
	/**
	 * Sets the selected choice
	 * @param p_choice The text of the choice to select
	 */
	public void setSelectedChoice(String p_choice)
	{
		f_choiceBox.setValue(p_choice);
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox p_container)
	{
		p_container.getChildren().add(getLabel());
		
		f_choiceBox = new ChoiceBox<>();
		f_choiceBox.setItems(FXCollections.observableArrayList(f_choices));
		f_choiceBox.getSelectionModel().selectedIndexProperty().addListener(
			(ObservableValue<? extends Number> value,
				Number oldIndex, Number newIndex) ->
					f_callback.accept(newIndex.intValue()));
		p_container.getChildren().add(indentElement(1, f_choiceBox));
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean p_enabled)
	{
		getLabel().disableProperty().set(!p_enabled);
		f_choiceBox.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			f_choiceBox.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			f_choiceBox.setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean p_error)
	{
		if (p_error)
			f_choiceBox.setStyle("-fx-highlight-fill: red");
		else
			f_choiceBox.setStyle("-fx-highlight-fill: white");
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
