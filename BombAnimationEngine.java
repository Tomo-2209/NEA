import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.RadialGradientPaint;
import java.util.ArrayList;
import java.util.Random;

/**
 * Handles all animation and drawing logic for a {@link BombTile}.
 *
 * <p>The engine models two sequential phases:</p>
 * <ol>
 *   <li><b>Fuse burn</b> – a spark travels along a quadratic Bézier fuse path
 *       from its tip toward the bomb body.  Burn progress is tracked as a
 *       {@code float} in the range {@code 0.0} (fresh fuse) to {@code 1.0}
 *       (fuse fully burned).  The fuse is designed to last approximately 40
 *       seconds so it is still visually burning when the 35-second diffuse
 *       timer in {@link BombDiffuseDialog} expires.  If the player fails to
 *       diffuse the bomb, {@link #triggerExplosion()} fast-forwards directly
 *       to the explosion phase.</li>
 *   <li><b>Explosion</b> – a set of {@link Particle} objects is created and
 *       updated each tick.  Once all particles have faded, the animation is
 *       considered complete.</li>
 * </ol>
 *
 * <p>All drawing is performed on a logical 300 × 300 canvas that is scaled to
 * fit the actual component size.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     BombTile
 */
public class BombAnimationEngine
{
// ── Constants ─────────────────────────────────────────────────────────

/**
 * Burn speed per animation tick (approximately 16 ms).
 *
 * <p>At 16 ms per tick there are 40 s &times; 1 000 / 16 = 2 500 ticks in
 * 40 seconds, so each tick advances burn progress by 1.0 / 2 500 = 0.00040.
 * This gives a ~40-second fuse, intentionally longer than the 35-second
 * diffuse countdown so the fuse is still visually alight when time expires.</p>
 */
private static final float BURN_SPEED = 0.00040f;

// ── Fields ────────────────────────────────────────────────────────────

/**
 * Current burn progress along the fuse; ranges from {@code 0.0} (no burn)
 * to {@code 1.0} (fully burned → explosion).
 */
private float burnProgress = 0.0f;

/** {@code true} once the fuse has fully burned and the explosion phase has
 *  started. */
private boolean exploded = false;

/** Live explosion particles; populated by {@link #createExplosion()}. */
private final ArrayList<Particle> particles = new ArrayList<>();

/** Random number generator used when creating explosion particles. */
private final Random random = new Random();

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a new {@code BombAnimationEngine} in its initial (pre-burn)
 * state.
 */
public BombAnimationEngine() {}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Advances the animation by one tick (~16 ms).
 *
 * <p>During the fuse phase the burn progress is incremented.  When it
 * reaches {@code 1.0} the engine transitions to the explosion phase and
 * populates the particle list.  During the explosion phase all active
 * particles are updated.</p>
 */
public void update()
{
if (!exploded)
{
burnProgress += BURN_SPEED;
if (burnProgress >= 1.0f)
{
burnProgress = 1.0f;
exploded     = true;
createExplosion();
}
}
else
{
updateParticles();
}
}

/**
 * Draws the bomb body, fuse and moving spark onto the supplied
 * {@link Graphics2D} context.
 *
 * <p>The logical 300 × 300 canvas is scaled and translated to fit centred
 * within the component bounds described by {@code width} and {@code height}.
 * The original graphics transform is restored before the method returns.</p>
 *
 * @param g2d    the graphics context to draw into
 * @param width  the component width in pixels
 * @param height the component height in pixels
 */
public void drawBomb(Graphics2D g2d, int width, int height)
{
AffineTransform saved = g2d.getTransform();

double scale = Math.min(width, height) / 300.0;
g2d.translate(width  / 2.0 - 150.0 * scale,
              height / 2.0 - 150.0 * scale);
g2d.scale(scale, scale);
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);

// 1. Bomb body with radial gradient (centre at (150, 205))
RadialGradientPaint bodyGradient = new RadialGradientPaint(
new Point2D.Double(130, 185), 70f,
new float[]{0f, 1f},
new Color[]{new Color(80, 80, 80), Color.BLACK}
);
g2d.setPaint(bodyGradient);
g2d.fillOval(95, 150, 110, 110);

// 2. Shine highlight on the bomb body
g2d.setColor(new Color(255, 255, 255, 60));
g2d.fillOval(110, 158, 38, 26);

// 3. Fuse casing (small rectangle at the top of the bomb body)
g2d.setColor(new Color(40, 40, 40));
g2d.fillRoundRect(130, 138, 40, 15, 6, 6);

// 4. Fuse rope with burn progression
drawFuse(g2d);

// 5. Moving spark at the current burn position
drawSpark(g2d);

g2d.setTransform(saved);
}

/**
 * Draws the explosion particle field onto the supplied
 * {@link Graphics2D} context.
 *
 * <p>Uses the same scale and translation as {@link #drawBomb} so that
 * particles originate from the correct position in the component.</p>
 *
 * @param g2d    the graphics context to draw into
 * @param width  the component width in pixels
 * @param height the component height in pixels
 */
public void drawExplosion(Graphics2D g2d, int width, int height)
{
AffineTransform saved = g2d.getTransform();

double scale = Math.min(width, height) / 300.0;
g2d.translate(width  / 2.0 - 150.0 * scale,
              height / 2.0 - 150.0 * scale);
g2d.scale(scale, scale);

for (Particle p : particles)
{
if (p.isVisible())
{
g2d.setColor(p.getColour());
int px = (int) p.getX() - p.getSize() / 2;
int py = (int) p.getY() - p.getSize() / 2;
g2d.fillOval(px, py, p.getSize(), p.getSize());
}
}

g2d.setTransform(saved);
}

/**
 * Fast-forwards the animation to the explosion phase immediately, bypassing
 * the remaining fuse burn.
 *
 * <p>This is called by {@link BombTile#triggerExplosion()} when the player
 * fails to diffuse the bomb so the explosion fires without waiting for the
 * fuse to burn naturally.</p>
 */
public void triggerExplosion()
{
if (!exploded)
{
burnProgress = 1.0f;
exploded     = true;
createExplosion();
}
}

/**
 * Resets the engine to its initial (pre-burn) state.
 *
 * <p>Called when the animation is stopped before it completes, for example
 * when the game resets or the bomb is successfully diffused.</p>
 */
public void reset()
{
burnProgress = 0.0f;
exploded     = false;
particles.clear();
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns {@code true} if the fuse has fully burned and the explosion has
 * started.
 *
 * @return {@code true} during and after the explosion phase
 */
public boolean hasExploded()
{
return exploded;
}

/**
 * Returns {@code true} once the entire animation (fuse burn and particle
 * explosion) has fully finished – i.e. all particles have faded out.
 *
 * @return {@code true} when the animation is complete
 */
public boolean isAnimationComplete()
{
if (!exploded || particles.isEmpty())
{
return false;
}
for (Particle p : particles)
{
if (p.isVisible())
{
return false;
}
}
return true;
}

/**
 * Returns the current fuse burn progress.
 *
 * @return a value in the range {@code 0.0} (unlit) to {@code 1.0} (fully
 *         burned)
 */
public float getBurnProgress()
{
return burnProgress;
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Populates the particle list with 50 randomly directed explosion particles
 * originating from the bomb body centre (logical co-ordinate (150, 205)).
 */
private void createExplosion()
{
for (int i = 0; i < 50; i++)
{
particles.add(new Particle(150, 205, random));
}
}

/**
 * Advances every particle by one tick.
 */
private void updateParticles()
{
for (Particle p : particles)
{
p.update();
}
}

/**
 * Draws the fuse rope in two segments: the unburned portion (brown) and
 * the burned portion (dark grey) from the tip to the current spark position.
 *
 * <p>Fuse path (quadratic Bézier in logical space):</p>
 * <ul>
 *   <li>P0 = (75, 30)   – fuse tip (burning starts here at t = 0)</li>
 *   <li>P1 = (80, 85)   – control point</li>
 *   <li>P2 = (150, 143) – base where fuse meets the bomb casing (t = 1)</li>
 * </ul>
 *
 * @param g2d the graphics context (already transformed to logical space)
 */
private void drawFuse(Graphics2D g2d)
{
// Full fuse path
Path2D fusePath = new Path2D.Double();
fusePath.moveTo(75, 30);
fusePath.quadTo(80, 85, 150, 143);

g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
g2d.setColor(new Color(110, 70, 40));
g2d.draw(fusePath);

// Burned segment from tip to current spark position
if (burnProgress > 0.01f)
{
Path2D burnedSegment = buildBurnedSegment(burnProgress);
g2d.setColor(new Color(30, 30, 30));
g2d.draw(burnedSegment);
}
}

/**
 * Draws the spark (orange glow + yellow core) at the current burn position
 * along the fuse.
 *
 * @param g2d the graphics context (already transformed to logical space)
 */
private void drawSpark(Graphics2D g2d)
{
Point2D sparkPos = getPointOnCurve(burnProgress);
int sx = (int) sparkPos.getX();
int sy = (int) sparkPos.getY();

// Outer orange glow
g2d.setColor(new Color(255, 100, 0, 180));
g2d.fillOval(sx - 10, sy - 10, 20, 20);

// Inner bright yellow core
g2d.setColor(Color.YELLOW);
g2d.fillOval(sx - 5, sy - 5, 10, 10);
}

/**
 * Builds a path segment from the fuse tip (t = 0) to the spark's current
 * position (t = {@code progress}).  This represents the portion of the fuse
 * that has already been burned.
 *
 * @param progress the burn progress in the range [0, 1]
 * @return a {@link Path2D} covering the burned portion
 */
private Path2D buildBurnedSegment(float progress)
{
Path2D segment = new Path2D.Double();
segment.moveTo(75, 30); // fuse tip

int steps = Math.max(2, (int) (progress * 100));
for (int i = 1; i <= steps; i++)
{
float t = (float) i / steps * progress;
Point2D pt = getPointOnCurve(t);
segment.lineTo(pt.getX(), pt.getY());
}

return segment;
}

/**
 * Evaluates the quadratic Bézier fuse path at parameter {@code t}.
 *
 * <p>Formula: B(t) = (1−t)²P0 + 2(1−t)t·P1 + t²P2</p>
 *
 * <p>Control points (logical 300 × 300 space):</p>
 * <ul>
 *   <li>P0 = (75,  30)  – fuse tip</li>
 *   <li>P1 = (80,  85)  – control point</li>
 *   <li>P2 = (150, 143) – bomb casing base</li>
 * </ul>
 *
 * @param t the curve parameter clamped to [0, 1]
 * @return the point on the Bézier curve at {@code t}
 */
private Point2D getPointOnCurve(float t)
{
t = Math.max(0f, Math.min(1f, t));

double mt   = 1.0 - t;
double mt2  = mt * mt;
double t2   = (double) t * t;
double tmt2 = 2.0 * mt * t;

// Control points
double x = mt2 * 75 + tmt2 * 80 + t2 * 150;
double y = mt2 * 30 + tmt2 * 85 + t2 * 143;

return new Point2D.Double(x, y);
}

// ── Inner class ───────────────────────────────────────────────────────

/**
 * Represents a single explosion particle.
 *
 * <p>Each particle is created at the bomb-body centre, launched in a random
 * direction at a random speed, and shrinks each tick until it disappears.</p>
 *
 * <p>All fields are private; the outer class {@link BombAnimationEngine}
 * accesses them via the provided getters, demonstrating encapsulation within
 * a static nested class.</p>
 */
public static class Particle
{
// ── Fields ────────────────────────────────────────────────────────

/** Current horizontal position in logical space. */
private double x;

/** Current vertical position in logical space. */
private double y;

/** Horizontal velocity component (pixels per tick). */
private final double velocityX;

/** Vertical velocity component (pixels per tick). */
private final double velocityY;

/** Remaining visible size of the particle in pixels; shrinks each tick. */
private int size;

/** Render colour of this particle (orange or red). */
private final Color colour;

// ── Constructor ───────────────────────────────────────────────────

/**
 * Creates a particle originating at ({@code startX}, {@code startY})
 * with a random direction, speed, size and colour.
 *
 * @param startX the starting x co-ordinate in logical space
 * @param startY the starting y co-ordinate in logical space
 * @param random the {@link Random} instance used for randomisation
 */
public Particle(int startX, int startY, Random random)
{
this.x = startX;
this.y = startY;

double angle = random.nextDouble() * 2 * Math.PI;
double speed = random.nextDouble() * 5 + 2;
this.velocityX = Math.cos(angle) * speed;
this.velocityY = Math.sin(angle) * speed;

this.size   = random.nextInt(10) + 5;
this.colour = random.nextBoolean() ? Color.ORANGE : Color.RED;
}

// ── Methods ───────────────────────────────────────────────────────

/**
 * Moves the particle by its velocity and decrements its size by one.
 * Once size reaches zero the particle is no longer drawn.
 */
public void update()
{
x    += velocityX;
y    += velocityY;
size  = Math.max(0, size - 1);
}

/**
 * Returns {@code true} while the particle still has a visible area
 * to draw.
 *
 * @return {@code true} if the particle size is greater than zero
 */
public boolean isVisible()
{
return size > 0;
}

// ── Getters ───────────────────────────────────────────────────────

/**
 * Returns the current horizontal position in logical space.
 *
 * @return the x co-ordinate
 */
public double getX()
{
return x;
}

/**
 * Returns the current vertical position in logical space.
 *
 * @return the y co-ordinate
 */
public double getY()
{
return y;
}

/**
 * Returns the remaining visible size of the particle.
 *
 * @return the diameter in pixels
 */
public int getSize()
{
return size;
}

/**
 * Returns the render colour of this particle.
 *
 * @return the particle colour (orange or red)
 */
public Color getColour()
{
return colour;
}
}
}
