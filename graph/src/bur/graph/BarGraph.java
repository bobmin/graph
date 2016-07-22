package bur.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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
	private static final int LENGTH = 6;

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(BarGraph.class.getName());

	/** die Balkenwerte */
	private Data values = null;

	/** die Beschriftung für die Y-Achse */
	private String[] axisText = null;

	@Override
	public void createGraph(final Graphics2D g2) {

		final double stroke = (graphSize - 2 * margin) / 11;

		final double top = margin;
		final double bottom = (graphSize * 0.5d) - stroke * 0.5f;

		g2.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

		g2.setFont(smallFont);
		final FontMetrics fontMetrics = g2.getFontMetrics();

		// Balken zeichnen
		for (int idx = 0; idx < LENGTH; idx++) {
			final double x1 = margin + stroke * 0.5d + (idx * stroke * 2);

			// Balken-Hintergrund: eine grauen Linie
			g2.setColor(GraphConstants.getTextColor());
			g2.draw(new Line2D.Double(x1, top, x1, bottom));

			if (null != values) {

				if (Mode.RED_IN_BLUE == values.mode) {
					// kein blauer Wert = nichts zu zeichnen
					if (0.0 < values.normBlueValues[idx]) {
						if (0.0 > values.normRedValues[idx]) {
							// nur blauen Wert zeichnen
							final double blue = (bottom - top) * values.normBlueValues[idx] / 100;
							g2.setColor(GraphConstants.getBlueColor());
							g2.draw(new Line2D.Double(x1, (bottom - blue), x1, bottom));
						} else {
							// blaue und rote Werte zeichnen
							final double blue = (bottom - top) * values.normBlueValues[idx] / 100;
							final double red = blue * values.normRedValues[idx] / 100;
							// ...blauer Werte
							g2.setColor(GraphConstants.getBlueColor());
							g2.draw(new Line2D.Double(x1, (bottom - blue + red), x1, bottom));
							// ...roter Werte
							g2.setColor(GraphConstants.getRedColor());
							g2.draw(new Line2D.Double(x1, (bottom - blue), x1, (bottom - blue + red)));
						}
					}
				} else if (Mode.RED_AND_BLUE_START_BY_ZERO == values.mode) {
					if (0.0 < Math.max(values.normBlueValues[idx], values.normRedValues[idx])) {
						// blaue und rote Werte zeichnen
						if (values.normBlueValues[idx] > values.normRedValues[idx]) {
							drawColorLine(g2, GraphConstants.getBlueColor(), x1, top, bottom,
									values.normBlueValues[idx]);
							drawColorLine(g2, GraphConstants.getRedColor(), x1, top, bottom, values.normRedValues[idx]);
						} else {
							drawColorLine(g2, GraphConstants.getBlueColor(), x1, top, bottom,
									values.normRedValues[idx]);
							drawColorLine(g2, GraphConstants.getRedColor(), x1, top, bottom,
									values.normBlueValues[idx]);
						}
					}
				} else {
					throw new IllegalStateException("[mode] unknown: " + values.mode);
				}

				LOG.fine("values painted, mode = " + values.mode + ", blue = " + Arrays.toString(values.normBlueValues)
						+ ", red = " + Arrays.toString(values.normRedValues));
			}

		}

		if (null != axisText) {
			final Color[] axisColor = new Color[axisText.length];
			Arrays.fill(axisColor, GraphConstants.getTextColor());
			axisColor[highlighter - 1] = GraphConstants.getBlueColor();
			drawSmallTextBottom(g2, 0, true, axisColor, axisText);
		}

		g2.setColor(GraphConstants.getTextColor());
		final int tx1 = (int) (graphSize - margin - (fontMetrics.getHeight() * 1));
		// g2.drawString(title, (int) ((graphSize -
		// fontMetrics.stringWidth(title)) * 0.5d), tx1);
		drawSmallTextBottom(g2, 1, false, string(0));

		// einen Balken hervorheben
		if (null != values) {
			final int idx = (highlighter - 1);

			final Color[] highlighterColor = new Color[3];
			final String[] highlighterText = new String[3];

			highlighterColor[0] = GraphConstants.getRedColor();
			highlighterText[0] = String.format("%.0f", values.origRedValues[idx]);

			highlighterColor[1] = GraphConstants.getTextColor();
			highlighterText[1] = " / ";

			highlighterColor[2] = GraphConstants.getBlueColor();
			highlighterText[2] = String.format("%.0f", values.origBlueValues[idx]);

			drawSmallTextBottom(g2, 2, false, highlighterColor, highlighterText);
		}

	}

	private void drawColorLine(final Graphics2D g2, final Color color, final double x, final double top,
			final double bottom, final double value) {
		g2.setColor(color);
		final double pixel = (bottom - top) * value / 100.0;
		g2.draw(new Line2D.Double(x, (bottom - pixel), x, bottom));
		LOG.fine("line plotted, top = " + top + ", bottom = " + bottom + ", value = " + value + ", pixel = " + pixel);
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
	public void setValues(final double[] blueValues, final double initMax) {
		this.values = new Data(Mode.RED_IN_BLUE, blueValues, null, initMax);
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
	public void setValues(final Mode mode, final double[] blueValues, final double[] redValues, final double initMax) {
		this.values = new Data(mode, blueValues, redValues, initMax);
		LOG.fine("[values] assigned: " + values);
	}

	/**
	 * Liefert die normalisierten Blauwerte.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public double[] getNormBlueValues() {
		return (null == values ? new double[LENGTH] : values.normBlueValues);
	}

	/**
	 * Liefert die normalisierten Rotwerte.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public double[] getNormRedValues() {
		return (null == values ? new double[LENGTH] : values.normRedValues);
	}

	/**
	 * Definiert die Anzeigebetriebsarten.
	 */
	public static enum Mode {
		RED_IN_BLUE, RED_AND_BLUE_START_BY_ZERO
	}

	/**
	 * Beschreibt die Daten zum Diagramm.
	 */
	private static class Data {

		/** die Betriebsart zur Berechnung/Anzeige */
		private final Mode mode;

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
		 * @param mode
		 *            die Betriebsart
		 * @param blueValues
		 *            die "echten" Blauwerte
		 * @param redValues
		 *            die "echten" Rotwerte
		 * @param initMax
		 */
		private Data(final Mode mode, final double[] blueValues, final double[] redValues, final double initMax) {
			this.mode = mode;
			// Originalwerte
			this.origBlueValues = (null == blueValues ? new double[LENGTH] : Arrays.copyOf(blueValues, LENGTH));
			this.origRedValues = (null == redValues ? new double[LENGTH] : Arrays.copyOf(redValues, LENGTH));
			// Normalisierung
			this.normBlueValues = new double[LENGTH];
			this.normRedValues = new double[LENGTH];
			double max = initMax;
			for (int idx = 0; idx < LENGTH; idx++) {
				if (Mode.RED_IN_BLUE == mode) {
					max = Math.max(origBlueValues[idx] + origRedValues[idx], max);
				} else if (Mode.RED_AND_BLUE_START_BY_ZERO == mode) {
					if (origBlueValues[idx] >= origRedValues[idx]) {
						max = Math.max(origBlueValues[idx], max);
					} else {
						max = Math.max(origRedValues[idx], max);
					}
				} else {
					throw new IllegalStateException("[mode] unknown: " + mode);
				}
			}
			for (int idx = 0; idx < LENGTH; idx++) {
				normBlueValues[idx] = origBlueValues[idx] * 100.0d / max;
				normRedValues[idx] = origRedValues[idx] * 100.0d / max;
			}
		}

	}

	@Override
	public int getLength() {
		return LENGTH;
	}

}
