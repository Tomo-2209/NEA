import java.util.Random;

/**
 * Generates randomised maths questions appropriate to the chosen difficulty
 * level.
 *
 * <h3>GCSE standard topics</h3>
 * <ul>
 *   <li>Linear equations</li>
 *   <li>Percentages</li>
 *   <li>Geometry (area of rectangles and triangles)</li>
 *   <li>Statistics (mean)</li>
 *   <li>Powers and indices</li>
 *   <li>Algebraic substitution</li>
 *   <li>Speed, distance and time</li>
 * </ul>
 *
 * <h3>GCSE harder topics (bomb-diffuse challenges)</h3>
 * <ul>
 *   <li>Quadratic equations</li>
 *   <li>Pythagoras' theorem</li>
 *   <li>Simultaneous equations</li>
 *   <li>nth-term sequences</li>
 * </ul>
 *
 * <h3>A-Level standard topics</h3>
 * <ul>
 *   <li>Differentiation (power rule)</li>
 *   <li>Second derivatives</li>
 *   <li>Stationary points</li>
 *   <li>Definite integration</li>
 *   <li>Logarithms</li>
 *   <li>Binomial coefficients</li>
 *   <li>Inverse-trig angles</li>
 * </ul>
 *
 * <h3>A-Level harder topics (bomb-diffuse challenges)</h3>
 * <ul>
 *   <li>Geometric series sum</li>
 *   <li>Chain-rule differentiation</li>
 *   <li>Harder definite integrals</li>
 * </ul>
 *
 * <p>All questions are designed so the correct answer is an integer (or a
 * short plain string) to keep on-screen rendering and answer-checking
 * straightforward.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     MathQuestion
 * @see     MathDifficulty
 */
public class MathQuestionBank
{
	// ── Constants ─────────────────────────────────────────────────────────

	/** Number of distinct GCSE standard question types. */
	private static final int GCSE_TYPES        = 7;

	/** Number of distinct harder GCSE question types (bomb-diffuse). */
	private static final int GCSE_HARD_TYPES   = 4;

	/** Number of distinct A-Level standard question types. */
	private static final int ALEVEL_TYPES      = 7;

	/** Number of distinct harder A-Level question types (bomb-diffuse). */
	private static final int ALEVEL_HARD_TYPES = 3;

	// ── Fields ────────────────────────────────────────────────────────────

	/** The difficulty level selected at the start of the game. */
	private final MathDifficulty difficulty;

	/** Random number generator used by all question generators. */
	private final Random random;

	// ── Constructor ───────────────────────────────────────────────────────

	/**
	 * Constructs a {@code MathQuestionBank} configured for the given difficulty.
	 *
	 * @param difficulty the difficulty level for this game session
	 */
	public MathQuestionBank(MathDifficulty difficulty)
	{
		this.difficulty = difficulty;
		this.random     = new Random();
	}

	// ── Public methods ────────────────────────────────────────────────────

	/**
	 * Returns a freshly generated question appropriate to the stored
	 * difficulty level.
	 *
	 * <p>GCSE games receive a standard GCSE question; A-Level games receive a
	 * standard A-Level question.</p>
	 *
	 * @return a randomly generated {@link MathQuestion}
	 */
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
	 * Returns a harder question used for bomb-diffuse challenges.
	 *
	 * <p>GCSE games receive a harder GCSE question; A-Level games receive a
	 * harder A-Level question.</p>
	 *
	 * @return a randomly generated harder {@link MathQuestion}
	 */
	public MathQuestion getHardQuestion()
	{
		if (difficulty == MathDifficulty.GCSE)
		{
			return getHardGcseQuestion();
		}
		else
		{
			return getHardALevelQuestion();
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

	// -----------------------------------------------------------------------
	// Harder GCSE generators  (used for bomb-diffuse challenges)
	// -----------------------------------------------------------------------

	private MathQuestion getHardGcseQuestion()
	{
		switch (random.nextInt(GCSE_HARD_TYPES))
		{
			case 0:  return generateQuadratic();
			case 1:  return generatePythagoras();
			case 2:  return generateSimultaneous();
			default: return generateNthTerm();
		}
	}

	/**
	 * Solve a quadratic x² − (r1+r2)x + r1·r2 = 0 with two positive integer roots.
	 * The player is asked for the larger root.
	 */
	private MathQuestion generateQuadratic()
	{
		int r1 = 1 + random.nextInt(5);            // smaller root: 1–5
		int r2 = r1 + 1 + random.nextInt(5);       // larger root: r1+1 … r1+5
		int b  = -(r1 + r2);                       // coefficient of x (negative)
		int c  = r1 * r2;                          // constant term

		String bStr = (b < 0)
			? "\u2212 " + (-b)     // e.g. "− 7"
			: "+ " + b;
		String cStr = "+ " + c;

		String questionBody = String.format(
			"<b>Solve the quadratic equation</b><br><br>"
			+ "x<sup>2</sup> %s x %s = 0<br><br>"
			+ "What is the <u>larger</u> root?",
			bStr, cStr);

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(r2), "Quadratic Equations");
	}

	/**
	 * Find the hypotenuse of a right-angled triangle using a Pythagorean triple.
	 */
	private MathQuestion generatePythagoras()
	{
		int[][] triples = {
			{3, 4, 5}, {5, 12, 13}, {8, 15, 17}, {7, 24, 25},
			{6, 8, 10}, {9, 12, 15}, {5, 12, 13}
		};
		int[] triple = triples[random.nextInt(triples.length)];
		int a = triple[0], b = triple[1], c = triple[2];

		String question = String.format(
			"<html><center><b>Pythagoras</b><br><br>"
			+ "A right-angled triangle has legs of length %d and %d.<br>"
			+ "What is the length of the hypotenuse?</center></html>", a, b);

		return new MathQuestion(question, String.valueOf(c), "Pythagoras");
	}

	/**
	 * Solve a pair of simultaneous linear equations; ask for x.
	 * Equations: a1·x + b1·y = c1  and  a2·x + b2·y = c2.
	 * x and y are chosen first (small positive integers) so integer solutions
	 * are guaranteed.
	 */
	private MathQuestion generateSimultaneous()
	{
		int x = 1 + random.nextInt(5);
		int y = 1 + random.nextInt(5);

		// Pick two distinct coefficient pairs so the system isn't degenerate
		int a1 = 1 + random.nextInt(3), b1 = 1 + random.nextInt(3);
		int a2 = a1 + 1 + random.nextInt(2), b2 = b1 - 1;
		if (b2 <= 0) b2 = b1 + 1;

		int c1 = a1 * x + b1 * y;
		int c2 = a2 * x + b2 * y;

		String questionBody = String.format(
			"<b>Solve the simultaneous equations</b><br><br>"
			+ "%dx + %dy = %d<br>"
			+ "%dx + %dy = %d<br><br>"
			+ "What is the value of x?",
			a1, b1, c1, a2, b2, c2);

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(x), "Simultaneous Equations");
	}

	/**
	 * Find the nth term of an arithmetic sequence given its general formula an + b.
	 */
	private MathQuestion generateNthTerm()
	{
		int a = 2 + random.nextInt(5);   // common difference: 2–6
		int b = 1 + random.nextInt(10);  // constant offset: 1–10
		int n = 5 + random.nextInt(6);   // term number to evaluate: 5–10
		int answer = a * n + b;

		String question = String.format(
			"<html><center><b>Sequences</b><br><br>"
			+ "The n<sup>th</sup> term of a sequence is given by <b>%dn + %d</b>.<br><br>"
			+ "What is the <b>%d</b><sup>th</sup> term?</center></html>",
			a, b, n);

		return new MathQuestion(question, String.valueOf(answer), "Sequences");
	}

	// -----------------------------------------------------------------------
	// Harder A-Level generators  (used for bomb-diffuse challenges)
	// -----------------------------------------------------------------------

	private MathQuestion getHardALevelQuestion()
	{
		switch (random.nextInt(ALEVEL_HARD_TYPES))
		{
			case 0:  return generateGeometricSeries();
			case 1:  return generateChainRule();
			default: return generateHarderIntegral();
		}
	}

	/**
	 * Find the sum of the first n terms of a geometric series  S = a(rⁿ − 1)/(r − 1).
	 * Parameters are chosen so the result is always a manageable integer.
	 */
	private MathQuestion generateGeometricSeries()
	{
		int a = 1 + random.nextInt(3);   // first term: 1–3
		int r = 2 + random.nextInt(2);   // common ratio: 2 or 3
		int n = 3 + random.nextInt(3);   // number of terms: 3–5
		int sum = a * ((int) Math.pow(r, n) - 1) / (r - 1);

		String question = String.format(
			"<html><center><b>Geometric Series</b><br><br>"
			+ "Find the sum of the first <b>%d</b> terms<br>"
			+ "of the geometric series with first term <b>%d</b><br>"
			+ "and common ratio <b>%d</b>.</center></html>",
			n, a, r);

		return new MathQuestion(question, String.valueOf(sum), "Sequences & Series");
	}

	/**
	 * Differentiate y = (ax)ⁿ using the chain rule and evaluate dy/dx at x = 0.
	 * dy/dx = an·(ax)^(n-1) evaluated at x=0 gives an·0^(n-1) = 0 for n > 1,
	 * so instead we ask for the coefficient of xⁿ⁻¹ in dy/dx.
	 *
	 * y = (ax)^n = aⁿ·xⁿ  →  dy/dx = n·aⁿ·x^(n-1).
	 * The player is asked for the coefficient  n·aⁿ.
	 */
	private MathQuestion generateChainRule()
	{
		int a = 2 + random.nextInt(3);   // inner coefficient: 2–4
		int n = 2 + random.nextInt(3);   // power: 2–4
		int coefficient = n * (int) Math.pow(a, n);
		int resultPower = n - 1;

		String questionBody;
		if (resultPower == 1)
		{
			questionBody = String.format(
				"<b>Chain Rule</b><br><br>"
				+ "Differentiate&nbsp;&nbsp;y = (%dx)<sup>%d</sup><br><br>"
				+ "What is the coefficient of x in dy/dx?", a, n);
		}
		else
		{
			questionBody = String.format(
				"<b>Chain Rule</b><br><br>"
				+ "Differentiate&nbsp;&nbsp;y = (%dx)<sup>%d</sup><br><br>"
				+ "What is the coefficient of x<sup>%d</sup> in dy/dx?", a, n, resultPower);
		}

		String question = "<html><center>" + questionBody + "</center></html>";
		return new MathQuestion(question, String.valueOf(coefficient), "Differentiation");
	}

	/**
	 * Evaluate a harder definite integral  ∫₀ᵇ ax² dx = ab³/3.
	 * a is chosen as a multiple of 3 so the result is always an integer.
	 */
	private MathQuestion generateHarderIntegral()
	{
		int b      = 2 + random.nextInt(4);          // upper limit: 2–5
		int k      = 1 + random.nextInt(4);          // multiplier: 1–4
		int a      = k * 3;                          // coefficient (multiple of 3)
		int result = k * b * b * b;                  // a·b³/3 = k·b³

		String question = String.format(
			"<html><center><b>Evaluate the definite integral</b><br><br>"
			+ "<table cellpadding='1' cellspacing='0'>"
			+ "<tr><td align='center' valign='bottom'><small>%d</small></td>"
			+ "    <td rowspan='3' valign='middle'>&nbsp;%dx<sup>2</sup>&nbsp;dx</td></tr>"
			+ "<tr><td align='center'>&#8747;</td></tr>"
			+ "<tr><td align='center' valign='top'><small>0</small></td></tr>"
			+ "</table></center></html>",
			b, a);

		return new MathQuestion(question, String.valueOf(result), "Integration");
	}
}

