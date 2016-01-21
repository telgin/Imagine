package ui.graphical;

import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ScrollAlert extends Alert
{
	/**
	 * @update_comment
	 * @param alertType
	 */
	public ScrollAlert(AlertType alertType)
	{
		super(alertType);
		this.resizableProperty().set(true);
	}
	
	public void setScrollText(String text)
	{
		ScrollPane scroll = new ScrollPane();
		Text content = new Text(text);
		scroll.setContent(content);
		scroll.setPrefSize(700, 120);
		scroll.setStyle("-fx-focus-color: transparent;"); //remove focus highlight
		getDialogPane().setContent(scroll);
	}
	
	public void setScrollText(List<String> lines)
	{
		setScrollText(String.join("\n", lines));
	}

}
