import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Manages the placement and tracking of bombs in the grid.
 * Determines the number of bombs based on grid size and places them randomly.
 */
public class BombManager
{
	private static final int[][] BOMB_COUNTS = {
		{3, 1},
		{4, 2},
		{5, 3},
		{6, 5},
		{7, 9},
		{8, 12},
		{9, 16},
		{10, 20}
	};
	
	private Set<String> bombPositions;
	private int gridSize;
	private Random random;
	
	public BombManager(int gridSize)
	{
		this.gridSize = gridSize;
		this.bombPositions = new HashSet<>();
		this.random = new Random();
		placeBombs();
	}
	
	/**
	 * Determine the number of bombs for a given grid size
	 */
	private int getBombCount(int size)
	{
		for (int[] pair : BOMB_COUNTS)
		{
			if (pair[0] == size)
			{
				return pair[1];
			}
		}
		// Default to 1 bomb if size not found
		return 1;
	}
	
	/**
	 * Place bombs randomly on the board
	 */
	private void placeBombs()
	{
		bombPositions.clear();
		int bombCount = getBombCount(gridSize);
		
		while (bombPositions.size() < bombCount)
		{
			int row = random.nextInt(gridSize);
			int col = random.nextInt(gridSize);
			String position = row + "," + col;
			bombPositions.add(position);
		}
	}
	
	/**
	 * Check if a tile should be a bomb
	 */
	public boolean isBombPosition(int row, int col)
	{
		return bombPositions.contains(row + "," + col);
	}
	
	/**
	 * Reset bombs for a new game with a potentially new grid size
	 */
	public void reset(int newGridSize)
	{
		this.gridSize = newGridSize;
		bombPositions.clear();
		placeBombs();
	}
	
	/**
	 * Get the set of bomb positions for debugging/testing
	 */
	public Set<String> getBombPositions()
	{
		return new HashSet<>(bombPositions);
	}
}