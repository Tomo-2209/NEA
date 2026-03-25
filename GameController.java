import java.util.Random;

/**
 * Controls the flow of a Tic-Tac-Total game session.
 *
 * <p>{@code GameController} is the central coordinator in the
 * Model-View-Controller (MVC) architecture used by the application:</p>
 * <ul>
 *   <li><b>Model</b>  – {@link GameModel} stores the board state.</li>
 *   <li><b>View</b>   – {@link GamePanel} renders the board;
 *                       {@link TicTacTotalUI} renders the header and stats.</li>
 *   <li><b>Controller</b> – this class processes player actions and
 *                           orchestrates updates to the model and view.</li>
 * </ul>
 *
 * <h3>Tile-click sequence</h3>
 * <ol>
 *   <li>The player clicks a standard or bomb tile.</li>
 *   <li>A maths question is presented (harder question for bomb tiles).</li>
 *   <li>
 *     <b>Standard tile:</b>
 *     <ul>
 *       <li>Correct – the symbol is placed, the tile flashes green and
 *           a win/tie check is run.</li>
 *       <li>Wrong   – the tile flashes red showing the correct answer;
 *           the turn passes to the other player.</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <b>Bomb tile:</b>
 *     <ul>
 *       <li>Diffused – the bomb converts to a standard tile and the
 *           player's symbol is placed.</li>
 *       <li>Failed   – the explosion animation clears the surrounding
 *           3 × 3 area and the turn passes.</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * @author  Tomo
 * @version 1.0
 * @see     GameModel
 * @see     GamePanel
 * @see     TicTacTotalUI
 */
public class GameController
{
// ── Fields ────────────────────────────────────────────────────────────

/** The game-board model. */
private final GameModel model;

/** The game-board view. */
private final GamePanel panel;

/** The main application UI (header label and stats panel). */
private final TicTacTotalUI ui;

/** The player using symbol {@code "X"}. */
private final Player playerX;

/** The player using symbol {@code "O"}. */
private final Player playerO;

/** The player whose turn it currently is. */
private Player currentPlayer;

/** {@code true} once a win or tie has been recorded for the current round. */
private boolean gameOver = false;

/** Bank of maths questions filtered to the chosen difficulty. */
private final MathQuestionBank questionBank;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs the controller, creates both {@link Player} objects and links
 * the panel back to this controller via
 * {@link GamePanel#setController(GameController)}.
 *
 * <p>A new round is started immediately so the board is ready when the
 * window is first shown.</p>
 *
 * @param model       the game-board model
 * @param panel       the game-board view
 * @param ui          the main application UI
 * @param player1Name display name for Player 1 (X)
 * @param player2Name display name for Player 2 (O)
 * @param difficulty  the selected maths difficulty level
 */
public GameController(GameModel model, GamePanel panel, TicTacTotalUI ui,
                      String player1Name, String player2Name,
                      MathDifficulty difficulty)
{
this.model        = model;
this.panel        = panel;
this.ui           = ui;
this.questionBank = new MathQuestionBank(difficulty);
this.playerX      = new Player(player1Name, Symbols.X);
this.playerO      = new Player(player2Name, Symbols.O);

panel.setController(this);
startNewRound();
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Resets the board and begins a new round.
 *
 * <p>The starting player is chosen at random, the score display and stats
 * panel are refreshed, and the turn label is updated.</p>
 */
public void startNewRound()
{
model.reset();
panel.reset(model.getGridSize());
gameOver = false;
selectRandomStartingPlayer();
ui.updateScoreDisplay(playerX, playerO);
ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
}

/**
 * Resets the game to a new round.
 *
 * <p>Called by the Reset button in {@link TicTacTotalUI}.</p>
 */
public void resetGame()
{
startNewRound();
}

/**
 * Resizes the board to the new grid size and starts a fresh round.
 *
 * <p>Called when the player moves the grid-size slider.</p>
 *
 * @param newSize the new grid dimension (number of rows / columns)
 */
public void changeGridSize(int newSize)
{
model.setGridSize(newSize);
panel.reset(newSize);
startNewRound();
}

/**
 * Handles a click on a standard tile.
 *
 * <p>The sequence is:</p>
 * <ol>
 *   <li>Ignore the click if the game is over or the cell is already
 *       occupied.</li>
 *   <li>Present a maths question to the current player.</li>
 *   <li>If the answer is <b>wrong</b>: record the wrong answer, flash the
 *       tile red (showing the correct answer), then switch turns.</li>
 *   <li>If the answer is <b>correct</b>: place the symbol, flash the tile
 *       green, then check for a win or tie.</li>
 * </ol>
 *
 * @param row the zero-based row index of the clicked tile
 * @param col the zero-based column index of the clicked tile
 */
public void handleTileClick(int row, int col)
{
if (gameOver)
{
return;
}

if (!model.isCellEmpty(row, col))
{
return;
}

// ── Maths challenge ───────────────────────────────────────────────
MathQuestion question = questionBank.getRandomQuestion();
boolean correct       = MathQuestionDialog.showQuestion(question, panel);

if (!correct)
{
// Wrong answer: flash tile red and switch turns
currentPlayer.addWrongAnswer();
final String correctAnswer = question.getPrimaryAnswer();
ui.updateTurnLabel("Wrong!  The answer was: " + correctAnswer);
panel.flashTileIncorrect(row, col, correctAnswer, () ->
{
switchPlayer();
ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
});
ui.updateStats(playerX, playerO);
return;
}

// ── Correct answer: place the symbol ──────────────────────────────
currentPlayer.addCorrectAnswer();
ui.updateStats(playerX, playerO);

if (!model.makeMove(row, col, currentPlayer.getSymbol()))
{
return;
}

panel.updateTile(row, col, currentPlayer.getSymbol());
panel.flashTileCorrect(row, col);

checkWinOrTie();
}

/**
 * Handles a click on a bomb tile.
 *
 * <p>A harder maths question is presented via {@link BombDiffuseDialog}.
 * The possible outcomes are:</p>
 * <ul>
 *   <li><b>Diffused</b> – the bomb is neutralised, the player's symbol is
 *       placed in the cell, and a win/tie check is run.</li>
 *   <li><b>Failed</b>   – the explosion animation is triggered and the
 *       turn switches once the animation completes (via
 *       {@link #onBombAnimationFinished()}).</li>
 * </ul>
 *
 * @param row the zero-based row index of the bomb tile
 * @param col the zero-based column index of the bomb tile
 */
public void handleBombClick(int row, int col)
{
if (gameOver)
{
return;
}

MathQuestion hardQuestion = questionBank.getHardQuestion();
BombDiffuseDialog.DiffuseResult diffuseResult =
BombDiffuseDialog.showDiffuseDialog(hardQuestion, panel);

if (diffuseResult == BombDiffuseDialog.DiffuseResult.DIFFUSED)
{
// Bomb diffused: convert to standard tile and place the symbol
currentPlayer.addCorrectAnswer();
panel.diffuseBomb(row, col);

if (model.makeMove(row, col, currentPlayer.getSymbol()))
{
panel.updateTile(row, col, currentPlayer.getSymbol());
panel.flashTileCorrect(row, col);
}

ui.updateStats(playerX, playerO);
checkWinOrTie();
}
else
{
// Bomb detonated: record wrong answer and trigger the explosion animation.
// The turn will switch in onBombAnimationFinished() once the
// particle animation has fully completed.
currentPlayer.addWrongAnswer();
ui.updateStats(playerX, playerO);
panel.triggerBombExplosion(row, col);
}
}

/**
 * Called by {@link GamePanel} once the bomb explosion animation has
 * finished and the surrounding area has been cleared.
 *
 * <p>Switches the active player and updates the turn label so that play
 * can continue after the explosion.</p>
 */
public void onBombAnimationFinished()
{
if (!gameOver)
{
switchPlayer();
ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
}
}

/**
 * Clears a single cell in the model.
 *
 * <p>Called by {@link GamePanel} when a bomb explosion removes symbols
 * from the surrounding area.</p>
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 */
public void clearCell(int row, int col)
{
model.clearCell(row, col);
}

// ── Getters ───────────────────────────────────────────────────────────

/**
 * Returns {@code true} if the current round has ended (win or tie).
 *
 * @return {@code true} when the round is over
 */
public boolean isGameOver()
{
return gameOver;
}

/**
 * Returns the player assigned symbol {@code "X"}.
 *
 * @return Player X
 */
public Player getPlayerX()
{
return playerX;
}

/**
 * Returns the player assigned symbol {@code "O"}.
 *
 * @return Player O
 */
public Player getPlayerO()
{
return playerO;
}

/**
 * Returns the player whose turn it currently is.
 *
 * @return the active {@link Player}
 */
public Player getCurrentPlayer()
{
return currentPlayer;
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Randomly selects which player takes the first turn of the new round.
 */
private void selectRandomStartingPlayer()
{
currentPlayer = new Random().nextBoolean() ? playerX : playerO;
}

/**
 * Switches the active player from X to O or from O to X.
 */
private void switchPlayer()
{
currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
}

/**
 * Checks the board for a win or tie following the most recent move and
 * updates the UI accordingly.
 *
 * <p>If a winner is found, the winning player's score is incremented, the
 * loser's streak is reset, and the winning tiles are highlighted.  If a
 * tie is detected both streaks are reset.  Otherwise the turn passes to the
 * other player.</p>
 */
private void checkWinOrTie()
{
WinResult result = model.checkForWin();

if (result.hasWinner())
{
gameOver = true;
String winnerSymbol = result.getWinnerSymbol();
Player winner = winnerSymbol.equals(playerX.getSymbol()) ? playerX : playerO;
Player loser  = (winner == playerX) ? playerO : playerX;

winner.incrementScore();
loser.resetStreak();

panel.flashWinningCells(result.getWinningCells());
ui.updateScoreDisplay(playerX, playerO);
ui.updateTurnLabel(winner.getName() + " wins!");
}
else if (result.isTie())
{
gameOver = true;
playerX.resetStreak();
playerO.resetStreak();
ui.updateScoreDisplay(playerX, playerO);
ui.updateTurnLabel("It's a draw!");
}
else
{
switchPlayer();
ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
}
}
}
