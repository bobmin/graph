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

		// final LineMetrics lm = bigFontMetrics.getLineMetrics("a", g2);
		// final double bigFontHeight = lm.getAscent();

		final int bigFontHeight = bigFontMetrics.getAscent();

		final double quarter = graphSize * 0.25;
		final double halfheight = bigFontHeight / 2;

		g2.drawLine(0, (int) (quarter - halfheight), graphSize, (int) (quarter - halfheight));
		g2.drawLine(0, (int) (quarter + halfheight), graphSize, (int) (quarter + halfheight));
		// ...drei kleine Zeilen
		g2.setFont(smallFont);
		final FontMetrics smallFontMetrics = g2.getFontMetrics();
		for (int i = 0; i < 3; i++) {
			final int tx = (int) (graphSize - margin - (smallFontMetrics.getHeight() * i));
			g2.drawLine(0, tx, graphSize, tx);
		}
	}

}
