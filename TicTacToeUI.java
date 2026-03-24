import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class TicTacToeUI
{
	private final JFrame frame;
	private final JLabel textLabel;
	private final GamePanel gamePanel;
	private final JButton reset;
	private final JSlider gridSlider;
	private final StatsPanel statsPanel;
	
	private final GameController controller;
	private final GameModel model;
	
	public TicTacToeUI(JFrame frame)
	{
		this.frame = frame;

		// ── 1. Difficulty selection ───────────────────────────────────────
		String[] options = {"GCSE", "A-Level"};
		int choice = JOptionPane.showOptionDialog(
			frame,
			"Select the difficulty level for maths questions:",
			"Tic-Tac-Total \u2013 Difficulty",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);

		MathDifficulty difficulty = (choice == 1) ? MathDifficulty.A_LEVEL : MathDifficulty.GCSE;

		// ── 2. Player names ───────────────────────────────────────────────
		String player1Name = JOptionPane.showInputDialog(frame, "Enter name for Player 1 (X):");
		if (player1Name == null || player1Name.isEmpty())
		{
			player1Name = "Player 1";
		}
		
		String player2Name = JOptionPane.showInputDialog(frame, "Enter name for Player 2 (O):");
		if (player2Name == null || player2Name.isEmpty())
		{
			player2Name = "Player 2";
		}
		
		// ── 3. Header label ───────────────────────────────────────────────
		textLabel = new JLabel("Tic-Tac-Tactics", JLabel.CENTER);
		textLabel.setFont(new Font("Arial", Font.BOLD, 32));
		textLabel.setBackground(Color.darkGray);
		textLabel.setForeground(Color.white);
		textLabel.setOpaque(true);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(textLabel);
		frame.add(textPanel, BorderLayout.NORTH);

		// ── 4. Stats panel (left side) ────────────────────────────────────
		statsPanel = new StatsPanel();
		frame.add(statsPanel, BorderLayout.WEST);
		
		// ── 5. Game panel + controller ────────────────────────────────────
		model = new GameModel(3);
		gamePanel = new GamePanel(null, model.getGridSize());
		controller = new GameController(model, gamePanel, this, player1Name, player2Name, difficulty);
		
		frame.add(gamePanel, BorderLayout.CENTER);
		
		// ── 6. Reset button ───────────────────────────────────────────────
		reset = new JButton("Reset");
		reset.setFont(new Font("Arial", Font.BOLD, 20));
		reset.setBackground(Color.darkGray);
		reset.setForeground(Color.black);
		reset.setFocusable(false);
		reset.addActionListener(e -> controller.resetGame());
		frame.add(reset, BorderLayout.SOUTH);
		
		// ── 7. Grid-size slider (right side, 3–10) ────────────────────────
		gridSlider = new JSlider(JSlider.VERTICAL, 3, 10, 3);
		gridSlider.setMajorTickSpacing(1);
		gridSlider.setPaintTicks(true);
		gridSlider.setPaintLabels(true);
		gridSlider.setBackground(Color.darkGray);
		gridSlider.setForeground(Color.white);
		gridSlider.addChangeListener(e -> {
			int newSize = gridSlider.getValue();
			controller.changeGridSize(newSize);
		});
		
		frame.add(gridSlider, BorderLayout.EAST);
		
		controller.startNewRound();
	}
         
	public void updateTurnLabel(String message)
	{
		textLabel.setText(message);
	}
         
	public void updateScoreDisplay(Player playerX, Player playerO)
	{
		textLabel.setText(playerX.getName() + " : " + playerX.getScore()
			+ "  |  " + playerO.getName() + " : " + playerO.getScore());
		statsPanel.update(playerX, playerO);
	}

	/** Refresh statistics panel without changing the score header. */
	public void updateStats(Player playerX, Player playerO)
	{
		statsPanel.update(playerX, playerO);
	}
}
