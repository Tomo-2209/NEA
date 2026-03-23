import java.util.Random;

/**
 * Generates randomised maths questions for the requested difficulty level.
 *
 * GCSE topics: linear equations, percentages, geometry (area), statistics
 *              (mean), powers / indices.
 *
 * A-Level topics: differentiation (power rule), definite integration,
 *                 logarithms, binomial coefficients, inverse-trig angles.
 *
 * All questions are designed so the correct answer is an integer (or a simple
 * string such as "45") – this keeps on-screen rendering and answer checking
 * straightforward.
 */
public class MathQuestionBank
{
	private final MathDifficulty difficulty;
	private final Random random;

	// Number of question generators available at each level
	private static final int GCSE_TYPES   = 5;
	private static final int ALEVEL_TYPES = 5;

	public MathQuestionBank(MathDifficulty difficulty)
	{
		this.difficulty = difficulty;
		this.random     = new Random();
	}

	/** Return one freshly generated question appropriate to the stored difficulty. */
	public MathQuestion getRandomQuestion()
	{
		if (difficulty == MathDifficulty.GCSE)
		{
			return getGcseQuestion();
		}
		else
		{
			return getALevelQuestion();
		}
	}

	// -----------------------------------------------------------------------
	// GCSE generators
	// -----------------------------------------------------------------------

	private MathQuestion getGcseQuestion()
	{
		switch (random.nextInt(GCSE_TYPES))
		{
			case 0:  return generateLinearEquation();
			case 1:  return generatePercentage();
			case 2:  return generateArea();
			case 3:  return generateMean();
			default: return generatePower();
		}
	}

	/**
	 * Solve a linear equation of the form  ax + b = c  (integer solution).
	 * a is chosen in [2, 9], solution in [-5, 5] \ {0}, b in [-10, 10].
	 */
	private MathQuestion generateLinearEquation()
	{
		int a        = 2 + random.nextInt(8);           // 2–9
		int solution = (random.nextInt(11) - 5);        // -5 to 5
		if (solution == 0) solution = 1;
		int b = random.nextInt(21) - 10;                // -10 to 10
		int c = a * solution + b;

		String question;
		if (b == 0)
		{
			question = String.format("Solve for x:    %dx = %d", a, c);
		}
		else if (b > 0)
		{
			question = String.format("Solve for x:    %dx + %d = %d", a, b, c);
		}
		else
		{
			question = String.format("Solve for x:    %dx - %d = %d", a, -b, c);
		}

		return new MathQuestion(question, String.valueOf(solution), "Algebra");
	}

	/**
	 * Calculate X% of Y where X is a common percentage and Y is a multiple of
	 * 20, guaranteeing an integer result.
	 */
	private MathQuestion generatePercentage()
	{
		int[] percents = {5, 10, 15, 20, 25, 30, 40, 50, 75};
		int pct = percents[random.nextInt(percents.length)];
		int y   = (1 + random.nextInt(20)) * 20;     // 20, 40, … 400
		int ans = pct * y / 100;

		String question = String.format("What is %d%% of %d?", pct, y);
		return new MathQuestion(question, String.valueOf(ans), "Percentages");
	}

	/**
	 * Area of a rectangle or a right-angled triangle (integer result).
	 */
	private MathQuestion generateArea()
	{
		if (random.nextBoolean())
		{
			// Rectangle
			int w = 2 + random.nextInt(12);
			int h = 2 + random.nextInt(12);
			String q = String.format("Find the area of a rectangle with width %d and height %d.", w, h);
			return new MathQuestion(q, String.valueOf(w * h), "Geometry");
		}
		else
		{
			// Triangle: use an even base so area = base/2 * height is always integer
			int base   = (1 + random.nextInt(10)) * 2;   // even, 2–20
			int height = 2 + random.nextInt(10);
			int area   = base / 2 * height;
			String q   = String.format(
				"Find the area of a triangle with base %d and perpendicular height %d.", base, height);
			return new MathQuestion(q, String.valueOf(area), "Geometry");
		}
	}

	/**
	 * Find the mean of a small list of positive integers.
	 * The list is constructed so the mean is always an integer in [2, 9].
	 */
	private MathQuestion generateMean()
	{
		int mean  = 2 + random.nextInt(8);           // target mean: 2–9
		int count = 3 + random.nextInt(3);           // 3, 4 or 5 numbers
		int[] nums = new int[count];
		int remaining = mean * count;

		for (int i = 0; i < count - 1; i++)
		{
			// Allow each value to be up to (mean + SPREAD) above the mean so that
			// the final element calculated as `remaining` stays ≥ 1.
			// SPREAD controls how varied the numbers in the list appear.
			final int SPREAD = 6;
			int maxAllowed = remaining - (count - 1 - i);
			int lo = Math.max(1, remaining - (count - 1 - i) * (mean + SPREAD));
			int hi = Math.min(maxAllowed, mean + SPREAD - 1);
			if (lo > hi) lo = hi = mean;
			nums[i]   = lo + (hi > lo ? random.nextInt(hi - lo + 1) : 0);
			remaining -= nums[i];
		}
		nums[count - 1] = remaining;

		// Safety fallback: if last element went negative, use all-equal array
		if (nums[count - 1] < 1)
		{
			for (int i = 0; i < count; i++) nums[i] = mean;
		}

		StringBuilder sb = new StringBuilder("Find the mean of:    ");
		for (int i = 0; i < count; i++)
		{
			sb.append(nums[i]);
			if (i < count - 1) sb.append(",  ");
		}

		return new MathQuestion(sb.toString(), String.valueOf(mean), "Statistics");
	}

	/**
	 * Evaluate a^n or a^2 + b^2 (integer result).
	 */
	private MathQuestion generatePower()
	{
		if (random.nextBoolean())
		{
			int base = 2 + random.nextInt(5);           // 2–6
			int exp  = 2 + random.nextInt(3);           // 2–4
			int ans  = (int) Math.pow(base, exp);
			String q = String.format("Evaluate    %d^%d", base, exp);
			return new MathQuestion(q, String.valueOf(ans), "Powers & Indices");
		}
		else
		{
			int a   = 2 + random.nextInt(9);
			int b   = 2 + random.nextInt(9);
			int ans = a * a + b * b;
			String q = String.format("Evaluate    %d\u00b2 + %d\u00b2", a, b);  // ²
			return new MathQuestion(q, String.valueOf(ans), "Powers & Indices");
		}
	}

	// -----------------------------------------------------------------------
	// A-Level generators
	// -----------------------------------------------------------------------

	private MathQuestion getALevelQuestion()
	{
		switch (random.nextInt(ALEVEL_TYPES))
		{
			case 0:  return generateDifferentiation();
			case 1:  return generateDefiniteIntegral();
			case 2:  return generateLogarithm();
			case 3:  return generateBinomialCoeff();
			default: return generateInverseTrig();
		}
	}

	/**
	 * Differentiate  y = ax^n  by the power rule.
	 * Ask for the integer coefficient of the resulting term.
	 */
	private MathQuestion generateDifferentiation()
	{
		int a = 2 + random.nextInt(8);   // 2–9
		int n = 2 + random.nextInt(4);   // 2–5  (keeps new power ≥ 1)
		int coefficient = a * n;
		int newPower    = n - 1;

		String questionBody;
		if (newPower == 1)
		{
			questionBody = String.format(
				"<b>Differentiate</b>    y = %dx<sup>%d</sup><br><br>"
				+ "What is the coefficient of x in dy/dx?", a, n);
		}
		else
		{
			questionBody = String.format(
				"<b>Differentiate</b>    y = %dx<sup>%d</sup><br><br>"
				+ "What is the coefficient of x<sup>%d</sup> in dy/dx?", a, n, newPower);
		}

		// Wrap in HTML so the JLabel renders the tags
		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(coefficient), "Differentiation");
	}

	/**
	 * Evaluate the definite integral  integral[0 to b] of a*x^n dx.
	 * The coefficient a is chosen as k*(n+1) so the result k*b^(n+1) is always
	 * an integer.
	 */
	private MathQuestion generateDefiniteIntegral()
	{
		int n      = 1 + random.nextInt(3);            // power: 1, 2 or 3
		int b      = 1 + random.nextInt(4);            // upper limit: 1–4
		int k      = 1 + random.nextInt(5);            // scale factor: 1–5
		int a      = k * (n + 1);                      // coefficient
		int result = k * (int) Math.pow(b, n + 1);

		String question = String.format(
			"<html><center><b>Evaluate the definite integral</b><br><br>"
			+ "\u222b<sub>0</sub><sup>%d</sup> %dx<sup>%d</sup> dx</center></html>",
			b, a, n);   // ∫

		return new MathQuestion(question, String.valueOf(result), "Integration");
	}

	/**
	 * Evaluate  log_base(value)  where value = base^exp, so the answer is exp.
	 */
	private MathQuestion generateLogarithm()
	{
		int base  = 2 + random.nextInt(4);   // 2–5
		int exp   = 1 + random.nextInt(4);   // 1–4
		int value = (int) Math.pow(base, exp);

		String question = String.format(
			"<html><center><b>Evaluate</b><br><br>"
			+ "log<sub>%d</sub>(%d)</center></html>", base, value);

		return new MathQuestion(question, String.valueOf(exp), "Logarithms");
	}

	/**
	 * Evaluate the binomial coefficient  nCr.
	 * n is in [4, 9], r is in [1, 3] to keep answers manageable.
	 */
	private MathQuestion generateBinomialCoeff()
	{
		int n      = 4 + random.nextInt(6);   // 4–9
		int r      = 1 + random.nextInt(3);   // 1–3
		int result = binomialCoeff(n, r);

		String question = String.format(
			"<html><center><b>Evaluate the binomial coefficient</b><br><br>"
			+ "<sup>%d</sup>C<sub>%d</sub></center></html>", n, r);

		return new MathQuestion(question, String.valueOf(result), "Binomial Theorem");
	}

	private int binomialCoeff(int n, int r)
	{
		if (r == 0 || r == n) return 1;
		int result = 1;
		for (int i = 0; i < r; i++)
		{
			result = result * (n - i) / (i + 1);
		}
		return result;
	}

	/**
	 * Ask for the acute angle (in degrees) corresponding to a standard
	 * sin / cos / tan value.  All answers are integers (30, 45, 60 or 90).
	 */
	private MathQuestion generateInverseTrig()
	{
		// Each entry: {function label, value string, answer in degrees}
		String[][] data =
		{
			{"sin(\u03b8) = 0.5",         "30"},   // θ
			{"sin(\u03b8) = 1",           "90"},
			{"cos(\u03b8) = 0.5",         "60"},
			{"cos(\u03b8) = 1",           "0"},
			{"tan(\u03b8) = 1",           "45"},
			{"sin(\u03b8) = \u221a3 / 2", "60"},   // √
			{"cos(\u03b8) = \u221a3 / 2", "30"},
			{"sin(\u03b8) = \u221a2 / 2", "45"},
			{"cos(\u03b8) = \u221a2 / 2", "45"},
		};

		String[] entry = data[random.nextInt(data.length)];
		String question = String.format(
			"<html><center><b>Find the acute angle \u03b8 in degrees</b><br><br>"
			+ "%s</center></html>", entry[0]);

		return new MathQuestion(question, entry[1], "Trigonometry");
	}
}
