import java.awt.Color;
import java.util.HashMap;

import structs.graphs.Digraph;
import structs.graphs.Vertex;
import structs.priorityqueue.AdaptableHeap;
import structs.priorityqueue.Entry;
import structs.graphs.Edge;

public class SeamCarver {
	
	private Picture picture;
	private Digraph<Integer,Boolean> energyGraph;
	private HashMap<Pixel, Vertex<Integer>> pixelToVertexMap = new HashMap<>();
	private HashMap<Vertex<Integer>, Pixel> vertexToPixelMap = new HashMap<>();
	
	private int width;
	private int height;
	
	// Create a seam carver object based on the given picture
	public SeamCarver(Picture picture){
		if(picture == null)
			throw new NullPointerException("Null picture given");
		
		this.picture = picture;
		this.width = picture.width()-1;
		this.height = picture.height()-1;
		
		System.out.println("Dimensions: " + height + " x " + width);
		
		this.energyGraph = new Digraph<>();
		
		for(int row = 0; row <= height; row++){
			for(int col = 0; col <= width; col++){
				Pixel pixel = new Pixel(col, row);
			
				int energy = ((Double)energy(col, row)).intValue();
				pixelToVertexMap.put(pixel, energyGraph.insertVertex(energy));
			}
		}
		
		for(int row = 0; row <= height; row++){
			for(int col = 0; col <= width; col++){
				Vertex<Integer> current = getVertexByPixel(col, row);
				//System.out.println("col/row of pixel " + col + " " + row);
				vertexToPixelMap.put(current, new Pixel(col, row));
				//System.out.println("returned: " + getPixelByVertex(current).getColumn() + " and " + getPixelByVertex(current).getRow());
				
				Vertex<Integer> bottomLeft = (col-1 < 0) ? null : getVertexByPixel(col-1,row+1);
				Vertex<Integer> bottom = (row + 1 > height)? null : getVertexByPixel(col, row+1);
				Vertex<Integer> bottomRight = (col + 1 > width) ? null : getVertexByPixel(col+1, row+1);
				
				if(bottomLeft != null){
					energyGraph.insertEdge(current, bottomLeft, true);
				}
				
				if(bottom != null){
					energyGraph.insertEdge(current, bottom, true);
				}
				
				if(bottomRight != null){
					energyGraph.insertEdge(current, bottomRight, true);
				}
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
	
	private Vertex<Integer> getVertexByPixel(int col, int row){
		if(((col < 0 || col > width)) || ((row < 0 || row > height)))
			return null;
		
		return pixelToVertexMap.get(new Pixel(col, row));
	}
	
	private Pixel getPixelByVertex(Vertex<Integer> v){
		if(v == null){
			throw new NullPointerException("Null value given");
		}
		
		return vertexToPixelMap.get(v);
	}
	
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
		if((x < 0 || x > width) || (y < 0 || y > height))
			throw new IndexOutOfBoundsException("Coordinates must be between pixel dimension bounds");
		
		int col = x;
		int row = y;
		
		// Calculating the x-gradient 
		int rightPos = (col + 1 > width) ? 0 : col + 1; 
		int leftPos = (col - 1 < 0) ? width : col - 1;
		
		Color rightNeighbor = new Color(picture.getRGB(rightPos, row));
		Color leftNeighbor = new Color(picture.getRGB(leftPos, row));
		int xGradient = getXGradient(rightNeighbor, leftNeighbor);
		
		// Calculating the y-gradient 
		int topPos = (row - 1 < 0) ? height : row - 1;
		int bottomPos = (row + 1 > height) ? 0 : row + 1;
		
		Color topNeighbor = new Color(picture.getRGB(col, topPos));
		Color bottomNeighbor = new Color(picture.getRGB(col, bottomPos));
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
		int[] seam = new int[height];
		int min = Integer.MAX_VALUE;
		
		for(int col = 0; col <= width; col++){
			int currentMin = 0;
			int currentRow = 0;
			int[] currentSeam = new int[height];
			Vertex<Integer> source = getVertexByPixel(col, 0);
			
			AdaptableHeap<Integer, Vertex<Integer>> pq = new AdaptableHeap<>();
			pq.insert(source.getElement(), source);
			
			while(!pq.isEmpty()){
				System.out.println("searching");
				Entry<Integer, Vertex<Integer>> pqMin = pq.removeMin();
				Vertex<Integer> currentV = pqMin.getValue(); 
				Pixel currentPixel = getPixelByVertex(currentV);
				System.out.println("Current pixel row: " + currentRow);
				System.out.println(currentPixel.getRow());
				
				if(currentPixel.getRow() == height){
					System.out.println("reached height of " + height + " at row " + currentRow);
					currentMin = pqMin.getKey();
					currentSeam[currentRow++] = currentPixel.getColumn(); 
					
					if(currentMin < min){
						min = currentMin;
						seam = currentSeam;
					}
					
					break;
				}
				
				seam[currentRow++] = currentPixel.getColumn();
				
				for(Edge<Boolean> e : energyGraph.outgoingEdges(currentV)){
					Vertex<Integer> nextV = energyGraph.opposite(currentV, e);
					int lengthToNext = currentV.getElement() + nextV.getElement();
					pq.insert(lengthToNext, nextV);
				}
				
				currentMin = pqMin.getKey();
			}
		}

		return seam;
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
		
		@Override
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
		
		@Override
		public boolean equals(Object o){
			
			// performance optimization 
			
			// self check
			if (this == o){
				return true;
			}
			
			// null check
			if (o == null)
				return false;
			
			if (getClass() != o.getClass()){
				return false;
			}
			
			Pixel p = (Pixel) o;
			// field comparison
			return ((column == p.column) && (row == p.row));
		}
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
		Picture p = new Picture("porsche.jpg");
		SeamCarver test = new SeamCarver(p);
		int[] testSeam = test.findVerticalSeam();
		System.out.println(testSeam);
	}
}
