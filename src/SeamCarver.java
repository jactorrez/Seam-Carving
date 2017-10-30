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
		// Width of image
		this.width = picture.width();
		// Height of image
		
		this.height = picture.height();		
		this.energyGraph = createEnergyGraph(height, width);
		this.YdistGraph = createYDistanceGraph(height, width);
		this.XdistGraph = createXDistanceGraph(height, width);
	}
	

	/*
	 * Returns a representation of what the current image looks like
	 */
	public Picture picture(){
		return picture;
	}
	
	/*
	 * Returns the width of the current image
	 */
	public int width(){
		return width;
	}
	
	/*
	 * Returns the height of the current image
	 */
	public int height(){
		return height;
	}
	
	/*
	 * Calculates the energy of the pixel at the given column and given row
	 */
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
	
	/*
	 * Calculates the minimum cost/distance seam from any left pixel to a right pixel
	 */
	public int[] findHorizontalSeam(){
		int[] seam = new int[width];
		
		int currentCol = width-1;
		int minPathRow = 0;
		
		// Find the smallest distance value in the right-most column
		for(int r = 0; r < height; r++){
			if(XdistGraph[r][currentCol] < XdistGraph[minPathRow][currentCol]){
				seam[currentCol] = minPathRow = r;
			}
		}
		
		// Backtrack from the current minimum cost pixel to create a minimum cost path that reaches
	    // a leftmost pixel
		for(int c = currentCol; c > 0; c--){
			int sumTerm = -1;
			
			for(int i = 0; i < 3; i++){
				// Calculates row index to check
				int row = minPathRow + (sumTerm++);
				
				if((row < 0) || (row >= height)){
					continue;
				} 
				
				int minDist = XdistGraph[minPathRow][c];
				int dist = XdistGraph[row][c-1] + energyGraph[minPathRow][c];
				
				// If the pixel at (row, c-1) led to us based on our distance in XdistGraph,
				// add the pixels row to the seam
				if(dist == minDist){
					seam[c-1] = minPathRow = row;
					break;
				}
			}
		}
		return seam;
	}
	
	/*
	 * Removes the given horizontal pixel seam from the current picture
	 */
	public void removeHorizontalSeam(int[] seam){
		if(seam == null)
			throw new NullPointerException("Null argument given");
		
		// Blank image used to recreate old image with the pixels in the given seam removed
		Picture newPicture = new Picture(width, height-1);
		
		// Loop through current image
		for(int col = 0; col < width; col++){
			for(int row = 0, cursor = 0; row < height; row++){
				// if the row we're on matches the row to remove
				// at our current column (based on seam), don't add it to the new picture
				if(row == seam[col])
					continue;
				
				Color color = picture.get(col, row);
				newPicture.set(col, cursor++, color);
			}
		}
		
		this.picture = newPicture;
		this.height = height-1;
		
		this.energyGraph = createEnergyGraph(height, width);
		this.YdistGraph = createYDistanceGraph(height, width);
		this.XdistGraph = createXDistanceGraph(height, width);
	}
	
	/*
	 * Calculates the minimum cost/distance seam from any top pixel to a bottom pixel
	 */
	public int[] findVerticalSeam(){	
		int[] seam = new int[height];
		
		int currentRow = height-1;
		int minPathCol = 0;
		
		// Find pixel in the bottom-most row with the lowest distance/cost
		for(int c = 0; c < width; c++){
			if((YdistGraph[currentRow][c] < YdistGraph[currentRow][minPathCol])){
				seam[currentRow] = minPathCol = c;
			}
		}
		
		// Backtrack from the current minimum cost pixel to create a minimum cost path that reaches
		// a topmost pixel
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
	
	/*
	 * Removes the given vertical pixel seam from the current picture
	 */
	public void removeVerticalSeam(int[] seam){
		if(seam == null)
			throw new NullPointerException("Null argument given");
		
		Picture newPicture = new Picture(width-1, height);
		
		for(int row = 0; row < height; row++){
			for(int col = 0, cursor = 0; col < width; col++){
				if(col == seam[row]){
					continue;
				}
				
				Color color = picture.get(col, row);
				newPicture.set(cursor++, row, color);
			}
		}
		
		this.picture = newPicture;
		this.width = width-1;
		
		this.energyGraph = createEnergyGraph(height, this.width);
		this.YdistGraph = createYDistanceGraph(height, this.width);
		this.XdistGraph = createXDistanceGraph(height, this.width);
	}
	
	/* ----------- Utility methods ----------- */
	
	/*
	 * Creates the energy graph by assigning each pixel its respective energy value
	 */
	private int[][] createEnergyGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				graph[row][col] = ((Double) energy(col, row)).intValue();
			}
		}
		return graph;
	}
	
	/*
	 * Creates the horizontal distance graph by assigning each pixel the minimum cost to reach it 
	 * from any left-most column
	 */
	private int[][] createXDistanceGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		// Initially assigns each pixel an arbitrary distance value
		for(int col = 1; col < width; col++){
			for(int row = 0; row < height; row++){
				graph[row][col] = Integer.MAX_VALUE;
			}
		}
		
		
		// Loop through the entire image
		for(int c = 0; c < width-1; c++){
			for(int r = 0; r < height; r++){
				
				// If we're on the leftmost column, their distance from a starting point 0 is
				// unchanged from their initial energy
				if(c == 0){
					graph[r][0] = energyGraph[r][0];
				}	
				
				// Term used to calculate allowable columns to inspect
				int sumTerm = -1;
				
				// Returns current distance from a leftmost column to the current pixel at (row, column)
				int currentDist = graph[r][c];
				
				// Calculates distance to the 3 pixels positioned immediately ahead of it
				for(int i = 1; i <= 3; i++){
					
					// Row of pixel to compare distance to
					int row = r + (sumTerm++);
					
					// If row exceeds image dimension bounds, ignore it
					if((row < 0) || (row >= height)){
						continue;
					} 
					
					int newDistance = currentDist + this.energyGraph[row][c+1];
					int oldDistance = graph[row][c+1];
				
					// Relax distance if cost/distance to arrive at pixel (row, c+1) is decreased
					// when reaching it from our current pixel
					if(newDistance < oldDistance){
						graph[row][c+1] = newDistance;
					}
				}
			}
		}
		
		return graph;
	}
	
	/*
	 * Creates the vertical distance graph by assigning each pixel the minimum cost/distance to reach it 
	 * from any top-most row
	 */
	
	private int[][] createYDistanceGraph(int height, int width){
		int[][] graph = new int[height][width];
		
		// Initially assigns each pixel an arbitrary distance value
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				graph[row][col] = Integer.MAX_VALUE;
			}
		}
		
		// Loop through the entire image
		for(int row = 0; row < height-1; row++){
			for(int col = 0; col < width; col++){
				
				// If we're on the top-most row, their distance from a starting point 0 is
				// unchanged from their initial energy
				if(row == 0){
					graph[0][col] = energyGraph[0][col];
				}	
				
				// Term used to calculate allowable rows to inspect
				int sumTerm = -1;
				int currentDist = graph[row][col];
				
				// Calculates distance to the 3 pixels positioned immediately ahead of it
				for(int i = 1; i <= 3; i++){
					
					// Column of pixel to compare distance to
					int colSum = col + (sumTerm++);
					
					// If column exceeds image dimension bounds, ignore it
					if((colSum < 0) || (colSum >= width)){
						continue;
					} 
					
					int newDistance = currentDist + this.energyGraph[row+1][colSum];
					int oldDistance = graph[row+1][colSum];
				
					// Relax distance if cost/distance to arrive at pixel (row, c+1) is decreased
					// when reaching it from our current pixel
					if(newDistance < oldDistance){
						graph[row+1][colSum] = newDistance;
					}
				}
			}
		}
		
		return graph;
	}
	
	/*
	 * Calculates a color based on the average RGB values of colors a & b
	 */
	private Color averageColors(Color a, Color b){
		int redAverage = (a.getRed() + b.getRed()) / 2;
		int greenAverage = (a.getGreen() + b.getGreen()) / 2;
		int blueAverage = (a.getBlue() + b.getBlue()) / 2;
		
		Color avgColor = new Color(redAverage, greenAverage, blueAverage);
		
		return avgColor;
	}
	
	/*
	 * Calculates gradient necessary to assign a given pixel an energy value
	 */
	private int calculateGradient(Color minuend, Color subtrahend){
		int Rdiff = minuend.getRed() - subtrahend.getRed();
		int Gdiff = minuend.getGreen() - subtrahend.getGreen();
		int Bdiff = minuend.getBlue() - subtrahend.getBlue();
		
		return ((Double) ((Math.pow(Rdiff, 2)) + (Math.pow(Gdiff, 2)) + (Math.pow(Bdiff, 2)))).intValue();
	}
	
	public static void main(String[] args){
		Picture p = new Picture("chameleon.png");
		SeamCarver test = new SeamCarver(p);
		
		for(int i = 0; i < 200; i++){
			test.removeVerticalSeam(test.findVerticalSeam());
		}
		
		test.picture().show();
	}
}
