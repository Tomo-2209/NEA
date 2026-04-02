/**
 * Immutable data object representing a single maths challenge question.
 *
 * <p>Each question carries:</p>
 * <ul>
 *   <li>The question text shown to the player (may contain HTML mark-up for
 *       multi-line or formatted questions).</li>
 *   <li>A <em>primary answer</em> string that is displayed to the player if
 *       they answer incorrectly.</li>
 *   <li>An array of <em>valid answers</em> – all accepted spellings or
 *       equivalent forms (e.g. {@code "0.5"} and {@code "1/2"}).</li>
 *   <li>A <em>topic</em> label shown in the dialog title bar
 *       (e.g. {@code "Differentiation"}).</li>
 * </ul>
 *
 * <p>Answer checking is case-insensitive and ignores leading/trailing
 * whitespace.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     MathQuestionBank
 * @see     MathQuestionDialog
 */
public class MathQuestion
{
// ── Fields ────────────────────────────────────────────────────────────

/** The question text presented to the player; may be plain or HTML. */
private final String questionText;

/**
 * The canonical correct answer shown to the player when they answer
 * incorrectly.
 */
private final String primaryAnswer;

/**
 * All accepted answer strings (case-insensitive).
 * Must contain at least one element.
 */
private final String[] validAnswers;

/** Topic label displayed in the question dialog title bar. */
private final String topic;

// ── Constructors ──────────────────────────────────────────────────────

/**
 * Constructs a {@code MathQuestion} with multiple accepted answer forms.
 *
 * @param questionText  the text (plain or HTML) shown to the player
 * @param primaryAnswer the canonical answer displayed on an incorrect attempt
 * @param validAnswers  all accepted answer strings; must not be {@code null}
 *                      or empty
 * @param topic         the topic label for the dialog title
 */
public MathQuestion(String questionText, String primaryAnswer,
                    String[] validAnswers, String topic)
{
this.questionText  = questionText;
this.primaryAnswer = primaryAnswer;
this.validAnswers  = validAnswers;
this.topic         = topic;
}

/**
 * Convenience constructor for questions that have exactly one valid answer.
 *
 * @param questionText the text (plain or HTML) shown to the player
 * @param answer       the single correct answer
 * @param topic        the topic label for the dialog title
 */
public MathQuestion(String questionText, String answer, String topic)
{
this(questionText, answer, new String[]{answer}, topic);
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Checks whether the player's typed input matches any accepted answer.
 *
 * <p>Comparison is case-insensitive and ignores surrounding whitespace.</p>
 *
 * @param userInput the string entered by the player; may be {@code null}
 * @return {@code true} if {@code userInput} matches a valid answer
 */
public boolean checkAnswer(String userInput)
{
if (userInput == null)
{
return false;
}
String normalised = userInput.trim().toLowerCase();
for (String valid : validAnswers)
{
if (valid.toLowerCase().equals(normalised))
{
return true;
}
}
return false;
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns the question text shown to the player.
 *
 * @return the question text (plain or HTML)
 */
public String getQuestionText()
{
return questionText;
}

/**
 * Returns the canonical correct answer, used when displaying feedback after
 * an incorrect attempt.
 *
 * @return the primary answer string
 */
public String getPrimaryAnswer()
{
return primaryAnswer;
}

/**
 * Returns the topic label shown in the question dialog title bar.
 *
 * @return the topic string (e.g. {@code "Differentiation"})
 */
public String getTopic()
{
return topic;
}
}
