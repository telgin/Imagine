package ui.graphical.top;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import algorithms.Parameter;
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
	private List<View> f_tabViews;
	private ArgParseResult f_args;
	private TabPane f_tabPane;
	
	/**
	 * @update_comment
	 * @param p_window
	 * @param p_args
	 */
	public TopView(Stage p_window, ArgParseResult p_args)
	{
		super(p_window);
		
		f_args = p_args;
		
		f_tabViews = new ArrayList<View>();
		f_tabViews.add(new EmbedView(p_window, p_args));
		f_tabViews.add(new OpenArchiveView(p_window, p_args));
		f_tabViews.add(new AlgorithmEditorView(p_window, p_args));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.View#getPassword()
	 */
	@Override
	public String getPassword()
	{
		//pass to correct tab
		int selectedTabIndex = f_tabPane.getSelectionModel().getSelectedIndex();
		return f_tabViews.get(selectedTabIndex).getPassword();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#setupScene(javafx.stage.Stage)
	 */
	@Override
	public Pane setupPane()
	{
		BorderPane borderPane = new BorderPane();
		f_tabPane = new TabPane();

		for (View view : f_tabViews)
		{
			Tab tab = new Tab();
			tab.setClosable(false);
			tab.setText(view.getTabName());
			Pane pane = view.setupPane();
			tab.setContent(pane);
			f_tabPane.getTabs().add(tab);
		}
		
		borderPane.setCenter(f_tabPane);
		
		//set active tab
		switch (f_args.getAction())
		{
			case k_editor:
				f_tabPane.getSelectionModel().select(2);
				break;
				
			case k_embed:
				f_tabPane.getSelectionModel().select(0);
				break;
				
			case k_open:
			case k_extract:		
			case k_install:
			case k_help:
			default:
				f_tabPane.getSelectionModel().select(1);
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
		int selectedTabIndex = f_tabPane.getSelectionModel().getSelectedIndex();
		return f_tabViews.get(selectedTabIndex).getEnclosingFolder();
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#getTabName()
	 */
	@Override
	public String getTabName()
	{
		return "Top View";
	}

	/* (non-Javadoc)
	 * @see ui.graphical.View#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter p_parameter)
	{
		int selectedTabIndex = f_tabPane.getSelectionModel().getSelectedIndex();
		return f_tabViews.get(selectedTabIndex).promptParameterValue(p_parameter);
	}

}
