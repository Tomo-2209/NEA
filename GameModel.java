import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the internal state of a Tic-Tac-Total game board.
 *
 * <p>{@code GameModel} is a pure model class that stores the grid, validates
 * moves and checks for win/draw conditions.  It contains no Swing code and
 * has no knowledge of the UI, following the Model-View-Controller (MVC)
 * separation used throughout the application.</p>
 *
 * <h3>Win-length table</h3>
 * <pre>
 *   Grid size | 3–4 | 5–6 | 7–8 | 9–10
 *   Win length|  3  |  4  |  5  |   6
 * </pre>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GameController
 */
public class GameModel
{
// ── Fields ────────────────────────────────────────────────────────────

/** Number of rows (and columns) in the current grid. */
private int gridSize;

/** Number of consecutive symbols required to win. */
private int winLength;

/** The game board; each cell holds {@link Symbols#EMPTY}, {@code "X"} or
 *  {@code "O"}. */
private String[][] board;

/** Total number of moves made since the last {@link #reset()}. */
private int movesMade;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code GameModel} for the given grid size, initialising an
 * empty board and calculating the corresponding win length.
 *
 * @param gridSize the initial number of rows / columns (3–10)
 */
public GameModel(int gridSize)
{
setGridSize(gridSize);
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Resizes the board to {@code newSize} × {@code newSize}, recalculates the
 * win length and resets all cells to empty.
 *
 * @param newSize the new grid dimension (number of rows / columns)
 */
public void setGridSize(int newSize)
{
this.gridSize = newSize;
calculateWinLength();
board     = new String[gridSize][gridSize];
movesMade = 0;
resetBoardCells();
}

/**
 * Attempts to place {@code symbol} at the specified cell.
 *
 * <p>The move is rejected if the co-ordinates are out of bounds or the
 * cell is already occupied.</p>
 *
 * @param row    the zero-based row index
 * @param col    the zero-based column index
 * @param symbol the player's symbol ({@code "X"} or {@code "O"})
 * @return {@code true} if the move was accepted; {@code false} otherwise
 * @throws NullPointerException if {@code symbol} is {@code null}
 */
public boolean makeMove(int row, int col, String symbol)
{
Objects.requireNonNull(symbol, "symbol must not be null");

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

/**
 * Clears a single cell (sets it back to {@link Symbols#EMPTY}) and
 * decrements the move counter.
 *
 * <p>Used after a bomb explosion clears the surrounding area so that the
 * tie-detection counter remains accurate.</p>
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
public void clearCell(int row, int col)
{
if (row >= 0 && row < gridSize && col >= 0 && col < gridSize)
{
if (!board[row][col].isEmpty())
{
board[row][col] = Symbols.EMPTY;
movesMade       = Math.max(0, movesMade - 1);
}
}
}

/**
 * Resets all cells to empty and sets the move counter to zero.
 *
 * <p>The grid size and win length are unchanged.</p>
 */
public void reset()
{
resetBoardCells();
movesMade = 0;
}

/**
 * Scans the board for a winning line or a full-board draw.
 *
 * <p>The method checks all rows, columns, and both diagonal directions for
 * a run of {@link #winLength} identical symbols.</p>
 *
 * @return a {@link WinResult} describing whether there is a winner, a tie,
 *         or the game is still in progress
 */
public WinResult checkForWin()
{
// Check rows
for (int r = 0; r < gridSize; r++)
{
for (int c = 0; c <= gridSize - winLength; c++)
{
String start = board[r][c];
if (!start.isEmpty() && checkLine(r, c, 0, 1, start))
{
return new WinResult(true, start, winningCells(r, c, 0, 1), false);
}
}
}

// Check columns
for (int c = 0; c < gridSize; c++)
{
for (int r = 0; r <= gridSize - winLength; r++)
{
String start = board[r][c];
if (!start.isEmpty() && checkLine(r, c, 1, 0, start))
{
return new WinResult(true, start, winningCells(r, c, 1, 0), false);
}
}
}

// Check diagonal (top-left to bottom-right)
for (int r = 0; r <= gridSize - winLength; r++)
{
for (int c = 0; c <= gridSize - winLength; c++)
{
String start = board[r][c];
if (!start.isEmpty() && checkLine(r, c, 1, 1, start))
{
return new WinResult(true, start, winningCells(r, c, 1, 1), false);
}
}
}

// Check diagonal (top-right to bottom-left)
for (int r = 0; r <= gridSize - winLength; r++)
{
for (int c = winLength - 1; c < gridSize; c++)
{
String start = board[r][c];
if (!start.isEmpty() && checkLine(r, c, 1, -1, start))
{
return new WinResult(true, start, winningCells(r, c, 1, -1), false);
}
}
}

// Check for a draw
if (movesMade == gridSize * gridSize)
{
return new WinResult(false, "", new ArrayList<>(), true);
}

// Game still in progress
return new WinResult(false, "", new ArrayList<>(), false);
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns the current grid dimension.
 *
 * @return the number of rows (and columns) in the grid
 */
public int getGridSize()
{
return gridSize;
}

/**
 * Returns the number of consecutive symbols needed for a win.
 *
 * @return the win length
 */
public int getWinLength()
{
return winLength;
}

/**
 * Returns the symbol stored in the cell at ({@code row}, {@code col}).
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 * @return {@code "X"}, {@code "O"}, or {@link Symbols#EMPTY}
 */
public String getCell(int row, int col)
{
return board[row][col];
}

/**
 * Returns {@code true} if the specified cell is within bounds and empty.
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 * @return {@code true} if the cell is empty
 */
public boolean isCellEmpty(int row, int col)
{
if (row < 0 || row >= gridSize || col < 0 || col >= gridSize)
{
return false;
}
return board[row][col].isEmpty();
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Sets {@link #winLength} based on the current {@link #gridSize} according
 * to the fixed mapping used by the game design.
 */
private void calculateWinLength()
{
if (gridSize <= 4)
{
winLength = 3;
}
else if (gridSize <= 6)
{
winLength = 4;
}
else if (gridSize <= 8)
{
winLength = 5;
}
else
{
winLength = 6; // grid sizes 9 and 10
}
}

/**
 * Fills every cell of the board with {@link Symbols#EMPTY}.
 */
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

/**
 * Checks whether a line of {@link #winLength} cells starting at
 * ({@code startRow}, {@code startCol}) and stepping by ({@code deltaRow},
 * {@code deltaCol}) contains only {@code symbol}.
 *
 * @param startRow  starting row index
 * @param startCol  starting column index
 * @param deltaRow  row step per cell (+1, 0, or -1)
 * @param deltaCol  column step per cell (+1, 0, or -1)
 * @param symbol    the symbol to match
 * @return {@code true} if all cells in the line contain {@code symbol}
 */
private boolean checkLine(int startRow, int startCol,
                          int deltaRow, int deltaCol, String symbol)
{
for (int i = 0; i < winLength; i++)
{
int r = startRow + i * deltaRow;
int c = startCol + i * deltaCol;
if (!board[r][c].equals(symbol))
{
return false;
}
}
return true;
}

/**
 * Builds a list of {@link Point} objects for the cells in a winning line.
 *
 * @param startRow  starting row index
 * @param startCol  starting column index
 * @param deltaRow  row step per cell
 * @param deltaCol  column step per cell
 * @return list of (row, col) points in the winning line
 */
private List<Point> winningCells(int startRow, int startCol,
                                 int deltaRow, int deltaCol)
{
List<Point> result = new ArrayList<>();
for (int i = 0; i < winLength; i++)
{
int r = startRow + i * deltaRow;
int c = startCol + i * deltaCol;
result.add(new Point(r, c));
}
return result;
}
}
