package ui.graphical.algorithmeditor;

import java.io.File;
import java.util.List;

import algorithms.Option;
import algorithms.Parameter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import system.CmdAction;
import ui.ArgParseResult;
import ui.graphical.BooleanModule;
import ui.graphical.ChoiceModule;
import ui.graphical.GUIModule;
import ui.graphical.DescriptionModule;
import ui.graphical.FileModule;
import ui.graphical.StringModule;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This is the view for the algorithm editor tab.
 */
public class AlgorithmEditorView extends View
{
	private AlgorithmEditorController f_controller;
	private ArgParseResult f_args;

	private ListView<String> f_presetList, f_parameterList;
	private Button f_createNewButton, f_saveButton;
	private StringModule f_presetName;
	private ChoiceModule f_algorithmType;
	private DescriptionModule f_algorithmDescription, f_parameterDescription;
	private BooleanModule f_parameterEnabled;
	private VBox f_optionSection;
	private GUIModule f_optionSelection;
	private Label f_parameterLabel;


	/**
	 * Constructs the algorithm editor tab
	 * @param p_window The javafx window
	 * @param p_args The command line arguments
	 */
	public AlgorithmEditorView(Stage p_window, ArgParseResult p_args)
	{
		super(p_window);
		
		f_args = p_args;
		f_controller = new AlgorithmEditorController(this);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	@Override
	public String getPassword()
	{
		//passwords no longer a part of the algorithm
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#setupPane()
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane base = new BorderPane();
		
		base.setTop(setupTitleSection());
		base.setLeft(setupAlgorithmSection());
		base.setRight(setupParameterSection());
		base.setBottom(setupButtonSection());
		
		reset();
		
		//setup stuff specified in args
		if (f_args.getAction() == CmdAction.k_editor)
		{
			if (f_args.getPresetName() != null && f_controller.getPresetNames().contains(f_args.getPresetName()))
			{
				f_presetList.getSelectionModel().select(f_args.getPresetName());
			}
		}
		
		//set to the first one if not specified (so everything's not grayed out)
		if (f_presetList.getSelectionModel().isEmpty())
		{
			f_presetList.getSelectionModel().select(0);
		}
		
		return base;
	}
	
	/**
	 * Creates the title section
	 * @return The title section
	 */
	private Node setupTitleSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(3);
		hbox.setPadding(new Insets(10,10,0,10));
		hbox.setAlignment(Pos.CENTER);
		
		Label sectionLabel = new Label("Add or Edit Algorithm Presets");
		sectionLabel.setFont(new Font("Arial", 20));
		hbox.getChildren().add(sectionLabel);
		
		return hbox;
	}
	
	/**
	 * Creates the algorithm section
	 * @return The algorithm section
	 */
	private Node setupAlgorithmSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,0,10,10));
		
		HBox hbox = new HBox();
		hbox.setSpacing(3);
		hbox.setPadding(new Insets(10,10,10,10));
		
		hbox.getChildren().add(setupPresetSelectionSection());
		hbox.getChildren().add(setupEditAttributesSection());
		
		vbox.getChildren().add(hbox);
		
		return vbox;
	}
	
	/**
	 * Creates the parameter section
	 * @return The parameter section
	 */
	private Node setupParameterSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,0));	
		
		HBox hbox = new HBox();
		hbox.setSpacing(20);
		hbox.setPadding(new Insets(10,10,10,10));
		
		hbox.getChildren().add(setupParameterSelectionSection());
		hbox.getChildren().add(setupEditParameterSection());
		
		vbox.getChildren().add(hbox);
		
		return vbox;
	}

	/**
	 * Creates the button section (create/save)
	 * @return The button section
	 */
	private Node setupButtonSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(40);
		hbox.setPadding(new Insets(10,10,20,10));
		hbox.setAlignment(Pos.CENTER);
		
		//create new preset button
		f_createNewButton = new Button();
		f_createNewButton.setText("Create New");
		f_createNewButton.setOnAction(e -> f_controller.createNewPressed());
		hbox.getChildren().add(f_createNewButton);
		
		//save button
		f_saveButton = new Button();
		f_saveButton.setText("Save");
		f_saveButton.setOnAction(e -> f_controller.savePressed());
		hbox.getChildren().add(f_saveButton);
		
		return hbox;
	}
	
	/**
	 * Creates the preset selection section
	 * @return The preset selection section
	 */
	private Node setupPresetSelectionSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//preset label
		Label presetsLabel = new Label("Presets");
		vbox.getChildren().add(presetsLabel);
		
		//preset list
		f_presetList = new ListView<String>();
		f_presetList.setItems(FXCollections.observableArrayList(f_controller.getPresetNames()));
		f_presetList.setPrefWidth(200);
		f_presetList.getSelectionModel().selectedIndexProperty().addListener(
			(ObservableValue<? extends Number> value,
				Number oldIndex, Number newIndex) ->
					f_controller.presetSelected(newIndex.intValue()));
		
		vbox.getChildren().add(f_presetList);
		
		return vbox;
	}
	
	/**
	 * Creates the edit attributes section
	 * @return The edit attributes section
	 */
	private Node setupEditAttributesSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//preset name
		f_presetName = new StringModule("Preset Name");
		f_presetName.setup(vbox);
		
		//algorithm type
		f_algorithmType = new ChoiceModule("Algorithm", 
						f_controller.getAlgorithmNames(),
						e -> f_controller.algorithmSelected(e));
		f_algorithmType.setup(vbox);
		
		//algorithm description
		f_algorithmDescription = new DescriptionModule("Algorithm Description");
		f_algorithmDescription.setup(vbox);
		f_algorithmDescription.getArea().setPrefWidth(200);
		f_algorithmDescription.getArea().setPrefHeight(300);
		f_algorithmDescription.getArea().setWrapText(true);
		
		return vbox;
	}
	
	/**
	 * Sets the selected algorithm
	 * @param p_choice The string to be selected
	 */
	public void setSelectedAlgorithm(String p_choice)
	{
		f_algorithmType.setSelectedChoice(p_choice);
	}

	
	/**
	 * Gets the text from the preset name field
	 * @return The preset name entered
	 */
	public String getPresetName()
	{
		return f_presetName.getText();
	}
	
	/**
	 * sets the preset name field
	 * @param p_name The text to set
	 */
	public void setPresetName(String p_name)
	{
		f_presetName.setText(p_name);
	}
	
	/**
	 * Sets the algorithm description text area
	 * @param p_text The text to set
	 */
	public void setAlgorithmDescription(String p_text)
	{
		f_algorithmDescription.setText(p_text);
	}
	
	/**
	 * Sets the parameter description text area
	 * @param p_text The text to set
	 */
	public void setParameterDescription(String p_text)
	{
		f_parameterDescription.setText(p_text);
	}
	
	/**
	 * Sets the list of parameter names
	 * @param p_parameterNames The list of parameter names
	 */
	public void setParameterNames(List<String> p_parameterNames)
	{
		f_parameterList.setItems(FXCollections.observableArrayList(p_parameterNames));
	}

	/**
	 * Creates the parameter selection section
	 * @return The parameter selection section
	 */
	private Node setupParameterSelectionSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,0,10,10));
		
		//parameter label
		f_parameterLabel = new Label("Parameters");
		vbox.getChildren().add(f_parameterLabel);
		
		//parameter list
		f_parameterList = new ListView<String>();
		f_parameterList.setItems(FXCollections.observableArrayList(f_controller.getParameterNames()));
		f_parameterList.setPrefWidth(200);
		f_parameterList.getSelectionModel().selectedIndexProperty().addListener(
			(ObservableValue<? extends Number> value,
				Number oldIndex, Number newIndex) ->
					f_controller.parameterSelected(newIndex.intValue()));
		
		vbox.getChildren().add(f_parameterList);
				
		return vbox;
	}

	/**
	 * Creates the parameter editing section
	 * @return The parameter editing section
	 */
	private Node setupEditParameterSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,0));
		
		//parameter description
		f_parameterDescription = new DescriptionModule("Parameter Description");
		f_parameterDescription.setup(vbox);
		f_parameterDescription.getArea().setPrefSize(300, 150);
		f_parameterDescription.getArea().setWrapText(true);
		
		//option section
		f_optionSection = new VBox();
		f_optionSection.setSpacing(3);
		f_optionSection.setPrefHeight(200);
		f_optionSection.setPrefWidth(300);
		vbox.getChildren().add(f_optionSection);
		
		return vbox;
	}
	
	/**
	 * Sets the enabled state of the parameter enabled checkbox
	 * @param p_allow The enabled state
	 */
	public void allowParameterEnabledChange(boolean p_allow)
	{
		f_parameterEnabled.setEnabled(p_allow);
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getEnclosingFolder()
	 */
	@Override
	public File getEnclosingFolder()
	{
		// this shouldn't happen in the algorithm configuration
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
	 * Sets the selected parameter
	 * @param p_index The index of the parameter to select
	 */
	public void setSelectedParameter(int p_index)
	{
		f_parameterList.getSelectionModel().select(p_index);
	}

	/**
	 * Removes all visible parameter options
	 */
	public void removeParameterOptions()
	{
		f_optionSection.getChildren().clear();
		f_optionSelection = null;
	}

	/**
	 * Displays the specific gui elements associated with the given parameter
	 * @param p_parameter The parameter to show the options of
	 */
	public void displayParameterOptions(Parameter p_parameter)
	{
		//enabled check box
		f_parameterEnabled = new BooleanModule("Enabled", b -> f_controller.parameterEnabledChecked(b));
		f_parameterEnabled.setup(f_optionSection);
		f_parameterEnabled.setPadding(new Insets(10, 0, 5, 0));
		f_parameterEnabled.setChecked(p_parameter.isEnabled());
		f_parameterEnabled.setEnabled(p_parameter.isOptional());

		if (p_parameter.getType().equals(Parameter.STRING_TYPE))
		{
			if (p_parameter.getOptions().size() == 1 && p_parameter.getOptions().get(0).getValue().equals("*"))
			{
				StringModule prop = new StringModule("Value");
				prop.setup(f_optionSection);
				f_optionSelection = prop;
			}
			else
			{
				ChoiceModule prop = new ChoiceModule("Value",
								p_parameter.getOptionDisplayValues(), e -> f_controller.optionSelected(e));
				prop.setup(f_optionSection);
				f_optionSelection = prop;
				prop.setSelectedChoice(p_parameter.getValue());
			}
		}
		else if (p_parameter.getType().equals(Parameter.INT_TYPE) || p_parameter.getType().equals(Parameter.LONG_TYPE))
		{
			Option opt = p_parameter.getOptions().get(0);

			//add value input
			StringModule prop = new StringModule("Value: " + opt.toString());
			prop.setup(f_optionSection);
			prop.setEditedCallback(e -> f_controller.optionSelected(e));
			f_optionSelection = prop;
			prop.setText(p_parameter.getValue());
		}
		else if (p_parameter.getType().equals(Parameter.FILE_TYPE))
		{
			FileModule prop = new FileModule("Value", e -> selectOptionFolder());
			prop.setup(f_optionSection);
			f_optionSelection = prop;
			
			if (p_parameter.getValue() != null && !p_parameter.getValue().equals(Option.PROMPT_OPTION.getValue()))
				prop.setPath(p_parameter.getValue());
		}
		
		//define at runtime check box
		if (p_parameter.getOptions().contains(Option.PROMPT_OPTION))
		{
			BooleanModule prop = new BooleanModule("Define at run time?", b -> f_controller.promptOptionSelected(b));
			prop.setup(f_optionSection);

			if (p_parameter.getValue() != null)
				prop.setChecked(p_parameter.getValue().equals(Option.PROMPT_OPTION.getValue()));
		}
	}

	/**
	 * Sets the error state of the visible parameter option
	 * @param p_error The error state
	 */
	public void setOptionSelectionErrorState(boolean p_error)
	{
		if (f_optionSelection != null)
		{
			f_optionSelection.setErrorState(p_error);
		}
	}
	
	/**
	 * Sets the enabled state of the option selection section
	 * @param p_enabled The enabled state
	 */
	public void setOptionSelectionEnabled(boolean p_enabled)
	{
		if (f_optionSelection != null)
		{
			f_optionSelection.setEnabled(p_enabled);
		}
	}
	
	/**
	 * Selects a folder to be set for the visible folder selection option.
	 */
	public void selectOptionFolder()
	{
		File folder = chooseFolder();
		f_controller.optionSelected(folder == null ? null : folder.getAbsolutePath());
		((FileModule) f_optionSelection).setPath(folder == null ? "[none selected]" : folder.getAbsolutePath());
	}

	/**
	 * Returns the gui back to the original state where no algorithm is selected. The
	 * other sections will reflect that change also.
	 */
	public void reset()
	{
		//clear all content
		f_presetList.setItems(null);
		f_presetName.setText(null);
		f_presetName.setEnabled(false);
		f_algorithmType.setSelectedChoice(null);
		f_algorithmType.setChoices(null);
		f_algorithmType.setEnabled(false);
		f_algorithmDescription.setText(null);
		f_algorithmDescription.setEnabled(false);
		f_parameterList.setItems(null);
		setParameterListEnabled(false);
		f_parameterDescription.setText(null);
		f_parameterDescription.setEnabled(false);
		removeParameterOptions();
		
		//re-add the list of presets
		f_presetList.setItems(FXCollections.observableArrayList(f_controller.getPresetNames()));
	}

	/**
	 * Sets the checked state of the parameter enabled checkbox
	 * @param p_checked The checked state
	 */
	public void setParameterEnabled(boolean p_checked)
	{
		f_parameterEnabled.setChecked(p_checked);
	}

	/**
	 * Sets the list of algorithm names
	 * @param p_algorithmDefinitionNames The list of algorithm definition names
	 */
	public void setAlgorithmNames(List<String> p_algorithmDefinitionNames)
	{
		f_algorithmType.setChoices(p_algorithmDefinitionNames);
	}

	/**
	 * Sets the enabled state of all sections which allow for edits to the algorithm
	 * @param p_enabled The enabled state
	 */
	public void setEditsEnabled(boolean p_enabled)
	{
		f_presetName.setEnabled(p_enabled);
		f_algorithmType.setEnabled(p_enabled);
		f_algorithmDescription.setEnabled(p_enabled);
		f_parameterDescription.setEnabled(p_enabled);
		setParameterListEnabled(p_enabled);
	}
	
	/**
	 * Sets the enabled state of the parameter list
	 * @param p_enabled The enabled state
	 */
	public void setParameterListEnabled(boolean p_enabled)
	{
		f_parameterLabel.disableProperty().set(!p_enabled);
		f_parameterList.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_parameterLabel.setStyle("-fx-opacity: .5");
			f_parameterList.setStyle("-fx-opacity: .75");
		}
		else
		{
			f_parameterLabel.setStyle("-fx-opacity: 1");
			f_parameterList.setStyle("-fx-opacity: 1");
		}
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter parameter)
	{
		//within the context of the configuration editor, we don't
		//want to prompt the user for the value of a parameter, only
		//show that we would prompt for the value during execution.
		return Option.PROMPT_OPTION.getValue();
		//TODO this works ok but it's weird
	}
}
