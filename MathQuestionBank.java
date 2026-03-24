import java.util.Random;

/**
 * Generates randomised maths questions for the requested difficulty level.
 *
 * GCSE topics  : linear equations, percentages, geometry (area), statistics
 *                (mean), powers/indices, substitution, speed-distance-time.
 *
 * A-Level topics: differentiation (power rule), second derivatives, stationary
 *                 points, definite integration, logarithms, binomial
 *                 coefficients, inverse-trig angles.
 *
 * All questions are designed so the correct answer is an integer (or a simple
 * plain string) – this keeps on-screen rendering and answer checking
 * straightforward.
 */
public class MathQuestionBank
{
	private final MathDifficulty difficulty;
	private final Random random;

	private static final int GCSE_TYPES   = 7;
	private static final int ALEVEL_TYPES = 7;

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

	/**
	 * Return a harder question used for bomb-diffuse challenges.
	 * Always draws from the A-Level pool regardless of the chosen difficulty.
	 */
	public MathQuestion getHardQuestion()
	{
		return getALevelQuestion();
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
			case 4:  return generatePower();
			case 5:  return generateSubstitution();
			default: return generateSpeedDistanceTime();
		}
	}

	/**
	 * Solve a linear equation of the form  ax + b = c  (integer solution).
	 */
	private MathQuestion generateLinearEquation()
	{
		int a        = 2 + random.nextInt(8);
		int solution = (random.nextInt(11) - 5);
		if (solution == 0) solution = 1;
		int b = random.nextInt(21) - 10;
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
			question = String.format("Solve for x:    %dx \u2212 %d = %d", a, -b, c);
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
		int y   = (1 + random.nextInt(20)) * 20;
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
			int w = 2 + random.nextInt(12);
			int h = 2 + random.nextInt(12);
			String q = String.format("Find the area of a rectangle with width %d and height %d.", w, h);
			return new MathQuestion(q, String.valueOf(w * h), "Geometry");
		}
		else
		{
			int base   = (1 + random.nextInt(10)) * 2;
			int height = 2 + random.nextInt(10);
			int area   = (base * height) / 2;   // base is always even, so result is exact
			String q   = String.format(
				"Find the area of a triangle with base %d and perpendicular height %d.", base, height);
			return new MathQuestion(q, String.valueOf(area), "Geometry");
		}
	}

	/**
	 * Find the mean of a small list of positive integers.
	 */
	private MathQuestion generateMean()
	{
		int mean  = 2 + random.nextInt(8);
		int count = 3 + random.nextInt(3);
		int[] nums = new int[count];
		int remaining = mean * count;

		for (int i = 0; i < count - 1; i++)
		{
			final int SPREAD = 6;
			int maxAllowed = remaining - (count - 1 - i);
			int lo = Math.max(1, remaining - (count - 1 - i) * (mean + SPREAD));
			int hi = Math.min(maxAllowed, mean + SPREAD - 1);
			if (lo > hi) lo = hi = mean;
			nums[i]   = lo + (hi > lo ? random.nextInt(hi - lo + 1) : 0);
			remaining -= nums[i];
		}
		nums[count - 1] = remaining;

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
	 * Evaluate a power expression using proper HTML superscript notation.
	 * Variant A: a^n (n = 2–4)
	 * Variant B: a² + b²
	 */
	private MathQuestion generatePower()
	{
		if (random.nextBoolean())
		{
			int base = 2 + random.nextInt(5);
			int exp  = 2 + random.nextInt(3);
			int ans  = (int) Math.pow(base, exp);
			String q = String.format(
				"<html><center><b>Evaluate</b><br><br>%d<sup>%d</sup></center></html>",
				base, exp);
			return new MathQuestion(q, String.valueOf(ans), "Powers & Indices");
		}
		else
		{
			int a   = 2 + random.nextInt(9);
			int b   = 2 + random.nextInt(9);
			int ans = a * a + b * b;
			String q = String.format(
				"<html><center><b>Evaluate</b><br><br>%d<sup>2</sup> + %d<sup>2</sup></center></html>",
				a, b);
			return new MathQuestion(q, String.valueOf(ans), "Powers & Indices");
		}
	}

	/**
	 * Evaluate a linear expression by substituting a given value of x.
	 * Expression: ax + b  (integer result).
	 */
	private MathQuestion generateSubstitution()
	{
		int a = 2 + random.nextInt(7);
		int b = random.nextInt(11) - 5;
		int x = 2 + random.nextInt(6);
		int result = a * x + b;

		String expr;
		if (b == 0)
		{
			expr = String.format("%dx", a);
		}
		else if (b > 0)
		{
			expr = String.format("%dx + %d", a, b);
		}
		else
		{
			expr = String.format("%dx \u2212 %d", a, -b);
		}

		String question = String.format("If x = %d,  find the value of    %s", x, expr);
		return new MathQuestion(question, String.valueOf(result), "Substitution");
	}

	/**
	 * Speed / Distance / Time question.
	 * All values chosen so the answer is a whole number.
	 */
	private MathQuestion generateSpeedDistanceTime()
	{
		int speed    = (1 + random.nextInt(9)) * 10;   // 10, 20, … 90
		int time     = 1 + random.nextInt(5);           // 1–5 hours
		int distance = speed * time;

		int variant = random.nextInt(3);
		String question;
		String answer;

		if (variant == 0)
		{
			question = String.format(
				"A car travels %d km in %d hour%s.  What is its speed in km/h?",
				distance, time, time > 1 ? "s" : "");
			answer = String.valueOf(speed);
		}
		else if (variant == 1)
		{
			question = String.format(
				"A car travels at %d km/h for %d hour%s.  How far does it travel (km)?",
				speed, time, time > 1 ? "s" : "");
			answer = String.valueOf(distance);
		}
		else
		{
			question = String.format(
				"A car travels %d km at %d km/h.  How long does the journey take (hours)?",
				distance, speed);
			answer = String.valueOf(time);
		}

		return new MathQuestion(question, answer, "Speed, Distance & Time");
	}

	// -----------------------------------------------------------------------
	// A-Level generators
	// -----------------------------------------------------------------------

	private MathQuestion getALevelQuestion()
	{
		switch (random.nextInt(ALEVEL_TYPES))
		{
			case 0:  return generateDifferentiation();
			case 1:  return generateSecondDerivative();
			case 2:  return generateStationaryPoint();
			case 3:  return generateDefiniteIntegral();
			case 4:  return generateLogarithm();
			case 5:  return generateBinomialCoeff();
			default: return generateInverseTrig();
		}
	}

	/**
	 * Differentiate  y = ax^n  by the power rule; ask for the coefficient.
	 */
	private MathQuestion generateDifferentiation()
	{
		int a = 2 + random.nextInt(8);
		int n = 2 + random.nextInt(4);
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

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(coefficient), "Differentiation");
	}

	/**
	 * Find the coefficient of the second derivative f''(x) for f(x) = ax^n.
	 * n is chosen in [3, 5] so the second derivative still contains an x term.
	 */
	private MathQuestion generateSecondDerivative()
	{
		int a      = 2 + random.nextInt(6);
		int n      = 3 + random.nextInt(3);   // 3–5
		int coeff  = a * n * (n - 1);
		int power2 = n - 2;

		String questionBody;
		if (power2 == 1)
		{
			questionBody = String.format(
				"<b>Find the second derivative</b><br><br>"
				+ "f(x) = %dx<sup>%d</sup><br><br>"
				+ "What is the coefficient of x in f''(x)?", a, n);
		}
		else
		{
			questionBody = String.format(
				"<b>Find the second derivative</b><br><br>"
				+ "f(x) = %dx<sup>%d</sup><br><br>"
				+ "What is the coefficient of x<sup>%d</sup> in f''(x)?", a, n, power2);
		}

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(coeff), "Differentiation");
	}

	/**
	 * Find the x-value of a stationary point given f'(x) = nx − c.
	 * The answer x = c/n is always a positive integer.
	 */
	private MathQuestion generateStationaryPoint()
	{
		int n  = 2 + random.nextInt(4);   // coefficient: 2–5
		int x0 = 2 + random.nextInt(7);   // answer: 2–8
		int c  = n * x0;                  // so f'(x) = nx − c  →  x = c/n

		String questionBody = String.format(
			"<b>Find the stationary point</b><br><br>"
			+ "f'(x) = %dx \u2212 %d<br><br>"
			+ "What is the x-value of the stationary point?", n, c);

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(x0), "Differentiation");
	}

	/**
	 * Evaluate the definite integral ∫[0 to b] ax^n dx.
	 * Uses a 3-row HTML table so the upper and lower limits are symmetrically
	 * placed above and below the integral sign.
	 */
	private MathQuestion generateDefiniteIntegral()
	{
		int n      = 1 + random.nextInt(3);
		int b      = 1 + random.nextInt(4);
		int k      = 1 + random.nextInt(5);
		int a      = k * (n + 1);
		int result = k * (int) Math.pow(b, n + 1);

		// 3-row table: upper limit / ∫ / lower limit, integrand spans all rows
		String question = String.format(
			"<html><center><b>Evaluate the definite integral</b><br><br>"
			+ "<table cellpadding='1' cellspacing='0'>"
			+ "<tr><td align='center' valign='bottom'><small>%d</small></td>"
			+ "    <td rowspan='3' valign='middle'>&nbsp;%dx<sup>%d</sup>&nbsp;dx</td></tr>"
			+ "<tr><td align='center'>&#8747;</td></tr>"
			+ "<tr><td align='center' valign='top'><small>0</small></td></tr>"
			+ "</table></center></html>",
			b, a, n);

		return new MathQuestion(question, String.valueOf(result), "Integration");
	}

	/**
	 * Evaluate  log_base(value)  where value = base^exp.
	 */
	private MathQuestion generateLogarithm()
	{
		int base  = 2 + random.nextInt(4);
		int exp   = 1 + random.nextInt(4);
		int value = (int) Math.pow(base, exp);

		String question = String.format(
			"<html><center><b>Evaluate</b><br><br>"
			+ "log<sub>%d</sub>(%d)</center></html>", base, value);

		return new MathQuestion(question, String.valueOf(exp), "Logarithms");
	}

	/**
	 * Evaluate the binomial coefficient  nCr.
	 */
	private MathQuestion generateBinomialCoeff()
	{
		int n      = 4 + random.nextInt(6);
		int r      = 1 + random.nextInt(3);
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
	 * Ask for the acute angle (in degrees) for a standard trig value.
	 */
	private MathQuestion generateInverseTrig()
	{
		String[][] data =
		{
			{"sin(\u03b8) = 0.5",         "30"},
			{"sin(\u03b8) = 1",           "90"},
			{"cos(\u03b8) = 0.5",         "60"},
			{"cos(\u03b8) = 1",           "0"},
			{"tan(\u03b8) = 1",           "45"},
			{"sin(\u03b8) = \u221a3 / 2", "60"},
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

