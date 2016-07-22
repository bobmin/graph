package bur.graph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
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

	/** die gelbe Farbgrenze; kleinere Werte werden gelb */
	private int yellow = 74;

	/** die rote Farbgrenze; kleinere Werte werden rot */
	private int red = 49;

	/**
	 * Instanziiert das Objekt.
	 */
	public PieGraph() {
	}

	@Override
	public void createGraph(final Graphics2D g2) {

		final float stokeSize = graphSize * 0.075f;

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
			drawBigTextMiddle(g2, vauleString, null);
			g2.setColor(GraphConstants.getTextColor());
			drawSmallTextBottom(g2, 0, false, unit);
			drawSmallTextBottom(g2, 1, false, string(0));
			break;
		case UNIT_AND_ONE_TEXTLINE:
			drawBigTextMiddle(g2, vauleString, unit);
			g2.setColor(GraphConstants.getTextColor());
			drawSmallTextBottom(g2, 0, false, string(0));
			drawSmallTextBottom(g2, 1, false, string(1));
			break;
		default:
			throw new IllegalStateException("[mode] unknown: " + mode);
		}

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

	@Override
	public int getLength() {
		return 1;
	}

}
