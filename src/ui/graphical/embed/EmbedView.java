package ui.graphical.embed;

import java.io.File;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.graphical.View;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class EmbedView extends View
{

	/**
	 * @update_comment
	 * @param window
	 */
	public EmbedView(Stage window)
	{
		super(window);
		// TODO Auto-generated constructor stub
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
	 * @see ui.graphical.View#setupPane()
	 */
	@Override
	public Pane setupPane()
	{
		return new BorderPane();
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
		return "Embed Data";
	}

}
