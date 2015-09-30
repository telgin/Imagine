package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Image image;
	private Color bgColor = new Color(0,0,0);
	
	public ImagePanel(Color backgroundColor){
		bgColor = backgroundColor;
		this.setBackground(bgColor);
	}
	public void setImage(Image i){
		image = i;
	}
	public void drawing(){
		repaint();
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(image != null){
			ImageIcon testwh = new ImageIcon(image);
			int w = testwh.getIconWidth();
			int h = testwh.getIconHeight();
			
			int x1 = (this.getWidth()/2) - (w/2);
			int y1 = (this.getHeight()/2) - (h/2);
			
			if(x1 < 0) x1 = 0;
			if(y1 < 0) y1 = 0;
			if(w > this.getWidth()) w = this.getWidth();
			if(h > this.getHeight()) h = this.getHeight();
			
			g.drawImage(image,x1,y1,w,h,null);
		}
		else{
			g.setColor(bgColor);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
}
