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
	private final Map<String, HeatmapValue> values = new HashMap<>();

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

		final double width = graphSize - (2 * margin);
		final double height = bottom - top;

		final double xStart = margin;
		final double yStart = margin;
		final double widthMax = width;
		final double heightMax = height;

		final double border = 0.08;

		String highlighterKey = null;
		if (highlighter <= values.size()) {
			highlighterKey = values.keySet().toArray(new String[values.size()])[highlighter - 1];
		}

		for (int xIdx = 0; xIdx < xCount; xIdx++) {
			for (int yIdx = 0; yIdx < yCount; yIdx++) {
				final double w = widthMax / xCount;
				final double h = heightMax / yCount;
				final double x = xStart + (w * xIdx);
				final double y = yStart + (h * yIdx);
				final double wBorder = w * border;
				final double hBorder = h * border;

				final String valueKey = String.format("%d:%d", xIdx, yIdx);
				if (values.containsKey(valueKey)) {
					final double value = values.get(valueKey).value;
					if (value < red) {
						g2.setPaint(GraphConstants.getRedColor());
					} else if (value < yellow) {
						g2.setPaint(GraphConstants.getYellowColor());
					} else {
						g2.setPaint(GraphConstants.getBlueColor());
					}
				} else {
					g2.setPaint(GraphConstants.getTextColor());
				}

				if (valueKey.equals(highlighterKey)) {
					g2.fill(new Ellipse2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
					final String hl = values.get(highlighterKey).label;
					drawSmallTextBottom(g2, 1, false, hl);
					final double hv = values.get(highlighterKey).value;
					drawSmallTextBottom(g2, 2, false, String.format("%.2f", hv));
				} else {
					g2.fill(new Rectangle2D.Double(x + wBorder, y + hBorder, (w - 2 * wBorder), (h - 2 * hBorder)));
				}
			}
		}

		// Titel
		drawSmallTextBottom(g2, 0, false, string(0));

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
	public void setValue(final int x, final int y, final String label, final double value) {
		values.put(String.format("%d:%d", (x - 1), (y - 1)), new HeatmapValue(label, value));
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

	/**
	 * Ein Wert in der Heatmap.
	 */
	private class HeatmapValue {

		/** die Bezeichnung */
		private final String label;

		/** der Zahlenwert */
		private final double value;

		/**
		 * Instaziiert das Objekt.
		 * 
		 * @param label
		 *            die Bezeichnung
		 * @param value
		 *            der Zahlenwert
		 */
		public HeatmapValue(final String label, final double value) {
			this.label = label;
			this.value = value;
		}

	}

}
