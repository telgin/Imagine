package ui.graphical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import config.Constants;
import data.FileKey;
import data.PasswordKey;
import data.TrackingGroup;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logging.LogLevel;
import logging.Logger;
import product.FileContents;
import product.ProductContents;
import product.ProductMode;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) //oracle recommends using rawtypes
public class OpenArchiveView extends Application
{
	//state variables
	private File file;
	private List<String> profiles;
	private List<String> algorithms;
	private TrackingGroup selectedProfile = null;
	private Algorithm selectedAlgorithm = null;
	
	//constants
	private final String tempProfileString = "Temporary Profile";
	private final String noSelectionString = "None Selected";
	private final String passwordToggleString = "Password";
	private final String keyFileToggleString = "Key File";
	
	//gui elements
	private Stage window;
	private ChoiceBox profileSelect, algorithmSelect;
	private PasswordField passwordField;
	private TextField keyFilePath;
	private Button keyFileBrowseButton, openButton, extractSelectedButton, extractAllButton;
	private ToggleGroup keySelectionButtons;
	private RadioButton keyFileToggle, passwordToggle;
	private TableView table;
	private Label passwordLabel, keyFileLabel, keySelectionLabel, algorithmLabel, profileLabel;
	
	
	public static void main(String[] args)
	{
		File f = new File("testing/bank/image_basic_sample/imagine_1453074440957_0.png");
		args = new String[]{f.getAbsolutePath()};
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		window = primaryStage;
		window.setTitle(Constants.APPLICATION_NAME_SHORT + " Archive View");
		file = new File(getParameters().getUnnamed().get(0));
		
		
		window.setScene(setupScene());
		
		//set the temporary profile as selected by default
		profileSelect.setValue(tempProfileString);
		
		
		
		window.show();
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Scene setupScene()
	{
		
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(setupConfigSelection());
		borderPane.setCenter(setupContentsSection());
		
		
		return new Scene(borderPane, 1000, 500);
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
		Label fileNameLabel = new Label("File: " + file.getName());
		vbox.getChildren().add(fileNameLabel);

		//scroll pane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		
		//table
		table = new TableView();
		table.setEditable(false);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		TableColumn indexColumn = new TableColumn("Index");
		indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
		indexColumn.setPrefWidth(70);
		table.getColumns().add(indexColumn);
		
		TableColumn typeColumn = new TableColumn("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeColumn.setPrefWidth(90);
		table.getColumns().add(typeColumn);
		
		TableColumn nameColumn = new TableColumn("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(250);
		table.getColumns().add(nameColumn);
		
		TableColumn createdColumn = new TableColumn("Date Created");
		createdColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
		createdColumn.setPrefWidth(110);
		table.getColumns().add(createdColumn);
		
		TableColumn modifiedColumn = new TableColumn("Date Modified");
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
		extractSelectedButton.setOnAction(e -> extractSelected());
		
		//extract all button
		extractAllButton = new Button();
		extractAllButton.setText("Extract All");
		extractSelectedButton.setOnAction(e -> extractAll());
		
		HBox extractionButtons = new HBox();
		extractionButtons.setAlignment(Pos.CENTER);
		extractionButtons.setSpacing(50);
		extractionButtons.setPadding(new Insets(10, 0, 0, 0));
		extractionButtons.getChildren().addAll(extractSelectedButton, extractAllButton);
		vbox.getChildren().add(extractionButtons);
		
		
		return vbox;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private void extractAll()
	{
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	private void extractSelected()
	{
		
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
		profiles = ConfigurationAPI.getTrackingGroupNames();
		profiles.add(0, tempProfileString);
		profileSelect = new ChoiceBox();
		profileSelect.setItems(FXCollections.observableArrayList(profiles));
		profileSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											profileSelected(value, oldIndex, newIndex));
		configSelection.getChildren().add(indentElement(1, profileSelect));
		
		//profile label
		algorithmLabel = new Label("Algorithm:");
		configSelection.getChildren().add(algorithmLabel);
		
		//algorithm select
		algorithms = ConfigurationAPI.getAlgorithmPresetNames();
		algorithms.add(0, noSelectionString);
		algorithmSelect = new ChoiceBox();
		algorithmSelect.setItems(FXCollections.observableArrayList(algorithms));
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
		keyFileBrowseButton.setOnAction(e -> chooseKeyFile());
		HBox keyFilePathRow = indentElement(1, keyFilePath);
		keyFilePathRow.getChildren().add(keyFileBrowseButton);
		configSelection.getChildren().add(keyFilePathRow);
		
		//open
		openButton = new Button();
		openButton.setText("Open");
		openButton.setOnAction(e -> openArchive());
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

	private void setKeySectionEnabled(boolean enabled)
	{
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
	
	private void openArchive()
	{
		try
		{
			openButton.disableProperty().set(true);
			ProductContents productContents = ConversionAPI.openArchive(selectedProfile, file);
			List<FileContentsTableRecord> records = new ArrayList<FileContentsTableRecord>();
			
			int count = 1;
			for (FileContents fileContents : productContents.getFileContents())
			{
				records.add(new FileContentsTableRecord(count++,
								fileContents.getMetadata().getType(),
								fileContents.getFragmentNumber(),
								fileContents.getMetadata().getFile(),
								fileContents.getMetadata().getDateCreated(),
								fileContents.getMetadata().getDateModified()));
			}

			table.setItems(FXCollections.observableArrayList(records));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			openButton.disableProperty().set(false);
			clearTable();
		}
		catch (UsageException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			openButton.disableProperty().set(false);
			clearTable();
		}
	}
	
	/**
	 * @update_comment
	 */
	private void clearTable()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @param b
	 */
	private void setKeyFileSectionEnabled(boolean enabled)
	{
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
		passwordField.disableProperty().set(!enabled);
		passwordLabel.disableProperty().set(!enabled);
		
		System.out.println("Password section enabled: " + enabled);
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
	
	private void clearPasswordSection()
	{
		passwordField.clear();
	}

	private void clearKeyFileSection()
	{
		keyFilePath.clear();
	}
	
	private void clearKeySection()
	{
		clearPasswordSection();
		clearKeyFileSection();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	private void chooseKeyFile()
	{
		FileChooser fileChooser = new FileChooser();
		File chosen = fileChooser.showOpenDialog(window);
		if (chosen != null)
		{
			keyFilePath.setText(chosen.getAbsolutePath());
		}
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
		
		int index = newIndex.intValue();
		if (index == 0)
		{
			//'no selection' selected
			setKeySectionEnabled(false);
		}
		else
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(algorithms.get(index));
				ProductMode mode = selectedAlgorithm.getProductSecurityLevel();
				if (mode.equals(ProductMode.k_basic))
				{
					setKeySectionEnabled(false);
				}
				else
				{
					setKeySectionEnabled(true);
				}
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				algorithmSelect.setValue(noSelectionString);
			}
		}
		
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
		
		clearKeySection();
		
		if (newIndex.intValue() == 0)
		{
			//the temporary profile is selected
			algorithmSelect.setValue(noSelectionString);
			algorithmSelect.disableProperty().set(false);
			selectedProfile = null;
			selectedAlgorithm = null;
		}
		else
		{
			int profileNameIndex = newIndex.intValue();
			String groupName = profiles.get(profileNameIndex);
			try
			{
				selectedProfile = ConfigurationAPI.getTrackingGroup(groupName);
				selectedAlgorithm = selectedProfile.getAlgorithm();
				
				algorithmSelect.setValue(selectedAlgorithm.getPresetName());
				//TODO handle case where preset name not found
				
				
				if (selectedAlgorithm.getProductSecurityLevel().isSecured())
				{
					setKeySectionEnabled(true);
					
					if (selectedProfile.getKey() instanceof FileKey)
					{
						keySelectionButtons.selectToggle(keyFileToggle);
						
						FileKey key = (FileKey) selectedProfile.getKey();
						keyFilePath.setText(key.getKeyFile().getAbsolutePath());
						//TODO handle case where key file not found
					}
					else if(selectedProfile.getKey() instanceof PasswordKey)
					{
						keySelectionButtons.selectToggle(passwordToggle);
						passwordField.setPromptText(selectedProfile.getKey().getName());
					}
				}
				else
				{
					setKeySectionEnabled(false);
				}
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				algorithmSelect.setValue(noSelectionString);
				clearKeySection();
				setKeySectionEnabled(false);
			}
			
			//disable algorithm selection b/c it's defined in the profile
			algorithmSelect.disableProperty().set(true);
			algorithmSelect.setStyle("-fx-opacity: 1");
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
