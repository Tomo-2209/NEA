import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable value object that encapsulates the outcome of a win-check on the
 * game board.
 *
 * <p>After every move, {@link GameModel#checkForWin()} creates and returns one
 * of three possible results:</p>
 * <ul>
 *   <li><b>Winner</b>   – {@link #hasWinner()} returns {@code true} and
 *       {@link #getWinnerSymbol()} / {@link #getWinningCells()} are populated.</li>
 *   <li><b>Tie</b>      – {@link #isTie()} returns {@code true}; all cells are
 *       filled but no winning line was found.</li>
 *   <li><b>Ongoing</b>  – both {@code hasWinner()} and {@code isTie()} return
 *       {@code false}; the game continues.</li>
 * </ul>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GameModel#checkForWin()
 */
public class WinResult
{
// ── Fields ────────────────────────────────────────────────────────────

/** {@code true} if a player has completed a winning line. */
private final boolean hasWinner;

/** The symbol ({@code "X"} or {@code "O"}) of the winning player, or an
 *  empty string if there is no winner. */
private final String winnerSymbol;

/** The grid co-ordinates of the tiles that form the winning line. */
private final List<Point> winningCells;

/** {@code true} if every cell is occupied but no winning line was found. */
private final boolean tie;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code WinResult} with all outcome data.
 *
 * <p>The {@code winningCells} list is defensively copied and wrapped in an
 * unmodifiable view so callers cannot alter the stored result.</p>
 *
 * @param hasWinner    {@code true} if a player has won
 * @param winnerSymbol the winning player's symbol, or {@code ""} if none
 * @param winningCells the cells forming the winning line (may be empty)
 * @param tie          {@code true} if the game is a draw
 */
public WinResult(boolean hasWinner, String winnerSymbol,
                 List<Point> winningCells, boolean tie)
{
this.hasWinner    = hasWinner;
this.winnerSymbol = winnerSymbol;
this.winningCells = (winningCells == null)
? Collections.emptyList()
: Collections.unmodifiableList(new ArrayList<>(winningCells));
this.tie          = tie;
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns {@code true} if a player has formed a winning line.
 *
 * @return {@code true} when a winner exists
 */
public boolean hasWinner()
{
return hasWinner;
}

/**
 * Returns the symbol of the winning player.
 *
 * @return {@code "X"}, {@code "O"}, or an empty string if there is no winner
 */
public String getWinnerSymbol()
{
return winnerSymbol;
}

/**
 * Returns an unmodifiable list of the grid co-ordinates that form the
 * winning line.  Each {@link Point} stores the row in {@code Point.x} and
 * the column in {@code Point.y}.
 *
 * @return the winning cells, or an empty list if there is no winner
 */
public List<Point> getWinningCells()
{
return winningCells;
}

/**
 * Returns {@code true} if every cell is filled but no winning line exists.
 *
 * @return {@code true} for a draw
 */
public boolean isTie()
{
return tie;
}
}
