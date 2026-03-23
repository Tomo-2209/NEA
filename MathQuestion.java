/**
 * Immutable data object representing a single maths challenge question.
 * Holds the question text, the primary answer (shown to the user if wrong),
 * one or more acceptable answer strings, and the topic label.
 */
public class MathQuestion
{
	private final String questionText;
	private final String primaryAnswer;   // displayed to the player when they are wrong
	private final String[] validAnswers;  // all accepted spellings / forms
	private final String topic;

	public MathQuestion(String questionText, String primaryAnswer, String[] validAnswers, String topic)
	{
		this.questionText   = questionText;
		this.primaryAnswer  = primaryAnswer;
		this.validAnswers   = validAnswers;
		this.topic          = topic;
	}

	/**
	 * Convenience constructor for questions that have exactly one valid answer string.
	 */
	public MathQuestion(String questionText, String answer, String topic)
	{
		this(questionText, answer, new String[]{answer}, topic);
	}

	/**
	 * Check whether the player's typed answer is correct.
	 * Comparison is case-insensitive and ignores surrounding whitespace.
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

	public String getQuestionText()
	{
		return questionText;
	}

	public String getPrimaryAnswer()
	{
		return primaryAnswer;
	}

	public String getTopic()
	{
		return topic;
	}
}
