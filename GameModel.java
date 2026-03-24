import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameModel 
{
	private int gridSize;
	private int winLength;
	private String[][] board;
	private int movesMade;
	
	public GameModel(int gridSize)
	{
		setGridSize(gridSize);
	}
	
	public void setGridSize(int newSize)
	{
		this.gridSize = newSize;
		calculateWinLength();
		board = new String[gridSize][gridSize];
		resetBoardCells();
		movesMade = 0;
	}
	
	private void calculateWinLength()
	{
		if (gridSize == 3 || gridSize == 4)
		{
			winLength = 3;
		}
		else if (gridSize == 5 || gridSize == 6)
		{
			winLength = 4;
		}
		else if (gridSize == 7 || gridSize == 8)
		{
			winLength = 5;
		}
		else
		{
			// 9 or 10
			winLength = 6;
		}
	}
	
	private void resetBoardCells()
	{
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				board[r][c] = Symbols.EMPTY;
			}
		}
	}
	
	public int getGridSize()
	{
		return gridSize;
	}
	
	public int getWinLength()
	{
		return winLength;
	}
	
	public String getCell(int row, int col)
	{
		return board[row][col];
	}
	
	public boolean isCellEmpty(int row, int col)
	{
		if (row < 0 || row >= gridSize || col < 0 || col >= gridSize)
		{
			return false;
		}
		return board[row][col].isEmpty();
	}

	public boolean makeMove(int row, int col, String symbol)
	{
		Objects.requireNonNull(symbol);
		
		if (row < 0 || row >= gridSize || col < 0 || col >= gridSize)
		{
			return false;
		}
		
		if (!board[row][col].isEmpty())
		{
			return false;
		}
		
		board[row][col] = symbol;
		movesMade++;
		return true;
	}
	
	public void reset()
	{
		resetBoardCells();
		movesMade = 0;
	}
	
	/**
	 * Clear a single cell (e.g. after a bomb explosion), resetting it to empty
	 * and decrementing the move counter so tie detection stays accurate.
	 */
	public void clearCell(int row, int col)
	{
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
		{
			if (!board[row][col].isEmpty())
			{
				board[row][col] = Symbols.EMPTY;
				movesMade = Math.max(0, movesMade - 1);
			}
		}
	}
	
	public WinResult checkForWin()
	{
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c <= gridSize - winLength; c++)
			{
				String startSymbol = board[r][c];
				if (!startSymbol.isEmpty() && checkLine(r, c , 0, 1, startSymbol))
				{
					return new WinResult(true, startSymbol, winningCells(r, c, 0, 1), false);
				}
			}
		}
		
		for (int c = 0; c < gridSize; c++)
		{
			for (int r = 0; r <= gridSize - winLength; r++)
			{
				String startSymbol = board[r][c];
				if (!startSymbol.isEmpty() && checkLine(r, c, 1, 0, startSymbol))
				{
					return new WinResult(true, startSymbol, winningCells(r, c, 1, 0), false);
				}
			}
		}
		
		for (int r = 0; r <= gridSize - winLength; r++)
		{
			for (int c = 0; c <= gridSize - winLength; c++)
			{
				String startSymbol = board[r][c];
				if (!startSymbol.isEmpty() && checkLine(r, c, 1, 1, startSymbol))
				{
					return new WinResult(true, startSymbol, winningCells(r, c, 1, 1), false);
				}
			}
		}
		
		for (int r = 0; r <= gridSize - winLength; r++)
		{
			for (int c = winLength - 1; c < gridSize; c++)
			{
				String startSymbol = board[r][c];
				if (!startSymbol.isEmpty() && checkLine(r, c, 1, -1, startSymbol))
				{
					return new WinResult(true, startSymbol, winningCells(r, c, 1, -1), false);
				}
			}
		}
		
		if (movesMade == gridSize * gridSize)
		{
			return new WinResult(false, "", new ArrayList<>(), true);
		}
		
		return new WinResult(false, "", new ArrayList<>(), false);
	}
	
	private boolean checkLine(int startRow, int startCol, int deltaRow, int deltaCol, String symbol)
	{
		for (int i = 0; i < winLength; i++)
		{
			int row = startRow + i * deltaRow;
			int col = startCol + i * deltaCol;
			if (!board[row][col].equals(symbol))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private List<Point> winningCells(int startRow, int startCol, int deltaRow, int deltaCol)
	{
		List<Point> result = new ArrayList<>();
		for (int i = 0; i < winLength; i++)
		{
			int row = startRow + i * deltaRow;
			int col = startCol + i * deltaCol;
			result.add(new Point(row, col));
		}
		
		return result;
	}
}
