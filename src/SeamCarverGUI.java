import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

public class SeamCarverGUI extends JFrame implements ActionListener{
	private int height;
	private int width;
	
	private ImageIcon img;
	private JLabel imgContainer;
	private JButton carveSeamBtn;
	private JTextField seamCountField;
	private JRadioButton horizontalSeamBtn, verticalSeamBtn;
	private SeamCarver seamCarver;
	
	public SeamCarverGUI(String filename){
		img = new ImageIcon(filename);
		seamCarver = new SeamCarver(new Picture(filename));
		
		height = img.getIconHeight();
		width = img.getIconWidth();
		
		initGUI();
		
		setTitle("Seam Carving");
		setSize(width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void initGUI(){
		JPanel cp = (JPanel) this.getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel optionsPanel = new JPanel(new BorderLayout());
		JPanel radioPanel = new JPanel(new GridLayout(2, 1));
		JPanel textPanel = new JPanel();
		
		// ----- Options Panel ----- 
		
		// Radio Button Panel
		horizontalSeamBtn = new JRadioButton("Horizontal Seam");
		
		verticalSeamBtn = new JRadioButton("Vertical Seam");
		verticalSeamBtn.setSelected(true);
		
		ButtonGroup radioBtnGroup = new ButtonGroup();
		radioBtnGroup.add(horizontalSeamBtn);
		radioBtnGroup.add(verticalSeamBtn);
		
		radioPanel.add(horizontalSeamBtn);
		radioPanel.add(verticalSeamBtn);
		
		// Text Panel
		JLabel pixelLabel = new JLabel("Pixels to remove: ");
		seamCountField = new JTextField(10);
		textPanel.add(pixelLabel);
		textPanel.add(seamCountField);
		textPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		// Seam Carve Button
		carveSeamBtn = new JButton("Begin carving");
		carveSeamBtn.addActionListener(this);

		optionsPanel.add(radioPanel, BorderLayout.WEST);
		optionsPanel.add(textPanel, BorderLayout.CENTER);
		optionsPanel.add(carveSeamBtn, BorderLayout.EAST);
		// --------------------------
		
		imgContainer = new JLabel(img);
		cp.add(imgContainer, BorderLayout.CENTER);
		cp.add(optionsPanel, BorderLayout.NORTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		int seamsCount = Integer.parseInt(seamCountField.getText());
		boolean removeVerticalSeam = verticalSeamBtn.isSelected();
		boolean removeHorizontalSeam = horizontalSeamBtn.isSelected();
		
		if(removeVerticalSeam){
			for(int i = 0; i < seamsCount; i++){
				seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
			}
		} else if(removeHorizontalSeam){
			for(int i = 0; i < seamsCount; i++){
				seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
			}
		}
		
		Picture seamImg = seamCarver.picture();
		ImageIcon seamIcon = (ImageIcon) (seamImg.getJLabel().getIcon());
		imgContainer.setIcon(seamIcon);
	}
	
	public static void main(String[] args){
		String imagePath = "chameleon.png";
		SeamCarverGUI seamWindow = new SeamCarverGUI(imagePath);
		seamWindow.setVisible(true);
	}
}
