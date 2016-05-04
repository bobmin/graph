package bur.graph;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/**
 * Zeichnet einen Kreis und den Wert.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class PieGraph extends AbstractGraph {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(PieGraph.class.getName());

	private int value = 0;

	private String unit = null;

	public PieGraph() {
	}

	@Override
	public BufferedImage createGraph(final int size) {

		final int graphSize = size;
		final double margin = size * 0.1d;
		final float stokeSize = size * 0.075f;
		final float fontSize = size * 0.29f;

		final StringBuffer sb = new StringBuffer();
		sb.append("\n\tgraphSize = ").append(graphSize);
		sb.append("\n\tfontSize = ").append(fontSize);
		LOG.info(sb.toString());

		final BufferedImage image = new BufferedImage(graphSize, graphSize,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(GraphConstants.COLOR_FUENF);
		g2.fillRect(0, 0, graphSize, graphSize);

		final BasicStroke stroke = new BasicStroke(stokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);

		g2.setStroke(stroke);

		g2.setColor(GraphConstants.COLOR_VIER);

		g2.draw(new Ellipse2D.Double(margin, margin,
				(graphSize - 2 * margin),
				(graphSize - 2 * margin)));

		g2.setColor(GraphConstants.COLOR_EINS);

		g2.draw(new Arc2D.Double(margin, margin,
				(graphSize - 2 * margin),
				(graphSize - 2 * margin),
				90, (360.0d * value / 100),
				Arc2D.OPEN));

		g2.setColor(GraphConstants.COLOR_VIER);

		final String textEins = String.valueOf(value);

		final Font fontBold = ROBOTO_BOLD.deriveFont(fontSize);
		g2.setFont(fontBold);

		final FontMetrics fontMetrics = g2.getFontMetrics();

		final int stringWidth = fontMetrics.stringWidth(textEins);

		g2.drawString(textEins, (graphSize / 2 - stringWidth / 2), (graphSize / 2));

		if (null != unit) {
			paintString(g2, graphSize, (fontSize * 0.35f), unit);
		}

		return image;

	}

	private void paintString(final Graphics2D g2, final int graphSize, final float fontSize, final String text) {
		final Font fontRegular = ROBOTO_REGULAR.deriveFont(fontSize);
		g2.setFont(fontRegular);

		final FontMetrics fontMetricsRegular = g2.getFontMetrics();

		final int stringWidthRegular = fontMetricsRegular.stringWidth(text);

		final int height = fontMetricsRegular.getHeight();

		g2.drawString(text, (graphSize / 2 - stringWidthRegular / 2), (graphSize / 2 + height));
	}

	public void setValue(final int x) {
		this.value = x;
		LOG.info("value assigned: " + x);
	}

	public void setUnit(final String x) {
		this.unit = x;
		LOG.info("unit assigned: " + x);
	}

}
