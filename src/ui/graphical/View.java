package ui.graphical;

import java.io.File;
import java.util.List;

import algorithms.Parameter;
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
	private Stage f_window;
	
	/**
	 * @update_comment
	 * @param p_window
	 */
	public View(Stage p_window)
	{
		this.f_window = p_window;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public abstract String getPassword();
	
	/**
	 * @update_comment
	 * @return
	 */
	public abstract Pane setupPane();
	
	/**
	 * @update_comment
	 * @return
	 */
	public abstract File getEnclosingFolder();
	
	/**
	 * @update_comment
	 * @return
	 */
	public abstract String getTabName();
	
	/**
	 * @update_comment
	 * @param p_parameter
	 * @return
	 */
	public String promptParameterValue(Parameter p_parameter)
	{
		return null;
	}

	/**
	 * @update_comment
	 * @param p_errors
	 * @param p_process
	 */
	public void showErrors(List<String> p_errors, String p_process)
	{
		ScrollAlert popup = new ScrollAlert(Alert.AlertType.ERROR);
		String header = p_errors.size() == 1 ? "An error " : "Errors ";
		header += "occurred during " + p_process + ":";
		
		popup.setHeaderText(header);
		popup.setScrollText(p_errors);
		popup.showAndWait();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		return fileChooser.showOpenDialog(f_window);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public File chooseFolder()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		return dirChooser.showDialog(f_window);
	}
}
