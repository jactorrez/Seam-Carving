import java.awt.Color;

public class SeamCarver {
	
	private Picture picture;
	private int[][] energyGraph, YdistGraph, XdistGraph;
	
	private int width;
	private int height;
	
	// Create a seam carver object based on the given picture
	public SeamCarver(Picture picture){
		if(picture == null)
			throw new NullPointerException("Null picture given");
		
		this.picture = picture;
		this.width = picture.width();
		this.height = picture.height();		
		this.energyGraph = createEnergyGraph(height, width);
		this.YdistGraph = createYDistanceGraph(height, width);
		this.XdistGraph = createXDistanceGraph(height, width);
		
	}
	
	private int[][] createEnergyGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				graph[row][col] = ((Double) energy(col, row)).intValue();
			}
		}
		
		return graph;
	}
	
	private void createXDistanceGraph(){
		
	}
	
	private int[][] createYDistanceGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				graph[row][col] = Integer.MAX_VALUE;
			}
		}
		
		for(int row = 0; row < height-1; row++){
			for(int col = 0; col < width; col++){
				
				if(row == 0){
					graph[0][col] = energyGraph[0][col];
				}	
				
				int sumBy = -1;
				int u = graph[row][col];
				
				for(int i = 1; i <= 3; i++){
					int colSum = col + (sumBy++);
					
					if((colSum < 0) || (colSum >= width)){
						continue;
					} 
					int newDistance = u + this.energyGraph[row+1][colSum];
					int oldDistance = graph[row+1][colSum];
				
					if(newDistance < oldDistance){
						graph[row+1][colSum] = newDistance;
					}
				}
			}
		}
		
		return graph;
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
	
	// Energy of pixel at column c and row r
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
		int[] seam = new int[height];
		
		int currentRow = height-1;
		int minPathCol = 0;
		
		// Find the shortest path to any pixel in the last row
		for(int c = 0; c < width; c++){
			if(YdistGraph[currentRow][c] < YdistGraph[currentRow][minPathCol]){
				seam[currentRow] = minPathCol = c;
			}
		}
		
		// Backtrack by finding the parent paths that led to the previously found shortest path
		for(int r = currentRow; r >= 0; r--){
			int sumTerm = -1;
			
			for(int i = 0; i < 3; i++){
				// Calculate column index to check
				int col = minPathCol + (sumTerm++);
				
				if((col < 0) || (col >= width)){
					continue;
				} 
				
				int minDist = YdistGraph[r][minPathCol];
				int dist = YdistGraph[r-1][col] + energyGraph[r][minPathCol];
				
				if(dist == minDist){
					seam[r-1] = minPathCol = col;
					break;
				}
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
		
		Picture newPicture = new Picture(width-1, height);
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				if(seam[row] == col)
					continue;
				
				Color color = picture.get(col, row);
				newPicture.set(col, row, color);
			}
		}
		
		this.picture = newPicture;
		this.width = width--;
		
		this.energyGraph = createEnergyGraph(height, width);
		this.YdistGraph = createYDistanceGraph(height, width);
		this.XdistGraph = createXDistanceGraph(height, width);
	}

	private int getXGradient(Color right, Color left){
		int Rdiff = ((Double) Math.pow(right.getRed() - left.getRed() , 2)).intValue();
		int Gdiff = ((Double) Math.pow(right.getGreen() - left.getGreen(), 2)).intValue();
		int Bdiff = ((Double) Math.pow(right.getBlue() - left.getBlue(),2)).intValue();
		
		return (Rdiff + Gdiff + Bdiff);
	}
	
	private int getYGradient(Color top, Color bottom){
		int Rdiff = ((Double) Math.pow(bottom.getRed() - top.getRed() , 2)).intValue();
		int Gdiff = ((Double) Math.pow(bottom.getGreen() - top.getGreen(), 2)).intValue();
		int Bdiff = ((Double) Math.pow(bottom.getBlue() - top.getBlue(),2)).intValue();
		
		return (Rdiff + Gdiff + Bdiff);
	}
	
	public static void main(String[] args){
		Picture p = new Picture("6x5.png");
		SeamCarver test = new SeamCarver(p);
		int[] testSeam = test.findVerticalSeam();
		System.out.println(testSeam);
	}
}
