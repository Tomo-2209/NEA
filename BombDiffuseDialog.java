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
 * Modal dialog displayed when a bomb tile is clicked.
 * The player has a limited time (COUNTDOWN_SECONDS) to answer a harder maths
 * question.  A Swing Timer drives the countdown progress bar and label.
 *
 * Possible results:
 *   DIFFUSED – player answered correctly within the time limit.
 *   FAILED   – player answered incorrectly OR the timer expired.
 */
public class BombDiffuseDialog extends JDialog
{
	/** Time allowed to answer the diffuse question. */
	private static final int COUNTDOWN_SECONDS = 35;

	public enum DiffuseResult { DIFFUSED, FAILED }

	private final MathQuestion question;
	private DiffuseResult result = DiffuseResult.FAILED;

	private int secondsLeft = COUNTDOWN_SECONDS;
	private Timer countdownTimer;
	private JLabel countdownLabel;
	private JProgressBar progressBar;

	public BombDiffuseDialog(Frame parent, MathQuestion question)
	{
		super(parent, "\uD83D\uDCA3  DEFUSE THE BOMB!", true);
		this.question = question;
		buildUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	private void buildUI()
	{
		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBackground(new Color(30, 0, 0));
		root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

		// ── Warning banner ────────────────────────────────────────────────
		JLabel warning = new JLabel("\u26A0  DEFUSE THE BOMB  \u26A0", JLabel.CENTER);
		warning.setFont(new Font("Arial", Font.BOLD, 18));
		warning.setForeground(Color.red);
		warning.setBackground(new Color(80, 0, 0));
		warning.setOpaque(true);
		warning.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		root.add(warning, BorderLayout.NORTH);

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
		timerPanel.add(progressBar, BorderLayout.SOUTH);

		// ── Question text ─────────────────────────────────────────────────
		String rawText = question.getQuestionText();
		String htmlText = rawText.trim().toLowerCase().startsWith("<html>") ? rawText
				: "<html><center>" + rawText.replace("\n", "<br>") + "</center></html>";

		JLabel questionLabel = new JLabel(htmlText, JLabel.CENTER);
		questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		questionLabel.setForeground(Color.white);
		questionLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

		JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
		centerPanel.setBackground(new Color(30, 0, 0));
		centerPanel.add(timerPanel, BorderLayout.NORTH);
		centerPanel.add(questionLabel, BorderLayout.CENTER);
		root.add(centerPanel, BorderLayout.CENTER);

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
			boolean correct = question.checkAnswer(answerField.getText());
			result = correct ? DiffuseResult.DIFFUSED : DiffuseResult.FAILED;
			dispose();
		};

		submitButton.addActionListener(e -> submit.run());
		answerField.addActionListener(e -> submit.run());

		// ── Countdown Swing Timer ─────────────────────────────────────────
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

		// Focus answer field when dialog opens; treat close button as failure
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

	/** @return the outcome of this diffuse attempt. */
	public DiffuseResult getResult()
	{
		return result;
	}

	/**
	 * Display the diffuse dialog modally and return the outcome.
	 * The bomb animation continues in the background while this dialog is shown.
	 *
	 * @param question a harder maths question
	 * @param parent   any component in the window hierarchy (used to centre the dialog)
	 * @return DIFFUSED if the player answered correctly in time, FAILED otherwise
	 */
	public static DiffuseResult showDiffuseDialog(MathQuestion question, java.awt.Component parent)
	{
		Frame frame = null;
		Window w = SwingUtilities.getWindowAncestor(parent);
		if (w instanceof Frame)
		{
			frame = (Frame) w;
		}
		BombDiffuseDialog dlg = new BombDiffuseDialog(frame, question);
		dlg.setVisible(true);   // modal – blocks until dispose() is called
		return dlg.getResult();
	}
}
