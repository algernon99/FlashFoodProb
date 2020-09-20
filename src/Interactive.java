/**
 * Instantiate and run an interactive Evolution of Communication simulation
 * 
 * @author richard
 *
 */
public class Interactive {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {	new userinterface.Controller(); }
		});
	}
}
