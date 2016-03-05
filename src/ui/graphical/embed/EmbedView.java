package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.ArchiveFile;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logging.LogLevel;
import logging.Logger;
import system.CmdAction;
import ui.ArgParseResult;
import ui.graphical.BooleanModule;
import ui.graphical.FileModule;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This is the view for the archive creator tab.
 */
public class EmbedView extends View
{
	//constants
	private static final String s_noKeyToggleString = "No Key";
	private static final String s_passwordToggleString = "Password";
	private static final String s_keyFileToggleString = "Key File";
	private static final String s_filesCreatedString = "Total output files created: ";
	
	//gui elements
	private ChoiceBox<String> f_algorithmSelect;
	private PasswordField f_passwordField;
	private TextField f_keyFilePath;
	private Button f_keyFileBrowseButton, f_inputAddFileButton, f_inputAddFolderButton,
		f_inputRemoveButton, f_targetSelectFolderButton, f_createArchivesButton;
	private ToggleGroup f_keySelectionButtons;
	private RadioButton f_noKeyToggle, f_keyFileToggle, f_passwordToggle;
	private Label f_passwordLabel, f_keyFileLabel, f_keySelectionLabel, f_algorithmLabel,
		f_inputFilesLabel, f_targetFilesLabel, f_filesCreatedLabel;
	private FileModule f_outputFolder;
	private BooleanModule f_structuredOutput;
	private TreeView<String> f_inputFiles, f_targetFiles;
	private ProgressBar f_creationProgress;
	private InputFileTreeItem f_inputFileRoot;
	private TargetFileTreeItem f_targetFileRoot;
	
	//state fields
	private EmbedController f_controller;
	private ArgParseResult f_args;
	private Map<TreeCell<String>, InputFileTreeItem> f_activeInputItems;
	private Map<TreeCell<String>, TargetFileTreeItem> f_activeTargetItems;
	boolean f_updatingCells = false;

	/**
	 * Constructs the archive creator tab
	 * @param p_window The javafx window
	 * @param p_args The command line arguments
	 */
	public EmbedView(Stage p_window, ArgParseResult p_args)
	{
		super(p_window);
		
		f_args = p_args;
		f_controller = new EmbedController(this);
		f_activeInputItems = new HashMap<TreeCell<String>, InputFileTreeItem>();
		f_activeTargetItems = new HashMap<TreeCell<String>, TargetFileTreeItem>();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#setupPane()
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(setupConfigSelection());
		borderPane.setRight(setupTargetFileSection());
		borderPane.setCenter(setupInputFileSection());
		borderPane.setBottom(setupProgressSection());
		
		f_keySelectionButtons.selectToggle(f_noKeyToggle);
		setInputSectionEnabled(false);
		setTargetSectionEnabled(false);
		setCreateArchivesEnabled(false);
		
		//setup stuff specified in args
		if (f_args.getAction() == CmdAction.k_embed)
		{
			for (File input : f_args.getInputFiles())
				if (input.exists())
					addInput(new ArchiveFile(input.getPath()));
			
			if (f_args.getPresetName() != null && f_controller.getPresetNames().contains(f_args.getPresetName()))
			{
				setAlgorithmSelection(f_args.getPresetName());
			}
			
			if (f_args.getOutputFolder() != null && f_args.getOutputFolder().exists())
			{
				setOutputFolder(f_args.getOutputFolder());
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
	
	/**
	 * Creates the progress section
	 * @return The progress section
	 */
	private Node setupProgressSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(10,10,10,10));
		hbox.setAlignment(Pos.CENTER);
		
		//progress bar
		f_creationProgress = new ProgressBar(0);
		f_creationProgress.setPrefWidth(230);
		hbox.getChildren().add(f_creationProgress);

		//files created label
		f_filesCreatedLabel = new Label("");
		setFilesCreated(0);
		hbox.getChildren().add(f_filesCreatedLabel);
		
		
		return hbox;
	}

	/**
	 * Creates the input file section
	 * @return The input file section
	 */
	private Node setupInputFileSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//input files label
		f_inputFilesLabel = new Label("Input Files:");
		vbox.getChildren().add(f_inputFilesLabel);
		
		//input file tree
		f_inputFiles = new TreeView<String>();
		f_inputFiles.setEditable(true);
		f_inputFiles.setShowRoot(false);
		
		f_inputFileRoot = new InputFileTreeItem("");
		f_inputFiles.setRoot(f_inputFileRoot);
		f_inputFiles.setCellFactory(e -> {
			TreeCell<String> cell = CheckBoxTreeCell.<String>forTreeView().call(e);
			
			//make sure items know focused state so they can change color slightly
			cell.focusedProperty().addListener(
							(ObservableValue<? extends Boolean> value,
								Boolean oldValue, Boolean newValue) -> {
										InputFileTreeItem item = f_activeInputItems.get(cell);
										if (item != null)
											item.setFocused(newValue.booleanValue());
										
										//change the focus state without looking up perfectly current
										//state/progress information
										updateInputCellStyle(cell);
									});

			//update active cell map when a cell changes its item
			cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {
				f_activeInputItems.put(cell, (InputFileTreeItem) newItem);
				
				//update this specific cell now
				f_controller.updateInputItem((InputFileTreeItem) newItem);
				updateInputCellStyle(cell);
			});

			return cell;
		});
		vbox.getChildren().add(f_inputFiles);
		
		HBox buttonRow1 = new HBox();
		buttonRow1.setSpacing(10);
		buttonRow1.setAlignment(Pos.CENTER);
		buttonRow1.setPadding(new Insets(10,10,10,10));
		
		//add file button
		f_inputAddFileButton = new Button("Add File");
		f_inputAddFileButton.setOnAction(e -> f_controller.inputAddFilePressed());
		buttonRow1.getChildren().add(f_inputAddFileButton);
		
		//add folder button
		f_inputAddFolderButton = new Button("Add Folder");
		f_inputAddFolderButton.setOnAction(e -> f_controller.inputAddFolderPressed());
		buttonRow1.getChildren().add(f_inputAddFolderButton);
		
		//remove entry
		f_inputRemoveButton = new Button("Remove Input");
		f_inputRemoveButton.setOnAction(e -> f_controller.removeInputPressed());
		buttonRow1.getChildren().add(f_inputRemoveButton);
		vbox.getChildren().add(buttonRow1);

		return vbox;
	}
	
	/**
	 * Updates the input cell style according to its associated item.
	 * @param p_cell The cell to update
	 */
	public void updateInputCellStyle(TreeCell<String> p_cell)
	{
		InputFileTreeItem item = f_activeInputItems.get(p_cell);
		if (item == null)
		{
			//cell is blank, remove progress bar
			p_cell.setStyle(null);
		}
		else
		{
			//set the cell style according to the item's status and progress
			item.setCellStyle(p_cell);
		}
	}
	
	/**
	 * Updates the target cell style according to its associated item.
	 * @param p_cell The cell to update
	 */
	public void updateTargetCellStyle(TreeCell<String> p_cell)
	{
		TargetFileTreeItem item = f_activeTargetItems.get(p_cell);
		if (item == null)
		{
			//cell is blank, remove progress bar
			p_cell.setStyle(null);
		}
		else
		{
			//set the cell style according to the item's status and progress
			item.setCellStyle(p_cell);
		}
	}
	
	/**
	 * Updates the css of all cells in the input or target section which are
	 * visible/active. The cells which fall under this category are determined by javafx.
	 */
	public void updateAllActiveCells()
	{
		if (!f_updatingCells)
		{
			f_updatingCells = true;
			
			Platform.runLater(() -> {
				//input cells
				for (TreeCell<String> cell : f_activeInputItems.keySet())
					updateInputCellStyle(cell);
				
				//target cells
				for (TreeCell<String> cell : f_activeTargetItems.keySet())
					updateTargetCellStyle(cell);

				f_updatingCells = false;
			});
		}
	}

	/**
	 * Creates the target section
	 * @return The target section node
	 */
	private Node setupTargetFileSection()
	{
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(10,10,10,10));
		
		//target files label
		f_targetFilesLabel = new Label("Target Files:");
		vbox.getChildren().add(f_targetFilesLabel);
				
		//input file tree
		f_targetFiles = new TreeView<String>();
		f_targetFiles.setEditable(true);
		f_targetFiles.setShowRoot(false);
		
		f_targetFileRoot = new TargetFileTreeItem("");
		f_targetFiles.setRoot(f_targetFileRoot);
		f_targetFiles.setCellFactory(e -> 
		{
			TreeCell<String> cell = new TreeCell<String>()
			{
				@Override
				public void updateItem(String item, boolean empty)
				{
					super.updateItem(item, empty);
					
					if (empty)
						setText(null);
					else
						setText(item);
				}
			};
			
			//make sure items know focused state so they can change color slightly
			cell.focusedProperty().addListener(
				(ObservableValue<? extends Boolean> value,
					Boolean oldValue, Boolean newValue) -> {
							TargetFileTreeItem item = f_activeTargetItems.get(cell);
							if (item != null)
								item.setFocused(newValue.booleanValue());
							
							//change the focus state without looking up perfectly current
							//state/progress information
							updateTargetCellStyle(cell);
						});
			
			//update active cell map when a cell changes its item
			cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {
				f_activeTargetItems.put(cell, (TargetFileTreeItem) newItem);
				
				//update this specific cell now
				f_controller.updateTargetItem((TargetFileTreeItem) newItem);
				updateTargetCellStyle(cell);
			});

			return cell;
		});
		vbox.getChildren().add(f_targetFiles);
		
		HBox buttonRow1 = new HBox();
		buttonRow1.setSpacing(3);
		buttonRow1.setAlignment(Pos.CENTER);
		buttonRow1.setPadding(new Insets(10,10,10,10));
		
		//select folder button
		f_targetSelectFolderButton = new Button("Select Folder");
		f_targetSelectFolderButton.setOnAction(e -> f_controller.targetSelectFolderPressed());
		buttonRow1.getChildren().add(f_targetSelectFolderButton);
		vbox.getChildren().add(buttonRow1);
		
		return vbox;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getTabName()
	 */
	@Override
	public String getTabName()
	{
		return "Archive Creator";
	}

	/**
	 * Creates the algorithm configuration section
	 * @return The configuration section node
	 */
	private Node setupConfigSelection()
	{
		VBox configSelection = new VBox();
		configSelection.setSpacing(3);
		configSelection.setPadding(new Insets(14,10,20,20));

		//insertion configuration label
		Label insertionConfigurationLabel = new Label("Insertion Configuration");
		insertionConfigurationLabel.setFont(new Font("Arial", 20));
		insertionConfigurationLabel.setPadding(new Insets(0, 0, 10, 0));
		configSelection.getChildren().add(insertionConfigurationLabel);
		
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
		
		//output folder
		f_outputFolder = new FileModule("Output Folder", e -> f_controller.browseOutputFolder());
		f_outputFolder.setup(configSelection);
		
		//structured output
		f_structuredOutput = new BooleanModule("Use Structured Output Folders?",
						b -> f_controller.structuredOutputChecked(b));
		f_structuredOutput.setup(configSelection);
		
		
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
		
		//run creation
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		
		f_createArchivesButton = new Button("Create Archives");
		f_createArchivesButton.setOnAction(e -> f_controller.createArchivesPressed());
		hbox.getChildren().add(f_createArchivesButton);
		configSelection.getChildren().add(hbox);

		return configSelection;
	}
	
	/**
	 * Indents an element by wrapping it in an hbox off center. Utility method
	 * used to give a constant indent to everything.
	 * @param p_indent The indent to use
	 * @param p_element The element to wrap
	 * @return The hbox containing the method
	 */
	private HBox indentElement(int p_indent, Node p_element)
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
	 * Sets the enabled state of the key section
	 * @param p_enabled The enabled state
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
	 * Enables/disables the correct sections when the key type. Called by javafx
	 * when the selection changes.
	 * @param p_value the observable value
	 * @param p_oldSelection The old toggle selection
	 * @param p_newSelection The new toggle selection
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
		}
		else //assumed file toggled
		{
			setPasswordSectionEnabled(false);
			setKeyFileSectionEnabled(true);
		}
	}

	/**
	 * Sets the enabled state of the key file selection
	 * @param p_enabled The enabled state
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
	 * Sets the enabled state of the password section
	 * @param p_enabled The enabled state
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
	 * Clears the text of the password field
	 */
	public void clearPasswordSection()
	{
		f_passwordField.clear();
	}

	/**
	 * Clears the key file section of input
	 */
	public void clearKeyFileSection()
	{
		f_keyFilePath.clear();
	}
	
	/**
	 * Clears the key file and password fields of input
	 */
	void clearKeySection()
	{
		clearPasswordSection();
		clearKeyFileSection();
	}
	
	/**
	 * Sets the selected algorithm preset
	 * @param p_presetName The preset name to select
	 */
	public void setAlgorithmSelection(String p_presetName)
	{
		f_algorithmSelect.setValue(p_presetName);
	}
	
	/**
	 * Sets the list of algorithm presets
	 * @param p_presetNames The list of preset names
	 */
	public void setAlgorithmPresets(List<String> p_presetNames)
	{
		f_algorithmSelect.setItems(FXCollections.observableArrayList(p_presetNames));
	}
	
	/**
	 * Sets the enabled state of the algorithm selection section
	 * @param p_enabled The state to set
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
	 * Adds an input file or folder
	 * @param p_inputFile The input file or folder
	 */
	public void addInput(ArchiveFile p_inputFile)
	{
		final InputFileTreeItem item = new InputFileTreeItem(p_inputFile);
		item.setSelected(true);
		
		f_inputFileRoot.getChildren().add(item);
	}
	
	/**
	 * Sets the target folder and updates the tree
	 * @param p_targetFolder The target folder to set
	 */
	public void setTarget(File p_targetFolder)
	{
		final TargetFileTreeItem item = new TargetFileTreeItem(p_targetFolder);
		
		f_targetFileRoot.getChildren().clear();
		f_targetFileRoot.getChildren().add(item);
	}
	
	/**
	 * Sets the enabled state of the target section
	 * @param p_enabled The state to set
	 */
	public void setTargetSectionEnabled(boolean p_enabled)
	{
		f_targetFiles.disableProperty().set(!p_enabled);
		f_targetSelectFolderButton.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_targetFilesLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			f_targetFilesLabel.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * Sets enabled state of the input section
	 * @param p_enabled The state to set
	 */
	public void setInputSectionEnabled(boolean p_enabled)
	{
		f_inputFiles.disableProperty().set(!p_enabled);
		f_inputAddFileButton.disableProperty().set(!p_enabled);
		f_inputAddFolderButton.disableProperty().set(!p_enabled);
		f_inputRemoveButton.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			f_inputFilesLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			f_inputFilesLabel.setStyle("-fx-opacity: 1");
		}
	}
	
	/**
	 * Sets the enabled state of the create archives button
	 * @param p_enabled The state to set
	 */
	public void setCreateArchivesEnabled(boolean p_enabled)
	{
		f_createArchivesButton.disableProperty().set(!p_enabled);
	}

	/**
	 * Selects the key file section toggle
	 */
	public void toggleKeySection()
	{
		f_keySelectionButtons.selectToggle(f_keyFileToggle);
	}
	
	/**
	 * Selects the password section toggle
	 */
	public void togglePasswordSection()
	{
		f_keySelectionButtons.selectToggle(f_passwordToggle);
	}
	
	/**
	 * Sets the key file path
	 * @param p_path The path to set
	 */
	public void setKeyFilePath(String p_path)
	{
		f_keyFilePath.setText(p_path);
	}
	
	/**
	 * Tells if the key file section is enabled
	 * @return True if it is enabled
	 */
	public boolean keyFileEnabled()
	{
		return !f_keyFilePath.disableProperty().get();
	}

	/**
	 * Tells if the password section is enabled
	 * @return True if it is enabled
	 */
	public boolean passwordEnabled()
	{
		return !f_passwordField.disableProperty().get();
	}
	
	/**
	 * Gets the key file path entered in the key file text field
	 * @return The key file path
	 */
	public String getKeyFilePath()
	{
		return f_keyFilePath.getText();
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	public String getPassword()
	{
		return f_passwordField.getText();
	}
	
	/**
	 * Gets the map of tree cells to tree items which are the active
	 * input cells/items in the view
	 * @return The map of active input files
	 */
	public Map<TreeCell<String>, InputFileTreeItem> getActiveInputItems()
	{
		return f_activeInputItems;
	}
	
	/**
	 * Gets the map of tree cells to tree items which are the active
	 * target cells/items in the view
	 * @return The map of active target files
	 */
	public Map<TreeCell<String>, TargetFileTreeItem> getActiveTargetItems()
	{
		return f_activeTargetItems;
	}
	
	/**
	 * Sets the number of files created
	 * @param p_number The number of files created
	 */
	public void setFilesCreated(int p_number)
	{
		Platform.runLater(() -> f_filesCreatedLabel.setText(s_filesCreatedString + p_number));
	}
	
	/**
	 * Sets the creation progress bar to a new value
	 * @param p_progress The progress of the creation job [0, 1]
	 */
	public void setCreationProgress(double p_progress)
	{
		Platform.runLater(() -> f_creationProgress.setProgress(p_progress));
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getEnclosingFolder()
	 */
	@Override
	public File getEnclosingFolder()
	{
		//not used in embed
		return null;
	}

	/**
	 * Sets the output folder displayed in the gui
	 * @param p_folder The output folder
	 */
	public void setOutputFolder(File p_folder)
	{
		f_outputFolder.setPath(p_folder.getAbsolutePath());
	}

	/**
	 * Gets the output folder as displayed in the output folder field
	 * @return The current output folder path
	 */
	public File getOutputFolder()
	{
		String path = f_outputFolder.getPath();
		
		if (path == null || path.isEmpty())
			return null;
		else
			return new File(path);
	}

	/**
	 * Removes the selected input from the list of inputs. The selected input must
	 * be a top level entry.
	 */
	public void removeSelectedInput()
	{
		TreeItem<String> selected = f_inputFiles.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			if (f_inputFileRoot.getChildren().contains(selected))
			{
				f_inputFileRoot.getChildren().remove(selected);
			}
			else
			{
				Logger.log(LogLevel.k_error, "You may only remove top level entries. "
								+ "This is equivalent to unchecking them.");
			}
		}
	}

	/**
	 * Gets the list of input files derived from the input file tree
	 * and the checked state of the tree items.
	 * @return The list of input files
	 */
	public List<ArchiveFile> getInputFileList()
	{
		Set<ArchiveFile> files = new HashSet<ArchiveFile>();
		
		for (TreeItem<String> child : f_inputFileRoot.getChildren())
		{
			collectInputFiles(files, (InputFileTreeItem) child);
		}
		
		return new ArrayList<ArchiveFile>(files);
	}

	/**
	 * Collects the set of input files by traversing the input file tree and
	 * paying attention to the checked state of each node. Since folders are
	 * allowed in the input file list, you only need to traverse the tree until you
	 * find something checked or not checked.
	 * @param p_files The set of files to add to
	 * @param p_parent The current tree item representing a parent file
	 */
	private void collectInputFiles(Set<ArchiveFile> p_files, InputFileTreeItem p_parent)
	{
		if (p_parent.isIndeterminate())
		{
			//parent is not selected, but some children are selected or indeterminate
			//(must be a folder and must have been expanded)
			for (TreeItem<String> child : p_parent.getChildren())
			{
				collectInputFiles(p_files, (InputFileTreeItem) child);
			}
		}
		else if (p_parent.isSelected())
		{
			//parent is selected
			//(file or folder, just add it)
			p_files.add(p_parent.getFile());
		}
		
		//else: unselected, don't add
	}
	
}
