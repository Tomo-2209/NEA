import java.awt.Color;

/**
 * A standard, playable game tile on the Tic-Tac-Total board.
 *
 * <p>{@code StandardTile} extends {@link Tile} and represents a normal cell
 * on which players place their symbols ({@code "X"} or {@code "O"}).  The
 * tile starts empty and can be set, queried and reset through the provided
 * methods.</p>
 *
 * <p>Click handling for standard tiles is managed entirely by
 * {@link GameController} via {@link GamePanel}; the {@link #onClick()} method
 * is therefore a no-operation stub provided to satisfy the abstract contract
 * defined by {@link Tile}.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     Tile
 * @see     BombTile
 */
public class StandardTile extends Tile
{
// ── Field ─────────────────────────────────────────────────────────────

/** The symbol currently displayed in this tile ({@code "X"}, {@code "O"},
 *  or {@code ""} when empty). */
private String symbol;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs an empty {@code StandardTile} at the specified grid position
 * and draws its initial (blank) state.
 *
 * @param row the zero-based row index in the grid
 * @param col the zero-based column index in the grid
 */
public StandardTile(int row, int col)
{
super(row, col);
this.symbol = "";
draw();
}

// ── Overridden abstract methods ───────────────────────────────────────

/**
 * Updates the visual representation of this tile to reflect its current
 * symbol.
 *
 * <p>An empty tile displays a blank white background; a claimed tile shows
 * the player's symbol in black text.</p>
 */
@Override
public void draw()
{
setText(symbol);
setBackground(Color.white);
setForeground(Color.black);
}

/**
 * Handles a click event on this tile.
 *
 * <p>Standard tile click logic is managed by {@link GameController}; this
 * method is intentionally empty and exists only to fulfil the abstract
 * contract of {@link Tile}.</p>
 */
@Override
public void onClick()
{
// Handled by GameController via GamePanel
}

// ── Getters and setters ───────────────────────────────────────────────

/**
 * Returns the symbol currently displayed in this tile.
 *
 * @return {@code "X"}, {@code "O"}, or {@code ""} when empty
 */
public String getSymbol()
{
return symbol;
}

/**
 * Sets the player's symbol in this tile and redraws it.
 *
 * @param symbol the symbol to display ({@code "X"} or {@code "O"})
 */
public void setSymbol(String symbol)
{
this.symbol = symbol;
draw();
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Returns {@code true} if this tile has not yet been claimed by either
 * player.
 *
 * @return {@code true} when the tile is empty
 */
public boolean isEmpty()
{
return symbol.isEmpty();
}

/**
 * Clears the tile's symbol and redraws it in its empty state.
 *
 * <p>Called when a bomb explosion clears the surrounding area or when a
 * new round begins.</p>
 */
public void reset()
{
symbol = "";
draw();
}
}
