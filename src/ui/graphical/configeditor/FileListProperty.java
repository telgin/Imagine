package ui.graphical.configeditor;

import java.io.File;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.graphical.archiveviewer.FileContentsTableRecord;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileListProperty extends ConfigurationProperty
{
	private String name;
	private TableView<FileTableRecord> table;
	private Button addFileButton;
	private Button addFolderButton;
	private Button removeButton;
	private Consumer<Void> addFileCallback;
	private Consumer<Void> addFolderCallback;
	private Consumer<Void> removeCallback;
	
	
	public FileListProperty(String name, Consumer<Void> addFileCallback,
					Consumer<Void> addFolderCallback, Consumer<Void> removeCallback)
	{
		this.name = name;
		this.addFileCallback = addFileCallback;
		this.addFolderCallback = addFolderCallback;
		this.removeCallback = removeCallback;
		setLabel(new Label(name));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setup(javafx.scene.layout.VBox)
	 */
	@Override
	public void setup(VBox container)
	{
		//label
		container.getChildren().add(getLabel());
		
		//table
		table = new TableView<FileTableRecord>();
		table.setEditable(false);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.setPrefHeight(200);
		
		TableColumn<FileTableRecord, String> pathColumn = new TableColumn<>("Path");
		pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
		pathColumn.setPrefWidth(200);
		table.getColumns().add(pathColumn);
		
		container.getChildren().add(indentElement(1, table));
		
		//buttons
		HBox buttonRow = new HBox();
		buttonRow.setSpacing(10);
		
		addFileButton = new Button();
		addFileButton.setText("Add File");
		addFileButton.setOnAction(e -> addFileCallback.accept(null));
		buttonRow.getChildren().add(addFileButton);
		
		addFolderButton = new Button();
		addFolderButton.setText("Add Folder");
		addFolderButton.setOnAction(e -> addFolderCallback.accept(null));
		buttonRow.getChildren().add(addFolderButton);
		
		removeButton = new Button();
		removeButton.setText("Remove");
		removeButton.setOnAction(e -> removeCallback.accept(null));
		buttonRow.getChildren().add(removeButton);
		
		container.getChildren().add(indentElement(1, buttonRow));
	}
	
	public void addFile(File file)
	{
		table.getItems().add(new FileTableRecord(file));
	}
	
	public void removeFile(int index)
	{
		table.getItems().remove(index);
	}
	
	public int getSelectedIndex()
	{
		TablePosition position = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
		return position.getRow();
	}
	
	public File getFile(int index)
	{
		return table.getItems().get(index).getFile();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO Auto-generated method stub
		
	}

}
