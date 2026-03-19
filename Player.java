
public class Player 
{
	private String name;
	private String symbol;
	private int score;
	
	public Player(String name, String symbol)
	{
		this.name = (name == null || name.isEmpty()) ? ("Player " + symbol) : name;
		this.symbol = symbol;
		this.score = 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void incrementScore()
	{
		score++;
	}
}
