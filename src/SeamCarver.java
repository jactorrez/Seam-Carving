import java.awt.Color;
import java.util.HashMap;

import structs.graphs.Digraph;
import structs.graphs.Vertex;

public class SeamCarver {
	
	private Picture picture;
	private Digraph<Integer,Boolean> energyGraph;
	private HashMap<Pixel, Vertex<Integer>> pixelToVertexMap = new HashMap<>();
	
	private int width;
	private int height;
	
	// Create a seam carver object based on the given picture
	public SeamCarver(Picture picture){
		if(picture == null)
			throw new NullPointerException("Null picture given");
		
		this.picture = picture;
		this.width = picture.width();
		this.height = picture.height();
		
		this.energyGraph = new Digraph<>();
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				Pixel pixel = new Pixel(col, row);
				int energy = getEnergy(pixel);
				pixelToVertexMap.put(pixel, energyGraph.insertVertex(energy));
			}
		}
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				Pixel pixel = new Pixel(col, row);
				Vertex<Integer> v = pixelToVertexMap.get(pixel);
				
			}
		}
	}
	
	// Calculate energy of pixel 
	// The energy of a pixel is the square of the x gradient + the square of the y gradient 
	// to calculate the x gradient, calculate the central differences of R, G, and B components between 
	// pixel (x + 1, y) and pixel (x-1, y),
	//	
	// The energy of pixel (1,2) is calculated from pixels (0,2) and (2,2) for the x-gradient 
	//
	// To handle pixels on the borders of the image, calculate energy by defining the leftmost and rightmost columns 
	// as adjacent and the topmost and bottommost rows as adjacent.
	
	private int getEnergy(Pixel pixel){
		int col = pixel.getColumn();
		int row = pixel.getRow();
		
		int rightPos = (col + 1 >= width()) ? 0 : col + 1; 
		int leftPos = (col - 1 < 0) ? width()-1 : col - 1;
		
		Color rightNeighbor = new Color(picture.getRGB(rightPos, row));
		Color leftNeighbor = new Color(picture.getRGB(leftPos, row));
		int xGradient = getXGradient(rightNeighbor, leftNeighbor);
		
		int topPos = (row - 1 < 0) ? height()-1 : row - 1;
		int bottomPos = (row + 1 > height()-1) ? 0 : row + 1;
		
		Color topNeighbor = new Color(picture.getRGB(col, topPos));
		Color bottomNeighbor = new Color(picture.getRGB(col, bottomPos));
		int yGradient = getYGradient(topNeighbor, bottomNeighbor);

		int energy = xGradient + yGradient;
		return energy;
	}
	
	private int getXGradient(Color right, Color left){
		int Rdiff = ((Double) Math.pow(right.getRed() - left.getRed() , 2)).intValue();
		int Gdiff = ((Double) Math.pow(right.getGreen() - left.getGreen(), 2)).intValue();
		int Bdiff = ((Double) Math.pow(right.getBlue() - left.getBlue(),2)).intValue();
		
		int gradient = Rdiff + Gdiff + Bdiff;
		return gradient;
	}
	
	private int getYGradient(Color top, Color bottom){
		int Rdiff = ((Double) Math.pow(bottom.getRed() - top.getRed() , 2)).intValue();
		int Gdiff = ((Double) Math.pow(bottom.getGreen() - top.getGreen(), 2)).intValue();
		int Bdiff = ((Double) Math.pow(bottom.getBlue() - top.getBlue(),2)).intValue();
		
		int gradient = Rdiff + Gdiff + Bdiff;
		return gradient;
	}
	
//	private Vertex<Integer> getVertex(int col, int row){
//
//	}
	
	// Current picture
	public Picture picture(){
		return picture;
	}
	
	// Width of current picture
	public int width(){
		return width;
	}
	
	// Height of current picture
	public int height(){
		return height;
	}
	
	// Energy of pixel at column x and row y
	public double energy(int x, int y){
		return 0.0;
	}
	
	// Sequence of indices for horizontal seam
	public int[] findHorizontalSeam(){
		return new int[]{1,2,3};
	}
	
	// Sequence of indices for vertical seam
	public int[] findVerticalSeam(){
		return new int[]{1,2,3};
	}
	
	// Remove horizontal seam for current picture
	public void removeHorizonalSeam(int[] seam){
		
	}
	
	// Remove vertical seam for current picture 
	public void removeVerticalSeam(int[] seam){
		
	}
	
	private class Pixel {
		private int column;
		private int row; 
		private int hash = 0;
		
		public Pixel(int col, int row){
			this.column = col;
			this.row = row;
		}
		
		public int getColumn(){
			return column;
		}
		
		public int getRow(){
			return row;
		}
		
		public int hashCode(){
			if(hash != 0){
				return hash;
			} else{
				hash = 17;
				hash = 31 * hash + Integer.hashCode(column);
				hash = 31 * hash + Integer.hashCode(row);
			}
			
			return hash;
		}
	}
	
	public static void main(String[] args){
		Picture p = new Picture("porsche.jpg");
		SeamCarver test = new SeamCarver(p);
	}
}
