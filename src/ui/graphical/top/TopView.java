package ui.graphical.top;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.ArgParseResult;
import ui.graphical.View;
import ui.graphical.archiveviewer.OpenArchiveView;
import ui.graphical.history.HistoryView;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TopView extends View
{
	private OpenArchiveView openArchiveView;
	private HistoryView historyView;
	
	public TopView(Stage window, ArgParseResult result)
	{
		super(window);
		
		openArchiveView = new OpenArchiveView(window, result.inputFile);
		historyView = new HistoryView(window);
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
	 * @see ui.graphical.View#setupScene(javafx.stage.Stage)
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane borderPane = new BorderPane();
		TabPane tabPane = new TabPane();
		
		{
			//open archive view
			Tab tab = new Tab();
			tab.setClosable(false);
			tab.setText(openArchiveView.getTabName());
			Pane pane = openArchiveView.setupPane();
			tab.setContent(pane);
			tabPane.getTabs().add(tab);
		}
		
		{
			//history view
			Tab tab = new Tab();
			tab.setClosable(false);
			tab.setText(historyView.getTabName());
			Pane pane = historyView.setupPane();
			tab.setContent(pane);
			tabPane.getTabs().add(tab);
		}
		
		
		
		
		borderPane.setCenter(tabPane);
		
		return borderPane;
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
		return "Top View";
	}

}
