package ui.graphical.top;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.ArgParseResult;
import ui.graphical.View;
import ui.graphical.algorithmeditor.AlgorithmEditorView;
import ui.graphical.archiveviewer.OpenArchiveView;
import ui.graphical.embed.EmbedView;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TopView extends View
{
	private List<View> tabViews;
	
	private TabPane tabPane;
	
	public TopView(Stage window, ArgParseResult result)
	{
		super(window);
		
		tabViews = new ArrayList<View>();
		tabViews.add(new OpenArchiveView(window, result.inputFile));
		tabViews.add(new EmbedView(window));
		tabViews.add(new AlgorithmEditorView(window));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	@Override
	public String getPassword()
	{
		//pass to correct tab
		int selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();
		return tabViews.get(selectedTabIndex).getPassword();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#setupScene(javafx.stage.Stage)
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane borderPane = new BorderPane();
		tabPane = new TabPane();

		for (View view : tabViews)
		{
			Tab tab = new Tab();
			tab.setClosable(false);
			tab.setText(view.getTabName());
			Pane pane = view.setupPane();
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
		//pass to correct tab
		int selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();
		return tabViews.get(selectedTabIndex).getEnclosingFolder();
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
