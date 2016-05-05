package bur.graph;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.JComponent;

/**
 * Zeichnet einen Kreis und den Wert.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BarGraph extends AbstractGraph {
	
	/** der Logger */
	private static final Logger LOG = Logger.getLogger(BarGraph.class.getName());

	/** die sechs Balkenwerte */
	private double[] values = new double[6];
	
	@Override
	public BufferedImage createGraph(int graphSize) {
		
		final double margin = graphSize * 0.1d;
		final double strokeSize = (graphSize - 2 * margin) / 11;
		
		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final BasicStroke stroke = new BasicStroke((float) strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);
		
		for (int idx=0; idx<6; idx++) {
			final double x1 = margin + strokeSize * 0.5d + (idx * strokeSize * 2);
			final double y1 = margin;
			final double y2 = margin + (graphSize * 0.5d);
			// Hintergrund
			g2.setColor(GraphConstants.COLOR_VIER);
			g2.draw(new Line2D.Double(x1, y1, x1, y2));
			// Farbe
			final double x = (y2 - y1) * values[idx] / 100;
			g2.setColor(GraphConstants.COLOR_EINS);
			g2.draw(new Line2D.Double(x1, y1 + x, x1, y2));
		}
		
		LOG.info("values painted: " + Arrays.toString(values));
		
		return image;
	}
	
	/**
	 * Setzt die Balkenwerte.
	 * @param values die Werte
	 */
	public void setValues(double[] values) {
		this.values = Arrays.copyOf(values, 6);
		LOG.info("values assigned: " + Arrays.toString(values));
	}
	
}
