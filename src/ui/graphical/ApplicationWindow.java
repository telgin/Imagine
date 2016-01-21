package ui.graphical;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.UIContext;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ApplicationWindow extends Application
{
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		View view = ((GUI) UIContext.getUI()).getView();
		
		view.setupScene(primaryStage);
		
		primaryStage.show();
	}
}
