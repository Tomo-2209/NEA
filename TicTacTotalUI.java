import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Main user-interface class for Tic-Tac-Total.
 *
 * <p>This class constructs the complete application window by assembling the
 * following regions inside the supplied {@link JFrame}:</p>
 * <ul>
 *   <li><b>North</b>  – a header label showing the current turn and score.</li>
 *   <li><b>West</b>   – a {@link StatsPanel} displaying per-player
 *                       statistics.</li>
 *   <li><b>Centre</b> – the {@link GamePanel} (the playable grid).</li>
 *   <li><b>South</b>  – a Reset button.</li>
 *   <li><b>East</b>   – a vertical {@link JSlider} for choosing the grid
 *                       size (3–10).</li>
 * </ul>
 *
 * <p>Before the board is shown, two start-up dialogs are displayed:</p>
 * <ol>
 *   <li>Difficulty selection (GCSE / A-Level).</li>
 *   <li>Player name entry for each player.</li>
 * </ol>
 *
 * <p>{@code TicTacTotalUI} acts as the <em>view</em> in the
 * Model-View-Controller (MVC) architecture; it delegates all game-logic
 * decisions to {@link GameController}.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GameController
 * @see     GamePanel
 * @see     StatsPanel
 */
public class TicTacTotalUI
{
	// ── Constants ─────────────────────────────────────────────────────────

	/** The minimum grid size (rows / columns) that can be selected. */
	public static final int MIN_GRID_SIZE = 3;

	/** The maximum grid size (rows / columns) that can be selected. */
	public static final int MAX_GRID_SIZE = 10;

	// ── Fields ────────────────────────────────────────────────────────────

/** The top-level application window. */
private final JFrame frame;

/** Header label showing the current turn message or score summary. */
private final JLabel headerLabel;

/** The playable game board. */
private final GamePanel gamePanel;

/** Reset button displayed at the bottom of the window. */
private final JButton resetButton;

/** Vertical slider used to select the grid size. */
private final JSlider gridSlider;

/** Side panel showing per-player statistics. */
private final StatsPanel statsPanel;

/** The game controller that coordinates all game logic. */
private final GameController controller;

/** The game model that stores the board state. */
private final GameModel model;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs the Tic-Tac-Total UI inside the given frame.
 *
 * <p>The constructor performs the following steps:</p>
 * <ol>
 *   <li>Shows a difficulty-selection dialog.</li>
 *   <li>Shows a name-entry dialog for each player.</li>
 *   <li>Builds and lays out all UI components.</li>
 *   <li>Creates the {@link GameModel}, {@link GamePanel} and
 *       {@link GameController}.</li>
 *   <li>Starts the first round via
 *       {@link GameController#startNewRound()}.</li>
 * </ol>
 *
 * @param frame the top-level application window to populate
 */
public TicTacTotalUI(JFrame frame)
{
this.frame = frame;

// ── 1. Difficulty selection ───────────────────────────────────────
String[] difficultyOptions = { "GCSE", "A-Level" };
int difficultyChoice = JOptionPane.showOptionDialog(
frame,
"Select the difficulty level for maths questions:",
"Tic-Tac-Total \u2013 Difficulty",
JOptionPane.DEFAULT_OPTION,
JOptionPane.PLAIN_MESSAGE,
null,
difficultyOptions,
difficultyOptions[0]);

MathDifficulty difficulty =
(difficultyChoice == 1) ? MathDifficulty.A_LEVEL : MathDifficulty.GCSE;

// ── 2. Player names ───────────────────────────────────────────────
String player1Name = JOptionPane.showInputDialog(
frame,
"Enter name for Player 1 (X):",
"Player 1 Name",
JOptionPane.PLAIN_MESSAGE);
if (player1Name == null || player1Name.isBlank())
{
player1Name = "Player 1";
}

String player2Name = JOptionPane.showInputDialog(
frame,
"Enter name for Player 2 (O):",
"Player 2 Name",
JOptionPane.PLAIN_MESSAGE);
if (player2Name == null || player2Name.isBlank())
{
player2Name = "Player 2";
}

// ── 3. Header label ───────────────────────────────────────────────
headerLabel = new JLabel("Tic-Tac-Total", JLabel.CENTER);
headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
headerLabel.setBackground(Color.darkGray);
headerLabel.setForeground(Color.white);
headerLabel.setOpaque(true);

JPanel headerPanel = new JPanel(new BorderLayout());
headerPanel.add(headerLabel);
frame.add(headerPanel, BorderLayout.NORTH);

// ── 4. Statistics panel ───────────────────────────────────────────
statsPanel = new StatsPanel();
frame.add(statsPanel, BorderLayout.WEST);

// ── 5. Game board (model + panel + controller) ────────────────────
model     = new GameModel(3);
gamePanel = new GamePanel(null, model.getGridSize());
controller = new GameController(
model, gamePanel, this, player1Name, player2Name, difficulty);

frame.add(gamePanel, BorderLayout.CENTER);

// ── 6. Reset button ───────────────────────────────────────────────
resetButton = new JButton("Reset");
resetButton.setFont(new Font("Arial", Font.BOLD, 20));
resetButton.setBackground(Color.darkGray);
resetButton.setForeground(new Color(255, 165, 0));
resetButton.setFocusable(false);
resetButton.addActionListener(e -> controller.resetGame());
frame.add(resetButton, BorderLayout.SOUTH);

// ── 7. Grid-size slider (MIN_GRID_SIZE–MAX_GRID_SIZE, vertical, right) ─────
gridSlider = new JSlider(JSlider.VERTICAL, MIN_GRID_SIZE, MAX_GRID_SIZE, MIN_GRID_SIZE);
gridSlider.setMajorTickSpacing(1);
gridSlider.setPaintTicks(true);
gridSlider.setPaintLabels(true);
gridSlider.setBackground(Color.darkGray);
gridSlider.setForeground(new Color(200, 200, 255));
gridSlider.addChangeListener(e ->
{
int newSize = gridSlider.getValue();
controller.changeGridSize(newSize);
});
frame.add(gridSlider, BorderLayout.EAST);

// Start the first round
controller.startNewRound();
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Updates the header label with the provided turn or result message.
 *
 * <p>Called by the controller to display messages such as
 * {@code "Alice's turn"}, {@code "Bob wins!"} or
 * {@code "Wrong!  The answer was: 42"}.</p>
 *
 * @param message the message to display in the header
 */
public void updateTurnLabel(String message)
{
headerLabel.setText(message);
}

/**
 * Refreshes the header label with the current score and updates the
 * statistics panel.
 *
 * <p>Called at the end of each round and after every maths question when
 * a score change occurs.</p>
 *
 * @param playerX the player using symbol {@code "X"}
 * @param playerO the player using symbol {@code "O"}
 */
public void updateScoreDisplay(Player playerX, Player playerO)
{
headerLabel.setText(
playerX.getName() + " : " + playerX.getScore()
+ "  |  "
+ playerO.getName() + " : " + playerO.getScore());
statsPanel.update(playerX, playerO);
}

/**
 * Refreshes the statistics panel without changing the header label.
 *
 * <p>Called after every answered maths question to keep the correct and
 * wrong answer counts up to date.</p>
 *
 * @param playerX the player using symbol {@code "X"}
 * @param playerO the player using symbol {@code "O"}
 */
public void updateStats(Player playerX, Player playerO)
{
statsPanel.update(playerX, playerO);
}
}
