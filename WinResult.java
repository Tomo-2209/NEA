import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinResult 
{
	private boolean hasWinner;
	private String winnerSymbol;
	private List<Point> winningCells;
	private boolean tie;
	
	public WinResult(boolean hasWinner, String winnerSymbol, List<Point> winningCells, boolean tie)
	{
		this.hasWinner = hasWinner;
		this.winnerSymbol = winnerSymbol;
		this.winningCells = (winningCells == null) ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(winningCells));
		this.tie = tie;
	}
	
	public boolean hasWinner()
	{
		return hasWinner;
	}
	
	public String getWinnerSymbol()
	{
		return winnerSymbol;
	}
	
	public List<Point> getWinningCells()
	{
		return winningCells;
	}
	
	public boolean isTie()
	{
		return tie;
	}
}
