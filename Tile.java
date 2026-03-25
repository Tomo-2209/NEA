import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
		setBackground(Color.white);
		setForeground(Color.black);
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
	 * Fill the background colour and draw the button text manually.
	 *
	 * Drawing the text ourselves prevents the Swing L&F from truncating
	 * a short symbol such as "X" or "O" to "..." when the tile is small
	 * (e.g. on a 9×10 grid).  HTML content (used for the incorrect-answer
	 * flash) is delegated to the standard renderer because the HTML layout
	 * engine does not suffer from the same truncation issue.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		// Always fill the background first so setBackground() calls are visible
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		String text = getText();
		boolean isHtml = text != null && text.trim().toLowerCase().startsWith("<html>");

		if (text == null || text.isEmpty() || isHtml)
		{
			// Empty or HTML text: let the standard Swing renderer handle it
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
			int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
			g2d.drawString(text, x, y);
			g2d.dispose();
		}
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