import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The main game-board view for Tic-Tac-Total.
 *
 * <p>{@code GamePanel} extends {@link JPanel} and is responsible for:</p>
 * <ul>
 *   <li>Building and laying out the grid of {@link Tile} objects
 *       ({@link StandardTile} and {@link BombTile}).</li>
 *   <li>Routing tile-click events to {@link GameController}.</li>
 *   <li>Providing visual feedback animations:
 *       <ul>
 *         <li>Green flash on a correct answer.</li>
 *         <li>Red flash (showing the correct answer) on an incorrect answer.</li>
 *         <li>Gold flashing highlight on the winning tiles.</li>
 *         <li>Bomb explosion and surrounding-area clear.</li>
 *       </ul>
 *   </li>
 *   <li>Scaling tile font sizes dynamically via {@link #doLayout()} when the
 *       window is resized.</li>
 * </ul>
 *
 * <h3>Input blocking</h3>
 * <p>Two flags prevent conflicting input during animations:</p>
 * <ul>
 *   <li>{@code flashBlocked} – set while a correct/incorrect answer flash is
 *       running.</li>
 *   <li>{@link #isBombDetonating()} – returns {@code true} while any bomb on
 *       the board is in its activated state.</li>
 * </ul>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GameController
 * @see     Tile
 * @see     BombManager
 */
public class GamePanel extends JPanel
{
// ── Constants ─────────────────────────────────────────────────────────

/** Duration (ms) of the green flash shown after a correct answer. */
private static final int CORRECT_FLASH_DURATION_MS = 600;

/** Duration (ms) of the red flash shown after an incorrect answer. */
private static final int INCORRECT_FLASH_DURATION_MS = 1500;

/**
 * Divisor applied to {@link #currentFontSize} when calculating the smaller
 * font used to display the answer text during the incorrect-answer flash.
 */
private static final int INCORRECT_ANSWER_FONT_DIVISOR = 3;

// ── Fields ────────────────────────────────────────────────────────────

/** Two-dimensional array of all tiles currently on the board. */
private Tile[][] board;

/** Current grid dimension (number of rows and columns). */
private int gridSize;

/** Reference to the controller; set via {@link #setController}. */
private GameController controller;

/** Manages the random placement of bomb tiles. */
private BombManager bombManager;

/** The currently running win-flash animation, or {@code null} if none. */
private WinFlashAnimation winFlash;

/** The dynamically calculated font size for tile symbols. */
private int currentFontSize = 48;

/**
 * {@code true} while a correct or incorrect flash animation is running.
 * All tile clicks are ignored until the animation completes.
 */
private boolean flashBlocked = false;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code GamePanel} for the given grid size.
 *
 * <p>The controller reference is {@code null} at this point; it must be
 * provided via {@link #setController(GameController)} before any tile
 * clicks can be processed.</p>
 *
 * @param controller the game controller (may be {@code null} initially)
 * @param gridSize   the initial number of rows / columns
 */
public GamePanel(GameController controller, int gridSize)
{
this.controller  = controller;
this.gridSize    = gridSize;
this.bombManager = new BombManager(gridSize);
setBackground(Color.darkGray);
initialiseGrid(gridSize);
}

// ── Setter ────────────────────────────────────────────────────────────

/**
 * Sets (or replaces) the controller that handles tile-click events.
 *
 * @param controller the {@link GameController} to use
 */
public void setController(GameController controller)
{
this.controller = controller;
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Returns {@code true} while at least one bomb on the board is in its
 * activated (animating) state.
 *
 * <p>All tile clicks are blocked while a bomb is detonating to prevent the
 * player from taking further actions during the animation.</p>
 *
 * @return {@code true} if any bomb is currently animating
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
if (board[r][c] instanceof BombTile
&& ((BombTile) board[r][c]).isActivated())
{
return true;
}
}
}
return false;
}

/**
 * Sets the symbol displayed in a standard tile.
 *
 * <p>Called by the controller after a move has been accepted by the
 * model.</p>
 *
 * @param row    the zero-based row index
 * @param col    the zero-based column index
 * @param symbol the player's symbol to display
 */
public void updateTile(int row, int col, String symbol)
{
if (board[row][col] instanceof StandardTile)
{
((StandardTile) board[row][col]).setSymbol(symbol);
}
}

/**
 * Clears a tile's symbol in the view and removes it from the model.
 *
 * <p>Provided as a convenience method; the bomb explosion path uses
 * {@link #onBombExploded(int, int)} directly.</p>
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
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
 * Flashes a tile <b>green</b> for {@link #CORRECT_FLASH_DURATION_MS} ms
 * to indicate a correct answer.
 *
 * <p>The symbol has already been placed before this method is called, so
 * restoring the background to white leaves the symbol visible.</p>
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
public void flashTileCorrect(int row, int col)
{
if (row < 0 || row >= gridSize || col < 0 || col >= gridSize)
{
return;
}
Tile tile    = board[row][col];
flashBlocked = true;

tile.setBackground(new Color(100, 220, 100));
tile.repaint();

javax.swing.Timer timer = new javax.swing.Timer(CORRECT_FLASH_DURATION_MS, e ->
{
tile.setBackground(Color.white);
tile.repaint();
flashBlocked = false;
});
timer.setRepeats(false);
timer.start();
}

/**
 * Flashes a tile <b>red</b> for {@link #INCORRECT_FLASH_DURATION_MS} ms,
 * temporarily displaying the correct answer, then restores the tile and
 * invokes {@code onComplete} on the Event Dispatch Thread.
 *
 * <p>The answer text is shown in the tile using small HTML text to prevent
 * it from being truncated on narrow tiles.  The turn label in the UI also
 * shows the answer for tiles that are too small to display it legibly.</p>
 *
 * @param row           the zero-based row index
 * @param col           the zero-based column index
 * @param correctAnswer the correct answer string to display
 * @param onComplete    callback invoked once the flash has finished (may be
 *                      {@code null})
 */
public void flashTileIncorrect(int row, int col,
                               String correctAnswer, Runnable onComplete)
{
if (row < 0 || row >= gridSize || col < 0 || col >= gridSize)
{
return;
}
Tile tile    = board[row][col];
flashBlocked = true;

tile.setBackground(new Color(240, 80, 80));
tile.setText("<html><center>= " + correctAnswer + "</center></html>");
tile.setFont(new Font("Arial", Font.BOLD,
             Math.max(8, currentFontSize / INCORRECT_ANSWER_FONT_DIVISOR)));
tile.repaint();

javax.swing.Timer timer = new javax.swing.Timer(INCORRECT_FLASH_DURATION_MS, e ->
{
tile.setBackground(Color.white);
tile.setText("");
tile.setFontSize(currentFontSize);
tile.repaint();
flashBlocked = false;
if (onComplete != null)
{
onComplete.run();
}
});
timer.setRepeats(false);
timer.start();
}

/**
 * Starts a gold flashing highlight on the tiles that form the winning line.
 *
 * <p>Any previous win-flash animation is stopped before the new one is
 * started.</p>
 *
 * @param cells the winning cell co-ordinates ({@link Point#x} = row,
 *              {@link Point#y} = col)
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
 * Diffuses a bomb tile: stops its animation and converts it to a playable
 * {@link StandardTile} without clearing the surrounding area.
 *
 * <p>Called by the controller when the player answers the diffuse question
 * correctly.</p>
 *
 * @param row the zero-based row index of the bomb
 * @param col the zero-based column index of the bomb
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
 * Triggers the explosion animation for a bomb, bypassing the remaining
 * fuse burn.
 *
 * <p>Called by the controller when the player fails to diffuse the bomb.</p>
 *
 * @param row the zero-based row index of the bomb
 * @param col the zero-based column index of the bomb
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
 * Called by a {@link BombTile} once its explosion animation has fully
 * finished.
 *
 * <p>Clears the 3 × 3 area surrounding the bomb in both the view and the
 * model, then converts the bomb tile itself into a playable
 * {@link StandardTile}.  Finally notifies the controller so it can switch
 * the active player.</p>
 *
 * @param bombRow the row index of the exploded bomb
 * @param bombCol the column index of the exploded bomb
 */
public void onBombExploded(int bombRow, int bombCol)
{
// Clear the 3×3 blast area
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
// Replace the bomb itself with an empty standard tile
convertToStandardTile(r, c);
}
else if (board[r][c] instanceof BombTile)
{
// A neighbouring bomb caught in the blast – convert it too
convertToStandardTile(r, c);
}
else if (board[r][c] instanceof StandardTile)
{
// Clear the symbol from the adjacent standard tile
((StandardTile) board[r][c]).reset();
clearCellInModel(r, c);
}
}
}

// Notify the controller once cleanup is done
if (controller != null)
{
controller.onBombAnimationFinished();
}
}

/**
 * Resets the panel for a new round or a new grid size.
 *
 * <p>Stops all active animations, resets the flash-blocked flag, and
 * rebuilds the grid with the new size.</p>
 *
 * @param newGridSize the new grid dimension
 */
public void reset(int newGridSize)
{
stopWinFlash();
stopAllBombAnimations();
flashBlocked  = false;
this.gridSize = newGridSize;
bombManager.reset(newGridSize);
initialiseGrid(newGridSize);
}

// ── Getter ────────────────────────────────────────────────────────────

/**
 * Returns the current grid dimension.
 *
 * @return the number of rows (and columns) in the grid
 */
public int getGridSize()
{
return gridSize;
}

// ── Layout override ───────────────────────────────────────────────────

/**
 * Recalculates and applies the tile font size whenever the panel is laid
 * out (e.g. on window resize).
 *
 * <p>The font size is derived from the smaller of the tile's width and
 * height, capped at a minimum of 12 pt so symbols remain readable on very
 * small grids.</p>
 */
@Override
public void doLayout()
{
super.doLayout();
int tileWidth  = getWidth()  / Math.max(1, gridSize);
int tileHeight = getHeight() / Math.max(1, gridSize);
int tileSize   = Math.min(tileWidth, tileHeight);
currentFontSize = Math.max(12, tileSize / 2);

for (int r = 0; r < gridSize; r++)
{
for (int c = 0; c < gridSize; c++)
{
board[r][c].setFontSize(currentFontSize);
}
}
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Builds the grid of tiles for the specified grid size.
 *
 * <p>Removes any existing components, creates a new {@link GridLayout},
 * constructs the appropriate tile type for each cell (bomb or standard),
 * attaches action listeners and calls {@link #revalidate()}.</p>
 *
 * @param size the number of rows / columns in the new grid
 */
private void initialiseGrid(int size)
{
removeAll();
setLayout(new GridLayout(size, size, 4, 4));
board = new Tile[size][size];

for (int r = 0; r < size; r++)
{
for (int c = 0; c < size; c++)
{
Tile tile;

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

// Attach click listener (captures tile reference for the lambda)
final Tile clickedTile = tile;
tile.addActionListener(new ActionListener()
{
@Override
public void actionPerformed(ActionEvent e)
{
if (controller != null)
{
handleTileClick(clickedTile);
}
}
});
}
}

revalidate();
repaint();
}

/**
 * Dispatches a tile-click event to the appropriate handler.
 *
 * <p>Clicks are silently ignored if a bomb animation or answer flash is in
 * progress.</p>
 *
 * @param tile the tile that was clicked
 */
private void handleTileClick(Tile tile)
{
if (isBombDetonating() || flashBlocked)
{
return;
}

if (tile instanceof BombTile)
{
BombTile bomb = (BombTile) tile;
if (controller.isGameOver())
{
return;
}
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
 * Stops the win-flash animation if one is currently running and resets
 * the reference to {@code null}.
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
 * Stops all active bomb animations before the grid is torn down.
 *
 * <p>Prevents a stale timer callback from modifying tiles in the new
 * grid after a reset.</p>
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

/**
 * Replaces the tile at ({@code row}, {@code col}) with a fresh, empty
 * {@link StandardTile}.
 *
 * <p>The new tile inherits the current font size and receives an action
 * listener so it behaves identically to any other standard tile on the
 * board.</p>
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
private void convertToStandardTile(int row, int col)
{
Tile    oldTile  = board[row][col];
int     index    = row * gridSize + col;

remove(oldTile);

StandardTile newTile = new StandardTile(row, col);
newTile.setFontSize(currentFontSize);
newTile.addActionListener(e ->
{
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
 * Notifies the model (via the controller) that the cell at the given
 * co-ordinates should be cleared.  The call is guarded against a
 * {@code null} controller.
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
private void clearCellInModel(int row, int col)
{
if (controller != null)
{
controller.clearCell(row, col);
}
}
}
