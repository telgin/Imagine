package ui.graphical.archiveviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import config.Constants;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import product.FileContents;
import ui.graphical.ScrollAlert;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class OpenArchiveView extends View
{
	//constants
	private final String passwordToggleString = "Password";
	private final String keyFileToggleString = "Key File";
	
	//gui elements
	private ChoiceBox<String> profileSelect, algorithmSelect;
	private PasswordField passwordField;
	private TextField keyFilePath;
	private Button keyFileBrowseButton, openButton, extractSelectedButton, extractAllButton;
	private ToggleGroup keySelectionButtons;
	private RadioButton keyFileToggle, passwordToggle;
	private TableView<FileContentsTableRecord> table;
	private Label passwordLabel, keyFileLabel, keySelectionLabel, algorithmLabel, profileLabel;
	
	//controller
	private OpenArchiveController controller;

	public OpenArchiveView(Stage window, File productFile)
	{
		super(window);
		
		controller = new OpenArchiveController(this, productFile);
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

		//set the temporary profile as selected by default
		profileSelect.setValue(controller.getDefaultProfileSelection());
		
		return borderPane;
	}
	
	@Override
	public String getTabName()
	{
		return "Archive Viewer";
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
		
		//file name label
		Label fileNameLabel = new Label("File: " + controller.getInputFile().getName());
		vbox.getChildren().add(fileNameLabel);

		//scroll pane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		
		//table
		table = new TableView<FileContentsTableRecord>();
		table.setEditable(false);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		TableColumn<FileContentsTableRecord, String> indexColumn = new TableColumn<>("Index");
		indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
		indexColumn.setPrefWidth(70);
		table.getColumns().add(indexColumn);
		
		TableColumn<FileContentsTableRecord, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeColumn.setPrefWidth(150);
		table.getColumns().add(typeColumn);
		
		TableColumn<FileContentsTableRecord, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(250);
		table.getColumns().add(nameColumn);
		
		TableColumn<FileContentsTableRecord, String> createdColumn = new TableColumn<>("Date Created");
		createdColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
		createdColumn.setPrefWidth(110);
		table.getColumns().add(createdColumn);
		
		TableColumn<FileContentsTableRecord, String> modifiedColumn = new TableColumn<>("Date Modified");
		modifiedColumn.setCellValueFactory(new PropertyValueFactory<>("dateModified"));
		modifiedColumn.setPrefWidth(110);
		table.getColumns().add(modifiedColumn);
		table.setPrefHeight(1000);

		scrollPane.setContent(table);
		vbox.getChildren().add(scrollPane);
		vbox.setAlignment(Pos.BASELINE_LEFT);
		
		//extract selected button
		extractSelectedButton = new Button();
		extractSelectedButton.setText("Extract Selected");
		extractSelectedButton.setOnAction(e -> controller.extractSelected());
		
		//extract all button
		extractAllButton = new Button();
		extractAllButton.setText("Extract All");
		extractAllButton.setOnAction(e -> controller.extractAll());
		
		HBox extractionButtons = new HBox();
		extractionButtons.setAlignment(Pos.CENTER);
		extractionButtons.setSpacing(50);
		extractionButtons.setPadding(new Insets(10, 0, 0, 0));
		extractionButtons.getChildren().addAll(extractSelectedButton, extractAllButton);
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
		
		//profile label
		profileLabel = new Label("Profile:");
		configSelection.getChildren().add(profileLabel);
		
		//profile select
		profileSelect = new ChoiceBox<>();
		profileSelect.setItems(FXCollections.observableArrayList(controller.getProfiles()));
		profileSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											profileSelected(value, oldIndex, newIndex));
		configSelection.getChildren().add(indentElement(1, profileSelect));
		
		//profile label
		algorithmLabel = new Label("Algorithm:");
		configSelection.getChildren().add(algorithmLabel);
		
		//algorithm select
		algorithmSelect = new ChoiceBox<>();
		algorithmSelect.setItems(FXCollections.observableArrayList(controller.getAlgorithms()));
		algorithmSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											algorithmSelected(value, oldIndex, newIndex));
		configSelection.getChildren().add(indentElement(1, algorithmSelect));
		
		//key selection label
		keySelectionLabel = new Label("Key Selection:");
		configSelection.getChildren().add(keySelectionLabel);
		
		//key selection buttons
		keySelectionButtons = new ToggleGroup();
		
		keyFileToggle = new RadioButton(keyFileToggleString);
		keyFileToggle.setToggleGroup(keySelectionButtons);
		keyFileToggle.setUserData(keyFileToggleString);
		passwordToggle = new RadioButton(passwordToggleString);
		passwordToggle.setUserData(passwordToggleString);
		passwordToggle.setToggleGroup(keySelectionButtons);
		keySelectionButtons.selectToggle(passwordToggle);
		keySelectionButtons.selectedToggleProperty().addListener(
						(ObservableValue<? extends Toggle> value,
										Toggle oldSelection, Toggle newSelection) ->
											keyTypeSelected(value, oldSelection, newSelection));
		
		HBox radioBox = new HBox();
		radioBox.setSpacing(10);
		radioBox.getChildren().addAll(passwordToggle, keyFileToggle);
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
		
		//open
		openButton = new Button();
		openButton.setText("Open");
		openButton.setOnAction(e -> controller.openArchive());
		HBox centered = new HBox();
		centered.getChildren().add(openButton);
		centered.setAlignment(Pos.CENTER);
		centered.setPadding(new Insets(20, 0, 0, 0));
		configSelection.getChildren().add(centered);

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
		
		keyFileToggle.disableProperty().set(!enabled);
		passwordToggle.disableProperty().set(!enabled);
		keySelectionLabel.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			keyFileToggle.setStyle("-fx-opacity: .75");
			passwordToggle.setStyle("-fx-opacity: .75");
			keySelectionLabel.setStyle("-fx-opacity: .5");
			
			//disable everything
			setKeyFileSectionEnabled(false);
			setPasswordSectionEnabled(false);
		}
		else
		{
			keyFileToggle.setStyle("-fx-opacity: 1");
			passwordToggle.setStyle("-fx-opacity: 1");
			keySelectionLabel.setStyle("-fx-opacity: 1");

			//enable only the selected one
			if (keySelectionButtons.getSelectedToggle().getUserData().equals(keyFileToggleString))
			{
				setKeyFileSectionEnabled(true);
				setPasswordSectionEnabled(false);
			}
			else
			{
				setKeyFileSectionEnabled(false);
				setPasswordSectionEnabled(true);
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
		
		if (newSelection.getUserData().equals(passwordToggleString))
		{
			setPasswordSectionEnabled(true);
			setKeyFileSectionEnabled(false);
			//clearKeyFileSection();
		}
		else //assumed file toggled
		{
			setKeyFileSectionEnabled(true);
			setPasswordSectionEnabled(false);
			//clearPasswordSection();
		}
	}
	
	public void setTableData(List<FileContents> data)
	{
		List<FileContentsTableRecord> records = new ArrayList<FileContentsTableRecord>();
		
		int count = 1;
		for (FileContents fileContents : data)
		{
			records.add(new FileContentsTableRecord(count++,
							fileContents.getMetadata().getType(),
							fileContents.getFragmentNumber(),
							fileContents.isFragment(),
							fileContents.getMetadata().getFile(),
							fileContents.getMetadata().getDateCreated(),
							fileContents.getMetadata().getDateModified()));
		}
		
		table.setItems(FXCollections.observableArrayList(records));
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
	
	public void setOpenSectionEnabled(boolean enabled)
	{
		openButton.disableProperty().set(!enabled);
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

	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	private void algorithmSelected(ObservableValue<? extends Number> value,
					Number oldIndex, Number newIndex)
	{
		System.out.println("Algorithm selected: " + newIndex);
		
		controller.algorithmSelected(newIndex.intValue());
	}

	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 */
	private void profileSelected(ObservableValue<? extends Number> value, Number oldIndex,
					Number newIndex)
	{
		System.out.println("Profile selected: " + newIndex.intValue());
		
		controller.profileSelected(newIndex.intValue());
	}

	/**
	 * @update_comment
	 * @param enabled
	 */
	public void setOpenButtonEnabled(boolean enabled)
	{
		System.out.println("Open button enabled: " + enabled);
		
		openButton.disableProperty().set(!enabled);

		if (!enabled)
		{
			openButton.setStyle("-fx-opacity: .75");
		}
		else
		{
			openButton.setStyle("-fx-opacity: 1");
		}
	}
	
	public void setExtractionButtonsEnabled(boolean enabled)
	{
		System.out.println("Extraction buttons enabled: " + enabled);
		
		extractSelectedButton.disableProperty().set(!enabled);
		extractAllButton.disableProperty().set(!enabled);

		if (!enabled)
		{
			extractSelectedButton.setStyle("-fx-opacity: .75");
			extractAllButton.setStyle("-fx-opacity: .75");
		}
		else
		{
			extractSelectedButton.setStyle("-fx-opacity: 1");
			extractAllButton.setStyle("-fx-opacity: 1");
		}
	}
	
	public void setAlgorithmSelection(String presetName)
	{
		algorithmSelect.setValue(presetName);
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
	
	public void setProfileSelectionEnabled(boolean enabled)
	{
		profileSelect.disableProperty().set(!enabled);
		
		if (!enabled)
		{
			profileSelect.setStyle("-fx-opacity: .75");
		}
		else
		{
			profileSelect.setStyle("-fx-opacity: 1");
		}
	}
	
	public void setPasswordPrompt(String prompt)
	{
		passwordField.setPromptText(prompt);
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
	
	
	
	
	
	
}
