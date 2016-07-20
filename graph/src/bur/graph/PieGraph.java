package bur.graph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
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

	/** die Anzeigebetriebsart */
	private Mode mode = Mode.TWO_TEXTLINES;

	/** die Zahl von 0 bis 100 */
	private int value = 0;

	/** die Maßeinheit */
	private String unit = null;

	/** der Zusatztext */
	private String[] text = null;

	private int red = 49;

	private int yellow = 74;

	/**
	 * Instanziiert das Objekt.
	 */
	public PieGraph() {
	}

	@Override
	public BufferedImage createGraph() {

		final float stokeSize = graphSize * 0.075f;

		final BufferedImage image = createEmptyImage();
		final Graphics2D g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final BasicStroke stroke = new BasicStroke(stokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);

		g2.setColor(GraphConstants.getTextColor());

		g2.draw(new Ellipse2D.Double(margin, margin, (graphSize - 2 * margin), (graphSize - 2 * margin)));

		g2.setColor(GraphConstants.getBlueColor());

		g2.draw(new Arc2D.Double(margin, margin, (graphSize - 2 * margin), (graphSize - 2 * margin), 90,
				(360.0d * value / 100), Arc2D.OPEN));

		final String vauleString = String.valueOf(value);

		if (0 == value || yellow < value) {
			g2.setColor(GraphConstants.getTextColor());
		} else if (red < value) {
			g2.setColor(GraphConstants.getYellowColor());
		} else {
			g2.setColor(GraphConstants.getRedColor());
		}

		switch (mode) {
		case TWO_TEXTLINES:
			paintString(g2, bigFont, vauleString, null, null, 0);
			g2.setColor(GraphConstants.getTextColor());
			if (null != unit) {
				paintString(g2, smallFont, unit, null, null, 1);
			}
			if (null != text) {
				paintString(g2, smallFont, text[0], null, null, 2);
			}
			break;
		case UNIT_AND_ONE_TEXTLINE:
			if (null == unit) {
				paintString(g2, bigFont, vauleString, null, null, 0);
			} else {
				// paintString(g2, bigFont, vauleString, smallFont, unit, 0);
				drawBigTextMiddle(g2, vauleString, unit);
			}
			g2.setColor(GraphConstants.getTextColor());
			if (null != text) {
				// paintString(g2, smallFont, text, null, null, 1);
				drawSmallTextBottom(g2, 0, false, text[0]);
				drawSmallTextBottom(g2, 1, false, text[1]);
			}
			break;
		default:
			throw new IllegalStateException("[mode] unknown: " + mode);
		}

		paintDebug(g2);

		g2.dispose();

		return image;

	}

	/**
	 * Setzt die Betriebsart der Anzeige. Wird <code>null</code> übergeben, wird
	 * {@link Mode#TWO_TEXTLINES} verwendet.
	 * 
	 * @param mode
	 *            die neue Betriebsart
	 */
	public void setMode(Mode mode) {
		this.mode = (null == mode ? Mode.TWO_TEXTLINES : mode);
	}

	/**
	 * Setzt den Wert. Gültige Werte sind Zahlen von 0 bis 100.
	 * 
	 * @param x
	 *            der neue Wert
	 * @throws IllegalArgumentException
	 *             wenn Wert ausserhalb des Gültigkeitsbereichs
	 */
	public void setValue(final int x) {
		if (!(0 <= x && x <= 100)) {
			throw new IllegalArgumentException("error \"0 <= [x] <= 100\" --> x = " + x);
		}
		this.value = x;
		LOG.fine("value assigned: " + x);
	}

	/**
	 * Setzt die Maßeinheit. Wird <code>null</code> übergeben, wird keine
	 * Maßeinheit angezeigt.
	 * 
	 * @param x
	 *            die Maßeinheit
	 */
	public void setUnit(final String x) {
		this.unit = x;
		LOG.fine("unit assigned: " + x);
	}

	/**
	 * Setzt den Zusatztext. Wird <code>null</code> übergeben, wird keine
	 * Zusatztext angezeigt.
	 * 
	 * @param x
	 *            der Zusatztext
	 */
	public void setText(final String[] x) {
		this.text = x;
		LOG.fine("text assigned: " + Arrays.toString(x));
	}

	/**
	 * Setzt die Werte für den Farbwechsel.
	 * 
	 * @param red
	 *            unter diesem Wert roter Wert
	 * @param yellow
	 *            unter diesem Wert gelber Wert
	 */
	public void setColorLimits(int red, int yellow) {
		this.red = red;
		this.yellow = yellow;
	}

	/**
	 * Definiert die Anzeigebetriebsarten.
	 */
	public static enum Mode {
		TWO_TEXTLINES, UNIT_AND_ONE_TEXTLINE
	}

}
