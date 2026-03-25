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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Modal dialog that presents a single maths challenge to the current player
 * and collects their typed answer.
 *
 * <p>The dialog is created and displayed via the static factory method
 * {@link #showQuestion(MathQuestion, java.awt.Component)}, which blocks the
 * calling thread until the player submits an answer and then returns
 * {@code true} if the answer is correct, or {@code false} otherwise.</p>
 *
 * <p>The dialog supports both plain-text and HTML question strings.  Plain
 * text is automatically wrapped in centred HTML for consistent rendering.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     MathQuestion
 * @see     MathQuestionBank
 * @see     GameController
 */
public class MathQuestionDialog extends JDialog
{
// ── Fields ────────────────────────────────────────────────────────────

/** The maths question to display. */
private final MathQuestion question;

/** Stores the outcome of the player's submission. */
private boolean answeredCorrectly = false;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code MathQuestionDialog} as a modal child of the given
 * parent frame.
 *
 * <p>Use the static factory {@link #showQuestion} rather than calling this
 * constructor directly.</p>
 *
 * @param parent   the owning frame (may be {@code null})
 * @param question the maths question to present
 */
public MathQuestionDialog(Frame parent, MathQuestion question)
{
super(parent, "Maths Challenge \u2013 " + question.getTopic(), true);
this.question = question;
buildUI();
pack();
setResizable(false);
setLocationRelativeTo(parent);
}

// ── Private UI construction ───────────────────────────────────────────

/**
 * Builds and lays out all UI components inside the dialog.
 *
 * <p>The layout consists of three regions:</p>
 * <ul>
 *   <li><b>North</b>  – a coloured banner showing the question topic.</li>
 *   <li><b>Centre</b> – the question text (HTML or plain).</li>
 *   <li><b>South</b>  – an answer input field and submit button.</li>
 * </ul>
 */
private void buildUI()
{
JPanel root = new JPanel(new BorderLayout(12, 12));
root.setBackground(Color.white);
root.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

// ── Topic banner ──────────────────────────────────────────────────
JLabel topicLabel = new JLabel(question.getTopic(), JLabel.CENTER);
topicLabel.setFont(new Font("Arial", Font.BOLD, 14));
topicLabel.setForeground(Color.white);
topicLabel.setBackground(new Color(50, 100, 200));
topicLabel.setOpaque(true);
topicLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
root.add(topicLabel, BorderLayout.NORTH);

// ── Question text ─────────────────────────────────────────────────
// Wrap plain text in HTML so multi-line content is centred correctly.
String rawText = question.getQuestionText();
String htmlText;
if (rawText.trim().toLowerCase().startsWith("<html>"))
{
htmlText = rawText;
}
else
{
htmlText = "<html><center>" + rawText.replace("\n", "<br>") + "</center></html>";
}

JLabel questionLabel = new JLabel(htmlText, JLabel.CENTER);
questionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
questionLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
root.add(questionLabel, BorderLayout.CENTER);

// ── Answer input row ──────────────────────────────────────────────
JLabel answerPrompt = new JLabel("Your answer:");
answerPrompt.setFont(new Font("Arial", Font.PLAIN, 16));

JTextField answerField = new JTextField(10);
answerField.setFont(new Font("Arial", Font.BOLD, 18));
answerField.setHorizontalAlignment(JTextField.CENTER);

JButton submitButton = new JButton("Submit");
submitButton.setFont(new Font("Arial", Font.BOLD, 16));
submitButton.setBackground(new Color(50, 150, 50));
submitButton.setForeground(Color.white);
submitButton.setFocusPainted(false);

JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
inputRow.setBackground(Color.white);
inputRow.add(answerPrompt);
inputRow.add(answerField);
inputRow.add(submitButton);
root.add(inputRow, BorderLayout.SOUTH);

// ── Submission logic ──────────────────────────────────────────────
Runnable submit = () ->
{
answeredCorrectly = question.checkAnswer(answerField.getText());
dispose();
};

submitButton.addActionListener(e -> submit.run());
answerField.addActionListener(e -> submit.run());

// Focus the answer field as soon as the dialog opens
addWindowListener(new WindowAdapter()
{
@Override
public void windowOpened(WindowEvent e)
{
answerField.requestFocusInWindow();
}
});

add(root);
setMinimumSize(new Dimension(420, 220));
}

// ── Getter ────────────────────────────────────────────────────────────

/**
 * Returns {@code true} if the player typed the correct answer.
 *
 * @return {@code true} for a correct answer
 */
public boolean isAnsweredCorrectly()
{
return answeredCorrectly;
}

// ── Static factory ────────────────────────────────────────────────────

/**
 * Displays the question dialog modally and blocks until the player submits
 * an answer.
 *
 * <p>The dialog is centred relative to the window containing
 * {@code parent}.</p>
 *
 * @param question the maths question to display
 * @param parent   any component in the window hierarchy used to locate the
 *                 parent frame
 * @return {@code true} if the player answered correctly; {@code false}
 *         otherwise
 */
public static boolean showQuestion(MathQuestion question, java.awt.Component parent)
{
Frame frame = null;
Window window = SwingUtilities.getWindowAncestor(parent);
if (window instanceof Frame)
{
frame = (Frame) window;
}

MathQuestionDialog dialog = new MathQuestionDialog(frame, question);
dialog.setVisible(true); // blocks (modal) until dispose() is called
return dialog.isAnsweredCorrectly();
}
}
