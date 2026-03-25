import javax.swing.JFrame;

public class Runner 
{
	public static void main(String[] args)
	{
		JFrame mainframe = new JFrame("Tic-Tac-Total");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(800, 700);
		mainframe.setResizable(false);
		mainframe.setLocationRelativeTo(null);
		
		new TicTacToeUI(mainframe);
		
		mainframe.setVisible(true);
	}
}
