package bur.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
	public BufferedImage createGraph(int graphSize) {
		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(GraphConstants.getTextColor());

		final Font tFont = GraphConstants.ROBOTO_BOLD.deriveFont(graphSize * 0.2222f);

		if (mode == Mode.TWO_BIG) {

			g2.setFont(tFont);
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
			paintString(g2, graphSize, bigFont, string(0), null, null, 0);
			paintString(g2, graphSize, smallFont, string(1), null, null, 1);
			paintString(g2, graphSize, smallFont, string(2), null, null, 2);
		} else {
			throw new IllegalStateException("[mode] unknown: " + mode);
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

	private void debug(final Graphics2D g2,
			final float x, final float y, final float width, final float height) {
		final boolean debugOn = false;
		if (debugOn) {
			final Color color = g2.getColor();
			g2.setColor(Color.YELLOW);
			g2.draw(new Rectangle2D.Float(x, y, width, height));
			g2.setColor(color);
		}
	}

	private void paintText(final Graphics2D g2, final int graphSize,
			final Font font, final String text,
			final Font fontTwo, final String textTwo,
			float offset) {
		g2.setFont(font);

		final FontMetrics fontMetrics = g2.getFontMetrics();
		final int stringWidth = fontMetrics.stringWidth(text);
		final int height = fontMetrics.getHeight();

		final float x = ((graphSize - stringWidth) * 0.5f);
		final float y = (graphSize * 0.5f + (height * offset));

		g2.drawString(text, x, y);

		// zweiter Text (Standard: rechts daneben, nicht mittig)
		if (null != textTwo) {
			if (null != fontTwo) {
				g2.setFont(fontTwo);
			}
			final FontMetrics fontMetricsTwo = g2.getFontMetrics();
			final int stringWidthTwo = fontMetricsTwo.stringWidth(text);
			// g2.setColor(g2.getColor().darker());
			g2.drawString(textTwo, x + stringWidth + 1, y);
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
