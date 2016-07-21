package bur.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

/**
 * Zentrale Klasse für die grafischen Komponenten.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public abstract class AbstractGraph extends JComponent {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(AbstractGraph.class.getName());

	/** die Standardgröße der Komponente */
	private static final Dimension SIZE = new Dimension(100, 100);

	/** der Faktor für die kleine Schriftgröße */
	private static final double FACTOR_SMALL_ASCENT = 0.72;

	/** der Faktor für den Abstand der kleinen Schriftzeilen */
	private static final double FACTOR_SMALL_SPACE = 0.388;

	/** der Faktor für den Ankerpunkt zu den kleinen Schriftzeilen */
	private static final double FACTOR_SMALL_ANKER = 0.565;

	/** die große Schrift wird beim Zeichnen berechnet */
	Font bigFont = null;

	/** die kleine Schrift wird beim Zeichnen berechnet */
	Font smallFont = null;

	/** die Grafikhöhe und -breite wird beim Zeichnen berechnet */
	int graphSize = 0;

	/** der Rand wird beim Zeichnen berechnet */
	double margin = 0.0;

	/** die Hilfslinieneinstellung: <code>true</code> zeichnet Linien */
	private boolean debugging = false;

	/**
	 * Instanziiert das Objekt.
	 */
	public AbstractGraph() {
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// TODO use Insets
		// final Insets border = getInsets();

		final int width = getWidth();
		final int height = getHeight();

		graphSize = Math.min(width, height);
		margin = graphSize * 0.1d;

		final double fontSize = graphSize * 0.29;

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("graphSize = " + graphSize + "margin = " + margin + ", fontSize = " + fontSize);
		}

		bigFont = GraphConstants.ROBOTO_BOLD.deriveFont((float) fontSize);
		smallFont = GraphConstants.ROBOTO_REGULAR.deriveFont((float) (fontSize * 0.35));

		// final BufferedImage image = new BufferedImage(graphSize, graphSize,
		// BufferedImage.TYPE_INT_RGB);
		// final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(GraphConstants.getBackgroundColor());
		g2.fillRect(0, 0, graphSize, graphSize);
		// g2.dispose();

		// final BufferedImage image = createGraph();
		// g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		createGraph(g2);

		if (debugging) {
			paintDebug(g2);
		}

	}

	abstract public void createGraph(final Graphics2D g2);

	// ----------------------------

	/**
	 * Liefert die durch Index festgelegte Zeichenkette. Ist das Array oder der
	 * Index nicht abrufbar, wird {@link GraphConstants#UNKNOWN} geliefert.
	 * 
	 * @param values
	 *            das Array
	 * @param index
	 *            der Index
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String string(final String[] values, final int index) {
		final String x = (null == values || index >= values.length ? GraphConstants.UNKNOWN : values[index]);
		return (null == x ? GraphConstants.UNKNOWN : x).trim();
	}

	/**
	 * Schaltet das Zeichnen der Hilfslinien an und aus.
	 */
	public void setDebug(final boolean value) {
		debugging = value;
	}

	/**
	 * Zeichnet die Hilfslinien in die Grafik.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param graphSize
	 *            die Grafikgröße
	 */
	protected void paintDebug(final Graphics2D g2) {
		// Stift + Farbe
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 3f }, 0));
		g2.setColor(GraphConstants.debugColorOne());
		// Rahmen
		g2.draw(new Rectangle2D.Double(1, 1, graphSize - 2, graphSize - 2));
		// Mittellinie
		g2.drawLine(0, (int) (graphSize * 0.5f), graphSize, (int) (graphSize * 0.5f));
		// Texte
		g2.setColor(Color.green);
		// ...eine große Zeile
		g2.setFont(bigFont);
		final FontMetrics bigFontMetrics = g2.getFontMetrics();

		final double bigFontHeight = bigFontMetrics.getAscent() * 0.72;

		final double quarter = graphSize * 0.25;
		final double halfheight = bigFontHeight * 0.5;

		g2.drawLine(0, (int) (quarter - halfheight), graphSize, (int) (quarter - halfheight));
		g2.drawLine(0, (int) (quarter + halfheight), graphSize, (int) (quarter + halfheight));
		// ...drei kleine Zeilen
		final double smallAnker = graphSize * FACTOR_SMALL_ANKER;
		g2.setFont(smallFont);
		final FontMetrics smallFontMetrics = g2.getFontMetrics();

		for (int i = 0; i < 3; i++) {

			final double smallFontHeight = smallFontMetrics.getAscent() * FACTOR_SMALL_ASCENT;
			final double smallHalfheight = smallFontHeight * 0.5;
			final double smallFontSpace = smallFontMetrics.getAscent() * FACTOR_SMALL_SPACE;

			final double offset = smallAnker + (smallFontHeight * i) + (smallFontSpace * i);

			g2.drawLine(0, (int) (offset - smallHalfheight), graphSize, (int) (offset - smallHalfheight));
			g2.drawLine(0, (int) (offset + smallHalfheight), graphSize, (int) (offset + smallHalfheight));
		}
	}

	protected void drawSmallTextBottom(final Graphics2D g2, final int lineIndex, boolean stretch,
			final String... value) {
		final double yAnker = graphSize * FACTOR_SMALL_ANKER;
		final double xAnker = graphSize * 0.5;

		g2.setFont(smallFont);
		final FontMetrics fm = g2.getFontMetrics();

		final double smallFontHeight = fm.getAscent() * FACTOR_SMALL_ASCENT;
		final double halfheight = smallFontHeight * 0.5;
		final double smallFontSpace = fm.getAscent() * FACTOR_SMALL_SPACE;

		final double offset = yAnker + (smallFontHeight * lineIndex) + (smallFontSpace * lineIndex);

		int smallValueWidth = 0;
		for (int idx = 0; idx < value.length; idx++) {
			final String str = value[idx];
			if (null != str) {
				smallValueWidth += fm.stringWidth(str);
			}
		}
		final double smallHalfwidth = smallValueWidth * 0.5;

		for (int idx = 0; idx < value.length; idx++) {
			final String str = value[idx];
			if (null != str) {
				g2.drawString(value[idx], (float) (xAnker - smallHalfwidth), (float) (offset + halfheight));
			}
		}

	}

	protected void drawBigTextTop(final Graphics2D g2, final String value) {
		drawBigText(g2, value, null, 0.25);
	}

	protected void drawBigTextMiddle(final Graphics2D g2, final String value, String unit) {
		drawBigText(g2, value, unit, 0.355);
	}

	private void drawBigText(final Graphics2D g2, final String value, final String unit, double factor) {
		final double anker = graphSize * factor;

		g2.setFont(bigFont);
		final FontMetrics bigFontMetrics = g2.getFontMetrics();

		final double bigFontHeight = bigFontMetrics.getAscent() * 0.72;
		final double halfheight = bigFontHeight * 0.5;

		final int valueWidth = bigFontMetrics.stringWidth(value);
		final double halfwidth = valueWidth * 0.5;

		g2.drawString(value, (int) (graphSize * 0.5 - halfwidth), (int) (anker + halfheight));

		if (null != unit) {
			g2.setFont(smallFont);
			g2.drawString(unit, (int) ((graphSize * 0.5 + halfwidth) * 1.01), (int) (anker + halfheight));
		}
	}

}
