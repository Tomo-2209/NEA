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
	 * Create explosion particles when bomb detonates
	 */
	private void createExplosion()
	{
		for (int i = 0; i < 50; i++)
		{
			particles.add(new Particle(150, 150, rand));
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
	 */
	public void drawBomb(Graphics2D g2d, int w, int h)
	{
		// Save the graphics state
		AffineTransform originalTransform = g2d.getTransform();
		
		// Scale so the logical 300x300 canvas fits centred inside the component
		double scale = Math.min(w, h) / 300.0;
		g2d.translate(w / 2.0 - 150.0 * scale, h / 2.0 - 150.0 * scale);
		g2d.scale(scale, scale);
		
		// 1. Tilt the bomb
		g2d.rotate(Math.toRadians(25), 150, 150);
		
		// 2. Draw the bomb body with gradient
		RadialGradientPaint bodyGrad = new RadialGradientPaint(
			new Point2D.Double(130, 130), 80f,
			new float[]{0f, 1f},
			new Color[]{new Color(80, 80, 80), Color.BLACK}
		);
		g2d.setPaint(bodyGrad);
		g2d.fillOval(100, 100, 100, 100);
		
		// 3. Draw the fuse casing
		g2d.setColor(new Color(40, 40, 40));
		g2d.fillRoundRect(135, 88, 30, 15, 5, 5);
		
		// 4. Draw the fuse rope with proper burn progression
		drawFuse(g2d);
		
		// 5. Draw the spark at the fuse tip
		drawSpark(g2d);
		
		// Restore graphics state
		g2d.setTransform(originalTransform);
	}
	
	/**
	 * Draw the fuse rope that burns down as time progresses.
	 * The spark follows the quadratic bezier path defined by
	 * P0=(150,88), P1=(70,110), P2=(60,40).
	 */
	private void drawFuse(Graphics2D g2d)
	{
		// Define the fuse path as a quadratic bezier (matches getPointOnCurve)
		Path2D fusePath = new Path2D.Double();
		fusePath.moveTo(150, 88);
		fusePath.quadTo(70, 110, 60, 40);
		
		// Draw the full fuse path (unburned)
		g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(new Color(110, 70, 40)); // Brown/rope color
		g2d.draw(fusePath);
		
		// Draw the burned/darkened portion following the path
		if (burnProgress > 0.01f)
		{
			Path2D burnedSegment = getSegmentUpToProgress(burnProgress);
			g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(new Color(30, 30, 30)); // Dark burned section
			g2d.draw(burnedSegment);
		}
	}
	
	/**
	 * Get a path segment from the start to the current burn progress point
	 */
	private Path2D getSegmentUpToProgress(float progress)
	{
		Path2D segment = new Path2D.Double();
		segment.moveTo(150, 88);
		
		// Approximate the path with multiple small segments
		int steps = Math.max(2, (int)(progress * 100));
		for (int i = 1; i <= steps; i++)
		{
			float t = (float) i / 100f;
			if (t > progress) t = progress;
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
			if (p.size > 0)
			{
				g2d.setColor(p.color);
				g2d.fillOval((int)p.x - p.size / 2, (int)p.y - p.size / 2, p.size, p.size);
			}
		}
		
		g2d.setTransform(originalTransform);
	}
	
	/**
	 * Calculate a point on the bezier curve based on progress (0.0 to 1.0)
	 * Using quadratic bezier formula: B(t) = (1-t)²P0 + 2(1-t)t P1 + t²P2
	 * Control points: P0=(150,88), P1=(70,110), P2=(60,40)
	 */
	private Point2D getPointOnCurve(float t)
	{
		// Ensure t is clamped between 0 and 1
		t = Math.max(0, Math.min(1, t));
		
		// Bezier curve coefficients
		double mt = 1 - t; // (1-t)
		double mt2 = mt * mt; // (1-t)²
		double t2 = t * t; // t²
		double tmt2 = 2 * mt * t; // 2(1-t)t
		
		// Control points
		double x0 = 150, y0 = 88;   // Start (fuse casing)
		double x1 = 70, y1 = 110;   // Control point
		double x2 = 60, y2 = 40;    // End (tip)
		
		// Calculate point on curve
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
			if (p.size > 0)
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
	}
}