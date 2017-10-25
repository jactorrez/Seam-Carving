import java.awt.Color;

public class SeamCarver {
	
	private Picture picture;
	private int[][] energyGraph, YdistGraph, XdistGraph;
	
	private int width;
	private int minEnergy;
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
		
		for(int h = 0; h < height; h++){
			System.out.print("[");
			for(int k : graph[h]){
				System.out.print(k + ", ");
			}
			System.out.print("]");
			System.out.println();
		}
		
		return graph;
	}
	
	private int[][] createXDistanceGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		return graph;
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
	public double energy(int col, int row){
		int width = this.width-1;
		int height = this.height-1;
		
		if((col < 0 || col > width) || (row < 0 || row > height))
			throw new IndexOutOfBoundsException("Coordinates must be between image dimension bounds");
	
		// Calculating the x-gradient 
		int rightPos = (col + 1 > width) ? 0 : col + 1; 
		int leftPos = (col - 1 < 0) ? width : col - 1;
		Color rightNeighbor = picture.get(rightPos, row);
		
		Color leftNeighbor = picture.get(leftPos, row);
		
		int xGradient = calculateGradient(rightNeighbor, leftNeighbor);
		
		// Calculating the y-gradient 
		int topPos = (row - 1 < 0) ? height : row - 1;
		int bottomPos = (row + 1 > height) ? 0 : row + 1;
		
		Color topNeighbor = picture.get(col, topPos);
		Color bottomNeighbor = picture.get(col, bottomPos);
		int yGradient = calculateGradient(topNeighbor, bottomNeighbor);

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
		
		this.minEnergy = YdistGraph[currentRow][minPathCol];
		// Backtrack by finding the parent paths that led to the previously found shortest path
		for(int r = currentRow; r > 0; r--){
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
		
		this.energyGraph = createEnergyGraph(height, this.width);
		this.YdistGraph = createYDistanceGraph(height, this.width);
		this.XdistGraph = createXDistanceGraph(height, this.width);
	}

	private int calculateGradient(Color minuend, Color subtrahend){
		int Rdiff = minuend.getRed() - subtrahend.getRed();
		int Gdiff = minuend.getGreen() - subtrahend.getGreen();
		int Bdiff = minuend.getBlue() - subtrahend.getBlue();
		
		return ((Double) ((Math.pow(Rdiff, 2)) + (Math.pow(Gdiff, 2)) + (Math.pow(Bdiff, 2)))).intValue();
	}
	
	public static void main(String[] args){
		Picture p = new Picture("6x5.png");
		SeamCarver test = new SeamCarver(p);
		int[] testSeam = test.findVerticalSeam();
		System.out.println("Min energy: " + test.minEnergy);
		
		for(int col : testSeam){
			System.out.print(col + " ");
		}
		System.out.println(testSeam);
	}
}
