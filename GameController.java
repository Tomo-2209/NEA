import java.util.Random;

public class GameController 
{
	private GameModel model;
	private GamePanel panel;
	private TicTacToeUI ui;
	
	private Player playerX;
	private Player playerO;
	private Player currentPlayer;
	private boolean gameOver = false;
	
	public GameController(GameModel model, GamePanel panel, TicTacToeUI ui, String player1Name, String player2Name)
	{
		this.model = model;
		this.panel = panel;
		this.ui = ui;
		
		this.playerX = new Player(player1Name, "X");
		this.playerO = new Player(player2Name, "O");
		
		panel.setController(this);
		startNewRound();
	}
	
	public void startNewRound()
	{
		model.reset();
		panel.reset(model.getGridSize());
		gameOver = false;
		selectRandomStartingPlayer();
		ui.updateScoreDisplay(playerX, playerO);
		ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
	}
	
	public void changeGridSize(int newSize)
	{
		model.setGridSize(newSize);
		panel.reset(newSize);
		startNewRound();
	}
	
	private void selectRandomStartingPlayer()
	{
		Random r = new Random();
		if (r.nextBoolean())
		{
			currentPlayer = playerX;
		}
		else
		{
			currentPlayer = playerO;
		}
	}
	
	/**
	 * Handle click on a standard tile (standard game logic)
	 */
	public void handleTileClick(int row, int col)
	{
		if (gameOver)
		{
			return;
		}
		
		if (!model.makeMove(row, col, currentPlayer.getSymbol()))
		{
			return;
		}
		
		panel.updateTile(row, col, currentPlayer.getSymbol());
		
		WinResult result = model.checkForWin();
		if (result.hasWinner())
		{
			gameOver = true;
			String winnerSymbol = result.getWinnerSymbol();
			Player winner = winnerSymbol.equals(playerX.getSymbol()) ? playerX : playerO;
			winner.incrementScore();
			panel.flashWinningCells(result.getWinningCells());
			ui.updateScoreDisplay(playerX, playerO);
			ui.updateTurnLabel(winner.getName() + " wins!");
		}
		else if (result.isTie())
		{
			gameOver = true;
			ui.updateTurnLabel("It's a tie!");
		}
		else
		{
			switchPlayer();
			ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
		}
	}
	
	/**
	 * Handle click on a bomb tile.
	 * The turn does NOT switch here; it switches only once the explosion
	 * animation has fully finished (see onBombAnimationFinished).
	 */
	public void handleBombClick(int row, int col)
	{
		if (gameOver)
		{
			return;
		}
		// Intentionally do nothing else here.
		// onBombAnimationFinished() will be called by GamePanel when the animation ends.
	}
	
	/**
	 * Called by GamePanel after the bomb explosion animation has fully finished
	 * and the blast area has been cleared.
	 * Switches the active player and updates the turn label.
	 */
	public void onBombAnimationFinished()
	{
		if (!gameOver)
		{
			switchPlayer();
			ui.updateTurnLabel(currentPlayer.getName() + "'s turn");
		}
	}
	
	private void switchPlayer()
	{
		currentPlayer = currentPlayer == playerX ? playerO : playerX;
	}
	
	/**
	 * Clear a single cell in the model (called after a bomb clears surrounding tiles).
	 */
	public void clearCell(int row, int col)
	{
		model.clearCell(row, col);
	}
	
	public Player getPlayerX() 
	{
		return playerX;
	}
	
	public Player getPlayerO()
	{
		return playerO;
	}
	
	public void resetGame()
	{
		startNewRound(); 
	}
}