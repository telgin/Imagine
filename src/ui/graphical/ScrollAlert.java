package ui.graphical;

import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A modified alert window. This is the standard alert window but with
 * the text body wrapped in a scroll pane.
 */
public class ScrollAlert extends Alert
{
	/**
	 * Constructs a scroll alert window
	 * @param p_alertType The javafx alert type for this popup
	 */
	public ScrollAlert(AlertType p_alertType)
	{
		super(p_alertType);
		this.resizableProperty().set(true);
	}
	
	/**
	 * Sets the text body of the scroll alert given a string
	 * @param p_text The alert text
	 */
	public void setScrollText(String p_text)
	{
		ScrollPane scroll = new ScrollPane();
		Text content = new Text(p_text);
		scroll.setContent(content);
		scroll.setPrefSize(700, 120);
		scroll.setStyle("-fx-focus-color: transparent;"); //this removes focus highlight
		getDialogPane().setContent(scroll);
	}
	
	/**
	 * Sets the text body of the scroll alert given a list of lines
	 * @param p_lines The list of alert text lines
	 */
	public void setScrollText(List<String> p_lines)
	{
		setScrollText(String.join("\n", p_lines));
	}

}
