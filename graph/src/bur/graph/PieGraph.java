package bur.graph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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

	/** die Anzeigebetriebsart */
	private Mode mode = Mode.TWO_TEXTLINES;

	/** die Zahl von 0 bis 100 */
	private int value = 0;

	/** die Maßeinheit */
	private String unit = null;

	/** der Zusatztext */
	private String text = null;

	private int red = 49;

	private int yellow = 74;

	/**
	 * Instanziiert das Objekt.
	 */
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
			paintString(g2, graphSize, bigFont, vauleString, null, null, 0);
			g2.setColor(GraphConstants.getTextColor());
			if (null != unit) {
				paintString(g2, graphSize, smallFont, unit, null, null, 1);
			}
			if (null != text) {
				paintString(g2, graphSize, smallFont, text, null, null, 2);
			}
			break;
		case UNIT_AND_ONE_TEXTLINE:
			if (null == unit) {
				paintString(g2, graphSize, bigFont, vauleString, null, null, 0);
			} else {
				paintString(g2, graphSize, bigFont, vauleString, smallFont, unit, 0);
			}
			g2.setColor(GraphConstants.getTextColor());
			if (null != text) {
				paintString(g2, graphSize, smallFont, text, null, null, 1);
			}
			break;
		default:
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
		LOG.info("value assigned: " + x);
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
		LOG.info("unit assigned: " + x);
	}

	/**
	 * Setzt den Zusatztext. Wird <code>null</code> übergeben, wird keine
	 * Zusatztext angezeigt.
	 * 
	 * @param x
	 *            der Zusatztext
	 */
	public void setText(final String x) {
		this.text = x;
		LOG.info("text assigned: " + x);
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
