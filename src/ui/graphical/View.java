package ui.graphical;

import java.io.File;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class View
{
	private Stage window;
	
	public View(Stage window)
	{
		this.window = window;
	}
	
	public abstract String getPassword();
	
	public abstract Pane setupPane();
	
	public abstract File getEnclosingFolder();
	
	public abstract String getTabName();

	/**
	 * @update_comment
	 * @param errors
	 */
	public void showErrors(List<String> errors, String process)
	{
		ScrollAlert popup = new ScrollAlert(Alert.AlertType.ERROR);
		String header = errors.size() == 1 ? "An error " : "Errors ";
		header += "occurred during " + process + ":";
		
		popup.setHeaderText(header);
		popup.setScrollText(errors);
		popup.showAndWait();
	}
	
	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		return fileChooser.showOpenDialog(window);
	}
	
	public File chooseFolder()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		return dirChooser.showDialog(window);
	}
}
