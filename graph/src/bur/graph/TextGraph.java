package bur.graph;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Zeigt eine Zeile mit großer Schrift und zwei Zeilen mit kleiner Schrift.
 * 
 * @author maik@btmx.net
 *
 */
public class TextGraph extends AbstractGraph {

	/** Konstante für fehlenden Text */
	private static final String UNKNOWN = "???";

	/** die Anzeigebetriebsart */
	private Mode mode = Mode.ONE_BIG_TWO_SMALL;

	/** die Werte werden angezeigt */
	private String[] values = null;

	@Override
	public BufferedImage createGraph() {
		final BufferedImage image = createEmptyImage();
		final Graphics2D g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(GraphConstants.getTextColor());

		// final Font tFont = GraphConstants.ROBOTO_BOLD.deriveFont(graphSize *
		// 0.2222f);

		if (mode == Mode.TWO_BIG) {

			g2.setFont(bigFont);
			final FontMetrics fontMetrics = g2.getFontMetrics();

			final String t1 = string(0);
			final LineMetrics lm = fontMetrics.getLineMetrics(t1, g2);

			final float height = lm.getAscent();

			final float b = (graphSize * 0.45f);

			float x, y;
			int w;

			w = fontMetrics.stringWidth(t1);
			x = ((graphSize - w) * 0.5f);
			y = b;

			g2.drawString(t1, x, y);
			debug(g2, x, y - height, w, height);

			final String t2 = string(1);
			w = fontMetrics.stringWidth(t2);
			x = ((graphSize - w) * 0.5f);
			y = b + height;

			g2.drawString(t2, x, y);
			debug(g2, x, y - height, w, height);

			debug(g2, 3, 3, graphSize - 6, graphSize - 6);

		} else if (mode == Mode.ONE_BIG_TWO_SMALL) {
			// paintString(g2, graphSize, bigFont, string(0), null, null, 0);

			g2.setFont(bigFont);

			final String t = string(0);

			final FontMetrics fontMetrics = g2.getFontMetrics();
			final int stringWidth = fontMetrics.stringWidth(t);
			final int height = fontMetrics.getHeight();

			final float x = ((graphSize - stringWidth) * 0.5f);
			final float y = (graphSize * 0.25f) + fontMetrics.getMaxAscent() * 0.5f;

			g2.drawString(t, x, y);

			paintString(g2, smallFont, string(1), null, null, 1);
			paintString(g2, smallFont, string(2), null, null, 2);
		} else {
			throw new IllegalStateException("[mode] unknown: " + mode);
		}

		paintDebug(g2);

		g2.dispose();

		return image;
	}

	private void debug(final Graphics2D g2, final float x, final float y, final float width, final float height) {
		final boolean debugOn = false;
		if (debugOn) {
			final Color color = g2.getColor();
			g2.setColor(Color.YELLOW);
			g2.draw(new Rectangle2D.Float(x, y, width, height));
			g2.setColor(color);
		}
	}

	private String string(final int index) {
		final String x = (null == values || index >= values.length ? UNKNOWN : values[index]);
		return (null == x ? UNKNOWN : x).trim();
	}

	/**
	 * Setzt die Werte zur Anzeige.
	 * 
	 * @param values
	 *            die Werte
	 */
	public void setValues(final String[] values) {
		this.values = values;
	}

	/**
	 * Setzt die Betriebsart.
	 * 
	 * @param mode
	 *            die Betreibsart
	 */
	public void setMode(final Mode mode) {
		this.mode = mode;
	}

	/**
	 * Definiert die Anzeigebetriebsarten.
	 */
	public static enum Mode {
		ONE_BIG_TWO_SMALL, TWO_BIG
	}

}
