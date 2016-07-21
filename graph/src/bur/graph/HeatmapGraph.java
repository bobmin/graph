package bur.graph;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class HeatmapGraph extends AbstractGraph {

	/** die maximalen Rechtecke in der Breite */
	private final int xCount;

	/** die maximalen Rechtecke in der Höhe */
	private final int yCount;

	/**
	 * der Index wird ggf. hervorgehoben; eine Ganzzahl zwischen 0 bis
	 * {@value #SIZE}
	 */
	private Integer xHighlighter = null;

	/**
	 * der Index wird ggf. hervorgehoben; eine Ganzzahl zwischen 0 bis
	 * {@value #SIZE}
	 */
	private Integer yHighlighter = null;

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
	public void createGraph(final Graphics2D g2) {

		final double top = margin;
		final double bottom = (graphSize * 0.5d);

		double width = graphSize - (2 * margin);
		double height = bottom - top;

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
				if (null != xHighlighter && null != yHighlighter && xIdx == xHighlighter.intValue()
						&& yIdx == yHighlighter) {
					g2.fill(new Ellipse2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
				} else {
					g2.fill(new Rectangle2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
				}
			}
		}

		for (int idx = 0; idx < 3; idx++) {
			drawSmallTextBottom(g2, idx, false, string(idx));
		}

	}

	public void setHighlighter(int x, int y) {
		this.xHighlighter = Integer.valueOf(x);
		this.yHighlighter = Integer.valueOf(y);
	}

}
