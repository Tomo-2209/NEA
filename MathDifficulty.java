/**
 * Represents the difficulty level chosen by the players before the game starts.
 * Determines which pool of maths questions is used during the game.
 */
public enum MathDifficulty
{
	GCSE,
	A_LEVEL;

	@Override
	public String toString()
	{
		return this == GCSE ? "GCSE" : "A-Level";
	}
}
