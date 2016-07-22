package bur.graph;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class HeatmapGraph extends AbstractGraph {

	/** die maximalen Rechtecke in der Breite */
	private final int xCount;

	/** die maximalen Rechtecke in der Höhe */
	private final int yCount;

	/** die gelbe Farbgrenze; kleinere Werte werden gelb */
	private double yellow = 0.0;

	/** die rote Farbgrenze; kleinere Werte werden rot */
	private double red = 0.0;

	/** die Werte mit dem Schlüssel "x:y" */
	private Map<String, Double> values = new HashMap<>();

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

		double xStart = margin;
		double yStart = margin;
		double widthMax = width;
		double heightMax = height;

		double border = 0.08;

		String highlighterKey = null;
		if (highlighter <= values.size()) {
			highlighterKey = values.keySet().toArray(new String[values.size()])[highlighter - 1];
		}

		for (int xIdx = 0; xIdx < xCount; xIdx++) {
			for (int yIdx = 0; yIdx < yCount; yIdx++) {
				double w = widthMax / xCount;
				double h = heightMax / yCount;
				double x = xStart + (w * xIdx);
				double y = yStart + (h * yIdx);
				double wBorder = w * border;
				double hBorder = h * border;

				final String valueKey = String.format("%d:%d", xIdx, yIdx);
				if (values.containsKey(valueKey)) {
					final Double value = values.get(valueKey);
					if (value.doubleValue() < red) {
						g2.setPaint(GraphConstants.getRedColor());
					} else if (value.doubleValue() < yellow) {
						g2.setPaint(GraphConstants.getYellowColor());
					} else {
						g2.setPaint(GraphConstants.getBlueColor());
					}
				} else {
					g2.setPaint(GraphConstants.getTextColor());
				}

				if (valueKey.equals(highlighterKey)) {
					g2.fill(new Ellipse2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
					final double hv = values.get(highlighterKey).doubleValue();
					drawSmallTextBottom(g2, 2, false, String.format("%.2f", hv));
				} else {
					g2.fill(new Rectangle2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
				}
			}
		}

		drawSmallTextBottom(g2, 0, false, string(0));
		drawSmallTextBottom(g2, 1, false, string(2));

	}

	/**
	 * Setzt die Werte für den Farbwechsel.
	 * 
	 * @param red
	 *            unter diesem Wert roter Wert
	 * @param yellow
	 *            unter diesem Wert gelber Wert
	 */
	public void setColorLimits(final double red, final double yellow) {
		this.red = red;
		this.yellow = yellow;
	}

	/**
	 * Setzt einen Wert für die X- und Y-Koordinate.
	 * 
	 * @param x
	 *            die X-Koordinate >= 1 und maximal {@link HeatmapGraph#xCount}
	 * @param y
	 *            die Y-Koordinate >= 1 und maximal {@link HeatmapGraph#yCount}
	 * @param value
	 *            der Wert
	 */
	public void setValue(final int x, final int y, final double value) {
		values.put(String.format("%d:%d", (x - 1), (y - 1)), Double.valueOf(value));
	}

	/**
	 * Löscht die gespeicherten Werte.
	 */
	public void clear() {
		values.clear();
	}

	@Override
	public int getLength() {
		return values.size();
	}

}
