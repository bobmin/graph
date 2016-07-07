package bur.graph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class HeatmapGraph extends AbstractGraph {

	/** die maximalen Rechtecke in der Breite */
	private final int xCount;

	/** die maximalen Rechtecke in der Höhe */
	private final int yCount;

	/**
	 * Instanziiert die Grafik für die Anzahl in der Breite und Höhe.
	 * 
	 * @param xCount
	 *            die maximalen Rechtecke in der Breite
	 * @param yCount
	 *            die maximalen Rechtecke in der Höhe
	 */
	public HeatmapGraph(final int xCount, final int yCount) {
		this.xCount = xCount;
		this.yCount = yCount;
	}

	@Override
	public BufferedImage createGraph(int graphSize) {
		final double margin = graphSize * 0.1d;

		final double top = margin;
		final double bottom = (graphSize * 0.5d);

		double width = graphSize - (2 * margin);
		double height = bottom - top;

		final BufferedImage image = createEmptyImage(graphSize);

		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(GraphConstants.getTextColor());

		double xStart = margin;
		double yStart = margin;
		double widthMax = width;
		double heightMax = height;

		double border = 0.08;

		for (int xIdx = 0; xIdx < xCount; xIdx++) {
			for (int yIdx = 0; yIdx < yCount; yIdx++) {
				double w = widthMax / xCount;
				double h = heightMax / yCount;
				double x = xStart + (w * xIdx);
				double y = yStart + (h * yIdx);
				double wBorder = w * border;
				double hBorder = h * border;
				g2.fill(new Rectangle2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
			}
		}

		// Hilfslinien zeichnen
		if (isDebugging()) {
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 3f }, 0));
			g2.setColor(GraphConstants.debugColor());
			// Rahmen
			g2.draw(new Rectangle2D.Double(1, 1, graphSize - 2, graphSize - 2));
			// Mittellinie
			g2.drawLine(0, (int) (graphSize * 0.5f), graphSize, (int) (graphSize * 0.5f));
		}

		g2.dispose();

		return image;
	}

}
