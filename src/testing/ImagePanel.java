package testing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A simple class used to display an image for testing purposes.
 */
public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private Image f_image;
	private Color f_bgColor;

	/**
	 * @update_comment
	 * @param p_backgroundColor
	 */
	public ImagePanel(Color p_backgroundColor)
	{
		f_bgColor = p_backgroundColor;
		this.setBackground(f_bgColor);
	}

	/**
	 * @update_comment
	 * @param p_image
	 */
	public void setImage(Image p_image)
	{
		f_image = p_image;
	}

	/**
	 * @update_comment
	 */
	public void drawing()
	{
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics p_graphics)
	{
		super.paintComponent(p_graphics);

		if (f_image != null)
		{
			ImageIcon testwh = new ImageIcon(f_image);
			int w = testwh.getIconWidth();
			int h = testwh.getIconHeight();

			int x1 = (this.getWidth() / 2) - (w / 2);
			int y1 = (this.getHeight() / 2) - (h / 2);

			if (x1 < 0)
				x1 = 0;
			if (y1 < 0)
				y1 = 0;
			if (w > this.getWidth())
				w = this.getWidth();
			if (h > this.getHeight())
				h = this.getHeight();

			p_graphics.drawImage(f_image, x1, y1, w, h, null);
		}
		else
		{
			p_graphics.setColor(f_bgColor);
			p_graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
}
