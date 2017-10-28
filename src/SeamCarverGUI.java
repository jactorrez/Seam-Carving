import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.*;

public class SeamCarverGUI extends JFrame{
	ImageIcon img;
	JLabel imgHolder;
	SeamCarver seamCarver;
	int currentHeight; 
	int currentWidth;
	
	public SeamCarverGUI(String filename){
		img = new ImageIcon(filename);
		seamCarver = new SeamCarver(new Picture(filename));
		currentHeight = img.getIconHeight();
		currentWidth = img.getIconWidth();
		
		initGUI();
	}
	
	public void initGUI(){
		JPanel cp = (JPanel) this.getContentPane();
		cp.addComponentListener(new resizeListener());
		cp.setLayout(new BorderLayout());
		
		imgHolder = new JLabel(img);
		cp.add(imgHolder);
		
		setTitle("Seam Carving");
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args){
		String imagePath = "chameleon.png";
		SeamCarverGUI seamWindow = new SeamCarverGUI(imagePath);
		seamWindow.setVisible(true);
	}
	
	private void updateHeight(int h, int w){
		this.currentHeight = h;
		this.currentWidth = w;
	}
	
	
	private class resizeListener extends ComponentAdapter{
		
		@Override
		public void componentResized(ComponentEvent evt){
			int oldHeight = currentHeight;
			int oldWidth = currentWidth;
			
			int newHeight = getHeight();
			int newWidth = getWidth();
			
			Image scaled;
			
			boolean isHorizontalDecrease = (oldWidth > newWidth);
			boolean isHorizontalIncrease = (oldWidth < newWidth);
			boolean isVerticalDecrease = (oldHeight > newHeight);
			boolean isVerticalIncrease = (oldHeight < newHeight);
			
			if(isHorizontalDecrease){
				System.out.println("Horizontal Decrease");
				seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
				Picture seamImg = seamCarver.picture();
				ImageIcon seamIcon = (ImageIcon) (seamImg.getJLabel().getIcon());
				imgHolder.setIcon(seamIcon);
			} else if(isVerticalDecrease){
				System.out.println("Vertical Decrease");
				seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
				Picture seamImg = seamCarver.picture();
				ImageIcon seamIcon = (ImageIcon) (seamImg.getJLabel().getIcon());
				imgHolder.setIcon(seamIcon);
			} else{
				System.out.println("Nothing");
			}
			
			updateHeight(newHeight, newWidth);
		}
	}
}
