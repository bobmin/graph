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
	public BufferedImage createGraph(final int graphSize) {

		final double margin = graphSize * 0.1d;
		final float stokeSize = graphSize * 0.075f;

		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();		

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
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

		final String vauleString = String.valueOf(value);

		paintString(g2, graphSize, bigFont, vauleString, 0);

		if (null != unit) {
			paintString(g2, graphSize, smallFont, unit, 1);
		}

		return image;

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
