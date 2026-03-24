
public class Player 
{
	private String name;
	private String symbol;
	private int score;
	private int winStreak;
	private int bestStreak;
	private int correctAnswers;
	private int wrongAnswers;
	
	public Player(String name, String symbol)
	{
		this.name = (name == null || name.isEmpty()) ? ("Player " + symbol) : name;
		this.symbol = symbol;
		this.score = 0;
		this.winStreak = 0;
		this.bestStreak = 0;
		this.correctAnswers = 0;
		this.wrongAnswers = 0;
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
		winStreak++;
		if (winStreak > bestStreak)
		{
			bestStreak = winStreak;
		}
	}
	
	/** Reset win streak – called when this player loses a round or the game ties. */
	public void resetStreak()
	{
		winStreak = 0;
	}
	
	/** Record a correct maths answer. */
	public void addCorrectAnswer()
	{
		correctAnswers++;
	}
	
	/** Record a wrong maths answer. */
	public void addWrongAnswer()
	{
		wrongAnswers++;
	}
	
	public int getWinStreak()
	{
		return winStreak;
	}
	
	public int getBestStreak()
	{
		return bestStreak;
	}
	
	public int getCorrectAnswers()
	{
		return correctAnswers;
	}
	
	public int getWrongAnswers()
	{
		return wrongAnswers;
	}
}
