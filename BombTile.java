import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Timer;

/**
 * Bomb tile that starts hidden and reveals when clicked.
 * When activated, displays an animated bomb that explodes,
 * clearing a 3x3 area around itself.
 */
public class BombTile extends Tile
{
	private GamePanel gamePanel;
	private boolean isActivated;
	private Timer animationTimer;
	private BombAnimationEngine animationEngine;
	
	public BombTile(int row, int col, GamePanel gamePanel)
	{
		super(row, col);
		this.gamePanel = gamePanel;
		this.isHidden = true;
		this.isActivated = false;
		this.animationEngine = new BombAnimationEngine();
		setForeground(Color.cyan); // Cyan for hidden bomb indicator
		draw();
	}
	
	@Override
	public void draw()
	{
		if (isHidden && !isActivated)
		{
			// Hidden bomb - looks like a regular empty tile so its position stays secret
			setText("");
			setBackground(Color.white);
			setForeground(Color.black);
		}
		else if (isActivated)
		{
			// Activated - custom graphics will be drawn in paintComponent
			setText(""); // Clear text, we'll draw the bomb graphically
			setBackground(Color.white);
		}
	}
	
	@Override
	public void onClick()
	{
		if (!isActivated)
		{
			activate();
		}
	}
	
	/**
	 * Activate the bomb, revealing it and starting the animation
	 */
	private void activate()
	{
		isActivated = true;
		isHidden = false;
		draw();
		startAnimation();
	}
	
	/**
	 * Start the bomb animation timer
	 */
	private void startAnimation()
	{
		// Animation timer: ~60 FPS
		animationTimer = new Timer(16, e -> {
			animationEngine.update();
			repaint();
			
			// Once the full explosion animation (fuse + particles) has finished,
			// delegate cleanup to the panel so the tile can be replaced and the
			// surrounding area cleared in both the view and the model.
			if (animationEngine.isAnimationComplete())
			{
				animationTimer.stop();
				isActivated = false;
				gamePanel.onBombExploded(row, col);
			}
		});
		animationTimer.start();
	}
	
	/**
	 * Custom painting for the bomb animation
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		// Draw the button background
		super.paintComponent(g);
		
		if (isActivated)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			
			int w = getWidth();
			int h = getHeight();
			
			// Determine what to draw based on animation state
			if (!animationEngine.hasExploded())
			{
				animationEngine.drawBomb(g2d, w, h);
			}
			else
			{
				animationEngine.drawExplosion(g2d, w, h);
			}
		}
	}
	
	public boolean isActivated()
	{
		return isActivated;
	}
	
	public void reset()
	{
		isActivated = false;
		isHidden = true;
		if (animationTimer != null)
		{
			animationTimer.stop();
		}
		animationEngine.reset();
		draw();
	}
}