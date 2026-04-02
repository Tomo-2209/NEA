import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * Abstract base class for all tile types used on the Tic-Tac-Total game board.
 *
 * <p>{@code Tile} extends {@link JButton} and provides the shared styling,
 * painting logic and abstract interface that concrete tile subclasses must
 * implement.  Two concrete subclasses exist:</p>
 * <ul>
 *   <li>{@link StandardTile} – a normal, playable cell.</li>
 *   <li>{@link BombTile}     – a hidden bomb that triggers an animation and
 *       clears the surrounding area on detonation.</li>
 * </ul>
 *
 * <h3>Custom painting</h3>
 * <p>{@link #paintComponent(Graphics)} overrides the default Swing rendering
 * to avoid the "..." truncation that Swing's look-and-feel applies to short
 * strings (such as {@code "X"} or {@code "O"}) on small tiles.  Plain-text
 * symbols are centred and drawn manually; HTML content (used for the
 * incorrect-answer flash) is delegated to the standard Swing renderer which
 * handles HTML layout correctly.</p>
 *
 * <h3>OOP design</h3>
 * <p>By declaring {@link #draw()} and {@link #onClick()} as {@code abstract},
 * this class enforces a contract that all tile types must define their own
 * visual representation and click behaviour – a direct application of
 * polymorphism.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     StandardTile
 * @see     BombTile
 */
public abstract class Tile extends JButton
{
// ── Fields ────────────────────────────────────────────────────────────

/** Zero-based row index of this tile in the grid. */
protected final int row;

/** Zero-based column index of this tile in the grid. */
protected final int col;

/**
 * {@code true} while the tile's true identity (e.g. bomb) is hidden from
 * the players.
 */
protected boolean isHidden;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code Tile} at the given grid position and applies the
 * shared visual style.
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
public Tile(int row, int col)
{
this.row      = row;
this.col      = col;
this.isHidden = false;

// ── Shared visual style ───────────────────────────────────────────
setBackground(Color.white);
setForeground(Color.black);
setFont(new Font("Arial", Font.BOLD, 48));
setFocusable(false);

// Ensure setBackground() is always honoured regardless of look-and-feel.
// We fill the background in paintComponent, so the L&F must not paint
// its own content area on top of our chosen colour.
setOpaque(true);
setContentAreaFilled(false);
setBorderPainted(false);
}

// ── Abstract methods (must be implemented by subclasses) ──────────────

/**
 * Updates the visual representation of this tile.
 *
 * <p>Subclasses use this method to set the tile's text, background colour
 * and foreground colour according to their current state.</p>
 */
public abstract void draw();

/**
 * Handles a click event on this tile.
 *
 * <p>The exact behaviour depends on the tile type – standard tiles delegate
 * to the controller, while bomb tiles start their animation sequence.</p>
 */
public abstract void onClick();

// ── Overridden painting ───────────────────────────────────────────────

/**
 * Fills the background colour and draws the button text manually.
 *
 * <p>Drawing the text ourselves prevents the Swing look-and-feel from
 * truncating a short symbol such as {@code "X"} or {@code "O"} to
 * {@code "..."} when the tile is small (e.g. on a 9×10 grid).  HTML
 * content (used for the incorrect-answer flash) is delegated to the
 * standard renderer because the HTML layout engine does not suffer from
 * the same truncation problem.</p>
 *
 * @param g the {@link Graphics} context supplied by Swing
 */
@Override
protected void paintComponent(Graphics g)
{
// Always fill the background first so setBackground() calls are visible
g.setColor(getBackground());
g.fillRect(0, 0, getWidth(), getHeight());

String text    = getText();
boolean isHtml = text != null && text.trim().toLowerCase().startsWith("<html>");

if (text == null || text.isEmpty() || isHtml)
{
// Empty or HTML text: delegate to the standard Swing renderer
super.paintComponent(g);
}
else
{
// Plain text: draw centred manually to avoid "..." clipping
Graphics2D g2d = (Graphics2D) g.create();
g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
g2d.setColor(getForeground());
g2d.setFont(getFont());
FontMetrics fm = g2d.getFontMetrics();
int x = (getWidth()  - fm.stringWidth(text)) / 2;
int y = (getHeight() - fm.getHeight())        / 2 + fm.getAscent();
g2d.drawString(text, x, y);
g2d.dispose();
}
}

// ── Getters and setters ───────────────────────────────────────────────

/**
 * Returns the zero-based row index of this tile.
 *
 * @return the row index
 */
public int getRow()
{
return row;
}

/**
 * Returns the zero-based column index of this tile.
 *
 * @return the column index
 */
public int getCol()
{
return col;
}

/**
 * Returns {@code true} if this tile is currently hidden from the players.
 *
 * @return {@code true} when the tile is hidden
 */
public boolean isHidden()
{
return isHidden;
}

/**
 * Sets whether this tile is hidden from the players.
 *
 * @param hidden {@code true} to hide the tile; {@code false} to reveal it
 */
public void setHidden(boolean hidden)
{
this.isHidden = hidden;
}

/**
 * Updates the font size used to render the tile's text symbol.
 *
 * <p>Called by {@link GamePanel#doLayout()} to scale the symbol to the
 * current tile dimensions whenever the window is resized.</p>
 *
 * @param fontSize the new font size in points
 */
public void setFontSize(int fontSize)
{
setFont(new Font("Arial", Font.BOLD, fontSize));
}
}
