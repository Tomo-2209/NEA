import java.awt.Color;
import java.util.List;
import javax.swing.Timer;

/**
 * Manages the flashing highlight animation shown on winning tiles after a win.
 * Tiles alternate between a gold highlight colour and their normal dark-grey
 * background at a fixed interval until stopped.
 */
public class WinFlashAnimation
{
	private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0); // Gold
	private static final Color NORMAL_COLOR = Color.darkGray;
	private static final int FLASH_INTERVAL_MS = 400;

	private final List<Tile> tiles;
	private final Timer flashTimer;
	private boolean highlighted = true;

	public WinFlashAnimation(List<Tile> tiles)
	{
		this.tiles = tiles;
		// Start in highlighted state immediately
		setHighlighted(true);

		flashTimer = new Timer(FLASH_INTERVAL_MS, e -> {
			highlighted = !highlighted;
			setHighlighted(highlighted);
		});
	}

	private void setHighlighted(boolean on)
	{
		Color c = on ? HIGHLIGHT_COLOR : NORMAL_COLOR;
		for (Tile t : tiles)
		{
			t.setBackground(c);
			t.repaint();
		}
	}

	/**
	 * Start the flashing animation.
	 */
	public void start()
	{
		flashTimer.start();
	}

	/**
	 * Stop the flashing animation and return tiles to their normal colour.
	 */
	public void stop()
	{
		flashTimer.stop();
		setHighlighted(false);
	}
}
