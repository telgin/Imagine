package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
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
import ui.graphical.BooleanProperty;
import ui.graphical.FileProperty;
import ui.graphical.View;
import ui.graphical.archiveviewer.FileContentsTableRecord;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
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
		f_inputRemoveButton, f_targetSelectFolderButton, f_runConversionButton;
	private ToggleGroup f_keySelectionButtons;
	private RadioButton f_noKeyToggle, f_keyFileToggle, f_passwordToggle;
	private TableView<FileContentsTableRecord> f_table;
	private Label f_passwordLabel, f_keyFileLabel, f_keySelectionLabel, f_algorithmLabel,
		f_inputFilesLabel, f_targetFilesLabel, f_filesCreatedLabel;
	private FileProperty f_outputFolder;
	private BooleanProperty f_structuredOutput;
	private TreeView<String> f_inputFiles, f_targetFiles;
	private ProgressBar f_conversionProgress;
	private InputFileTreeItem f_inputFileRoot;
	private TargetFileTreeItem f_targetFileRoot;
	
	//state fields
	private EmbedController f_controller;
	private ArgParseResult f_args;
	private Map<TreeCell<String>, InputFileTreeItem> f_activeInputItems;
	private Map<TreeCell<String>, TargetFileTreeItem> f_activeTargetItems;
	boolean f_updatingCells = false;

	public EmbedView(Stage p_window, ArgParseResult p_args)
	{
		super(p_window);
		
		f_args = p_args;
		f_controller = new EmbedController(this);
		f_activeInputItems = new HashMap<TreeCell<String>, InputFileTreeItem>();
		f_activeTargetItems = new HashMap<TreeCell<String>, TargetFileTreeItem>();
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
		borderPane.setRight(setupTargetFileSection());
		borderPane.setCenter(setupInputFileSection());
		borderPane.setBottom(setupProgressSection());
		
		f_keySelectionButtons.selectToggle(f_noKeyToggle);
		setInputSectionEnabled(false);
		setTargetSectionEnabled(false);
		setRunConversionEnabled(false);
		
		//setup stuff specified in args
		if (f_args.action == CmdAction.k_embed)
		{
			for (File input : f_args.inputFiles)
				if (input.exists())
					addInput(input);
			
			if (f_args.presetName != null && f_controller.getPresetNames().contains(f_args.presetName))
			{
				setAlgorithmSelection(f_args.presetName);
			}
			
			if (f_args.outputFolder != null && f_args.outputFolder.exists())
			{
				setOutputFolder(f_args.outputFolder);
			}
			
			if (f_args.keyFile != null && f_args.keyFile.exists())
			{
				setKeyFilePath(f_args.keyFile.getAbsolutePath());
				toggleKeySection();
			}
			
			if (f_args.usingPassword)
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
	 * @update_comment
	 * @return
	 */
	private Node setupProgressSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(10,10,10,10));
		hbox.setAlignment(Pos.CENTER);
		
		//progress bar
		f_conversionProgress = new ProgressBar(0);
		f_conversionProgress.setPrefWidth(230);
		hbox.getChildren().add(f_conversionProgress);

		//files created label
		f_filesCreatedLabel = new Label("");
		setFilesCreated(0);
		hbox.getChildren().add(f_filesCreatedLabel);
		
		
		return hbox;
	}

	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @param p_cell
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
	 * @update_comment
	 * @param p_cell
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
	 * @update_comment
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
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @return
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
		f_outputFolder = new FileProperty("Output Folder", e -> f_controller.browseOutputFolder());
		f_outputFolder.setup(configSelection);
		
		//structured output
		f_structuredOutput = new BooleanProperty("Use Structured Output Folders?",
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
		
		//run conversion
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		
		f_runConversionButton = new Button("Run Conversion");
		f_runConversionButton.setOnAction(e -> f_controller.runConversionPressed());
		hbox.getChildren().add(f_runConversionButton);
		configSelection.getChildren().add(hbox);

		return configSelection;
	}
	
	/**
	 * @update_comment
	 * @param p_indent
	 * @param p_element
	 * @return
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
		}
		else //assumed file toggled
		{
			setPasswordSectionEnabled(false);
			setKeyFileSectionEnabled(true);
		}
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
	 * @param p_enabled
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
	 * @param p_inputFile
	 */
	public void addInput(File p_inputFile)
	{
		final InputFileTreeItem item = new InputFileTreeItem(p_inputFile);
		item.setSelected(true);
		
		f_inputFileRoot.getChildren().add(item);
	}
	
	/**
	 * @update_comment
	 * @param p_targetFolder
	 */
	public void setTarget(File p_targetFolder)
	{
		final TargetFileTreeItem item = new TargetFileTreeItem(p_targetFolder);
		
		f_targetFileRoot.getChildren().clear();
		f_targetFileRoot.getChildren().add(item);
	}
	
	/**
	 * @update_comment
	 * @param p_enabled
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
	 * @update_comment
	 * @param p_enabled
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
	 * @update_comment
	 * @param p_enabled
	 */
	public void setRunConversionEnabled(boolean p_enabled)
	{
		f_runConversionButton.disableProperty().set(!p_enabled);
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
	public String getPassword()
	{
		return f_passwordField.getText();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public Map<TreeCell<String>, InputFileTreeItem> getActiveInputItems()
	{
		return f_activeInputItems;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public Map<TreeCell<String>, TargetFileTreeItem> getActiveTargetItems()
	{
		return f_activeTargetItems;
	}
	
	/**
	 * @update_comment
	 * @param p_number
	 */
	public void setFilesCreated(int p_number)
	{
		Platform.runLater(() -> f_filesCreatedLabel.setText(s_filesCreatedString + p_number));
	}
	
	/**
	 * @update_comment
	 * @param progress
	 */
	public void setConversionProgress(double progress)
	{
		Platform.runLater(() -> f_conversionProgress.setProgress(progress));
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
			TablePosition position = (TablePosition) selectedRows.get(x);
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
		//not used in embed
		return null;
	}

	/**
	 * @update_comment
	 * @param p_folder
	 */
	public void setOutputFolder(File p_folder)
	{
		f_outputFolder.setPath(p_folder.getAbsolutePath());
	}

	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
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
	 * @update_comment
	 * @return
	 */
	public List<File> getInputFileList()
	{
		Set<File> files = new HashSet<File>();
		
		for (TreeItem<String> child : f_inputFileRoot.getChildren())
		{
			collectInputFiles(files, (InputFileTreeItem) child);
		}
		
		return new ArrayList<File>(files);
	}

	/**
	 * @update_comment
	 * @param p_files
	 * @param p_parent
	 */
	private void collectInputFiles(Set<File> p_files, InputFileTreeItem p_parent)
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
