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
 * The view superclass for javafx gui views
 */
public abstract class View
{
	private Stage f_window;
	
	/**
	 * Creates a view object
	 * @param p_window The javafx stage
	 */
	public View(Stage p_window)
	{
		this.f_window = p_window;
	}
	
	/**
	 * Gets the password from the user
	 * @return The password
	 */
	public abstract String getPassword();
	
	/**
	 * Initializes the main pane of this view
	 * @return The created pane
	 */
	public abstract Pane setupPane();
	
	/**
	 * Gets the archive enclosing folder from the user
	 * @return The enclosing folder
	 */
	public abstract File getEnclosingFolder();
	
	/**
	 * Gets the name of the tab to display
	 * @return The tab name
	 */
	public abstract String getTabName();
	
	/**
	 * NOT IMPLEMENTED YET (TODO)
	 * Prompts the user for a parameter value
	 * @param p_parameter The parameter that needs a value
	 * @return The value returned by the user
	 */
	public String promptParameterValue(Parameter p_parameter)
	{
		//not implemented in gui yet
		return null;
	}

	/**
	 * Shows the list of errors in an alert popup
	 * @param p_errors The list of error messages
	 * @param p_process The name of the process/goal that was interrupted
	 * when these errors occurred.
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
	 * Opens the os specific prompt to choose a file
	 * @return The file chosen
	 */
	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		return fileChooser.showOpenDialog(f_window);
	}
	
	/**
	 * Opens the os specific prompt to choose a directory
	 * @return The directory chosen
	 */
	public File chooseFolder()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		return dirChooser.showDialog(f_window);
	}
}
