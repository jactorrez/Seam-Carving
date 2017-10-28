import javax.swing.*;

import java.awt.Image;
import java.awt.event.*;

public class SeamCarverGUI extends JFrame{
	ImageIcon img;
	JLabel imgHolder;
	SeamCarver seamCarver;
	
	public SeamCarverGUI(String filename){
		img = new ImageIcon(filename);
		seamCarver = new SeamCarver(new Picture(filename));
		initGUI();
	}
	
	public void initGUI(){
		JPanel cp = (JPanel) this.getContentPane();
		cp.addComponentListener(new resizeListener());
		
		imgHolder = new JLabel(img);
		cp.add(imgHolder);
		
		setTitle("Seam Carving");
		setSize(img.getIconWidth(), img.getIconHeight());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args){
		String imagePath = "chameleon.png";
		SeamCarverGUI seamWindow = new SeamCarverGUI(imagePath);
		seamWindow.setVisible(true);
	}
	
	private class resizeListener extends ComponentAdapter{
		
		@Override
		public void componentResized(ComponentEvent e){
			seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
			ImageIcon t = (ImageIcon) (seamCarver.picture().getJLabel().getIcon());
						
			imgHolder.setIcon(t);
		}
	}
}
