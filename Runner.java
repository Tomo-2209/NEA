import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point for Tic-Tac-Total.
 *
 * <p>Creates the main application window, sets its basic properties and
 * delegates UI construction to {@link TicTacTotalUI}.  The entire setup is
 * performed on the <em>Event Dispatch Thread</em> (EDT) via
 * {@link SwingUtilities#invokeLater} as required by Swing's single-thread
 * rule.</p>
 *
 * @author  Tomo
 * @version 1.0
 * @see     TicTacTotalUI
 */
public class Runner
{
/**
 * Application entry point.
 *
 * <p>Schedules the creation of the game window on the EDT and then returns,
 * leaving the Swing event loop to drive the application from that point.</p>
 *
 * @param args command-line arguments (not used)
 */
public static void main(String[] args)
{
SwingUtilities.invokeLater(() ->
{
JFrame mainFrame = new JFrame("Tic-Tac-Total");
mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
mainFrame.setSize(800, 700);
mainFrame.setResizable(false);
mainFrame.setLocationRelativeTo(null);

new TicTacTotalUI(mainFrame);

mainFrame.setVisible(true);
});
}
}
