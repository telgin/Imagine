package ui.graphical;

import java.io.File;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface View
{
	public String getPassword();
	
	public void setupScene(Stage window);
	
	public File getEnclosingFolder();
}
