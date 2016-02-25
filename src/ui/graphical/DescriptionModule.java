package ui.graphical;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DescriptionModule extends GUIModule
{
	private String f_name;
	private TextArea f_area;
	
	/**
	 * @update_comment
	 * @param p_name
	 */
	public DescriptionModule(String p_name)
	{
		setName(p_name);
		setLabel(new Label(p_name));
	}
	
	/* (non-Javadoc)
	 * @see ui.graphical.GUIModule#setup(javafx.scene.layout.VBox)
	 */
	public void setup(VBox p_container)
	{
		p_container.getChildren().add(getLabel());
		
		setArea(new TextArea());
		f_area.setEditable(false);
		
		p_container.getChildren().add(f_area);
	}
	
	/**
	 * @update_comment
	 * @param p_text
	 */
	public void setText(String p_text)
	{
		f_area.setText(p_text);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getText()
	{
		return f_area.getText();
	}

	/**
	 * @return the area
	 */
	public TextArea getArea()
	{
		return f_area;
	}

	/**
	 * @param p_area the area to set
	 */
	public void setArea(TextArea p_area)
	{
		f_area = p_area;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean p_enabled)
	{
		getLabel().disableProperty().set(!p_enabled);
		f_area.disableProperty().set(!p_enabled);
		
		if (!p_enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			f_area.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			f_area.setStyle("-fx-opacity: 1");
		}
	}

	/* (non-Javadoc)
	 * @see ui.graphical.algorithmeditor.ConfigurationProperty#setErrorState(boolean)
	 */
	@Override
	public void setErrorState(boolean p_error)
	{
		//description not editable
	}
	
	
	/**
	 * @update_comment
	 * @param p_insets
	 */
	public void setPadding(Insets p_insets)
	{
		getLabel().setPadding(new Insets(p_insets.getTop(), p_insets.getRight(), 0, p_insets.getLeft()));
		f_area.setPadding(new Insets(0, p_insets.getRight(), p_insets.getBottom(), p_insets.getLeft()));
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		f_name = name;
	}
}
