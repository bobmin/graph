package bur.graph;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Zeichnet sechs Balken in blauer Farbe und optional ein rotes Balkenende. Die
 * Größe des roten Balken wird vom blauen abgezogen.
 * <p>
 * Optional kann ein Balken markiert werden und sein Wert am unteren Rand
 * erscheinen.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BarGraph extends AbstractGraph {

	/** die Anzahl der Balken */
	private static final int SIZE = 6;

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(BarGraph.class.getName());

	/** der Titel */
	private String title = null;

	/** die Balkenwerte */
	private Data values = null;

	/** die Beschriftung für die Y-Achse */
	private String[] axisText = null;

	/**
	 * der Index wird ggf. hervorgehoben; eine Ganzzahl zwischen 0 bis
	 * {@value #SIZE}
	 */
	private Integer highlighter = null;

	/** das Format der zweiten Zeile mit den Werten */
	private String highlighterFormat = "%.0f/%.0f";

	@Override
	public BufferedImage createGraph(int graphSize) {
		final double margin = graphSize * 0.1d;

		final double stroke = (graphSize - 2 * margin) / 11;

		final double top = margin;
		final double bottom = (graphSize * 0.5d) - stroke * 0.5f;

		final BufferedImage image = createEmptyImage(graphSize);

		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		g2.setFont(smallFont);
		final FontMetrics fontMetrics = g2.getFontMetrics();

		if (null != title) {
			g2.setColor(GraphConstants.getTextColor());
			final int tx1 = (int) (graphSize - margin - (fontMetrics.getHeight() * 1));
			g2.drawString(title, (int) ((graphSize - fontMetrics.stringWidth(title)) * 0.5d), tx1);
		}

		// Balken zeichnen
		for (int idx = 0; idx < SIZE; idx++) {
			final double x1 = margin + stroke * 0.5d + (idx * stroke * 2);

			// Balken-Hintergrund: eine grauen Linie
			g2.setColor(GraphConstants.getTextColor());
			g2.draw(new Line2D.Double(x1, top, x1, bottom));

			// optionale Beschriftung
			final int tx2 = (int) (graphSize - margin - (fontMetrics.getHeight() * 2));
			drawAxisText(g2, idx, x1, tx2);

			if (null != values && 0.05d < values.origBlueValues[idx]) {
				if (0.05d < values.normRedValues[idx]) {
					final double blue = (bottom - top) * values.normBlueValues[idx] / 100;
					final double red = blue * values.normRedValues[idx] / 100;
					// blaue Werte
					g2.setColor(GraphConstants.getBlueColor());
					g2.draw(new Line2D.Double(x1, (bottom - blue + red), x1, bottom));
					// rote Werte
					g2.setColor(GraphConstants.getRedColor());
					g2.draw(new Line2D.Double(x1, (bottom - blue), x1, (bottom - blue + red)));
				} else {
					final double blue = (bottom - top) * values.normBlueValues[idx] / 100;
					// nur blaue Werte
					g2.setColor(GraphConstants.getBlueColor());
					g2.draw(new Line2D.Double(x1, (bottom - blue), x1, bottom));
				}
				LOG.fine("values painted: " + Arrays.toString(values.normBlueValues));
			}

		}

		// einen Balken hervorheben
		if (null != values && null != highlighter) {
			final int idx = highlighter.intValue();
			final String highlighterText;
			highlighterText = String.format(highlighterFormat, values.origRedValues[idx], values.origBlueValues[idx]);
			g2.setColor(GraphConstants.getTextColor());
			final int tx0 = (int) (graphSize - margin - (fontMetrics.getHeight() * 0));
			g2.drawString(highlighterText, (int) ((graphSize - fontMetrics.stringWidth(highlighterText)) * 0.5d), tx0);
		}

		// Hilfslinien zeichnen
		if (isDebugging()) {
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 3f }, 0));
			g2.setColor(GraphConstants.debugColor());
			// Rahmen
			g2.draw(new Rectangle2D.Double(1, 1, graphSize - 2, graphSize - 2));
			// Mittellinie
			g2.drawLine(0, (int) (graphSize * 0.5f), graphSize, (int) (graphSize * 0.5f));
			// drei Textzeilen
			for (int i = 0; i < 3; i++) {
				final int tx = (int) (graphSize - margin - (fontMetrics.getHeight() * i));
				g2.drawLine(0, tx, graphSize, tx);
			}
		}

		g2.dispose();

		return image;
	}

	/**
	 * Beschriftet einen Balken.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param index
	 *            der Balkenindex
	 * @param x
	 *            die X-Position
	 * @param y
	 *            die Y-Position
	 */
	private void drawAxisText(final Graphics2D g2, final int index, final double x, final int y) {
		final FontMetrics fm = g2.getFontMetrics();
		final String text = (null == axisText ? null : axisText[index]);
		if (null != text) {
			if (null != highlighter && highlighter.intValue() == index) {
				g2.setColor(GraphConstants.getBlueColor());
			} else {
				g2.setColor(GraphConstants.getTextColor());
			}

			final int stringWidth = fm.stringWidth(text);

			g2.drawString(text, (int) (x - (stringWidth * 0.47d)), y);

		}
	}

	/**
	 * Setzt einen Titel zu den Werten.
	 * 
	 * @param title
	 *            der Titel
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Setzt die Beschriftung für die Y-Achse.
	 * 
	 * @param axisText
	 *            die Beschriftungen
	 */
	public void setAxisText(final String[] axisText) {
		this.axisText = Arrays.copyOf(axisText, 6);
		LOG.fine("[axisText] assigned: " + Arrays.toString(axisText));
	}

	/**
	 * Setzt die blauen Balkenwerte.
	 * 
	 * @param blueValues
	 *            die Werte
	 */
	public void setValues(final double[] blueValues) {
		this.values = new Data(blueValues, null);
		LOG.fine("[values] assigned: " + values);
	}

	/**
	 * Setzt die roten Balkenwerte.
	 * 
	 * @param blueValues
	 *            die "blauen" Werte
	 * @param redValues
	 *            die "roten" Werte
	 */
	public void setValues(final double[] blueValues, final double[] redValues) {
		this.values = new Data(blueValues, redValues);
		LOG.fine("[values] assigned: " + values);
	}

	/**
	 * Liefert die normalisierten Blauwerte.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public double[] getNormBlueValues() {
		return (null == values ? new double[SIZE] : values.normBlueValues);
	}

	/**
	 * Liefert die normalisierten Rotwerte.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public double[] getNormRedValues() {
		return (null == values ? new double[SIZE] : values.normRedValues);
	}

	public void setHighlighter(int value) {
		if (1 > value || value > SIZE) {
			throw new IllegalArgumentException("[0 < x <= SIZE]: " + value);
		}
		highlighter = Integer.valueOf(value) - 1;
	}

	/**
	 * Setzt das Format der zweiten Zeile vom Highlighter.
	 * 
	 * @param value
	 *            das Format
	 */
	public void setHighlighterFormat(String value) {
		this.highlighterFormat = value;
	}

	private static class Data {

		/** die "echten" blauen Balkenwerte */
		private final double[] origBlueValues;

		/** die normalisierten Blauwerte */
		private final double[] normBlueValues;

		/** die roten Balkenwerte */
		private final double[] origRedValues;

		/** die normalisierten Rotwerte */
		private final double[] normRedValues;

		/**
		 * Instanziiert das Objekt mit den Originalwerten. Der Blauwert
		 * entspricht dem Gesamtwert im jeweiligen Balken. Der Rotwert ist
		 * optional und wird von oben nach unten in den Blauwert gezeichnet.
		 * 
		 * @param blueValues
		 *            die "echten" Blauwerte
		 * @param redValues
		 *            die "echten" Rotwerte
		 */
		private Data(final double[] blueValues, final double[] redValues) {
			// Originalwerte
			this.origBlueValues = (null == blueValues ? new double[SIZE] : Arrays.copyOf(blueValues, SIZE));
			this.origRedValues = (null == redValues ? new double[SIZE] : Arrays.copyOf(redValues, SIZE));
			// Normalisierung
			this.normBlueValues = new double[SIZE];
			this.normRedValues = new double[SIZE];
			double max = 0.0d;
			for (int idx = 0; idx < SIZE; idx++) {
				max = Math.max(origBlueValues[idx] + origRedValues[idx], max);
			}
			for (int idx = 0; idx < SIZE; idx++) {
				normBlueValues[idx] = origBlueValues[idx] * 100.0d / max;
				normRedValues[idx] = origRedValues[idx] * 100.0d / max;
			}
		}

	}

}
