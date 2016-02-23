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
 * @update_comment
 */
public class ChoiceProperty extends ConfigurationProperty
{

	private String name;
	private ChoiceBox<String> choiceBox;
	private List<String> choices;
	private Consumer<Integer> callback;
	
	public ChoiceProperty(String name, List<String> choices, Consumer<Integer> callback)
	{
		this.setName(name);
		this.choices = choices;
		this.callback = callback;
		setLabel(new Label(name));
	}
	
	public void setChoices(List<String> choices)
	{
		this.choices = choices;
	}
	
	public List<String> getChoices()
	{
		return choices;
	}
	
	public void setSelectedChoice(String choice)
	{
		choiceBox.setValue(choice);
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox container)
	{
		container.getChildren().add(getLabel());
		
		choiceBox = new ChoiceBox<>();
		choiceBox.setItems(FXCollections.observableArrayList(choices));
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											choiceSelected(value, oldIndex, newIndex));
		container.getChildren().add(indentElement(1, choiceBox));
	}

	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	private void choiceSelected(ObservableValue<? extends Number> value,
					Number oldIndex, Number newIndex)
	{
		callback.accept(newIndex.intValue());
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		getLabel().disableProperty().set(!enabled);
		choiceBox.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			choiceBox.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			choiceBox.setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean error)
	{
		if (error)
			choiceBox.setStyle("-fx-highlight-fill: red");
		else
			choiceBox.setStyle("-fx-highlight-fill: white");
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}
