import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Modal dialog displayed when a player clicks a bomb tile.
 *
 * <p>The current player has {@link #COUNTDOWN_SECONDS} seconds to answer a
 * harder maths question in order to diffuse the bomb.  A Swing {@link Timer}
 * drives a countdown label and progress bar.  The possible outcomes are:</p>
 * <ul>
 *   <li>{@link DiffuseResult#DIFFUSED} – the player answered correctly within
 *       the time limit.</li>
 *   <li>{@link DiffuseResult#FAILED}   – the player answered incorrectly
 *       <em>or</em> the timer reached zero.</li>
 * </ul>
 *
 * <p>The bomb fuse animation in {@link BombAnimationEngine} is deliberately
 * set to ~40 seconds – longer than the 35-second countdown here – so that the
 * fuse is still visibly burning when the timer expires.  On failure,
 * {@link BombTile#triggerExplosion()} fast-forwards the animation rather than
 * waiting for the fuse to burn naturally.</p>
 *
 * <p>Use the static factory {@link #showDiffuseDialog} rather than
 * constructing the dialog directly.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     BombTile
 * @see     BombAnimationEngine
 * @see     GameController#handleBombClick(int, int)
 */
public class BombDiffuseDialog extends JDialog
{
// ── Constant ──────────────────────────────────────────────────────────

/**
 * The number of seconds the player has to answer the diffuse question.
 *
 * <p>This value is intentionally shorter than the ~40-second fuse burn in
 * {@link BombAnimationEngine} so that time expiry triggers an immediate
 * explosion rather than a gradual fuse burn-out.</p>
 */
private static final int COUNTDOWN_SECONDS = 35;

// ── Enum ──────────────────────────────────────────────────────────────

/**
 * Represents the outcome of a bomb-diffuse attempt.
 */
public enum DiffuseResult
{
/** The player answered correctly within the time limit. */
DIFFUSED,

/** The player answered incorrectly or the countdown reached zero. */
FAILED
}

// ── Fields ────────────────────────────────────────────────────────────

/** The hard maths question the player must answer to diffuse the bomb. */
private final MathQuestion question;

/** The result of this diffuse attempt; defaults to {@link DiffuseResult#FAILED}. */
private DiffuseResult result = DiffuseResult.FAILED;

/** Remaining seconds on the countdown; decremented by the timer. */
private int secondsLeft = COUNTDOWN_SECONDS;

/** Swing timer that fires once per second to update the countdown. */
private Timer countdownTimer;

/** Label showing the remaining time in seconds. */
private JLabel countdownLabel;

/** Progress bar visualising the remaining time. */
private JProgressBar progressBar;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code BombDiffuseDialog} as a modal child of the given
 * parent frame.
 *
 * <p>Use the static factory {@link #showDiffuseDialog} rather than
 * calling this constructor directly.</p>
 *
 * @param parent   the owning frame (may be {@code null})
 * @param question the harder maths question to present
 */
public BombDiffuseDialog(Frame parent, MathQuestion question)
{
super(parent, "\uD83D\uDCA3  DEFUSE THE BOMB!", true);
this.question = question;
buildUI();
pack();
setResizable(false);
setLocationRelativeTo(parent);
}

// ── Private UI construction ───────────────────────────────────────────

/**
 * Builds and lays out all UI components inside the dialog and starts the
 * countdown timer.
 *
 * <p>The layout consists of:</p>
 * <ul>
 *   <li><b>North</b>  – a red warning banner.</li>
 *   <li><b>Centre</b> – a countdown label, progress bar and the question
 *                       text.</li>
 *   <li><b>South</b>  – an answer input field and submit button.</li>
 * </ul>
 */
private void buildUI()
{
JPanel root = new JPanel(new BorderLayout(12, 12));
root.setBackground(new Color(30, 0, 0));
root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

// ── Warning banner ────────────────────────────────────────────────
JLabel warningLabel = new JLabel("\u26A0  DEFUSE THE BOMB  \u26A0", JLabel.CENTER);
warningLabel.setFont(new Font("Arial", Font.BOLD, 18));
warningLabel.setForeground(Color.red);
warningLabel.setBackground(new Color(80, 0, 0));
warningLabel.setOpaque(true);
warningLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
root.add(warningLabel, BorderLayout.NORTH);

// ── Countdown area ────────────────────────────────────────────────
JPanel timerPanel = new JPanel(new BorderLayout(4, 6));
timerPanel.setBackground(new Color(30, 0, 0));

countdownLabel = new JLabel("Time remaining: " + secondsLeft + "s", JLabel.CENTER);
countdownLabel.setFont(new Font("Arial", Font.BOLD, 20));
countdownLabel.setForeground(Color.yellow);

progressBar = new JProgressBar(0, COUNTDOWN_SECONDS);
progressBar.setValue(COUNTDOWN_SECONDS);
progressBar.setForeground(new Color(255, 140, 0));
progressBar.setBackground(new Color(80, 0, 0));
progressBar.setStringPainted(false);
progressBar.setPreferredSize(new Dimension(400, 18));

timerPanel.add(countdownLabel, BorderLayout.NORTH);
timerPanel.add(progressBar,    BorderLayout.SOUTH);

// ── Question text ─────────────────────────────────────────────────
String rawText  = question.getQuestionText();
String htmlText = rawText.trim().toLowerCase().startsWith("<html>") ? rawText
: "<html><center>" + rawText.replace("\n", "<br>") + "</center></html>";

JLabel questionLabel = new JLabel(htmlText, JLabel.CENTER);
questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
questionLabel.setForeground(Color.white);
questionLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

JPanel centrePanel = new JPanel(new BorderLayout(8, 8));
centrePanel.setBackground(new Color(30, 0, 0));
centrePanel.add(timerPanel,    BorderLayout.NORTH);
centrePanel.add(questionLabel, BorderLayout.CENTER);
root.add(centrePanel, BorderLayout.CENTER);

// ── Answer input row ──────────────────────────────────────────────
JLabel answerPrompt = new JLabel("Answer:");
answerPrompt.setFont(new Font("Arial", Font.PLAIN, 16));
answerPrompt.setForeground(Color.white);

JTextField answerField = new JTextField(10);
answerField.setFont(new Font("Arial", Font.BOLD, 18));
answerField.setHorizontalAlignment(JTextField.CENTER);

JButton submitButton = new JButton("DEFUSE!");
submitButton.setFont(new Font("Arial", Font.BOLD, 16));
submitButton.setBackground(new Color(180, 40, 40));
submitButton.setForeground(Color.white);
submitButton.setFocusPainted(false);

JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
inputRow.setBackground(new Color(30, 0, 0));
inputRow.add(answerPrompt);
inputRow.add(answerField);
inputRow.add(submitButton);
root.add(inputRow, BorderLayout.SOUTH);

// ── Submission logic ──────────────────────────────────────────────
Runnable submit = () ->
{
countdownTimer.stop();
result = question.checkAnswer(answerField.getText())
? DiffuseResult.DIFFUSED
: DiffuseResult.FAILED;
dispose();
};

submitButton.addActionListener(e -> submit.run());
answerField.addActionListener(e -> submit.run());

// ── Countdown timer (fires every second) ──────────────────────────
countdownTimer = new Timer(1000, e ->
{
secondsLeft--;
countdownLabel.setText("Time remaining: " + secondsLeft + "s");
progressBar.setValue(secondsLeft);

if (secondsLeft <= 5)
{
countdownLabel.setForeground(Color.red);
}

if (secondsLeft <= 0)
{
countdownTimer.stop();
result = DiffuseResult.FAILED;
dispose();
}
});
countdownTimer.start();

// Focus the answer field when the dialog opens; treat window-close as failure
addWindowListener(new WindowAdapter()
{
@Override
public void windowOpened(WindowEvent e)
{
answerField.requestFocusInWindow();
}

@Override
public void windowClosing(WindowEvent e)
{
countdownTimer.stop();
result = DiffuseResult.FAILED;
}
});

add(root);
setMinimumSize(new Dimension(460, 280));
}

// ── Getter ────────────────────────────────────────────────────────────

/**
 * Returns the outcome of this diffuse attempt.
 *
 * @return {@link DiffuseResult#DIFFUSED} if the player answered correctly
 *         in time; {@link DiffuseResult#FAILED} otherwise
 */
public DiffuseResult getResult()
{
return result;
}

// ── Static factory ────────────────────────────────────────────────────

/**
 * Displays the diffuse dialog modally and blocks until the player submits
 * an answer or the timer expires.
 *
 * <p>The bomb animation continues in the background while this dialog is
 * shown.</p>
 *
 * @param question a harder maths question appropriate to the chosen
 *                 difficulty
 * @param parent   any component in the window hierarchy (used to centre
 *                 the dialog and locate the parent frame)
 * @return {@link DiffuseResult#DIFFUSED} if the player answered correctly
 *         in time; {@link DiffuseResult#FAILED} otherwise
 */
public static DiffuseResult showDiffuseDialog(MathQuestion question,
                                               java.awt.Component parent)
{
Frame frame = null;
Window window = SwingUtilities.getWindowAncestor(parent);
if (window instanceof Frame)
{
frame = (Frame) window;
}

BombDiffuseDialog dialog = new BombDiffuseDialog(frame, question);
dialog.setVisible(true); // blocks (modal) until dispose() is called
return dialog.getResult();
}
}
