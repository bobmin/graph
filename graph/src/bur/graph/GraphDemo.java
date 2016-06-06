package bur.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Startet eine Demo zu den verschiedenen grafischem Komponenten.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class GraphDemo extends JFrame implements ActionListener {

	/** die Donut-Grafiken */
	private final PieGraph[] pieGraph;

	/** die Text-Grafiken */
	private final TextGraph[] textGraph;

	/** die Balken-Grafiken */
	private final BarGraph[] barGraph;

	private Timer timer = null;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final GraphDemo demo = new GraphDemo();
				demo.setVisible(true);
				demo.startTimer();
			}
		});
	}

	public GraphDemo() {
		setTitle("Demo: grafische Komponenten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JPanel contentPane = new JPanel(new BorderLayout(0, 0));
		contentPane.add(new ColorPanel(), BorderLayout.NORTH);
		final int size = 5;
		pieGraph = new PieGraph[size];
		final JPanel centerPanel = new JPanel(new GridLayout(3, size));
		for (int idx = 0; idx < size; idx++) {
			pieGraph[idx] = new PieGraph();
			centerPanel.add(pieGraph[idx]);
		}
		textGraph = new TextGraph[size];
		for (int idx = 0; idx < size; idx++) {
			textGraph[idx] = new TextGraph();
			centerPanel.add(textGraph[idx]);
		}
		barGraph = new BarGraph[size];
		for (int idx = 0; idx < size; idx++) {
			barGraph[idx] = new BarGraph();
			centerPanel.add(barGraph[idx]);
		}
		contentPane.add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(contentPane);
		setSize(650, 500);
		setLocationRelativeTo(null);
	}

	private void startTimer() {
		timer = new Timer(3000, this);
		timer.setInitialDelay(1000);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final String[] texte = new String[] { "Dateien", "abgerechnet", "abger", "fertig", "Mbit/s", "autom" };
		final String[] axisText = new String[] { "12", "1", "2", "3", "4", "5" };
		for (int idx = 0; idx < pieGraph.length; idx++) {
			// die Donut-Grafiken
			pieGraph[idx].setMode(PieGraph.Mode.UNIT_AND_ONE_TEXTLINE);
			pieGraph[idx].setValue(new Random().nextInt(100));
			pieGraph[idx].setUnit("%");
			pieGraph[idx].setText(texte[new Random().nextInt(texte.length - 1)]);
			pieGraph[idx].repaint();
			// die Balken-Grafiken
			final double[] blueValues = new double[6];
			final double[] redValues = new double[6];
			for (int vIdx = 0; vIdx < 6; vIdx++) {
				final Random rand = new Random();
				blueValues[vIdx] = (100.0d * rand.nextDouble());
				redValues[vIdx] = (blueValues[vIdx] * 0.6d * rand.nextDouble());
			}
			barGraph[idx].setBlueValues(blueValues);
			barGraph[idx].setRedValues(redValues);
			barGraph[idx].setAxisText(axisText);
			barGraph[idx].repaint();
			// die Texte
			final String[] values = new String[3];
			values[0] = texte[new Random().nextInt(texte.length - 1)];
			values[1] = texte[new Random().nextInt(texte.length - 1)];
			values[2] = texte[new Random().nextInt(texte.length - 1)];
			textGraph[idx].setValues(values);
			if (5 > new Random().nextInt(10)) {
				textGraph[idx].setMode(TextGraph.Mode.ONE_BIG_TWO_SMALL);
			} else {
				textGraph[idx].setMode(TextGraph.Mode.TWO_BIG);
			}
			textGraph[idx].repaint();
		}
	}

	private static class ColorPanel extends JPanel {

		public ColorPanel() {
			super(new GridLayout(1, GraphConstants.COLORS.length));
			for (int i = 0; i < GraphConstants.COLORS.length; i++) {
				final Color c = GraphConstants.COLORS[i];
				initColorButton("#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen())
						+ Integer.toHexString(c.getBlue()), c);
			}
		}

		private void initColorButton(final String label, final Color bg) {
			final JButton x = new JButton(label);
			x.setBackground(bg);
			add(x);
		}

	}

}
