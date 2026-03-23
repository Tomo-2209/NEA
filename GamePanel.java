import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GamePanel extends JPanel
{
	private Tile[][] board;
	private int gridSize;
	private GameController controller;
	private BombManager bombManager;
	private WinFlashAnimation winFlash;
	private int currentFontSize = 48;
	
	public GamePanel(GameController controller, int gridSize)
	{
		this.controller = controller;
		this.gridSize = gridSize;
		this.bombManager = new BombManager(gridSize);
		initialiseGrid(gridSize);
		setBackground(Color.darkGray);
	}
	
	public void setController(GameController controller)
	{
		this.controller = controller;
	}
	
	private void initialiseGrid(int gridSize)
	{
		removeAll();
		setLayout(new GridLayout(gridSize, gridSize));
		board = new Tile[gridSize][gridSize];
		
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				Tile tile;
				
				// Create either a BombTile or StandardTile based on bomb manager
				if (bombManager.isBombPosition(r, c))
				{
					tile = new BombTile(r, c, this);
				}
				else
				{
					tile = new StandardTile(r, c);
				}
				
				board[r][c] = tile;
				add(tile);
				
				// Add action listener to handle clicks
				tile.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (controller != null)
						{
							handleTileClick(tile);
						}
					}
				});
			}
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Returns true while any bomb on the board is currently animating (fuse or explosion).
	 * All tile clicks are blocked during this period.
	 */
	public boolean isBombDetonating()
	{
		if (board == null)
		{
			return false;
		}
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				if (board[r][c] instanceof BombTile && ((BombTile) board[r][c]).isActivated())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Handle tile click - dispatch to appropriate tile type handler
	 */
	private void handleTileClick(Tile tile)
	{
		// Block all input while a bomb animation is running
		if (isBombDetonating())
		{
			return;
		}

		if (tile instanceof BombTile)
		{
			BombTile bomb = (BombTile) tile;
			bomb.onClick();
			controller.handleBombClick(bomb.getRow(), bomb.getCol());
		}
		else if (tile instanceof StandardTile)
		{
			StandardTile standard = (StandardTile) tile;
			controller.handleTileClick(standard.getRow(), standard.getCol());
		}
	}
	
	/**
	 * Update a standard tile with a player symbol
	 */
	public void updateTile(int row, int col, String symbol)
	{
		if (board[row][col] instanceof StandardTile)
		{
			((StandardTile) board[row][col]).setSymbol(symbol);
		}
	}
	
	/**
	 * Clear a tile visually and in the model (used externally if needed).
	 */
	public void clearTile(int row, int col)
	{
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
		{
			if (board[row][col] instanceof StandardTile)
			{
				((StandardTile) board[row][col]).reset();
				clearCellInModel(row, col);
			}
		}
	}
	
	/**
	 * Clear a single cell in the model via the controller, guarding against a null controller.
	 */
	private void clearCellInModel(int row, int col)
	{
		if (controller != null)
		{
			controller.clearCell(row, col);
		}
	}
	
	/**
	 * Start a flashing highlight animation on the winning tiles.
	 * Any previous flash animation is stopped first.
	 */
	public void flashWinningCells(List<Point> cells)
	{
		stopWinFlash();
		List<Tile> winTiles = new ArrayList<>();
		for (Point p : cells)
		{
			winTiles.add(board[p.x][p.y]);
		}
		winFlash = new WinFlashAnimation(winTiles);
		winFlash.start();
	}
	
	/**
	 * Stop the win-flash animation if one is running.
	 */
	private void stopWinFlash()
	{
		if (winFlash != null)
		{
			winFlash.stop();
			winFlash = null;
		}
	}
	
	/**
	 * Called by a BombTile when its explosion animation finishes.
	 * Clears the 3×3 area around the bomb in both the view and the model,
	 * then converts the bomb tile itself into a playable StandardTile so
	 * players can place symbols on it again.
	 * Finally notifies the controller so it can switch the active player.
	 */
	public void onBombExploded(int bombRow, int bombCol)
	{
		for (int r = bombRow - 1; r <= bombRow + 1; r++)
		{
			for (int c = bombCol - 1; c <= bombCol + 1; c++)
			{
				if (r < 0 || c < 0 || r >= gridSize || c >= gridSize)
				{
					continue;
				}
				
				if (r == bombRow && c == bombCol)
				{
					// Replace the bomb tile with a playable standard tile
					convertToStandardTile(r, c);
				}
				else if (board[r][c] instanceof BombTile)
				{
					// A neighbouring bomb was also in the blast radius — convert it
					convertToStandardTile(r, c);
				}
				else if (board[r][c] instanceof StandardTile)
				{
					// Clear the symbol in the view and in the model
					((StandardTile) board[r][c]).reset();
					clearCellInModel(r, c);
				}
			}
		}
		
		// Now that the animation has fully finished, hand control back to the controller
		if (controller != null)
		{
			controller.onBombAnimationFinished();
		}
	}
	
	/**
	 * Replace the tile at (row, col) with a fresh, empty StandardTile.
	 * The new tile gets the current font size and an action listener so it
	 * behaves identically to any other standard tile on the board.
	 */
	private void convertToStandardTile(int row, int col)
	{
		Tile oldTile = board[row][col];
		int index = row * gridSize + col;
		
		remove(oldTile);
		
		StandardTile newTile = new StandardTile(row, col);
		newTile.setFontSize(currentFontSize);
		newTile.addActionListener(e -> {
			if (controller != null)
			{
				handleTileClick(newTile);
			}
		});
		
		board[row][col] = newTile;
		add(newTile, index);
		revalidate();
		repaint();
	}
	
	/**
	 * Stop all active bomb animations before the grid is torn down.
	 * Prevents a stale timer callback from modifying the new grid.
	 */
	private void stopAllBombAnimations()
	{
		if (board == null)
		{
			return;
		}
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				if (board[r][c] instanceof BombTile)
				{
					((BombTile) board[r][c]).reset();
				}
			}
		}
	}
	
	public int getGridSize()
	{
		return gridSize;
	}
	
	public void reset(int newGridSize)
	{
		stopWinFlash();
		stopAllBombAnimations();
		this.gridSize = newGridSize;
		bombManager.reset(newGridSize);
		initialiseGrid(newGridSize);
	}
	
	public void doLayout()
	{
		super.doLayout();
		int tileWidth = getWidth() / Math.max(1, gridSize);
		int tileHeight = getHeight() / Math.max(1, gridSize);
		int tileSize = Math.min(tileWidth, tileHeight);
		currentFontSize = Math.max(12, tileSize / 2);
		
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				board[r][c].setFontSize(currentFontSize);
			}
		}
	}
}