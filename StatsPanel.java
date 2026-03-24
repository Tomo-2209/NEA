import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Panel that displays live game statistics for both players,
 * positioned to the left of the main game board.
 *
 * Statistics shown per player:
 *   - Total rounds won
 *   - Current win streak
 *   - Best win streak
 *   - Correct maths answers
 *   - Wrong maths answers
 */
public class StatsPanel extends JPanel
{
	private static final Color BG_COLOR   = new Color(40, 40, 40);
	private static final Color HDR_COLOR  = new Color(60, 60, 100);
	private static final Color TEXT_COLOR = Color.white;
	private static final Font  HDR_FONT   = new Font("Arial", Font.BOLD, 14);
	private static final Font  LBL_FONT   = new Font("Arial", Font.PLAIN, 13);
	private static final Font  VAL_FONT   = new Font("Arial", Font.BOLD, 13);

	// Player X stat labels
	private final JLabel xNameLabel;
	private final JLabel xWinsLabel;
	private final JLabel xStreakLabel;
	private final JLabel xBestLabel;
	private final JLabel xCorrectLabel;
	private final JLabel xWrongLabel;

	// Player O stat labels
	private final JLabel oNameLabel;
	private final JLabel oWinsLabel;
	private final JLabel oStreakLabel;
	private final JLabel oBestLabel;
	private final JLabel oCorrectLabel;
	private final JLabel oWrongLabel;

	public StatsPanel()
	{
		setBackground(BG_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
		setPreferredSize(new Dimension(180, 0));

		// ── Title ─────────────────────────────────────────────────────────
		JLabel title = centeredLabel("STATISTICS", HDR_FONT, new Color(200, 200, 255));
		add(title);
		add(Box.createVerticalStrut(8));
		add(makeSeparator());
		add(Box.createVerticalStrut(8));

		// ── Player X section ──────────────────────────────────────────────
		xNameLabel    = centeredLabel("Player 1 (X)", HDR_FONT, TEXT_COLOR);
		xWinsLabel    = makeStatLabel("Wins:",    "0");
		xStreakLabel  = makeStatLabel("Streak:",  "0");
		xBestLabel    = makeStatLabel("Best:",    "0");
		xCorrectLabel = makeStatLabel("Correct:", "0");
		xWrongLabel   = makeStatLabel("Wrong:",   "0");

		JPanel xHeader = makeHeaderPanel(xNameLabel);
		add(xHeader);
		add(Box.createVerticalStrut(6));
		add(xWinsLabel);
		add(xStreakLabel);
		add(xBestLabel);
		add(xCorrectLabel);
		add(xWrongLabel);

		add(Box.createVerticalStrut(10));
		add(makeSeparator());
		add(Box.createVerticalStrut(10));

		// ── Player O section ──────────────────────────────────────────────
		oNameLabel    = centeredLabel("Player 2 (O)", HDR_FONT, TEXT_COLOR);
		oWinsLabel    = makeStatLabel("Wins:",    "0");
		oStreakLabel  = makeStatLabel("Streak:",  "0");
		oBestLabel    = makeStatLabel("Best:",    "0");
		oCorrectLabel = makeStatLabel("Correct:", "0");
		oWrongLabel   = makeStatLabel("Wrong:",   "0");

		JPanel oHeader = makeHeaderPanel(oNameLabel);
		add(oHeader);
		add(Box.createVerticalStrut(6));
		add(oWinsLabel);
		add(oStreakLabel);
		add(oBestLabel);
		add(oCorrectLabel);
		add(oWrongLabel);
	}

	/**
	 * Refresh the panel with the latest player statistics.
	 * Should be called after every round and every answered question.
	 */
	public void update(Player playerX, Player playerO)
	{
		xNameLabel.setText(playerX.getName() + " (X)");
		xWinsLabel.setText("Wins:    " + playerX.getScore());
		xStreakLabel.setText("Streak: " + playerX.getWinStreak());
		xBestLabel.setText("Best:    " + playerX.getBestStreak());
		xCorrectLabel.setText("Correct: " + playerX.getCorrectAnswers());
		xWrongLabel.setText("Wrong:  " + playerX.getWrongAnswers());

		oNameLabel.setText(playerO.getName() + " (O)");
		oWinsLabel.setText("Wins:    " + playerO.getScore());
		oStreakLabel.setText("Streak: " + playerO.getWinStreak());
		oBestLabel.setText("Best:    " + playerO.getBestStreak());
		oCorrectLabel.setText("Correct: " + playerO.getCorrectAnswers());
		oWrongLabel.setText("Wrong:  " + playerO.getWrongAnswers());

		repaint();
	}

	// ── Private helpers ───────────────────────────────────────────────────

	private JLabel centeredLabel(String text, Font font, Color color)
	{
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(font);
		label.setForeground(color);
		label.setAlignmentX(CENTER_ALIGNMENT);
		return label;
	}

	/**
	 * Create a stat row JLabel.  Format: "Label  value"
	 * (The caller replaces the full text on update so combining label+value into
	 * one JLabel keeps the layout simple.)
	 */
	private JLabel makeStatLabel(String labelText, String value)
	{
		JLabel label = new JLabel(labelText + "  " + value);
		label.setFont(VAL_FONT);
		label.setForeground(TEXT_COLOR);
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}

	private JPanel makeHeaderPanel(JLabel nameLabel)
	{
		JPanel header = new JPanel();
		header.setBackground(HDR_COLOR);
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
		header.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		header.add(nameLabel);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		return header;
	}

	private JSeparator makeSeparator()
	{
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setForeground(new Color(100, 100, 100));
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
		return sep;
	}
}
