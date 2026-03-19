import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Handles all animation and drawing logic for bomb tiles.
 * Manages fuse burning, spark animation, and explosion effects.
 */
public class BombAnimationEngine
{
	private float burnProgress = 0.0f; // 0.0 (full fuse) to 1.0 (explodes)
	private boolean exploded = false;
	private ArrayList<Particle> particles = new ArrayList<>();
	private Random rand = new Random();
	private static final float BURN_SPEED = 0.0075f; // Adjust speed of fuse burning
	
	public BombAnimationEngine()
	{
		this.particles = new ArrayList<>();
	}
	
	/**
	 * Update the animation state (call this regularly from the animation timer)
	 */
	public void update()
	{
		if (!exploded)
		{
			burnProgress += BURN_SPEED;
			if (burnProgress >= 1.0f)
			{
				exploded = true;
				createExplosion();
			}
		}
		else
		{
			updateParticles();
		}
	}
	
	/**
	 * Create explosion particles when bomb detonates.
	 * Particles originate from the bomb body centre (150, 205) in logical space.
	 */
	private void createExplosion()
	{
		for (int i = 0; i < 50; i++)
		{
			particles.add(new Particle(150, 205, rand));
		}
	}
	
	/**
	 * Update all active explosion particles
	 */
	private void updateParticles()
	{
		for (Particle p : particles)
		{
			p.update();
		}
	}
	
	/**
	 * Draw the bomb in pre-explosion state, scaled to fit the component dimensions.
	 * The bomb body sits in the lower portion of the tile with the fuse running
	 * upward and to the left so the whole visual is centred in the tile.
	 */
	public void drawBomb(Graphics2D g2d, int w, int h)
	{
		// Save the graphics state
		AffineTransform originalTransform = g2d.getTransform();
		
		// Scale so the logical 300x300 canvas fits centred inside the component
		double scale = Math.min(w, h) / 300.0;
		g2d.translate(w / 2.0 - 150.0 * scale, h / 2.0 - 150.0 * scale);
		g2d.scale(scale, scale);
		
		// 1. Draw the bomb body with radial gradient (centred at (150, 205))
		RadialGradientPaint bodyGrad = new RadialGradientPaint(
			new Point2D.Double(130, 185), 70f,
			new float[]{0f, 1f},
			new Color[]{new Color(80, 80, 80), Color.BLACK}
		);
		g2d.setPaint(bodyGrad);
		g2d.fillOval(95, 150, 110, 110);
		
		// 2. Shine highlight on bomb body
		g2d.setColor(new Color(255, 255, 255, 60));
		g2d.fillOval(110, 158, 38, 26);
		
		// 3. Draw the fuse casing (small rectangle at the top of the bomb body)
		g2d.setColor(new Color(40, 40, 40));
		g2d.fillRoundRect(130, 138, 40, 15, 6, 6);
		
		// 4. Draw the fuse rope with proper burn progression
		drawFuse(g2d);
		
		// 5. Draw the spark at the current burn position
		drawSpark(g2d);
		
		// Restore graphics state
		g2d.setTransform(originalTransform);
	}
	
	/**
	 * Draw the fuse rope that burns from the tip (top) down toward the bomb.
	 *
	 * Fuse path is a quadratic Bezier:
	 *   P0 = (75,  30)  – free tip (top of fuse, where burning starts)
	 *   P1 = (80,  85)  – control point
	 *   P2 = (150, 143) – base where fuse meets the casing on the bomb
	 *
	 * The spark starts at P0 and travels to P2 as burnProgress goes 0→1.
	 */
	private void drawFuse(Graphics2D g2d)
	{
		// Full fuse path: tip (P0) → control (P1) → bomb base (P2)
		Path2D fusePath = new Path2D.Double();
		fusePath.moveTo(75, 30);
		fusePath.quadTo(80, 85, 150, 143);
		
		// Draw the complete (unburned) fuse in brown/rope colour
		g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(new Color(110, 70, 40));
		g2d.draw(fusePath);
		
		// Draw the burned (darkened) portion from the tip to the current spark position
		if (burnProgress > 0.01f)
		{
			Path2D burnedSegment = getBurnedSegment(burnProgress);
			g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(new Color(30, 30, 30));
			g2d.draw(burnedSegment);
		}
	}
	
	/**
	 * Build a path segment from the fuse tip (t=0) to the spark's current position (t=progress).
	 * This represents the portion of the fuse that has already burned.
	 */
	private Path2D getBurnedSegment(float progress)
	{
		Path2D segment = new Path2D.Double();
		// Start at the fuse tip (P0, t=0)
		segment.moveTo(75, 30);
		
		int steps = Math.max(2, (int)(progress * 100));
		for (int i = 1; i <= steps; i++)
		{
			float t = (float) i / steps * progress;
			Point2D p = getPointOnCurve(t);
			segment.lineTo(p.getX(), p.getY());
		}
		
		return segment;
	}
	
	/**
	 * Draw the spark that follows the fuse as it burns
	 */
	private void drawSpark(Graphics2D g2d)
	{
		Point2D sparkPos = getPointOnCurve(burnProgress);
		
		// Outer glow (orange)
		g2d.setColor(new Color(255, 100, 0, 180));
		g2d.fillOval((int)sparkPos.getX() - 10, (int)sparkPos.getY() - 10, 20, 20);
		
		// Inner bright spark (yellow)
		g2d.setColor(Color.YELLOW);
		g2d.fillOval((int)sparkPos.getX() - 5, (int)sparkPos.getY() - 5, 10, 10);
	}
	
	/**
	 * Draw the explosion particles after detonation, scaled to fit the component.
	 */
	public void drawExplosion(Graphics2D g2d, int w, int h)
	{
		AffineTransform originalTransform = g2d.getTransform();
		
		// Same scale/translate as drawBomb so particles originate from the bomb centre
		double scale = Math.min(w, h) / 300.0;
		g2d.translate(w / 2.0 - 150.0 * scale, h / 2.0 - 150.0 * scale);
		g2d.scale(scale, scale);
		
		for (Particle p : particles)
		{
			if (p.isVisible())
			{
				g2d.setColor(p.color);
				g2d.fillOval((int)p.x - p.size / 2, (int)p.y - p.size / 2, p.size, p.size);
			}
		}
		
		g2d.setTransform(originalTransform);
	}
	
	/**
	 * Calculate a point on the fuse Bezier curve for parameter t (0.0 → 1.0).
	 * Uses the quadratic Bezier formula: B(t) = (1-t)²P0 + 2(1-t)t·P1 + t²P2
	 *
	 * Control points (in logical 300×300 space):
	 *   P0 = (75,  30)  – fuse tip (burning starts here at t=0)
	 *   P1 = (80,  85)  – control point
	 *   P2 = (150, 143) – fuse base at bomb casing (fully burned at t=1)
	 */
	private Point2D getPointOnCurve(float t)
	{
		// Ensure t is clamped between 0 and 1
		t = Math.max(0, Math.min(1, t));
		
		double mt   = 1 - t;       // (1-t)
		double mt2  = mt * mt;     // (1-t)²
		double t2   = t * t;       // t²
		double tmt2 = 2 * mt * t;  // 2(1-t)t
		
		// P0 = fuse tip, P1 = control, P2 = bomb base
		double x0 = 75,  y0 = 30;
		double x1 = 80,  y1 = 85;
		double x2 = 150, y2 = 143;
		
		double x = mt2 * x0 + tmt2 * x1 + t2 * x2;
		double y = mt2 * y0 + tmt2 * y1 + t2 * y2;
		
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Check if the bomb has exploded
	 */
	public boolean hasExploded()
	{
		return exploded;
	}
	
	/**
	 * Check if the full explosion animation (fuse + particles) has finished.
	 * Returns true once all particles have faded out.
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
	 * Get current burn progress (0.0 to 1.0)
	 */
	public float getBurnProgress()
	{
		return burnProgress;
	}
	
	/**
	 * Reset the animation to the beginning
	 */
	public void reset()
	{
		burnProgress = 0.0f;
		exploded = false;
		particles.clear();
	}
	
	/**
	 * Inner class for explosion particles
	 */
	public static class Particle
	{
		public double x, y, vx, vy;
		public int size;
		public Color color;
		
		public Particle(int startX, int startY, Random r)
		{
			this.x = startX;
			this.y = startY;
			
			// Random direction and speed
			double angle = r.nextDouble() * 2 * Math.PI;
			double speed = r.nextDouble() * 5 + 2;
			this.vx = Math.cos(angle) * speed;
			this.vy = Math.sin(angle) * speed;
			
			this.size = r.nextInt(10) + 5;
			this.color = r.nextBoolean() ? Color.ORANGE : Color.RED;
		}
		
		/**
		 * Update particle position and size
		 */
		public void update()
		{
			x += vx;
			y += vy;
			size = Math.max(0, size - 1);
		}
		
		/**
		 * Returns true while the particle still has visible area to draw.
		 */
		public boolean isVisible()
		{
			return size > 0;
		}
	}
}