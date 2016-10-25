package bur.graph;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class GraphDemoFps extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new GraphDemoFps();
			}
		});
	}

	private GraphDemoFps() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().add(new GraphPanel());
		pack();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent event) {
				if (event.getSource() instanceof GraphDemoFps) {
					((GraphDemoFps) event.getSource()).exitFrame();
				}
			}
		});
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void exitFrame() {
		System.exit(0);
	}

}
