package ui.graphical.embed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
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
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logging.LogLevel;
import logging.Logger;
import product.FileContents;
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
	private final String noKeyToggleString = "No Key";
	private final String passwordToggleString = "Password";
	private final String keyFileToggleString = "Key File";
	private final String filesCreatedString = "Total output files created: ";
	private final String estimatedOutputSizeString = "Estimated total output file size: ";
	private final InputFileTreeItem tempExpandableChildItem = new InputFileTreeItem("Loading...");
	
	//gui elements
	private ChoiceBox<String> algorithmSelect;
	private PasswordField passwordField;
	private TextField keyFilePath;
	private Button keyFileBrowseButton, inputAddFileButton, inputAddFolderButton,
		inputRemoveButton, targetSelectFolderButton, runConversionButton;
	private ToggleGroup keySelectionButtons;
	private RadioButton noKeyToggle, keyFileToggle, passwordToggle;
	private TableView<FileContentsTableRecord> table;
	private Label passwordLabel, keyFileLabel, keySelectionLabel, algorithmLabel,
		estimatedOutputSizeLabel, filesCreatedLabel;
	private FileProperty outputFolder;
	private BooleanProperty structuredOutput;
	private TreeView<String> inputFiles, targetFiles;
	private ProgressBar conversionProgress;
	private InputFileTreeItem inputFileRoot;
	private TargetFileTreeItem targetFileRoot;
	
	//controller
	private EmbedController controller;
	
	private Map<TreeCell<String>, InputFileTreeItem> activeInputCells;
	private Map<TreeCell<String>, TargetFileTreeItem> activeTargetCells;

	public EmbedView(Stage window)
	{
		super(window);
		
		controller = new EmbedController(this);
		activeInputCells = new HashMap<TreeCell<String>, InputFileTreeItem>();
		activeTargetCells = new HashMap<TreeCell<String>, TargetFileTreeItem>();
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
		//borderPane.setBottom(setupProgressSection());
		
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
		conversionProgress = new ProgressBar(.75);
		conversionProgress.setPrefWidth(230);
		hbox.getChildren().add(conversionProgress);

		//files created label
		filesCreatedLabel = new Label("");
		setFilesCreated(0);
		hbox.getChildren().add(filesCreatedLabel);
		
		
		return hbox;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupFileSection()
	{
		HBox hbox = new HBox();
		hbox.setSpacing(3);
		hbox.setPadding(new Insets(10,10,10,10));
		
		hbox.getChildren().add(setupInputFileSection());
		hbox.getChildren().add(setupTargetFileSection());
		
		
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
		
		//output size label
		estimatedOutputSizeLabel = new Label("");
		setEstimatedOutputSize(0);
		vbox.getChildren().add(estimatedOutputSizeLabel);
		
		//input file tree
		inputFiles = new TreeView<String>();
		inputFiles.setEditable(true);
		inputFiles.setShowRoot(false);
		
		inputFileRoot = new InputFileTreeItem("");
		inputFiles.setRoot(inputFileRoot);
		inputFiles.setCellFactory(e -> {
			TreeCell<String> cell = CheckBoxTreeCell.<String>forTreeView().call(e);

			//update active cell map when a cell changes its item
			cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {
				activeInputCells.put(cell, (InputFileTreeItem) newItem);
				
				//update this specific cell now
				updateInputCellStyle(cell);
			});

			return cell;
		});
		vbox.getChildren().add(inputFiles);
		
		HBox buttonRow1 = new HBox();
		buttonRow1.setSpacing(10);
		buttonRow1.setAlignment(Pos.CENTER);
		buttonRow1.setPadding(new Insets(10,10,10,10));
		
		//add file button
		inputAddFileButton = new Button("Add File");
		inputAddFileButton.setOnAction(e -> controller.inputAddFilePressed());
		buttonRow1.getChildren().add(inputAddFileButton);
		
		//add folder button
		inputAddFolderButton = new Button("Add Folder");
		inputAddFolderButton.setOnAction(e -> controller.inputAddFolderPressed());
		buttonRow1.getChildren().add(inputAddFolderButton);
		
		//remove entry
		inputRemoveButton = new Button("Remove Input");
		inputRemoveButton.setOnAction(e -> controller.removeInputPressed());
		buttonRow1.getChildren().add(inputRemoveButton);
		vbox.getChildren().add(buttonRow1);
		
		vbox.getChildren().add(setupProgressSection());
		
		return vbox;
	}
	
	public void updateInputCellStyle(TreeCell<String> cell)
	{
		InputFileTreeItem item = activeInputCells.get(cell);
		if (item == null)
		{
			//cell is blank, remove progress bar
			cell.setStyle(null);
		}
		else
		{
			//set the cell style according to the item's status and progress
			item.setCellStyle(cell);
		}
	}
	
	public void updateTargetCellStyle(TreeCell<String> cell)
	{
		TargetFileTreeItem item = activeTargetCells.get(cell);
		if (item == null)
		{
			//cell is blank, remove progress bar
			cell.setStyle(null);
		}
		else
		{
			//set the cell style according to the item's status and progress
			item.setCellStyle(cell);
		}
	}
	
	public void updateAllActiveCells()
	{
		//input cells
		for (TreeCell<String> cell : activeInputCells.keySet())
			updateInputCellStyle(cell);
		
		//target cells
		for (TreeCell<String> cell : activeTargetCells.keySet())
			updateTargetCellStyle(cell);
	}
	
	//TODO remove
	public void setAll(double set)
	{
		for (InputFileTreeItem item : activeInputCells.values())
		{
			if (item != null)
			{
				item.setProgress(set);
			}
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
		
		//input file tree
		targetFiles = new TreeView<String>();
		targetFiles.setEditable(true);
		targetFiles.setShowRoot(false);
		
		targetFileRoot = new TargetFileTreeItem("");
		targetFiles.setRoot(targetFileRoot);
		targetFiles.setCellFactory(e -> 
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
			
			//update active cell map when a cell changes its item
			cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {
				activeTargetCells.put(cell, (TargetFileTreeItem) newItem);
				
				//update this specific cell now
				updateTargetCellStyle(cell);
			});

			return cell;
		});
		vbox.getChildren().add(targetFiles);
		
		HBox buttonRow1 = new HBox();
		buttonRow1.setSpacing(3);
		buttonRow1.setAlignment(Pos.CENTER);
		buttonRow1.setPadding(new Insets(10,10,10,10));
		
		//select folder button
		targetSelectFolderButton = new Button("Select Folder");
		targetSelectFolderButton.setOnAction(e -> controller.targetSelectFolderPressed());
		buttonRow1.getChildren().add(targetSelectFolderButton);
		vbox.getChildren().add(buttonRow1);
		
		return vbox;
	}

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
		algorithmLabel = new Label("Algorithm:");
		configSelection.getChildren().add(algorithmLabel);
		
		//algorithm select
		algorithmSelect = new ChoiceBox<>();
		setAlgorithmPresets(controller.getPresetNames());
		algorithmSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											controller.algorithmSelected(newIndex.intValue()));
		algorithmSelect.focusedProperty().addListener(
						(ObservableValue<? extends Boolean> value,
										Boolean oldValue, Boolean newValue) ->
											controller.algorithmSelectFocus(newValue.booleanValue()));
		configSelection.getChildren().add(indentElement(1, algorithmSelect));
		
		//output folder
		outputFolder = new FileProperty("Output Folder", e -> controller.browseOutputFolder());
		outputFolder.setup(configSelection);
		
		//structured output
		structuredOutput = new BooleanProperty("Use Structured Output Folders?",
						b -> controller.structuredOutputChecked(b));
		structuredOutput.setup(configSelection);
		
		
		//key selection label
		keySelectionLabel = new Label("Key Selection:");
		configSelection.getChildren().add(keySelectionLabel);
		
		//key selection buttons
		keySelectionButtons = new ToggleGroup();
		
		noKeyToggle = new RadioButton(noKeyToggleString);
		noKeyToggle.setToggleGroup(keySelectionButtons);
		noKeyToggle.setUserData(noKeyToggleString);
		keyFileToggle = new RadioButton(keyFileToggleString);
		keyFileToggle.setToggleGroup(keySelectionButtons);
		keyFileToggle.setUserData(keyFileToggleString);
		passwordToggle = new RadioButton(passwordToggleString);
		passwordToggle.setUserData(passwordToggleString);
		passwordToggle.setToggleGroup(keySelectionButtons);
		keySelectionButtons.selectToggle(noKeyToggle);
		keySelectionButtons.selectedToggleProperty().addListener(
						(ObservableValue<? extends Toggle> value,
										Toggle oldSelection, Toggle newSelection) ->
											keyTypeSelected(value, oldSelection, newSelection));
		
		HBox radioBox = new HBox();
		radioBox.setSpacing(10);
		radioBox.getChildren().addAll(noKeyToggle, passwordToggle, keyFileToggle);
		configSelection.getChildren().add(indentElement(1, radioBox));
		
		//password label
		passwordLabel = new Label("Password:");
		configSelection.getChildren().add(passwordLabel);
		
		//password text field
		passwordField = new PasswordField();
		configSelection.getChildren().add(indentElement(1, passwordField));
		
		//key file path label
		keyFileLabel = new Label("Key File Path:");
		configSelection.getChildren().add(keyFileLabel);
		
		//key file path
		keyFilePath = new TextField();
		keyFilePath.setEditable(false);
		
		//key file browse button
		keyFileBrowseButton = new Button();
		keyFileBrowseButton.setText("Browse");
		keyFileBrowseButton.setOnAction(e -> controller.chooseKeyFile());
		HBox keyFilePathRow = indentElement(1, keyFilePath);
		keyFilePathRow.getChildren().add(keyFileBrowseButton);
		configSelection.getChildren().add(keyFilePathRow);
		
		//run conversion
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		
		runConversionButton = new Button("Run Conversion");
		runConversionButton.setOnAction(e -> controller.runConversionPressed());
		hbox.getChildren().add(runConversionButton);
		configSelection.getChildren().add(hbox);

		return configSelection;
	}
	
	private HBox indentElement(int indent, Node element)
	{
		String indentation = "    ";
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(10, 0, 10, 0));
		hbox.setSpacing(10);
		Label space = new Label(new String(new char[indent]).replace("\0", indentation));
		hbox.getChildren().addAll(space, element);
		return hbox;
	}

	void setKeySectionEnabled(boolean enabled)
	{
		System.out.println("Key section enabled: " + enabled);
		
		noKeyToggle.disableProperty().set(!enabled);
		keyFileToggle.disableProperty().set(!enabled);
		passwordToggle.disableProperty().set(!enabled);
		keySelectionLabel.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			noKeyToggle.setStyle("-fx-opacity: .75");
			keyFileToggle.setStyle("-fx-opacity: .75");
			passwordToggle.setStyle("-fx-opacity: .75");
			keySelectionLabel.setStyle("-fx-opacity: .5");
			
			//disable everything
			setKeyFileSectionEnabled(false);
			setPasswordSectionEnabled(false);
		}
		else
		{
			noKeyToggle.setStyle("-fx-opacity: 1");
			keyFileToggle.setStyle("-fx-opacity: 1");
			passwordToggle.setStyle("-fx-opacity: 1");
			keySelectionLabel.setStyle("-fx-opacity: 1");

			//enable only the selected one
			if (keySelectionButtons.getSelectedToggle().getUserData().equals(keyFileToggleString))
			{
				setKeyFileSectionEnabled(true);
				setPasswordSectionEnabled(false);
			}
			else if (keySelectionButtons.getSelectedToggle().getUserData().equals(passwordToggleString))
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
	 * @param value
	 * @param oldSelection
	 * @param newSelection
	 * @return
	 */
	private void keyTypeSelected(ObservableValue<? extends Toggle> value,
					Toggle oldSelection, Toggle newSelection)
	{
		System.out.println("Key type selected: " + newSelection.getUserData());
		if (newSelection.getUserData().equals(noKeyToggleString))
		{
			setPasswordSectionEnabled(false);
			setKeyFileSectionEnabled(false);
		}
		else if (newSelection.getUserData().equals(passwordToggleString))
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
	 */
	void clearTable()
	{
		table.setItems(null);
	}

	/**
	 * @update_comment
	 * @param enabled
	 */
	private void setKeyFileSectionEnabled(boolean enabled)
	{
		System.out.println("Key file section enabled: " + enabled);
		
		keyFilePath.disableProperty().set(!enabled);
		keyFileBrowseButton.disableProperty().set(!enabled);
		keyFileLabel.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			keyFilePath.setStyle("-fx-opacity: .75");
			keyFileBrowseButton.setStyle("-fx-opacity: .75");
			keyFileLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			keyFilePath.setStyle("-fx-opacity: 1");
			keyFileBrowseButton.setStyle("-fx-opacity: 1");
			keyFileLabel.setStyle("-fx-opacity: 1");
		}
		
	}

	/**
	 * @update_comment
	 * @param b
	 */
	private void setPasswordSectionEnabled(boolean enabled)
	{
		System.out.println("Password section enabled: " + enabled);
		
		passwordField.disableProperty().set(!enabled);
		passwordLabel.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			passwordField.setStyle("-fx-opacity: .75");
			passwordLabel.setStyle("-fx-opacity: .5");
		}
		else
		{
			passwordField.setStyle("-fx-opacity: 1");
			passwordLabel.setStyle("-fx-opacity: 1");
		}
	}
	
	public void clearPasswordSection()
	{
		passwordField.clear();
	}

	public void clearKeyFileSection()
	{
		keyFilePath.clear();
	}
	
	void clearKeySection()
	{
		clearPasswordSection();
		clearKeyFileSection();
	}
	
	public void setAlgorithmSelection(String presetName)
	{
		algorithmSelect.setValue(presetName);
	}
	
	public void setAlgorithmPresets(List<String> presetNames)
	{
		algorithmSelect.setItems(FXCollections.observableArrayList(presetNames));
	}
	
	public String getAlgorithmSelection()
	{
		return (String) algorithmSelect.getValue();
	}
	
	
	public void setAlgorithmSelectionEnabled(boolean enabled)
	{
		algorithmSelect.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			algorithmSelect.setStyle("-fx-opacity: .75");
		}
		else
		{
			algorithmSelect.setStyle("-fx-opacity: 1");
		}
	}
	
	public void setPasswordPrompt(String prompt)
	{
		passwordField.setPromptText(prompt);
	}
	
	public void addInput(File inputFile)
	{
		final InputFileTreeItem item = new InputFileTreeItem(inputFile);
		item.setStatus(new Random().nextInt(5));//TODO remove
		item.setSelected(true);
		
		inputFileRoot.getChildren().add(item);
	}
	
	public void setTarget(File targetFolder)
	{
		final TargetFileTreeItem item = new TargetFileTreeItem(targetFolder);
		item.setStatus(new Random().nextInt(5));//TODO remove
		
		targetFileRoot.getChildren().clear();
		targetFileRoot.getChildren().add(item);
	}
	
	public void clearPasswordPrompt()
	{
		passwordField.setPromptText("");
	}
	
	public void toggleKeySection()
	{
		keySelectionButtons.selectToggle(keyFileToggle);
	}
	
	public void togglePasswordSection()
	{
		keySelectionButtons.selectToggle(passwordToggle);
	}
	
	public void setKeyFilePath(String path)
	{
		keyFilePath.setText(path);
	}
	
	public boolean keyFileEnabled()
	{
		return !keyFilePath.disableProperty().get();
	}

	public boolean passwordEnabled()
	{
		return !passwordField.disableProperty().get();
	}
	
	public String getKeyFilePath()
	{
		return keyFilePath.getText();
	}
	
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	public void setFilesCreated(int number)
	{
		filesCreatedLabel.setText(filesCreatedString + number);
	}
	
	public void setEstimatedOutputSize(long kb)
	{
		estimatedOutputSizeLabel.setText(estimatedOutputSizeString + kb + " KB");
	}

	/**
	 * @update_comment
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Integer> getSelectedRows()
	{
		List<Integer> indices = new LinkedList<Integer>();
		
		ObservableList<TablePosition> selectedRows = table.getSelectionModel().getSelectedCells();
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
		return chooseFolder();
	}

	/**
	 * @update_comment
	 * @param folder
	 */
	public void setOutputFolder(File folder)
	{
		outputFolder.setPath(folder.getAbsolutePath());
	}

	public File getOutputFolder()
	{
		String path = outputFolder.getPath();
		
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
		TreeItem<String> selected = inputFiles.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			if (inputFileRoot.getChildren().contains(selected))
			{
				inputFileRoot.getChildren().remove(selected);
			}
			else
			{
				Logger.log(LogLevel.k_error, "You may only remove top level entries. "
								+ "This is equivalent to unchecking them.");
			}
		}
	}
	
}
