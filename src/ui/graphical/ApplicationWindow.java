package ui.graphical;

import config.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.ArgParseResult;
import ui.UIContext;
import ui.graphical.top.TopView;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This is the actual "application" required by javafx. It allows for the
 * creation of the primary window frame in gui mode.
 */
public class ApplicationWindow extends Application
{
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage p_primaryStage) throws Exception
	{	
		ArgParseResult result = ((GUI) UIContext.getUI()).getArgParseResult();
		
		TopView topView = new TopView(p_primaryStage, result);
		((GUI) UIContext.getUI()).setTopView(topView);
		
		Pane topViewPane = topView.setupPane();
		Scene scene = new Scene(topViewPane, 1040, 550);
		
		topViewPane.prefHeightProperty().bind(scene.heightProperty());
		topViewPane.prefWidthProperty().bind(scene.widthProperty());
		
		p_primaryStage.setScene(scene);
		
		p_primaryStage.setTitle(Constants.APPLICATION_NAME_FULL + " " +
						Constants.APPLICATION_FORMATTED_VERSION);
		
		p_primaryStage.show();
	}
}
