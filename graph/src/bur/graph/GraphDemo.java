package bur.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bur.graph.BarGraph.Mode;

/**
 * Startet eine Demo zu den verschiedenen grafischem Komponenten.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class GraphDemo extends JFrame implements ActionListener {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(GraphDemo.class.getName());

	/** die Donut-Grafiken */
	private final PieGraph[] pieGraph;

	/** die Text-Grafiken */
	private final TextGraph[] textGraph;

	/** die Balken-Grafiken */
	private final BarGraph[] barGraph;

	/** die Heatmap-Grafiken */
	private final HeatmapGraph[] heatmapGraph;

	/** der Zeitgeber */
	private Timer timer = null;

	private int MAXTICK = 10;

	private int tick = MAXTICK;

	/** die Fehlersuche */
	private boolean debug = false;

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
		contentPane.setBackground(GraphConstants.getBackgroundColor());
		contentPane.add(new ColorPanel(), BorderLayout.SOUTH);
		final int graphCount = 5;
		pieGraph = new PieGraph[graphCount];
		final JPanel centerPanel = new JPanel(new GridLayout(4, graphCount));
		centerPanel.setOpaque(false);
		for (int idx = 0; idx < graphCount; idx++) {
			pieGraph[idx] = new PieGraph();
			centerPanel.add(pieGraph[idx]);
		}
		textGraph = new TextGraph[graphCount];
		for (int idx = 0; idx < graphCount; idx++) {
			textGraph[idx] = new TextGraph();
			centerPanel.add(textGraph[idx]);
		}
		barGraph = new BarGraph[graphCount];
		for (int idx = 0; idx < graphCount; idx++) {
			barGraph[idx] = new BarGraph();
			centerPanel.add(barGraph[idx]);
		}
		heatmapGraph = new HeatmapGraph[graphCount];
		for (int idx = 0; idx < graphCount; idx++) {
			heatmapGraph[idx] = new HeatmapGraph(8, 4);
			heatmapGraph[idx].setColorLimits(250.0, 650.0);
			centerPanel.add(heatmapGraph[idx]);
		}
		contentPane.add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(contentPane);
		setSize(650, 500);
		setLocationRelativeTo(null);
		// Keyboard
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				final char cmd = e.getKeyChar();
				if ('d' == cmd) {
					debug = !debug;
					for (int idx = 0; idx < graphCount; idx++) {
						pieGraph[idx].setDebug(debug);
						textGraph[idx].setDebug(debug);
						barGraph[idx].setDebug(debug);
						heatmapGraph[idx].setDebug(debug);
					}
					LOG.info("set [debug]: " + debug);
				}
			}

		});
	}

	private void startTimer() {
		timer = new Timer(3000, this);
		timer.setInitialDelay(1000);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		tick++;
		if (tick > MAXTICK) {
			final String[] texte = new String[] { "Dateien", "abgerechnet", "abger", "fertig", "Mbit/s", "autom" };
			final String[] axisText = new String[] { "12", "1", "2", "3", "4", "5" };
			for (int idx = 0; idx < pieGraph.length; idx++) {
				final String[] values = new String[4];
				values[0] = String.valueOf(new Random().nextInt(1500));
				values[1] = texte[new Random().nextInt(texte.length - 1)];
				values[2] = texte[new Random().nextInt(texte.length - 1)];
				values[3] = texte[new Random().nextInt(texte.length - 1)];
				// die Donut-Grafiken
				if (idx % 2 == 0) {
					pieGraph[idx].setMode(PieGraph.Mode.UNIT_AND_ONE_TEXTLINE);
				} else {
					pieGraph[idx].setMode(PieGraph.Mode.TWO_TEXTLINES);
				}
				pieGraph[idx].setValue(new Random().nextInt(100));
				pieGraph[idx].setUnit("%");
				pieGraph[idx].setTexts(Arrays.copyOfRange(values, 1, 3));
				// die Balken-Grafiken
				final double[] blueValues = new double[6];
				final double[] redValues = new double[6];
				for (int vIdx = 0; vIdx < 6; vIdx++) {
					final Random rand = new Random();
					blueValues[vIdx] = (1000.0d * rand.nextDouble());
					redValues[vIdx] = (blueValues[vIdx] * 0.6d * rand.nextDouble());
				}
				barGraph[idx].setTexts(texte[new Random().nextInt(texte.length - 1)]);
				barGraph[idx].setValues(Mode.RED_IN_BLUE, blueValues, redValues, 0.0);
				barGraph[idx].setAxisText(axisText);
				// die Texte
				textGraph[idx].setTexts(values);
				if (idx % 2 == 0) {
					textGraph[idx].setMode(TextGraph.Mode.ONE_BIG_TWO_SMALL);
				} else {
					textGraph[idx].setMode(TextGraph.Mode.TWO_BIG);
				}
				// die Heatmap
				final Random rand = new Random();
				heatmapGraph[idx].clear();
				heatmapGraph[idx].setTexts(Arrays.copyOfRange(values, 1, 3));
				for (int vIdx = 0; vIdx < MAXTICK; vIdx++) {
					final int x = rand.nextInt(9);
					final int y = rand.nextInt(5);
					heatmapGraph[idx].setValue(x, y, (1000.0 * rand.nextDouble()));
				}
			}
			tick = 1;
		}
		for (int idx = 0; idx < barGraph.length; idx++) {
			barGraph[idx].highlighterTick();
			heatmapGraph[idx].highlighterTick();
			pieGraph[idx].repaint();
			textGraph[idx].repaint();
			barGraph[idx].repaint();
			heatmapGraph[idx].repaint();
		}
	}

	private static class ColorPanel extends JPanel {

		public ColorPanel() {
			super(new GridLayout(1, GraphConstants.COLORS.length));
			setOpaque(false);
			for (int i = 0; i < GraphConstants.COLORS.length; i++) {
				final Color c = GraphConstants.COLORS[i];
				initColorButton("#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen())
						+ Integer.toHexString(c.getBlue()), c);
			}
		}

		private void initColorButton(final String label, final Color bg) {
			final JLabel x = new JLabel(label, SwingConstants.CENTER);
			x.setBackground(bg);
			x.setOpaque(true);
			add(x);
		}

	}

}
