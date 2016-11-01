package bur.graph.draft;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import bur.graph.GraphConstants;

/**
 * Zeichnet ein Balkendiagramm.
 * 
 * @author maik@btmx.net
 *
 */
public class BarDraft {

	/** die Breite/Höhe der Zeichenfläche */
	private static final int SIZE = 100;

	/** die Anzahl der Balken */
	private int count = 2;

	private int tick = 0;

	private boolean debug = false;

	public BarDraft() {
	}

	public void update() {
		tick++;
		if (tick > SIZE) {
			tick = 0;
		}
	}

	public void draw(final Graphics2D g, final int x, final int y, final int scale) {

		g.setColor(GraphConstants.getTextColor());

		g.fillRect(x + tick - 3, y + (int) (SIZE * 0.5), 6, 6);

		final XAxis axis = new XAxis(x);

		for (int idx = 0; idx < count; idx++) {
			g.fill(new Rectangle2D.Double(axis.values[idx], y + 10, axis.width, SIZE * 0.5 - 10));
		}

		if (debug) {
			g.setColor(GraphConstants.debugColorOne());
			g.drawRect(x, y, SIZE, SIZE);
			g.drawLine(x, (int) (y + SIZE * 0.5), x + SIZE, (int) (y + SIZE * 0.5));
			g.drawString("BALKEN", x + 5, y + 15);
			g.drawString("COUNT: " + count, x + 5, y + SIZE - 5);
		}

	}

	private class XAxis {

		private final double[] values;

		private final double width;

		public XAxis(final int x0) {
			values = new double[count];

			// links + ((balken + abstand) * n)
			// 0 | 1 | 3 | l b a
			// 1 | 2 | 5 | l b a b a
			// 2 | 3 | 7 | l b a b a b a
			int bars = 1 + (count * 2);

			width = (double) SIZE / bars;

			for (int idx = 0; idx < count; idx++) {
				values[idx] = x0 + width + (idx * (width * 2));
			}

		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer(XAxis.class.getName());
			sb.append(" [width = ").append(width);
			for (int idx = 0; idx < count; idx++) {
				sb.append(", x[").append(idx).append("] = ").append(values[idx]);
			}
			sb.append("]");
			return sb.toString();
		}

	}

	public void increaseBars() {
		count++;
	}

	public void decreaseBars() {
		count--;
	}

	public void toogleDebug() {
		debug = !debug;
	}

}
