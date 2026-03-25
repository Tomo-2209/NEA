/**
 * Enumeration representing the difficulty level selected by the players before
 * the game begins.
 *
 * <p>The chosen difficulty determines which pool of maths questions is used
 * throughout the session:</p>
 * <ul>
 *   <li>{@link #GCSE}    – standard and harder GCSE-level questions.</li>
 *   <li>{@link #A_LEVEL} – standard and harder A-Level questions.</li>
 * </ul>
 *
 * <p>The enum value is passed to {@link MathQuestionBank} at construction time
 * and stored for the lifetime of the game.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     MathQuestionBank
 */
public enum MathDifficulty
{
/** Standard and harder GCSE-level maths questions. */
GCSE,

/** Standard and harder A-Level maths questions. */
A_LEVEL;

/**
 * Returns a human-readable display string for this difficulty level.
 *
 * @return {@code "GCSE"} or {@code "A-Level"}
 */
@Override
public String toString()
{
return this == GCSE ? "GCSE" : "A-Level";
}
}
