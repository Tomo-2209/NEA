import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Modal dialog that presents a single maths question to the current player and
 * collects their typed answer.
 *
 * Call the static factory {@link #showQuestion(MathQuestion, java.awt.Component)}
 * which blocks until the player submits an answer, then returns {@code true} if
 * the answer is correct and {@code false} otherwise.
 */
public class MathQuestionDialog extends JDialog
{
	/**
	 * Path to the icon shown in the dialog title bar.
	 * Replace with the actual path to your question/challenge image file.
	 */
	public static final String DIALOG_ICON_PATH = "images/question_icon.png";

	private final MathQuestion question;
	private boolean answeredCorrectly = false;

	public MathQuestionDialog(Frame parent, MathQuestion question)
	{
		super(parent, "Maths Challenge  \u2013  " + question.getTopic(), true);
		this.question = question;
		applyIcon();
		buildUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	/**
	 * Set the dialog window icon.
	 * Replace {@link #DIALOG_ICON_PATH} with your actual image path.
	 */
	private void applyIcon()
	{
		try
		{
			Image icon = new ImageIcon(DIALOG_ICON_PATH).getImage();
			setIconImage(icon);
		}
		catch (Exception e)
		{
			// Icon file not present yet – skip silently
		}
	}

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
		// questionText may already be an HTML string (from A-Level generators).
		// Wrap plain text in HTML so multi-line displays are centred.
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

		// Focus the text field immediately
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowOpened(java.awt.event.WindowEvent e)
			{
				answerField.requestFocusInWindow();
			}
		});

		add(root);
		setMinimumSize(new java.awt.Dimension(420, 220));
	}

	/** @return {@code true} if the player typed the correct answer. */
	public boolean isAnsweredCorrectly()
	{
		return answeredCorrectly;
	}

	/**
	 * Display the question dialog and block until the player submits.
	 *
	 * @param question the question to display
	 * @param parent   any component in the window hierarchy (used to centre the dialog)
	 * @return {@code true} if the player answered correctly
	 */
	public static boolean showQuestion(MathQuestion question, java.awt.Component parent)
	{
		Frame frame = null;
		Window w = SwingUtilities.getWindowAncestor(parent);
		if (w instanceof Frame)
		{
			frame = (Frame) w;
		}
		MathQuestionDialog dlg = new MathQuestionDialog(frame, question);
		dlg.setVisible(true);   // blocks (modal) until dispose() is called
		return dlg.isAnsweredCorrectly();
	}
}
