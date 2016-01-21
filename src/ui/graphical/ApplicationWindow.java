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
		ArgParseResult result = ((GUI) UIContext.getUI()).getArgParseResult();
		
		TopView topView = new TopView(primaryStage, result);
		((GUI) UIContext.getUI()).setTopView(topView);
		
		Pane topViewPane = topView.setupPane();
		Scene scene = new Scene(topViewPane, 1040, 500);
		
		topViewPane.prefHeightProperty().bind(scene.heightProperty());
		topViewPane.prefWidthProperty().bind(scene.widthProperty());
		
		primaryStage.setScene(scene);
		
		primaryStage.setTitle(Constants.APPLICATION_NAME_FULL + " " +
						Constants.APPLICATION_FORMATTED_VERSION);
		
		primaryStage.show();
	}
}
