package bur.graph;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Zeichnet sechs Balken.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BarGraph extends AbstractGraph {

	/** die Anzahl der Balken */
	private static final int SIZE = 6;

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(BarGraph.class.getName());

	/** die sechs blauen Balkenwerte */
	private double[] blueValues = new double[SIZE];

	/** die sechs roten Balkenwerte */
	private double[] redValues = new double[SIZE];

	/** die Beschriftung für die Y-Achse */
	private String[] axisText = new String[SIZE];

	/**
	 * der Index wird ggf. hervorgehoben;
	 * eine Ganzzahl zwischen 0 bis {@link #SIZE}
	 */
	private Integer highlighter = null;

	@Override
	public BufferedImage createGraph(int graphSize) {

		final double margin = graphSize * 0.1d;
		final double strokeSize = (graphSize - 2 * margin) / 11;

		final double y1 = margin;
		final double y2 = margin + (graphSize * 0.5d);

		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final BasicStroke stroke = new BasicStroke((float) strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);

		g2.setFont(smallFont);
		final FontMetrics fontMetrics = g2.getFontMetrics();

		for (int idx = 0; idx < 6; idx++) {
			final double x1 = margin + strokeSize * 0.5d + (idx * strokeSize * 2);

			// Balken-Hintergrund: eine grauen Linie
			g2.setColor(GraphConstants.getTextColor());
			g2.draw(new Line2D.Double(x1, y1, x1, y2));

			// Beschriftung
			final String yt = axisText[idx];
			if (null != yt) {
				if (null != highlighter && highlighter.intValue() == idx) {
					g2.setColor(GraphConstants.getBlueColor());
				} else {
					g2.setColor(GraphConstants.getTextColor());
				}

				final int stringWidth = fontMetrics.stringWidth(yt);
				final int height = fontMetrics.getHeight();

				g2.drawString(yt, (int) (x1 - (stringWidth * 0.47d)), (int) (y2 + height));

			}

			if (0.05d < redValues[idx]) {
				final double blue = (y2 - y1) * blueValues[idx] / 100;
				final double red = blue * redValues[idx] / 100;
				// blaue Werte
				g2.setColor(GraphConstants.getBlueColor());
				g2.draw(new Line2D.Double(x1, (y2 - blue + red), x1, y2));
				// rote Werte
				g2.setColor(GraphConstants.getRedColor());
				g2.draw(new Line2D.Double(x1, (y2 - blue), x1, (y2 - blue + red)));
			} else {
				final double blue = (y2 - y1) * blueValues[idx] / 100;
				// blaue Werte
				g2.setColor(GraphConstants.getBlueColor());
				g2.draw(new Line2D.Double(x1, (y2 - blue), x1, y2));
			}

		}

		if (null != highlighter) {
			final String highlighterText = String.format("%6.2f", blueValues[highlighter.intValue()]);
			g2.setColor(GraphConstants.getTextColor());
			g2.drawString(highlighterText,
					(int) ((graphSize - fontMetrics.stringWidth(highlighterText)) * 0.5d),
					(int) (y2 + (fontMetrics.getHeight() * 2.2d)));
		}

		LOG.fine("values painted: " + Arrays.toString(blueValues));

		return image;
	}

	private double[] truncate(final double[] values) {
		final double[] x = new double[6];
		for (int idx = 0; idx < values.length; idx++) {
			if (values[idx] > 100) {
				x[idx] = 100.0d;
				LOG.warning("value truncated: " + values[idx]);
			} else {
				x[idx] = values[idx];
			}
		}
		return x;
	}

	/**
	 * Setzt die blauen Balkenwerte.
	 * 
	 * @param values
	 *            die Werte
	 */
	public void setBlueValues(double[] values) {
		this.blueValues = Arrays.copyOf(values, 6);
		this.blueValues = truncate(this.blueValues);
		LOG.fine("[blueValues] assigned: " + Arrays.toString(values));
	}

	/**
	 * Setzt die roten Balkenwerte.
	 * 
	 * @param values
	 *            die Werte
	 */
	public void setRedValues(double[] values) {
		this.redValues = Arrays.copyOf(values, 6);
		this.redValues = truncate(this.redValues);
		LOG.fine("[redValues] assigned: " + Arrays.toString(values));
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

	public void setHighlighter(int value) {
		if (1 > value || value > SIZE) {
			throw new IllegalArgumentException("[0 < x <= SIZE]: " + value);
		}
		highlighter = Integer.valueOf(value) - 1;
	}

}
