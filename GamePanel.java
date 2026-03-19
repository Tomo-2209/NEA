import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

public class GamePanel extends JPanel
{
	private Tile[][] board;
	private int gridSize;
	private GameController controller;
	private BombManager bombManager;
	
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
	 * Handle tile click - dispatch to appropriate tile type handler
	 */
	private void handleTileClick(Tile tile)
	{
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
	 * Clear a tile (remove any symbol from it)
	 */
	public void clearTile(int row, int col)
	{
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
		{
			if (board[row][col] instanceof StandardTile)
			{
				((StandardTile) board[row][col]).reset();
			}
		}
	}
	
	public void highlightCells(List<Point> cells)
	{
		for (Point p : cells)
		{
			board[p.x][p.y].setBackground(Color.lightGray);
		}
	}
	
	public int getGridSize()
	{
		return gridSize;
	}
	
	public void reset(int newGridSize)
	{
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
		int fontSize = Math.max(12, tileSize / 2);
		
		for (int r = 0; r < gridSize; r++)
		{
			for (int c = 0; c < gridSize; c++)
			{
				board[r][c].setFontSize(fontSize);
			}
		}
	}
}