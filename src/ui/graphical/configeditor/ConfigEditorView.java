package ui.graphical.configeditor;

import java.io.File;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
public class ConfigEditorView extends View
{
	private ConfigEditorController controller;
	
	private Label profileLabel;
	private ChoiceBox<String> profileSelect;
	private Button saveProfileButton;

	/**
	 * @update_comment
	 * @param window
	 */
	public ConfigEditorView(Stage window)
	{
		super(window);
		
		controller = new ConfigEditorController();
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
		
		base.setLeft(setupEditProfileSection());
		base.setRight(setupEditAlgorithmSection());
		
		return base;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupEditProfileSection()
	{	
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		vbox.setPadding(new Insets(14,10,20,20));

		//section label
		Label sectionLabel = new Label("Edit Profiles");
		sectionLabel.setFont(new Font("Arial", 20));
		sectionLabel.setPadding(new Insets(0, 0, 10, 0));
		vbox.getChildren().add(sectionLabel);
		
		//profile selection row
		HBox profileSelectionRow = new HBox();
		profileSelectionRow.setPadding(new Insets(2, 2, 2, 2));
		profileSelectionRow.setSpacing(10);
		
		//profile label
		profileLabel = new Label("Profile:");
		profileLabel.setPadding(new Insets(5, 0, 0, 0));
		profileSelectionRow.getChildren().add(profileLabel);
		
		//profile select
		profileSelect = new ChoiceBox<>();
		profileSelect.setItems(FXCollections.observableArrayList(controller.getProfiles()));
		profileSelect.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> value,
										Number oldIndex, Number newIndex) ->
											profileSelected(value, oldIndex, newIndex));
		profileSelectionRow.getChildren().add(profileSelect);
		
		//save profile button
		saveProfileButton = new Button();
		saveProfileButton.setText("Save Profile");
		saveProfileButton.setOnAction(e -> controller.saveProfilePressed());
		profileSelectionRow.getChildren().add(saveProfileButton);
		
		vbox.getChildren().add(profileSelectionRow);
		
		//profile scroll pane
		ScrollPane profileScroll = new ScrollPane();
		VBox profileProperties = new VBox();
		profileProperties.setPadding(new Insets(2, 2, 2, 5));
		profileProperties.setSpacing(5);
		
		//profile name
		StringProperty profileName = new StringProperty("Profile Name:");
		profileName.setup(profileProperties);
		
		//profile algorithm preset
		ChoiceProperty algoPresetSelect = new ChoiceProperty("Algorithm Preset:",
						controller.getProfiles(), i -> controller.algorithmSelected(i));
		algoPresetSelect.setup(profileProperties);
		
		//use key file
		BooleanProperty useKeyFile = new BooleanProperty("Use key file?",
						b -> controller.keyFileChecked(b));
		useKeyFile.setup(profileProperties);
		
		//key file
		FileProperty keyFilePath = new FileProperty("Key File:", e -> controller.browseKeyFile());
		keyFilePath.setup(profileProperties);
		
		//static tracked files
		FileListProperty trackedFiles = new FileListProperty("Tracked Files/Folders: (Optional)",
						e -> controller.addTrackedFilePressed(),
						e -> controller.addTrackedFolderPressed(),
						e -> controller.removeTrackedFilePressed());
		trackedFiles.setup(profileProperties);
		
		//static untracked files
		FileListProperty untrackedFiles = new FileListProperty("Untracked Files/Folders: (Optional)",
						e -> controller.addUntrackedFilePressed(),
						e -> controller.addUntrackedFolderPressed(),
						e -> controller.removeUntrackedFilePressed());
		untrackedFiles.setup(profileProperties);
		
		//use static output folder
		BooleanProperty useStaticOutputFolder = new BooleanProperty("Use static output folder?",
						b -> controller.staticOutputFolderChecked(b));
		useStaticOutputFolder.setup(profileProperties);
		
		//static output folder
		FileProperty staticOutputFolderPath = new FileProperty("Static Output Folder:",
						e -> controller.browseStaticOutputFolder());
		staticOutputFolderPath.setup(profileProperties);
		
		//use custom database file
		BooleanProperty useCustomHashDB = new BooleanProperty("Use custom database file?",
						b -> controller.customDBChecked(b));
		useCustomHashDB.setup(profileProperties);
		
		//hashdb file
		FileProperty hashDBPath = new FileProperty("Database File:",
						e -> controller.browseHashDBFile());
		hashDBPath.setup(profileProperties);
		
		//using index files
		BooleanProperty useIndexFiles = new BooleanProperty("Index file system?",
						b -> controller.useIndexFilesChecked(b));
		useIndexFiles.setup(profileProperties);
		
		//using absolute paths
		BooleanProperty useAbsolutePaths = new BooleanProperty("Use absolute paths?",
						b -> controller.useAbsolutePathsChecked(b));
		useAbsolutePaths.setup(profileProperties);
		
		//using structured output folders
		BooleanProperty useStructuredOutput = new BooleanProperty("Structure file output?",
						b -> controller.useStructuredOutputChecked(b));
		useStructuredOutput.setup(profileProperties);
		
		
		profileProperties.setPrefWidth(350);
		profileScroll.setContent(profileProperties);
		vbox.getChildren().add(profileScroll);
		
		
		return vbox;
	}

	/**
	 * @update_comment
	 * @return
	 */
	private void browseKeyFile()
	{
		
	}

	/**
	 * @update_comment
	 * @param value
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	private void profileSelected(ObservableValue<? extends Number> value,
					Number oldIndex, Number newIndex)
	{
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Node setupEditAlgorithmSection()
	{
		return new BorderPane();
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
		return "Configuration Editor";
	}

}
