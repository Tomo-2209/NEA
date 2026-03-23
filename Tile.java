import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;

/**
 * Abstract base class for all tile types in the game.
 * Contains common attributes and methods for tile behavior.
 */
public abstract class Tile extends JButton
{
	protected int row;
	protected int col;
	protected boolean isHidden;
	
	public Tile(int row, int col)
	{
		this.row = row;
		this.col = col;
		this.isHidden = false;
		
		// Common styling for all tiles
		setBackground(Color.darkGray);
		setForeground(Color.yellow); // Changed from white for better visibility
		setFont(new Font("Arial", Font.BOLD, 48));
		setFocusable(false);
		
		// Ensure setBackground() is always visually honoured, regardless of L&F.
		// We fill the background ourselves in paintComponent, so the L&F must not
		// paint its own content area on top of our colour.
		setOpaque(true);
		setContentAreaFilled(false);
		setBorderPainted(false);
	}
	
	/**
	 * Explicitly fill the background colour before delegating to the L&F painter.
	 * This guarantees that calls to setBackground() (e.g. from WinFlashAnimation)
	 * are always visible on screen, regardless of which Look & Feel is active.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}
	
	/**
	 * Draw/update the visual representation of the tile
	 */
	public abstract void draw();
	
	/**
	 * Handle tile click event
	 */
	public abstract void onClick();
	
	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public boolean isHidden()
	{
		return isHidden;
	}
	
	public void setHidden(boolean hidden)
	{
		this.isHidden = hidden;
	}
	
	public void setFontSize(int fontSize)
	{
		setFont(new Font("Arial", Font.BOLD, fontSize));
	}
}