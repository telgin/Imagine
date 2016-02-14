package ui.graphical.algorithmeditor;

import java.io.File;
import java.util.List;

import algorithms.Option;
import algorithms.Parameter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class AlgorithmEditorView extends View
{
	private AlgorithmEditorController controller;

	private ListView<String> presetList, parameterList;
	private Button createNewButton, saveButton;
	private StringProperty presetName;
	private ChoiceProperty algorithmType;
	private DescriptionProperty algorithmDescription, parameterDescription;
	private BooleanProperty parameterEnabled;
	private VBox optionSection;
	private ConfigurationProperty optionSelection;

	/**
	 * @update_comment
	 * @param window
	 */
	public AlgorithmEditorView(Stage window)
	{
		super(window);
		
		controller = new AlgorithmEditorController(this);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	@Override
	public String getPassword()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#setupPane()
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane base = new BorderPane();
		
		base.setCenter(setupAlgorithmSection());
		
		reset();
		
		return base;
	}
	
	private Node setupAlgorithmSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(3);
		hbox.setPadding(new Insets(20,20,20,20));
		
		hbox.getChildren().add(setupPresetSelectionSection());
		hbox.getChildren().add(setupEditAttributesSection());
		hbox.getChildren().add(setupParameterSelectionSection());
		hbox.getChildren().add(setupEditParameterSection());
		
		return hbox;
	}
	


	private Node setupButtonSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(40);
		hbox.setPadding(new Insets(20,20,20,20));
		
		//create new preset button
		createNewButton = new Button();
		createNewButton.setText("Create New");
		createNewButton.setOnAction(e -> controller.createNewPressed());
		hbox.getChildren().add(createNewButton);
		
		//save button
		saveButton = new Button();
		saveButton.setText("Save");
		saveButton.setOnAction(e -> controller.savePressed());
		hbox.getChildren().add(saveButton);
		
		return hbox;
	}
	
	private Node setupPresetSelectionSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));

		//presets label
		Label sectionLabel = new Label("Existing Presets");
		sectionLabel.setFont(new Font("Arial", 20));
		sectionLabel.setPadding(new Insets(0, 0, 10, 0));
		vbox.getChildren().add(sectionLabel);
		
		//preset list
		presetList = new ListView<String>();
		presetList.setItems(FXCollections.observableArrayList(controller.getPresetNames()));
		presetList.setPrefWidth(presetList.getPrefWidth() - 75);
		presetList.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											presetSelected(value, oldIndex, newIndex));
		
		vbox.getChildren().add(presetList);
		
		//buttons
		vbox.getChildren().add(setupButtonSection());
		
		return vbox;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	private Node setupEditAttributesSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//preset name
		presetName = new StringProperty("Preset Name");
		presetName.setup(vbox);
		
		//algorithm type
		algorithmType = new ChoiceProperty("Algorithm", 
						controller.getAlgorithmNames(),
						e -> controller.algorithmSelected(e));
		algorithmType.setup(vbox);
		
		//algorithm description
		algorithmDescription = new DescriptionProperty("Algorithm Description");
		algorithmDescription.setup(vbox);
		algorithmDescription.getArea().setPrefSize(175, 180);
		algorithmDescription.getArea().setWrapText(true);
		
		return vbox;
	}
	
	public void setSelectedAlgorithm(String choice)
	{
		algorithmType.setSelectedChoice(choice);
	}

	
	public String getPresetName()
	{
		return presetName.getText();
	}
	
	public void setPresetName(String name)
	{
		presetName.setText(name);
	}
	
	public void setAlgorithmDescription(String text)
	{
		algorithmDescription.setText(text);
	}
	
	public void setParameterDescription(String text)
	{
		parameterDescription.setText(text);
	}
	
	public void setParameterNames(List<String> parameterNames)
	{
		parameterList.setItems(FXCollections.observableArrayList(parameterNames));
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupParameterSelectionSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//presets label
		Label sectionLabel = new Label("Parameters");
		sectionLabel.setFont(new Font("Arial", 20));
		sectionLabel.setPadding(new Insets(0, 0, 10, 0));
		vbox.getChildren().add(sectionLabel);
		
		//parameter list
		parameterList = new ListView<String>();
		parameterList.setItems(FXCollections.observableArrayList(controller.getParameterNames()));
		parameterList.setPrefWidth(150);
		parameterList.setPrefHeight(250);
		parameterList.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											parameterSelected(value, oldIndex, newIndex));
		
		vbox.getChildren().add(parameterList);
		
		//parameter description
		parameterDescription = new DescriptionProperty("Parameter Description");
		parameterDescription.setup(vbox);
		parameterDescription.getArea().setPrefSize(175, 180);
		parameterDescription.getArea().setWrapText(true);
		parameterDescription.setPadding(new Insets(20, 0, 0, 0));
				
		return vbox;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupEditParameterSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//option section
		optionSection = new VBox();
		optionSection.setSpacing(3);
		optionSection.setPadding(new Insets(10,10,10,10));
		optionSection.setPrefHeight(200);
		optionSection.setPrefWidth(300);
		vbox.getChildren().add(optionSection);
		
		return vbox;
	}
	
	public void allowParameterEnabledChange(boolean allow)
	{
		parameterEnabled.setEnabled(allow);
	}


	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	private void presetSelected(ObservableValue<? extends Number> value,
					Number oldIndex, Number newIndex)
	{
		controller.presetSelected(newIndex.intValue());
	}
	
	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	private void parameterSelected(ObservableValue<? extends Number> value,
					Number oldIndex, Number newIndex)
	{
		controller.parameterSelected(newIndex.intValue());
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getEnclosingFolder()
	 */
	@Override
	public File getEnclosingFolder()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getTabName()
	 */
	@Override
	public String getTabName()
	{
		return "Algorithm Editor";
	}

	/**
	 * @update_comment
	 * @param object
	 */
	public void setSelectedParameter(int index)
	{
		parameterList.getSelectionModel().select(index);
	}

	/**
	 * @update_comment
	 */
	public void removeParameterOptions()
	{
		optionSection.getChildren().clear();
		optionSelection = null;
	}

	/**
	 * @update_comment
	 * @param selectedParameter
	 */
	public void displayParameterOptions(Parameter parameter)
	{
		//enabled check box
		parameterEnabled = new BooleanProperty("Enabled", b -> controller.parameterEnabledChecked(b));
		parameterEnabled.setup(optionSection);
		parameterEnabled.setPadding(new Insets(0, 0, 20, 0));
		parameterEnabled.setChecked(parameter.isEnabled());
		parameterEnabled.setEnabled(parameter.isOptional());
		
		//define at runtime check box
		if (parameter.getOptions().contains(Option.PROMPT_OPTION))
		{
			BooleanProperty prop = new BooleanProperty("Define at run time?", b -> controller.promptOptionSelected(b));
			prop.setup(optionSection);
			
			System.out.println("The value: " + parameter.getValue());
			if (parameter.getValue() != null)
				prop.setChecked(parameter.getValue().equals(Option.PROMPT_OPTION.getValue()));
		}
		
		if (parameter.getType().equals(Parameter.STRING_TYPE))
		{
			if (parameter.getOptions().size() == 1 && parameter.getOptions().get(0).getValue().equals("*"))
			{
				StringProperty prop = new StringProperty("Value");
				prop.setup(optionSection);
				optionSelection = prop;
			}
			else
			{
				ChoiceProperty prop = new ChoiceProperty("Value",
								parameter.getOptionDisplayValues(), e -> controller.optionSelected(e));
				prop.setup(optionSection);
				optionSelection = prop;
				prop.setSelectedChoice(parameter.getValue());
			}
		}
		else if (parameter.getType().equals(Parameter.INT_TYPE) || parameter.getType().equals(Parameter.LONG_TYPE))
		{
			Option opt = parameter.getOptions().get(0);

			//add value input
			StringProperty prop = new StringProperty("Value: " + opt.toString());
			prop.setup(optionSection);
			prop.setEditedCallback(e -> controller.optionSelected(e));
			optionSelection = prop;
			prop.setText(parameter.getValue());
		}
		else if (parameter.getType().equals(Parameter.FILE_TYPE))
		{
			FileProperty prop = new FileProperty("Value", e -> selectOptionFolder());
			prop.setup(optionSection);
			optionSelection = prop;
			
			if (parameter.getValue() != null && !parameter.getValue().equals(Option.PROMPT_OPTION.getValue()))
				prop.setPath(parameter.getValue());
		}
	}

	/**
	 * @update_comment
	 * @param b
	 */
	public void setOptionSelectionErrorState(boolean error)
	{
		if (optionSelection != null)
		{
			optionSelection.setErrorState(error);
		}
	}
	
	public void setOptionSelectionEnabled(boolean enabled)
	{
		if (optionSelection != null)
		{
			optionSelection.setEnabled(enabled);
		}
	}
	
	public void selectOptionFolder()
	{
		File folder = chooseFolder();
		controller.optionSelected(folder == null ? null : folder.getAbsolutePath());
		((FileProperty) optionSelection).setPath(folder == null ? "[none selected]" : folder.getAbsolutePath());
	}

	/**
	 * @update_comment
	 */
	public void reset()
	{
		//clear all content
		presetList.setItems(null);
		presetName.setText(null);
		algorithmType.setSelectedChoice(null);
		algorithmType.setChoices(null);
		algorithmType.setEnabled(false);
		algorithmDescription.setText(null);
		parameterList.setItems(null);
		parameterDescription.setText(null);
		removeParameterOptions();
		
		//re-add the list of presets
		presetList.setItems(FXCollections.observableArrayList(controller.getPresetNames()));
	}

	/**
	 * @update_comment
	 * @param enabled
	 */
	public void setParameterEnabled(boolean checked)
	{
		parameterEnabled.setChecked(checked);
	}

	/**
	 * @update_comment
	 * @param algorithmDefinitionNames
	 */
	public void setAlgorithmNames(List<String> algorithmDefinitionNames)
	{
		algorithmType.setChoices(algorithmDefinitionNames);
	}

	/**
	 * @update_comment
	 * @param b
	 */
	public void setAlgorithmSelectionEnabled(boolean enabled)
	{
		algorithmType.setEnabled(enabled);
	}
	
	@Override
	public String promptParameterValue(Parameter parameter)
	{
		//within the context of the configuration editor, we don't
		//want to prompt the user for the value of a parameter, only
		//show that we would prompt for the value during execution.
		return Option.PROMPT_OPTION.getValue();
	}
}
