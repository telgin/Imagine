package ui.graphical.algorithmeditor;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DescriptionProperty extends ConfigurationProperty
{
	private String name;
	private TextArea area;
	
	public DescriptionProperty(String name)
	{
		this.name = name;
		setLabel(new Label(name));
	}
	
	public void setup(VBox container)
	{
		container.getChildren().add(getLabel());
		
		setArea(new TextArea());
		area.setEditable(false);
		
		container.getChildren().add(area);
	}
	
	public void setText(String text)
	{
		area.setText(text);
	}
	
	public String getText()
	{
		return area.getText();
	}

	/**
	 * @return the area
	 */
	public TextArea getArea()
	{
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(TextArea area)
	{
		this.area = area;
	}

	/* (non-Javadoc)
	 * @see ui.graphical.configeditor.ConfigurationProperty#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		System.out.println(name + " enabled: " + enabled);
		
		getLabel().disableProperty().set(!enabled);
		area.disableProperty().set(!enabled);
		
		
		if (!enabled)
		{
			getLabel().setStyle("-fx-opacity: .5");
			area.setStyle("-fx-opacity: .75");
		}
		else
		{
			getLabel().setStyle("-fx-opacity: 1");
			area.setStyle("-fx-opacity: 1");
		}
	}
}
