import java.awt.Color;
import java.util.HashMap;

import structs.graphs.Digraph;
import structs.graphs.Vertex;
import structs.priorityqueue.AdaptableHeap;
import structs.priorityqueue.Entry;
import structs.graphs.Edge;

public class SeamCarver {
	
	private Picture picture;
	private int[][] energyGraph, distGraph;
	
	private int width;
	private int height;
	
	// Create a seam carver object based on the given picture
	public SeamCarver(Picture picture){
		if(picture == null)
			throw new NullPointerException("Null picture given");
		
		this.picture = picture;
		this.width = picture.width();
		this.height = picture.height();
		
		System.out.println("Dimensions: " + height + " x " + width);
		
		this.energyGraph = new int[height][width];
		this.distGraph = new int[height][width];
		
		//int energy = ((Double) energy(col, row)).intValue();
		createEnergyGraph(energyGraph);
		createDistanceGraph(energyGraph, distGraph);
	}
	
	private int[][] createEnergyGraph(int[][] graph){
		int height = graph.length;
		int width = graph[0].length;
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				graph[row][col] = ((Double) energy(col, row)).intValue();
			}
		}
		
		return graph;
	}
	
	private int[][] createDistanceGraph(int[][] energy, int[][] distTo){
		int height = distTo.length;
		int width = distTo[0].length;
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				distTo[row][col] = Integer.MAX_VALUE;
			}
		}
		
		for(int row = 0; row < height-1; row++){
			for(int col = 0; col < width; col++){
				
				if(row == 0){
					distTo[0][col] = energyGraph[0][col];
				}	
				
				int sumBy = -1;
				int u = distTo[row][col];
				
				for(int i = 1; i <= 3; i++){
					int colSum = col + (sumBy++);
					
					if((colSum < 0) || (colSum >= width)){
						continue;
					} 
					
					int newDistance = u + energy[row+1][colSum];
					int oldDistance = distTo[row+1][colSum];
				
					if(newDistance < oldDistance){
						distTo[row+1][colSum] = newDistance;
					}
				}
			}
		}
		
		return distTo;
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
	public double energy(int c, int r){
		int width = this.width-1;
		int height = this.height-1;
		if((c < 0 || c > width) || (r < 0 || r > height))
			throw new IndexOutOfBoundsException("Coordinates must be between image dimension bounds");
		
		int col = c;
		int row = r;
		
		// Calculating the x-gradient 
		int rightPos = (col + 1 > width) ? 0 : col + 1; 
		int leftPos = (col - 1 < 0) ? width : col - 1;
		
		Color rightNeighbor = picture.get(rightPos, row);
		Color leftNeighbor = picture.get(leftPos, row);
		int xGradient = getXGradient(rightNeighbor, leftNeighbor);
		
		// Calculating the y-gradient 
		int topPos = (row - 1 < 0) ? height : row - 1;
		int bottomPos = (row + 1 > height) ? 0 : row + 1;
		
		Color topNeighbor = picture.get(col, topPos);
		Color bottomNeighbor = picture.get(col, bottomPos);
		int yGradient = getYGradient(topNeighbor, bottomNeighbor);
		
		return (xGradient + yGradient);
	}
	
	// Sequence of indices for horizontal seam
	public int[] findHorizontalSeam(){
		int[] seam = new int[width];
		
		return new int[]{1,2,3};
	}
	
	// Sequence of indices for vertical seam
	public int[] findVerticalSeam(){
		return new int[]{2,3,2};
	}
	
	// Remove horizontal seam for current picture
	public void removeHorizonalSeam(int[] seam){
		if(seam == null)
			throw new NullPointerException("Null argument given");
		
	}
	
	// Remove vertical seam for current picture 
	public void removeVerticalSeam(int[] seam){
		if(seam == null)
			throw new NullPointerException("Null argument given");
		
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
	
	public static void main(String[] args){
		Picture p = new Picture("6x5.png");
		SeamCarver test = new SeamCarver(p);
		int[] testSeam = test.findVerticalSeam();
		System.out.println(testSeam);
	}
}
