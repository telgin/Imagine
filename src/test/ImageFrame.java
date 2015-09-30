package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageFrame extends JFrame implements WindowListener{
	
	public ImageFrame(BufferedImage img){
		JLabel label = new JLabel(new ImageIcon(img));
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}
	
	public void display(){
		setVisible(true);
	}
	
	public void destroy(){
		setVisible(false);
		dispose();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {
		destroy();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		destroy();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		destroy();
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		destroy();
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	public void setRandomLocation() {
		int xMax = 1920 - (getWidth() + 50);
		int yMax = 1080 - (getHeight() + 50);
		Random random = new Random(System.currentTimeMillis());
		setLocation(random.nextInt(xMax),random.nextInt(yMax));
	}
}
