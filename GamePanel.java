import java.awt.Color;
import java.awt.Font;
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
	/** True while a correct/incorrect flash animation is running; blocks new tile clicks. */
	private boolean flashBlocked = false;

	private static final int CORRECT_FLASH_DURATION_MS   = 600;
	private static final int INCORRECT_FLASH_DURATION_MS = 1500;
	/** Divisor applied to currentFontSize when showing the answer on an incorrect flash. */
	private static final int INCORRECT_ANSWER_FONT_DIVISOR = 3;
	
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
		setLayout(new GridLayout(gridSize, gridSize, 4, 4));
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
		// Block all input while a bomb animation or answer flash is running
		if (isBombDetonating() || flashBlocked)
		{
			return;
		}

		if (tile instanceof BombTile)
		{
			BombTile bomb = (BombTile) tile;
			// Do not activate a bomb once the game has been won
			if (controller.isGameOver())
			{
				return;
			}
			SoundManager.getInstance().playTileClick(controller.getCurrentPlayer().getSymbol());
			bomb.onClick();
			controller.handleBombClick(bomb.getRow(), bomb.getCol());
		}
		else if (tile instanceof StandardTile)
		{
			SoundManager.getInstance().playTileClick(controller.getCurrentPlayer().getSymbol());
			StandardTile standard = (StandardTile) tile;
			controller.handleTileClick(standard.getRow(), standard.getCol());
		}
	}
	
	/**
	 * Flash a tile green for 600 ms to indicate a correct answer, then restore it.
	 * The symbol has already been placed, so restoring the background to white
	 * leaves the symbol text visible.
	 */
	public void flashTileCorrect(int row, int col)
	{
		if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) return;
		Tile tile = board[row][col];
		flashBlocked = true;
		tile.setBackground(new Color(100, 220, 100));
		tile.repaint();
		javax.swing.Timer t = new javax.swing.Timer(CORRECT_FLASH_DURATION_MS, e ->
		{
			tile.setBackground(Color.white);
			tile.repaint();
			flashBlocked = false;
		});
		t.setRepeats(false);
		t.start();
	}

	/**
	 * Flash a tile red for 1.5 s, temporarily displaying the correct answer,
	 * then restore the tile and invoke {@code onComplete} (on the EDT).
	 * The answer is shown both in the tile (small HTML text) and via the
	 * turn-label in the UI to avoid truncation on small tiles.
	 * Used when a player answers the maths question incorrectly.
	 */
	public void flashTileIncorrect(int row, int col, String correctAnswer, Runnable onComplete)
	{
		if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) return;
		Tile tile = board[row][col];
		flashBlocked = true;
		tile.setBackground(new Color(240, 80, 80));

		// Use HTML so the text wraps and the JButton does not truncate to "..."
		tile.setText("<html><center>= " + correctAnswer + "</center></html>");

		// Use a smaller font so the answer fits even on small tiles
		tile.setFont(new Font("Arial", Font.BOLD, Math.max(8, currentFontSize / INCORRECT_ANSWER_FONT_DIVISOR)));
		tile.repaint();

		javax.swing.Timer t = new javax.swing.Timer(INCORRECT_FLASH_DURATION_MS, e ->
		{
			tile.setBackground(Color.white);
			tile.setText("");
			tile.setFontSize(currentFontSize);
			tile.repaint();
			flashBlocked = false;
			if (onComplete != null) onComplete.run();
		});
		t.setRepeats(false);
		t.start();
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
	 * Diffuse a bomb tile: stop its animation and convert it to a playable
	 * standard tile without clearing any surrounding cells.
	 * Called by the controller when the player successfully answers the
	 * bomb-diffuse question.
	 */
	public void diffuseBomb(int row, int col)
	{
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
		{
			if (board[row][col] instanceof BombTile)
			{
				((BombTile) board[row][col]).stopAnimation();
				convertToStandardTile(row, col);
			}
		}
	}

	/**
	 * Trigger the explosion animation for a bomb immediately, bypassing the
	 * remaining fuse burn.  Called by the controller when the player fails to
	 * diffuse the bomb (wrong answer or timeout).
	 */
	public void triggerBombExplosion(int row, int col)
	{
		if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
		{
			if (board[row][col] instanceof BombTile)
			{
				((BombTile) board[row][col]).triggerExplosion();
			}
		}
	}

	/**
	 * Called by a BombTile when its explosion animation finishes.
	 * Plays the explosion sound, clears the 3×3 area around the bomb in both
	 * the view and the model, then converts the bomb tile itself into a
	 * playable StandardTile so players can place symbols on it again.
	 * Finally notifies the controller so it can switch the active player.
	 */
	public void onBombExploded(int bombRow, int bombCol)
	{
		SoundManager.getInstance().playBombExplode();

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
		flashBlocked = false;
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