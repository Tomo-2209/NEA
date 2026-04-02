/**
 * Represents a player in the Tic-Tac-Total game.
 *
 * <p>Each player has a display name, a board symbol ({@code "X"} or
 * {@code "O"}), and a set of statistics that are accumulated across rounds:</p>
 * <ul>
 *   <li><b>Score</b>          – total number of rounds won.</li>
 *   <li><b>Win streak</b>     – consecutive rounds won without a loss or draw.</li>
 *   <li><b>Best streak</b>    – the highest win streak achieved in this session.</li>
 *   <li><b>Correct answers</b> – total correct maths answers given.</li>
 *   <li><b>Wrong answers</b>  – total incorrect maths answers given.</li>
 * </ul>
 *
 * <p>All fields are private; public getter and setter methods are provided
 * following standard encapsulation principles.</p>
 *
 * @author  Tomo
 * @version 1.0
 */
public class Player
{
// ── Fields ────────────────────────────────────────────────────────────

/** The display name of the player. */
private final String name;

/** The board symbol assigned to this player ({@code "X"} or {@code "O"}). */
private final String symbol;

/** Total number of rounds won by this player. */
private int score;

/** Number of consecutive rounds won without a loss or draw. */
private int winStreak;

/** The highest win streak ever reached by this player in the session. */
private int bestStreak;

/** Running total of correct maths answers given this session. */
private int correctAnswers;

/** Running total of incorrect maths answers given this session. */
private int wrongAnswers;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code Player} with the given name and board symbol.
 *
 * <p>If {@code name} is {@code null} or empty, a default name of
 * {@code "Player X"} or {@code "Player O"} is used instead.</p>
 *
 * @param name   the player's display name
 * @param symbol the board symbol assigned to this player ({@code "X"} or
 *               {@code "O"})
 */
public Player(String name, String symbol)
{
this.name           = (name == null || name.trim().isEmpty()) ? "Player " + symbol : name;
this.symbol         = symbol;
this.score          = 0;
this.winStreak      = 0;
this.bestStreak     = 0;
this.correctAnswers = 0;
this.wrongAnswers   = 0;
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns the player's display name.
 *
 * @return the display name
 */
public String getName()
{
return name;
}

/**
 * Returns the board symbol assigned to this player.
 *
 * @return {@code "X"} for Player 1, {@code "O"} for Player 2
 */
public String getSymbol()
{
return symbol;
}

/**
 * Returns the total number of rounds won by this player.
 *
 * @return the round-win count
 */
public int getScore()
{
return score;
}

/**
 * Returns the current consecutive-win streak.
 *
 * @return the number of rounds won in a row
 */
public int getWinStreak()
{
return winStreak;
}

/**
 * Returns the highest win streak this player has ever achieved in the
 * current session.
 *
 * @return the best streak recorded
 */
public int getBestStreak()
{
return bestStreak;
}

/**
 * Returns the running total of correct maths answers given this session.
 *
 * @return the number of correct answers
 */
public int getCorrectAnswers()
{
return correctAnswers;
}

/**
 * Returns the running total of incorrect maths answers given this session.
 *
 * @return the number of wrong answers
 */
public int getWrongAnswers()
{
return wrongAnswers;
}

// ── Mutators ──────────────────────────────────────────────────────────

/**
 * Increments the player's round-win score by one and updates the win streak
 * and best-streak records accordingly.
 *
 * <p>Call this method when this player wins a round.</p>
 */
public void incrementScore()
{
score++;
winStreak++;
if (winStreak > bestStreak)
{
bestStreak = winStreak;
}
}

/**
 * Resets the current win streak to zero.
 *
 * <p>Call this method when this player loses a round or the round ends in a
 * draw.</p>
 */
public void resetStreak()
{
winStreak = 0;
}

/**
 * Records one correct maths answer for this player.
 *
 * <p>Call this method immediately after the player answers a maths
 * question correctly.</p>
 */
public void addCorrectAnswer()
{
correctAnswers++;
}

/**
 * Records one incorrect maths answer for this player.
 *
 * <p>Call this method immediately after the player answers a maths
 * question incorrectly or runs out of time on a bomb-diffuse challenge.</p>
 */
public void addWrongAnswer()
{
wrongAnswers++;
}
}
