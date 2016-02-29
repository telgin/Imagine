package testing;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A simple class used to display an image for testing purposes.
 */
@SuppressWarnings("serial")
public class ImageFrame extends JFrame implements WindowListener
{

	/**
	 * Constructs an image frame for an image
	 * @param p_img The image
	 */
	public ImageFrame(BufferedImage p_img)
	{
		JLabel label = new JLabel(new ImageIcon(p_img));
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Sets the frame visible
	 */
	public void display()
	{
		setVisible(true);
	}

	/**
	 * Disposes this frame
	 */
	public void destroy()
	{
		setVisible(false);
		dispose();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent p_arg)
	{
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent p_arg)
	{
		destroy();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent p_arg)
	{
		destroy();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent p_arg)
	{
		destroy();

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent p_arg)
	{
		destroy();

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent p_arg)
	{
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent p_arg)
	{
	}

	/**
	 * Sets the window in a random location on the screen.
	 * Just a fun method, used to display multiple small images without
	 * them overlapping so much.
	 */
	public void setRandomLocation()
	{
		int xMax = 1920 - (getWidth() + 50);
		int yMax = 1080 - (getHeight() + 50);
		Random random = new Random(System.currentTimeMillis());
		setLocation(random.nextInt(xMax), random.nextInt(yMax));
	}
}
