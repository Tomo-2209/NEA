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
	private static final String HIDDEN_SYMBOL = "?";
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
			// Hidden bomb - just show ?
			setText(HIDDEN_SYMBOL);
			setBackground(Color.darkGray);
			setForeground(Color.cyan);
		}
		else if (isActivated)
		{
			// Activated - custom graphics will be drawn in paintComponent
			setText(""); // Clear text, we'll draw the bomb graphically
			setBackground(Color.darkGray);
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
		// Animation timer: 60 FPS
		animationTimer = new Timer(16, e -> {
			animationEngine.update();
			repaint();
			
			// When explosion occurs, clear the surrounding area after a brief delay
			if (animationEngine.hasExploded())
			{
				animationTimer.stop();
				clearSurroundingAreaWithDelay();
			}
		});
		animationTimer.start();
	}
	
	/**
	 * Clear the 3x3 area with a small delay after explosion
	 */
	private void clearSurroundingAreaWithDelay()
	{
		Timer delayTimer = new Timer(300, e -> {
			clearSurroundingArea();
			((Timer) e.getSource()).stop();
		});
		delayTimer.setRepeats(false);
		delayTimer.start();
	}
	
	/**
	 * Clear all symbols in the 3x3 area surrounding this bomb
	 */
	private void clearSurroundingArea()
	{
		// Define the 3x3 area around the bomb
		for (int r = row - 1; r <= row + 1; r++)
		{
			for (int c = col - 1; c <= col + 1; c++)
			{
				// Skip if out of bounds
				if (r < 0 || c < 0 || r >= gamePanel.getGridSize() || c >= gamePanel.getGridSize())
				{
					continue;
				}
				
				// Don't clear the bomb itself, but clear everything else
				if (r != row || c != col)
				{
					gamePanel.clearTile(r, c);
				}
			}
		}
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
			
			// Determine what to draw based on animation state
			if (!animationEngine.hasExploded())
			{
				animationEngine.drawBomb(g2d);
			}
			else
			{
				animationEngine.drawExplosion(g2d);
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