import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Timer;

/**
 * A bomb tile on the Tic-Tac-Total game board.
 *
 * <p>{@code BombTile} extends {@link Tile} and represents a hidden hazard.
 * From the players' perspective it looks identical to an empty
 * {@link StandardTile} until it is clicked; at that point the bomb is
 * <em>activated</em> and the following sequence occurs:</p>
 * <ol>
 *   <li>The tile reveals itself and begins rendering an animated bomb (via
 *       {@link BombAnimationEngine}).</li>
 *   <li>Simultaneously, {@link BombDiffuseDialog} is displayed by
 *       {@link GameController#handleBombClick}, giving the current player
 *       35 seconds to answer a harder maths question.</li>
 *   <li>If the player answers correctly the bomb is <em>diffused</em>:
 *       {@link #stopAnimation()} halts the fuse and the tile is converted to a
 *       standard tile by {@link GamePanel}.</li>
 *   <li>If the player fails (wrong answer or timeout),
 *       {@link #triggerExplosion()} fast-forwards to the explosion phase, which
 *       clears the surrounding 3 × 3 area once the particle animation
 *       completes.</li>
 * </ol>
 *
 * <p>Custom painting is provided in {@link #paintComponent(Graphics)} – when
 * activated the tile delegates drawing to {@link BombAnimationEngine} instead
 * of displaying text.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     Tile
 * @see     StandardTile
 * @see     BombAnimationEngine
 * @see     GamePanel
 */
public class BombTile extends Tile
{
// ── Fields ────────────────────────────────────────────────────────────

/** Reference to the parent panel used to notify it when the explosion
 *  animation completes. */
private final GamePanel gamePanel;

/** {@code true} once the bomb has been clicked and is animating. */
private boolean isActivated;

/** Swing timer driving the ~60 FPS animation loop. */
private Timer animationTimer;

/** The animation engine responsible for fuse-burn and explosion logic. */
private final BombAnimationEngine animationEngine;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code BombTile} at the given grid position.  The tile is
 * initially hidden (appears as a blank white tile) and not yet activated.
 *
 * @param row       the zero-based row index in the grid
 * @param col       the zero-based column index in the grid
 * @param gamePanel the owning {@link GamePanel} (notified on explosion)
 */
public BombTile(int row, int col, GamePanel gamePanel)
{
super(row, col);
this.gamePanel       = gamePanel;
this.isHidden        = true;
this.isActivated     = false;
this.animationEngine = new BombAnimationEngine();
draw();
}

// ── Overridden abstract methods ───────────────────────────────────────

/**
 * Updates the visual representation of this tile based on its current
 * state.
 *
 * <ul>
 *   <li>Hidden / not activated – appears as a blank white tile so its
 *       position is not revealed to the players.</li>
 *   <li>Activated – text is cleared; the bomb is drawn in
 *       {@link #paintComponent(Graphics)}.</li>
 * </ul>
 */
@Override
public void draw()
{
if (isHidden && !isActivated)
{
// Hidden bomb – looks like an empty standard tile
setText("");
setBackground(Color.white);
setForeground(Color.black);
}
else if (isActivated)
{
// Activated – custom graphics handle drawing
setText("");
setBackground(Color.white);
}
}

/**
 * Handles a click event on this tile.
 *
 * <p>If the bomb has not yet been activated, {@link #activate()} is called
 * to begin the animation sequence.  Subsequent clicks while the bomb is
 * already animating are ignored.</p>
 */
@Override
public void onClick()
{
if (!isActivated)
{
activate();
}
}

/**
 * Custom painting that delegates to {@link BombAnimationEngine} when the
 * bomb is activated.
 *
 * <p>While not activated the standard {@link Tile} rendering is used.
 * Anti-aliasing and stroke-control hints are applied for smooth
 * curves.</p>
 *
 * @param g the {@link Graphics} context supplied by Swing
 */
@Override
protected void paintComponent(Graphics g)
{
super.paintComponent(g); // fill background

if (isActivated)
{
Graphics2D g2d = (Graphics2D) g;
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                     RenderingHints.VALUE_STROKE_PURE);

if (!animationEngine.hasExploded())
{
animationEngine.drawBomb(g2d, getWidth(), getHeight());
}
else
{
animationEngine.drawExplosion(g2d, getWidth(), getHeight());
}
}
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Stops the animation without triggering an explosion and resets the tile
 * to a plain empty state ready for conversion to a {@link StandardTile}.
 *
 * <p>Called by {@link GamePanel#diffuseBomb(int, int)} when the player
 * successfully answers the diffuse question.</p>
 */
public void stopAnimation()
{
if (animationTimer != null)
{
animationTimer.stop();
animationTimer = null;
}
isActivated = false;
animationEngine.reset();
draw();
}

/**
 * Skips the remaining fuse burn and immediately advances to the explosion
 * phase.
 *
 * <p>The existing animation timer continues running; when
 * {@link BombAnimationEngine#isAnimationComplete()} returns {@code true},
 * the timer calls {@link GamePanel#onBombExploded(int, int)} as normal.</p>
 */
public void triggerExplosion()
{
animationEngine.triggerExplosion();
repaint();
}

/**
 * Resets the bomb tile to its original hidden, unactivated state.
 *
 * <p>Called by {@link GamePanel} when a new round starts or the grid size
 * changes, before the existing grid is torn down.</p>
 */
public void reset()
{
isActivated = false;
isHidden    = true;
if (animationTimer != null)
{
animationTimer.stop();
animationTimer = null;
}
animationEngine.reset();
draw();
}

// ── Getter ────────────────────────────────────────────────────────────

/**
 * Returns {@code true} if the bomb has been clicked and its animation is
 * currently running.
 *
 * @return {@code true} when the bomb is animating
 */
public boolean isActivated()
{
return isActivated;
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Transitions the tile from hidden to activated and starts the animation
 * timer at approximately 60 FPS.
 */
private void activate()
{
isActivated = true;
isHidden    = false;
draw();
startAnimation();
}

/**
 * Creates and starts the Swing {@link Timer} that drives the animation
 * loop at ~60 FPS (16 ms intervals).
 *
 * <p>Each tick advances the {@link BombAnimationEngine} and repaints the
 * tile.  When the engine reports that the animation is complete (all
 * particles faded), the timer stops and
 * {@link GamePanel#onBombExploded(int, int)} is notified.</p>
 */
private void startAnimation()
{
animationTimer = new Timer(16, e ->
{
animationEngine.update();
repaint();

if (animationEngine.isAnimationComplete())
{
animationTimer.stop();
isActivated = false;
gamePanel.onBombExploded(row, col);
}
});
animationTimer.start();
}
}
