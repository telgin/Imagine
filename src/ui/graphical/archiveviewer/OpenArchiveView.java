package ui.graphical.archiveviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import archive.FileContents;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import system.CmdAction;
import ui.ArgParseResult;
import ui.graphical.FileModule;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This is the view for the archive viewer tab.
 */
public class OpenArchiveView extends View
{
	//constants
	private static final String s_noKeyToggleString = "No Key";
	private static final String s_passwordToggleString = "Password";
	private static final String s_keyFileToggleString = "Key File";
	
	//gui elements
	private ChoiceBox<String> f_algorithmSelect;
	private PasswordField f_passwordField;
	private TextField f_keyFilePath;
	private Button f_keyFileBrowseButton, f_openButton, f_extractSelectedButton, f_extractAllButton;
	private ToggleGroup f_keySelectionButtons;
	private RadioButton f_noKeyToggle, f_keyFileToggle, f_passwordToggle;
	private TableView<FileContentsTableRecord> f_table;
	private Label f_passwordLabel, f_keyFileLabel, f_keySelectionLabel, f_algorithmLabel;
	private FileModule f_inputFile, f_outputFolder;
	
	//state fields
	private OpenArchiveController f_controller;
	private ArgParseResult f_args;

	/**
	 * Constructs the archive viewer tab
	 * @param p_window The javafx window
	 * @param p_args The command line arguments
	 */
	public OpenArchiveView(Stage p_window, ArgParseResult p_args)
	{
		super(p_window);
		
		f_args = p_args;
		f_controller = new OpenArchiveController(this);
	}

	/**
	 * @update_comment
	 * @return
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(setupConfigSelection());
		borderPane.setCenter(setupContentsSection());
		
		f_keySelectionButtons.selectToggle(f_noKeyToggle);
		
		//setup stuff specified in args
		if (f_args.getAction() == CmdAction.k_open)
		{
			if (f_args.getInputFiles().size() > 0 && f_args.getInputFiles().get(0).exists())
			{
				setInputFilePath(f_args.getInputFiles().get(0).getAbsolutePath());
			}
			
			if (f_args.getPresetName() != null && f_controller.getPresetNames().contains(f_args.getPresetName()))
			{
				setAlgorithmSelection(f_args.getPresetName());
			}

			if (f_args.getOutputFolder() != null && f_args.getOutputFolder().exists())
			{
				setOutputFolderPath(f_args.getOutputFolder().getAbsolutePath());
			}
			
			if (f_args.getKeyFile() != null && f_args.getKeyFile().exists())
			{
				setKeyFilePath(f_args.getKeyFile().getAbsolutePath());
				toggleKeySection();
			}
			
			if (f_args.isUsingPassword())
			{
				togglePasswordSection();
			}
		}

		//set to the first one if not specified (so everything's not grayed out)
		if (f_algorithmSelect.getSelectionModel().isEmpty())
		{
			setAlgorithmSelection(f_controller.getPresetNames().get(0));
		}
		
		return borderPane;
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#getTabName()
	 */
	@Override
	public String getTabName()
	{
		return "Archive Extractor";
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupContentsSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10, 20, 20, 10));

		//archive contents label
		Label archiveContentsLabel = new Label("Archive Contents");
		archiveContentsLabel.setFont(new Font("Arial", 20));
		vbox.getChildren().add(archiveContentsLabel);

		//scroll pane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		
		//table
		f_table = new TableView<FileContentsTableRecord>();
		f_table.setEditable(false);
		f_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		f_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		TableColumn<FileContentsTableRecord, String> indexColumn = new TableColumn<>("Index");
		indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
		indexColumn.setPrefWidth(70);
		f_table.getColumns().add(indexColumn);
		
		TableColumn<FileContentsTableRecord, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeColumn.setPrefWidth(150);
		f_table.getColumns().add(typeColumn);
		
		TableColumn<FileContentsTableRecord, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(250);
		f_table.getColumns().add(nameColumn);
		
		TableColumn<FileContentsTableRecord, String> createdColumn = new TableColumn<>("Date Created");
		createdColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
		createdColumn.setPrefWidth(110);
		f_table.getColumns().add(createdColumn);
		
		TableColumn<FileContentsTableRecord, String> modifiedColumn = new TableColumn<>("Date Modified");
		modifiedColumn.setCellValueFactory(new PropertyValueFactory<>("dateModified"));
		modifiedColumn.setPrefWidth(110);
		f_table.getColumns().add(modifiedColumn);
		f_table.setPrefHeight(1000);

		scrollPane.setContent(f_table);
		vbox.getChildren().add(scrollPane);
		vbox.setAlignment(Pos.BASELINE_LEFT);
		
		//extract selected button
		f_extractSelectedButton = new Button();
		f_extractSelectedButton.setText("Extract Selected");
		f_extractSelectedButton.setOnAction(e -> f_controller.extractSelected());
		
		//extract all button
		f_extractAllButton = new Button();
		f_extractAllButton.setText("Extract All");
		f_extractAllButton.setOnAction(e -> f_controller.extractAll());
		
		HBox extractionButtons = new HBox();
		extractionButtons.setAlignment(Pos.CENTER);
		extractionButtons.setSpacing(50);
		extractionButtons.setPadding(new Insets(10, 0, 0, 0));
		extractionButtons.getChildren().addAll(f_extractSelectedButton, f_extractAllButton);
		setExtractionButtonsEnabled(false);
		vbox.getChildren().add(extractionButtons);
		
		return vbox;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupConfigSelection()
	{
		VBox configSelection = new VBox();
		configSelection.setSpacing(3);
		configSelection.setPadding(new Insets(14,10,20,20));

		//extraction configuration label
		Label extractionConfigurationLabel = new Label("Extraction Configuration");
		extractionConfigurationLabel.setFont(new Font("Arial", 20));
		extractionConfigurationLabel.setPadding(new Insets(0, 0, 10, 0));
		configSelection.getChildren().add(extractionConfigurationLabel);
		
		//input file
		f_inputFile = new FileModule("Input File", e -> f_controller.browseInputFile());
		f_inputFile.setup(configSelection);
		
		//algorithm label
		f_algorithmLabel = new Label("Algorithm:");
		configSelection.getChildren().add(f_algorithmLabel);
		
		//algorithm select
		f_algorithmSelect = new ChoiceBox<>();
		setAlgorithmPresets(f_controller.getPresetNames());
		f_algorithmSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											f_controller.algorithmSelected(newIndex.intValue()));
		f_algorithmSelect.focusedProperty().addListener(
						(ObservableValue<? extends Boolean> value,
										Boolean oldValue, Boolean newValue) ->
											f_controller.algorithmSelectFocus(newValue.booleanValue()));
		configSelection.getChildren().add(indentElement(1, f_algorithmSelect));
		
		//output file
		f_outputFolder = new FileModule("Output Folder", e -> f_controller.browseOutputFolder());
		f_outputFolder.setup(configSelection);
		
		//key selection label
		f_keySelectionLabel = new Label("Key Selection:");
		configSelection.getChildren().add(f_keySelectionLabel);
		
		//key selection buttons
		f_keySelectionButtons = new ToggleGroup();
		
		f_noKeyToggle = new RadioButton(s_noKeyToggleString);
		f_noKeyToggle.setToggleGroup(f_keySelectionButtons);
		f_noKeyToggle.setUserData(s_noKeyToggleString);
		f_keyFileToggle = new RadioButton(s_keyFileToggleString);
		f_keyFileToggle.setToggleGroup(f_keySelectionButtons);
		f_keyFileToggle.setUserData(s_keyFileToggleString);
		f_passwordToggle = new RadioButton(s_passwordToggleString);
		f_passwordToggle.setUserData(s_passwordToggleString);
		f_passwordToggle.setToggleGroup(f_keySelectionButtons);
		f_keySelectionButtons.selectedToggleProperty().addListener(
						(ObservableValue<? extends Toggle> value,
										Toggle oldSelection, Toggle newSelection) ->
											keyTypeSelected(value, oldSelection, newSelection));
		
		HBox radioBox = new HBox();
		radioBox.setSpacing(10);
		radioBox.getChildren().addAll(f_noKeyToggle, f_passwordToggle, f_keyFileToggle);
		configSelection.getChildren().add(indentElement(1, radioBox));
		
		//password label
		f_passwordLabel = new Label("Password:");
		configSelection.getChildren().add(f_passwordLabel);
		
		//password text field
		f_passwordField = new PasswordField();
		configSelection.getChildren().add(indentElement(1, f_passwordField));
		
		//key file path label
		f_keyFileLabel = new Label("Key File Path:");
		configSelection.getChildren().add(f_keyFileLabel);
		
		//key file path
		f_keyFilePath = new TextField();
		f_keyFilePath.setEditable(false);
		
		//key file browse button
		f_keyFileBrowseButton = new Button();
		f_keyFileBrowseButton.setText("Browse");
		f_keyFileBrowseButton.setOnAction(e -> f_controller.chooseKeyFile());
		HBox keyFilePathRow = indentElement(1, f_keyFilePath);
		keyFilePathRow.getChildren().add(f_keyFileBrowseButton);
		configSelection.getChildren().add(keyFilePathRow);
		
		//open
		f_openButton = new Button();
		f_openButton.setText("Open");
		f_openButton.setOnAction(e -> f_controller.openArchive());
		HBox centered = new HBox();
		centered.getChildren().add(f_openButton);
		centered.setAlignment(Pos.CENTER);
		centered.setPadding(new Insets(20, 0, 0, 0));
		configSelection.getChildren().add(centered);

		return configSelection;
	}
	
	/**
	 * @update_comment
	 * @param p_indent
	 * @param p_element
	 * @return
	 */
	private HBox indentElement(int p_indent, Node p_element)//TODO replace with modules
	{
		String indentation = "    ";
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(10, 0, 10, 0));
		hbox.setSpacing(10);
		Label space = new Label(new String(new char[p_indent]).replace("\0", indentation));
		hbox.getChildren().addAll(space, p_element);
		return hbox;
	}

	/**
	 * @update_comment
	 * @param p_enabled
	 */
	void setKeySectionEnabled(boolean p_enabled)
	{
		f_noKeyToggle.disableProperty().set(!p_enabled);
		f_keyFileToggle.disableProperty().set(!p_enabled);
		f_passwordToggle.disableProperty().set(!p_enabled);
		f_keySelectionLabel.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_noKeyToggle.setStyle("-fx-opacity: .75");
			f_keyFileToggle.setStyle("-fx-opacity: .75");
			f_passwordToggle.setStyle("-fx-opacity: .75");
			f_keySelectionLabel.setStyle("-fx-opacity: .5");
			
			//disable everything
			setKeyFileSectionEnabled(false);
			setPasswordSectionEnabled(false);
		}
		else
		{
			f_noKeyToggle.setStyle("-fx-opacity: 1");
			f_keyFileToggle.setStyle("-fx-opacity: 1");
			f_passwordToggle.setStyle("-fx-opacity: 1");
			f_keySelectionLabel.setStyle("-fx-opacity: 1");

			//enable only the selected one
			if (f_keySelectionButtons.getSelectedToggle().getUserData().equals(s_keyFileToggleString))
			{
				setKeyFileSectionEnabled(true);
				setPasswordSectionEnabled(false);
			}
			else if (f_keySelectionButtons.getSelectedToggle().getUserData().equals(s_passwordToggleString))
			{
				setKeyFileSectionEnabled(false);
				setPasswordSectionEnabled(true);
			}
			else //assuming no key selected
			{
				//disable everything
				setKeyFileSectionEnabled(false);
				setPasswordSectionEnabled(false);
			}
		}
	}
	
	/**
	 * @update_comment
	 * @param p_value
	 * @param p_oldSelection
	 * @param p_newSelection
	 * @return
	 */
	private void keyTypeSelected(ObservableValue<? extends Toggle> p_value,
					Toggle p_oldSelection, Toggle p_newSelection)
	{
		if (p_newSelection.getUserData().equals(s_noKeyToggleString))
		{
			setPasswordSectionEnabled(false);
			setKeyFileSectionEnabled(false);
		}
		else if (p_newSelection.getUserData().equals(s_passwordToggleString))
		{
			setPasswordSectionEnabled(true);
			setKeyFileSectionEnabled(false);
			//clearKeyFileSection();
		}
		else //assumed file toggled
		{
			setPasswordSectionEnabled(false);
			setKeyFileSectionEnabled(true);
			//clearPasswordSection();
		}
	}
	
	/**
	 * @update_comment
	 * @param p_data
	 */
	public void setTableData(List<FileContents> p_data)
	{
		List<FileContentsTableRecord> records = new ArrayList<FileContentsTableRecord>();
		
		int count = 1;
		for (FileContents fileContents : p_data)
		{
			records.add(new FileContentsTableRecord(count++,
							fileContents.getMetadata().getType(),
							fileContents.getFragmentNumber(),
							fileContents.isFragment(),
							fileContents.getMetadata().getFile(),
							fileContents.getMetadata().getDateCreated(),
							fileContents.getMetadata().getDateModified()));
		}
		
		f_table.setItems(FXCollections.observableArrayList(records));
	}
	
	/**
	 * @update_comment
	 */
	void clearTable()
	{
		f_table.setItems(null);
	}

	/**
	 * @update_comment
	 * @param p_enabled
	 */
	private void setKeyFileSectionEnabled(boolean p_enabled)
	{
		f_keyFilePath.disableProperty().set(!p_enabled);
		f_keyFileBrowseButton.disableProperty().set(!p_enabled);
		f_keyFileLabel.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_keyFilePath.setStyle("-fx-opacity: .75");
			f_keyFileBrowseButton.setStyle("-fx-opacity: .75");
			f_keyFileLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			f_keyFilePath.setStyle("-fx-opacity: 1");
			f_keyFileBrowseButton.setStyle("-fx-opacity: 1");
			f_keyFileLabel.setStyle("-fx-opacity: 1");
		}
		
	}

	/**
	 * @update_comment
	 * @param b
	 */
	private void setPasswordSectionEnabled(boolean p_enabled)
	{
		f_passwordField.disableProperty().set(!p_enabled);
		f_passwordLabel.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_passwordField.setStyle("-fx-opacity: .75");
			f_passwordLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			f_passwordField.setStyle("-fx-opacity: 1");
			f_passwordLabel.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public void setOpenSectionEnabled(boolean p_enabled)
	{
		f_openButton.disableProperty().set(!p_enabled);
	}
	
	/**
	 * @update_comment
	 */
	public void clearPasswordSection()
	{
		f_passwordField.clear();
	}

	/**
	 * @update_comment
	 */
	public void clearKeyFileSection()
	{
		f_keyFilePath.clear();
	}
	
	/**
	 * @update_comment
	 */
	void clearKeySection()
	{
		clearPasswordSection();
		clearKeyFileSection();
	}

	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public void setOpenButtonEnabled(boolean p_enabled)
	{
		f_openButton.disableProperty().set(!p_enabled);

		if (!p_enabled)
		{
			f_openButton.setStyle("-fx-opacity: .75");
		}
		else
		{
			f_openButton.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public void setExtractionButtonsEnabled(boolean p_enabled)
	{
		f_extractSelectedButton.disableProperty().set(!p_enabled);
		f_extractAllButton.disableProperty().set(!p_enabled);

		if (!p_enabled)
		{
			f_extractSelectedButton.setStyle("-fx-opacity: .75");
			f_extractAllButton.setStyle("-fx-opacity: .75");
		}
		else
		{
			f_extractSelectedButton.setStyle("-fx-opacity: 1");
			f_extractAllButton.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * @update_comment
	 * @param p_presetName
	 */
	public void setAlgorithmSelection(String p_presetName)
	{
		f_algorithmSelect.setValue(p_presetName);
	}
	
	/**
	 * @update_comment
	 * @param p_presetNames
	 */
	public void setAlgorithmPresets(List<String> p_presetNames)
	{
		f_algorithmSelect.setItems(FXCollections.observableArrayList(p_presetNames));
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getAlgorithmSelection()
	{
		return (String) f_algorithmSelect.getValue();
	}
	
	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public void setAlgorithmSelectionEnabled(boolean p_enabled)
	{
		f_algorithmSelect.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_algorithmSelect.setStyle("-fx-opacity: .75");
		}
		else
		{
			f_algorithmSelect.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * @update_comment
	 * @param p_prompt
	 */
	public void setPasswordPrompt(String p_prompt)
	{
		f_passwordField.setPromptText(p_prompt);
	}
	
	/**
	 * @update_comment
	 */
	public void clearPasswordPrompt()
	{
		f_passwordField.setPromptText("");
	}
	
	/**
	 * @update_comment
	 */
	public void toggleKeySection()
	{
		f_keySelectionButtons.selectToggle(f_keyFileToggle);
	}
	
	/**
	 * @update_comment
	 */
	public void togglePasswordSection()
	{
		f_keySelectionButtons.selectToggle(f_passwordToggle);
	}
	
	/**
	 * @update_comment
	 * @param p_path
	 */
	public void setInputFilePath(String p_path)
	{
		f_inputFile.setPath(p_path);
	}
	
	/**
	 * @update_comment
	 * @param p_path
	 */
	public void setOutputFolderPath(String p_path)
	{
		f_outputFolder.setPath(p_path);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getInputFilePath()
	{
		return f_inputFile.getPath();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getOutputFolderPath()
	{
		return f_outputFolder.getPath();
	}
	
	/**
	 * @update_comment
	 * @param p_path
	 */
	public void setKeyFilePath(String p_path)
	{
		f_keyFilePath.setText(p_path);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public boolean keyFileEnabled()
	{
		return !f_keyFilePath.disableProperty().get();
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean passwordEnabled()
	{
		return !f_passwordField.disableProperty().get();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getKeyFilePath()
	{
		return f_keyFilePath.getText();
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	@Override
	public String getPassword()
	{
		return f_passwordField.getText();
	}

	/**
	 * @update_comment
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Integer> getSelectedRows()
	{
		List<Integer> indices = new LinkedList<Integer>();
		
		ObservableList<TablePosition> selectedRows = f_table.getSelectionModel().getSelectedCells();
		for (int x = 0; x < selectedRows.size(); ++x)
		{
			TablePosition position = selectedRows.get(x);
			indices.add(position.getRow());
		}
		
		return indices;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getEnclosingFolder()
	 */
	@Override
	public File getEnclosingFolder()
	{
		return chooseFolder();
	}

}
