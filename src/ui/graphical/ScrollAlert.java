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
	 * @param p_alertType
	 */
	public ScrollAlert(AlertType p_alertType)
	{
		super(p_alertType);
		this.resizableProperty().set(true);
	}
	
	/**
	 * @update_comment
	 * @param p_text
	 */
	public void setScrollText(String p_text)
	{
		ScrollPane scroll = new ScrollPane();
		Text content = new Text(p_text);
		scroll.setContent(content);
		scroll.setPrefSize(700, 120);
		scroll.setStyle("-fx-focus-color: transparent;"); //remove focus highlight
		getDialogPane().setContent(scroll);
	}
	
	/**
	 * @update_comment
	 * @param p_lines
	 */
	public void setScrollText(List<String> p_lines)
	{
		setScrollText(String.join("\n", p_lines));
	}

}
