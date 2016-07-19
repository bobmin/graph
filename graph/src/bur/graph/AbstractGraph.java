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
import java.awt.image.BufferedImage;
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

	private static final double FACTOR_SMALL_ASCENT = 0.72;
	private static final double FACTOR_SMALL_SPACE = 0.388;
	private static final double FACTOR_SMALL_ANKER = 0.565;

	Font bigFont = null;

	Font smallFont = null;

	int graphSize = 0;

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

		final BufferedImage image = createGraph();

		g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

	}

	abstract public BufferedImage createGraph();

	// ----------------------------

	BufferedImage createEmptyImage() {
		final BufferedImage image = new BufferedImage(graphSize, graphSize, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(GraphConstants.getBackgroundColor());
		g2.fillRect(0, 0, graphSize, graphSize);
		g2.dispose();
		return image;
	}

	void paintString(final Graphics2D g2, final Font font, final String text, final Font fontTwo, final String textTwo,
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

	/**
	 * Schaltet das Zeichnen der Hilfslinien an und aus.
	 */
	public void setDebug(final boolean value) {
		debugging = value;
	}

	/**
	 * Liefert <code>true</code> wenn Hilfslinien gezeichnet werden sollen.
	 * 
	 * @return <code>true</code> die Hilfslinien sollen gezeichnet werden
	 */
	public boolean isDebugging() {
		return debugging;
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
		if (!debugging) {
			return;
		}
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
			smallValueWidth += fm.stringWidth(value[idx]);
		}
		final double smallHalfwidth = smallValueWidth * 0.5;

		for (int idx = 0; idx < value.length; idx++) {
			g2.drawString(value[idx], (float) (xAnker - smallHalfwidth), (float) (offset + halfheight));
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
