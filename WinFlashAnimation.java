import java.awt.Color;
import java.util.List;
import javax.swing.Timer;

/**
 * Manages the animated flashing highlight shown on the winning tiles after a
 * player wins a round.
 *
 * <p>The animation alternates the background colour of each winning tile
 * between a gold highlight and the standard white background at a fixed
 * interval until {@link #stop()} is called.  A new instance is created by
 * {@link GamePanel} for each round that has a winner.</p>
 *
 * <p>The animation is driven by a Swing {@link Timer} so all colour updates
 * occur on the Event Dispatch Thread.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GamePanel#flashWinningCells(List)
 */
public class WinFlashAnimation
{
// ── Constants ─────────────────────────────────────────────────────────

/** The gold highlight colour applied to winning tiles when they flash. */
private static final Color HIGHLIGHT_COLOUR = new Color(255, 215, 0);

/** The colour restored to winning tiles when the highlight is off. */
private static final Color NORMAL_COLOUR = Color.white;

/** The time in milliseconds between each toggle of the highlight. */
private static final int FLASH_INTERVAL_MS = 400;

// ── Fields ────────────────────────────────────────────────────────────

/** The tiles that participate in the flash animation. */
private final List<Tile> tiles;

/** The Swing timer driving the alternating colour updates. */
private final Timer flashTimer;

/** {@code true} when the tiles are currently showing the highlight colour. */
private boolean highlighted = true;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code WinFlashAnimation} for the supplied list of winning
 * tiles.  The tiles are immediately set to the highlight colour; calling
 * {@link #start()} then begins the alternating timer.
 *
 * @param tiles the tiles that should flash; must not be {@code null}
 */
public WinFlashAnimation(List<Tile> tiles)
{
this.tiles = tiles;
setHighlighted(true); // apply initial highlight immediately

flashTimer = new Timer(FLASH_INTERVAL_MS, e ->
{
highlighted = !highlighted;
setHighlighted(highlighted);
});
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Starts the flashing animation timer.
 *
 * <p>Call this method after constructing the animation to begin the
 * alternating highlight.</p>
 */
public void start()
{
flashTimer.start();
}

/**
 * Stops the flashing animation and restores the tiles to their normal
 * background colour.
 *
 * <p>This method is called automatically by {@link GamePanel} when a new
 * round begins or the grid is reset.</p>
 */
public void stop()
{
flashTimer.stop();
setHighlighted(false);
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Applies either the highlight or normal colour to all tiles in the list.
 *
 * @param on {@code true} to apply the gold highlight; {@code false} to
 *           restore the normal background
 */
private void setHighlighted(boolean on)
{
Color colour = on ? HIGHLIGHT_COLOUR : NORMAL_COLOUR;
for (Tile tile : tiles)
{
tile.setBackground(colour);
tile.repaint();
}
}
}
