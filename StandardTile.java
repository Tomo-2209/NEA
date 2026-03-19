import java.awt.Color;

/**
 * Standard game tile for player moves (X or O).
 * Extends the base Tile class with standard tic-tac-toe behavior.
 */
public class StandardTile extends Tile
{
	private String symbol;
	
	public StandardTile(int row, int col)
	{
		super(row, col);
		this.symbol = "";
		draw();
	}
	
	@Override
	public void draw()
	{
		setText(symbol);
		setBackground(Color.darkGray);
		setForeground(Color.white);
	}
	
	@Override
	public void onClick()
	{
		// Standard tile click is handled by the controller
		// This method is provided for potential override in subclasses
	}
	
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
		draw();
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public boolean isEmpty()
	{
		return symbol.isEmpty();
	}
	
	public void reset()
	{
		symbol = "";
		draw();
	}
}