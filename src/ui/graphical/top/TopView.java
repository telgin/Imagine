package ui.graphical.top;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import algorithms.Parameter;
import api.ConfigurationAPI;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import system.Imagine;
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
	private ArgParseResult args;
	private TabPane tabPane;
	
	public TopView(Stage window, ArgParseResult args)
	{
		super(window);
		
		this.args = args;
		
		tabViews = new ArrayList<View>();
		tabViews.add(new OpenArchiveView(window, args));
		tabViews.add(new EmbedView(window, args));
		tabViews.add(new AlgorithmEditorView(window, args));
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
		
		//set active tab
		switch (args.action)
		{
			case k_editor:
				tabPane.getSelectionModel().select(2);
				break;
				
			case k_embed:
				tabPane.getSelectionModel().select(1);
				break;
				
			case k_open:
			case k_extract:		
			case k_install:
			case k_help:
			default:
				tabPane.getSelectionModel().select(0);
		}
		
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

	/**
	 * @update_comment
	 * @return
	 */
	public String promptParameterValue(Parameter parameter)
	{
		int selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();
		return tabViews.get(selectedTabIndex).promptParameterValue(parameter);
	}

}
