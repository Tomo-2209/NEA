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
 * Side panel that displays live statistics for both players.
 *
 * <p>{@code StatsPanel} is placed to the left of the main game board by
 * {@link TicTacTotalUI} and is refreshed after every maths question and every
 * round.  It shows the following information for each player:</p>
 * <ul>
 *   <li>Total rounds won (score)</li>
 *   <li>Current win streak</li>
 *   <li>Best win streak</li>
 *   <li>Number of correct maths answers</li>
 *   <li>Number of incorrect maths answers</li>
 * </ul>
 *
 * <p>Labels are constructed once in the constructor and updated in-place by
 * {@link #update(Player, Player)} to avoid rebuilding the component tree on
 * every refresh.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     TicTacTotalUI
 * @see     GameController
 */
public class StatsPanel extends JPanel
{
// ── Style constants ───────────────────────────────────────────────────

/** Background colour for the panel. */
private static final Color BG_COLOUR  = new Color(40, 40, 40);

/** Background colour for each player's name header. */
private static final Color HDR_COLOUR = new Color(60, 60, 100);

/** Default text colour for stat labels. */
private static final Color TEXT_COLOUR = Color.white;

/** Font used for player-name headers and the "STATISTICS" title. */
private static final Font HDR_FONT = new Font("Arial", Font.BOLD, 14);

/** Font used for stat row labels. */
private static final Font VAL_FONT = new Font("Arial", Font.BOLD, 13);

// ── Player X stat labels ──────────────────────────────────────────────

/** Player X display-name label. */
private final JLabel xNameLabel;

/** Player X rounds-won label. */
private final JLabel xWinsLabel;

/** Player X current-streak label. */
private final JLabel xStreakLabel;

/** Player X best-streak label. */
private final JLabel xBestLabel;

/** Player X correct-answers label. */
private final JLabel xCorrectLabel;

/** Player X wrong-answers label. */
private final JLabel xWrongLabel;

// ── Player O stat labels ──────────────────────────────────────────────

/** Player O display-name label. */
private final JLabel oNameLabel;

/** Player O rounds-won label. */
private final JLabel oWinsLabel;

/** Player O current-streak label. */
private final JLabel oStreakLabel;

/** Player O best-streak label. */
private final JLabel oBestLabel;

/** Player O correct-answers label. */
private final JLabel oCorrectLabel;

/** Player O wrong-answers label. */
private final JLabel oWrongLabel;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs the {@code StatsPanel}, laying out all static structure and
 * initialising every label with placeholder values of {@code "0"}.
 */
public StatsPanel()
{
setBackground(BG_COLOUR);
setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
setPreferredSize(new Dimension(180, 0));

// ── Panel title ───────────────────────────────────────────────────
JLabel title = makeCentredLabel("STATISTICS", HDR_FONT, new Color(200, 200, 255));
add(title);
add(Box.createVerticalStrut(8));
add(makeSeparator());
add(Box.createVerticalStrut(8));

// ── Player X section ──────────────────────────────────────────────
xNameLabel    = makeCentredLabel("Player 1 (X)", HDR_FONT, TEXT_COLOUR);
xWinsLabel    = makeStatLabel("Wins:",    "0");
xStreakLabel  = makeStatLabel("Streak:",  "0");
xBestLabel    = makeStatLabel("Best:",    "0");
xCorrectLabel = makeStatLabel("Correct:", "0");
xWrongLabel   = makeStatLabel("Wrong:",   "0");

add(makeNameHeader(xNameLabel));
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
oNameLabel    = makeCentredLabel("Player 2 (O)", HDR_FONT, TEXT_COLOUR);
oWinsLabel    = makeStatLabel("Wins:",    "0");
oStreakLabel  = makeStatLabel("Streak:",  "0");
oBestLabel    = makeStatLabel("Best:",    "0");
oCorrectLabel = makeStatLabel("Correct:", "0");
oWrongLabel   = makeStatLabel("Wrong:",   "0");

add(makeNameHeader(oNameLabel));
add(Box.createVerticalStrut(6));
add(oWinsLabel);
add(oStreakLabel);
add(oBestLabel);
add(oCorrectLabel);
add(oWrongLabel);
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Refreshes all statistic labels with the latest values from both players.
 *
 * <p>This method should be called after every maths question is answered
 * and at the end of each round.</p>
 *
 * @param playerX the player using symbol {@code "X"}
 * @param playerO the player using symbol {@code "O"}
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

/**
 * Creates a horizontally centred {@link JLabel} with the specified text,
 * font and colour.
 *
 * @param text   the label text
 * @param font   the font to apply
 * @param colour the foreground colour
 * @return the configured label
 */
private JLabel makeCentredLabel(String text, Font font, Color colour)
{
JLabel label = new JLabel(text, SwingConstants.CENTER);
label.setFont(font);
label.setForeground(colour);
label.setAlignmentX(CENTER_ALIGNMENT);
return label;
}

/**
 * Creates a left-aligned stat-row {@link JLabel} showing the label text
 * and initial value combined in a single string.
 *
 * <p>The full text is replaced on each {@link #update} call.</p>
 *
 * @param labelText the stat name (e.g. {@code "Wins:"})
 * @param value     the initial value string (e.g. {@code "0"})
 * @return the configured label
 */
private JLabel makeStatLabel(String labelText, String value)
{
JLabel label = new JLabel(labelText + "  " + value);
label.setFont(VAL_FONT);
label.setForeground(TEXT_COLOUR);
label.setAlignmentX(LEFT_ALIGNMENT);
return label;
}

/**
 * Creates a coloured header panel containing the player's name label.
 *
 * @param nameLabel the label to embed in the header
 * @return the styled header panel
 */
private JPanel makeNameHeader(JLabel nameLabel)
{
JPanel header = new JPanel();
header.setBackground(HDR_COLOUR);
header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
header.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
header.add(nameLabel);
header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
return header;
}

/**
 * Creates a thin horizontal separator line for visual grouping.
 *
 * @return the configured {@link JSeparator}
 */
private JSeparator makeSeparator()
{
JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
separator.setForeground(new Color(100, 100, 100));
separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
return separator;
}
}
