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
 * Zeichnet einen Kreis und den Wert.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BarGraph extends AbstractGraph {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(BarGraph.class.getName());

	/** die sechs blauen Balkenwerte */
	private double[] blueValues = new double[6];

	/** die sechs roten Balkenwerte */
	private double[] redValues = new double[6];

	/** die Beschriftung für die Y-Achse */
	private String[] axisText = new String[6];

	@Override
	public BufferedImage createGraph(int graphSize) {

		final double margin = graphSize * 0.1d;
		final double strokeSize = (graphSize - 2 * margin) / 11;

		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final BasicStroke stroke = new BasicStroke((float) strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);

		g2.setFont(smallFont);
		final FontMetrics fontMetrics = g2.getFontMetrics();

		for (int idx = 0; idx < 6; idx++) {
			final double x1 = margin + strokeSize * 0.5d + (idx * strokeSize * 2);
			final double y1 = margin;
			final double y2 = margin + (graphSize * 0.5d);

			// Hintergrund
			g2.setColor(GraphConstants.COLOR_TEXT);
			g2.draw(new Line2D.Double(x1, y1, x1, y2));

			// Beschriftung
			final String yt = axisText[idx];
			if (null != yt) {
				g2.setColor(GraphConstants.COLOR_TEXT);

				final int stringWidth = fontMetrics.stringWidth(yt);
				final int height = fontMetrics.getHeight();

				g2.drawString(yt, (int) (x1 - (stringWidth * 0.47d)), (int) (y2 + height));

			}

			final double blue = (y2 - y1) * blueValues[idx] / 100;
			final double red = blue * redValues[idx] / 100;

			// blaue Werte
			g2.setColor(GraphConstants.COLOR_BLUE);
			g2.draw(new Line2D.Double(x1, (y2 - blue + red), x1, y2));

			// rote Werte
			g2.setColor(GraphConstants.COLOR_RED);
			g2.draw(new Line2D.Double(x1, (y2 - blue), x1, (y2 - blue + red)));

		}

		LOG.fine("values painted: " + Arrays.toString(blueValues));

		return image;
	}

	/**
	 * Setzt die blauen Balkenwerte.
	 * 
	 * @param values
	 *            die Werte
	 */
	public void setBlueValues(double[] values) {
		this.blueValues = Arrays.copyOf(values, 6);
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

}
