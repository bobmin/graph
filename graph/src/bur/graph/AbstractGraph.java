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
import java.util.Arrays;
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

	/** die Konfiguraion */
	private final GraphConfig graphConfig;

	/** der Faktor für die kleine Schriftgröße */
	private static final double FACTOR_SMALL_ASCENT = 0.72;

	/** der Faktor für den Abstand der kleinen Schriftzeilen */
	private static final double FACTOR_SMALL_SPACE = 0.388;

	/** der Faktor für den Ankerpunkt zu den kleinen Schriftzeilen */
	private static final double FACTOR_SMALL_ANKER = 0.565;

	/** der Faktor für den oberen Ankerpunkt der großen Schriftzeile */
	private static final double FACTOR_BIG_TOP_ANKER = 0.25;

	/** der Faktor für den unteren Ankerpunkt der großen Schriftzeile */
	private static final double FACTOR_BIG_MIDDLE_ANKER = 0.355;

	/** die große fette Schrift wird beim Zeichnen berechnet */
	Font bigBoldFont = null;

	/** die große fette Schrift wird beim Zeichnen berechnet */
	Font bigRegularFont = null;

	/** die kleine Schrift wird beim Zeichnen berechnet */
	Font smallFont = null;

	/** die Grafikhöhe wird beim Zeichnen berechnet */
	int graphHeight = 0;

	/** die Grafikbreite wird beim Zeichnen berechnet */
	int graphWidth = 0;

	/** der Rand wird beim Zeichnen berechnet */
	double margin = 0.0;

	/** die Hilfslinieneinstellung: <code>true</code> zeichnet Linien */
	private boolean debugging = false;

	/** die Texte */
	private String[] texts = null;

	/** der Zähler für die Auswahl */
	int highlighter = 1;

	/**
	 * Instanziiert das Objekt mit der Standardkonfiguration.
	 */
	public AbstractGraph() {
		this(GraphConfig.FULL);
	}

	/**
	 * Instanziiert das Objekt in der gewünschten Konfiguration.
	 * 
	 * @param graphConfig
	 *            die gewünschte Konfiguration
	 */
	public AbstractGraph(final GraphConfig graphConfig) {
		this.graphConfig = graphConfig;
		final int width = (graphConfig.expand ? Integer.MAX_VALUE : 100);
		final Dimension size = new Dimension(width, graphConfig.height);
		setMinimumSize(size);
		setPreferredSize(size);
	}

	public void highlighterTick() {
		highlighter++;
		if (highlighter > getLength()) {
			highlighter = 1;
		}
	}

	/**
	 * Liefert die Anzahl der Werte.
	 * 
	 * @return eine Zahl >= 0
	 */
	abstract public int getLength();

	/**
	 * Setzt die Texte.
	 * 
	 * @param values
	 *            die Texte
	 */
	public void setTexts(final String... values) {
		this.texts = values;
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// TODO use Insets
		// final Insets border = getInsets();

		final int height = getHeight();
		final int width = getWidth();

		graphHeight = Math.min(width, height);
		graphWidth = (graphConfig.expand ? width : graphHeight);

		margin = graphHeight * 0.1d;

		final double fontSize = graphHeight * 0.29;

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("graphHeight = " + graphHeight + ", graphWidth = " + graphWidth + ", margin = " + margin
					+ ", fontSize = " + fontSize);
		}

		bigBoldFont = GraphConstants.ROBOTO_BOLD.deriveFont((float) fontSize);
		bigRegularFont = GraphConstants.ROBOTO_REGULAR.deriveFont((float) fontSize);
		smallFont = GraphConstants.ROBOTO_REGULAR.deriveFont((float) (fontSize * 0.35));

		// final BufferedImage image = new BufferedImage(graphSize, graphSize,
		// BufferedImage.TYPE_INT_RGB);
		// final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(GraphConstants.getBackgroundColor());
		g2.fillRect(0, 0, graphWidth, graphHeight);
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
	private String string(final String[] values, final int index) {
		final String x = (null == values || index >= values.length ? GraphConstants.UNKNOWN : values[index]);
		return (null == x ? GraphConstants.UNKNOWN : x).trim();
	}

	String string(final int index) {
		return string(texts, index);
	}

	int getTextsLenght() {
		return (null == texts ? 0 : texts.length);
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
	 * @param graphHeight
	 *            die Grafikgröße
	 */
	protected void paintDebug(final Graphics2D g2) {
		// Stift
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 3f }, 0));

		// Rahmen + Mittellinie
		g2.setColor(GraphConstants.debugColorOne());
		g2.draw(new Rectangle2D.Double(1, 1, graphWidth - 2, graphHeight - 2));
		g2.drawLine(0, (int) (graphHeight * 0.5f), graphWidth, (int) (graphHeight * 0.5f));

		if (!graphConfig.expand) {
			// Texte
			g2.setColor(Color.green);
			// ...eine große Zeile
			g2.setFont(bigBoldFont);
			final FontMetrics bigFontMetrics = g2.getFontMetrics();

			final double bigFontHeight = bigFontMetrics.getAscent() * 0.72;

			final double quarter = graphHeight * 0.25;
			final double halfheight = bigFontHeight * 0.5;

			g2.drawLine(0, (int) (quarter - halfheight), graphHeight, (int) (quarter - halfheight));
			g2.drawLine(0, (int) (quarter + halfheight), graphHeight, (int) (quarter + halfheight));
			// ...drei kleine Zeilen
			final double smallAnker = graphHeight * FACTOR_SMALL_ANKER;
			g2.setFont(smallFont);
			final FontMetrics smallFontMetrics = g2.getFontMetrics();

			for (int i = 0; i < 3; i++) {

				final double smallFontHeight = smallFontMetrics.getAscent() * FACTOR_SMALL_ASCENT;
				final double smallHalfheight = smallFontHeight * 0.5;
				final double smallFontSpace = smallFontMetrics.getAscent() * FACTOR_SMALL_SPACE;

				final double offset = smallAnker + (smallFontHeight * i) + (smallFontSpace * i);

				g2.drawLine(0, (int) (offset - smallHalfheight), graphHeight, (int) (offset - smallHalfheight));
				g2.drawLine(0, (int) (offset + smallHalfheight), graphHeight, (int) (offset + smallHalfheight));
			}
		}
	}

	/**
	 * Schreibt {@code value} als kleine Textzeile in die untere Grafikhälfte.
	 * Die Schriftfarbe ist {@link GraphConstants#getTextColor()}.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param lineIndex
	 *            der Linienindex
	 * @param stretch
	 *            die Verteilung
	 * @param value
	 *            der kleine Text
	 */
	protected void drawSmallTextBottom(final Graphics2D g2, final int lineIndex, boolean stretch,
			final String... value) {
		final Color[] color = createTextColorArray(value.length);
		drawSmallTextBottom(g2, lineIndex, stretch, color, value);
	}

	protected Color[] createTextColorArray(final int size) {
		final Color[] x = new Color[size];
		Arrays.fill(x, GraphConstants.getTextColor());
		return x;
	}

	/**
	 * Schreibt {@code value} als kleine Textzeile in die untere Grafikhälfte.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param lineIndex
	 *            der Linienindex
	 * @param stretch
	 *            die Verteilung
	 * @param color
	 *            die Textfarbe
	 * @param value
	 *            der kleine Text
	 */
	protected void drawSmallTextBottom(final Graphics2D g2, final int lineIndex, boolean stretch, final Color[] color,
			final String... value) {
		final double yAnker = graphHeight * FACTOR_SMALL_ANKER;

		g2.setFont(smallFont);
		final FontMetrics fm = g2.getFontMetrics();

		final double smallFontHeight = fm.getAscent() * FACTOR_SMALL_ASCENT;
		final double halfheight = smallFontHeight * 0.5;
		final double smallFontSpace = fm.getAscent() * FACTOR_SMALL_SPACE;

		final float yOffset = (float) (yAnker + (smallFontHeight * lineIndex) + (smallFontSpace * lineIndex)
				+ halfheight);

		if (stretch) {
			drawSmallStretchText(g2, fm, color, value, yOffset);
		} else {
			drawSmallOneText(g2, fm, color, value, yOffset);
		}

	}

	private void drawSmallStretchText(final Graphics2D g2, final FontMetrics fm, final Color[] color,
			final String[] value, final float y) {

		final double width = graphHeight - (2 * margin);

		final double xWidth = width / ((value.length * 2) - 1);

		for (int idx = 0; idx < value.length; idx++) {
			final String str = value[idx];
			if (null != str) {
				final double smallHalfwidth = fm.stringWidth(str) * 0.5;
				final double xAnker = margin + (idx * (2 * xWidth)) + (xWidth * 0.5);
				g2.setColor(color[idx]);
				g2.drawString(value[idx], (float) (xAnker - smallHalfwidth), y);
			}
		}

	}

	/**
	 * Schreibt {@code value} als einen kleinen Text mittig ausgerichtet in die
	 * Zeile.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param fm
	 *            die Schriftmaße
	 * @param value
	 *            die Textbausteine
	 * @param y
	 *            die Y-Position der Zeile
	 */
	private void drawSmallOneText(final Graphics2D g2, final FontMetrics fm, final Color[] color, final String[] value,
			final float y) {
		int smallValueWidth = 0;
		for (int idx = 0; idx < value.length; idx++) {
			final String str = value[idx];
			if (null != str) {
				smallValueWidth += fm.stringWidth(str);
			}
		}
		final double smallHalfwidth = smallValueWidth * 0.5;

		final double xAnker = graphHeight * 0.5;

		double xOffset = 0.0;
		for (int idx = 0; idx < value.length; idx++) {
			final String str = value[idx];
			if (null != str) {
				g2.setColor(color[idx]);
				g2.drawString(value[idx], (float) (xAnker - smallHalfwidth + xOffset), y);
				xOffset += fm.stringWidth(str);
			}
		}
	}

	/**
	 * Schreibt {@code value} als obere große Schriftzeile.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param value
	 *            der große Text
	 */
	protected void drawBigTextTop(final Graphics2D g2, final String value) {
		drawBigText(g2, value, null, FACTOR_BIG_TOP_ANKER);
	}

	/**
	 * Schreibt {@code value} und {@code unit} als große Schriftzeile (
	 * {@code unit} mit kleiner Schrift) auf die Mittellinie.
	 * 
	 * @param g2
	 *            die Grafik
	 * @param value
	 *            der große Text
	 * @param unit
	 *            der optionale kleine zweite Text
	 */
	protected void drawBigTextMiddle(final Graphics2D g2, final String value, String unit) {
		drawBigText(g2, value, unit, FACTOR_BIG_MIDDLE_ANKER);
	}

	private void drawBigText(final Graphics2D g2, final String value, final String unit, double factor) {
		final double anker = graphHeight * factor;

		g2.setFont(bigBoldFont);
		final FontMetrics bigFontMetrics = g2.getFontMetrics();

		final double bigFontHeight = bigFontMetrics.getAscent() * 0.72;
		final double halfheight = bigFontHeight * 0.5;

		final int valueWidth = bigFontMetrics.stringWidth(value);
		final double halfwidth = valueWidth * 0.5;

		g2.drawString(value, (int) (graphHeight * 0.5 - halfwidth), (int) (anker + halfheight));

		if (null != unit) {
			g2.setFont(smallFont);
			g2.drawString(unit, (int) ((graphHeight * 0.5 + halfwidth) * 1.01), (int) (anker + halfheight));
		}
	}

	public static class GraphConfig {

		public static final GraphConfig FULL = new GraphConfig(100, false);

		public static final GraphConfig HALF = new GraphConfig(50, true);

		private final int height;

		private final boolean expand;

		public GraphConfig(final int heigth, final boolean expand) {
			this.height = heigth;
			this.expand = expand;
		}

	}

}
