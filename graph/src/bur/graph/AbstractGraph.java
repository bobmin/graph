package bur.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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

		final Insets border = getInsets();

		final int width = getWidth();
		final int height = getHeight();

		// final int x1 = 0 + border.left;
		// final int y1 = 0 + border.top;
		//
		// final int x2 = width - border.right;
		// final int y2 = height - border.bottom;
		//
		// g.setColor(GraphConstants.COLOR_FUENF);
		// g.fillRect(x1, y1, x2, y2);

		final int graphSize = Math.min(width, height);
		final float fontSize = graphSize * 0.29f;

		final StringBuffer sb = new StringBuffer();
		sb.append("\n\tgraphSize = ").append(graphSize);
		sb.append("\n\tfontSize = ").append(fontSize);
		LOG.fine(sb.toString());

		bigFont = GraphConstants.ROBOTO_BOLD.deriveFont(fontSize);
		smallFont = GraphConstants.ROBOTO_REGULAR.deriveFont(fontSize * 0.35f);

		final BufferedImage image = createGraph(graphSize);

		g2.drawImage(image, border.left, border.top, image.getWidth(), image.getHeight(), null);

	}

	abstract public BufferedImage createGraph(final int size);

	// ----------------------------

	BufferedImage createEmptyImage(final int graphSize) {
		final BufferedImage image = new BufferedImage(graphSize, graphSize, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(GraphConstants.getBackgroundColor());
		g2.fillRect(0, 0, graphSize, graphSize);

		return image;
	}

	void paintString(final Graphics2D g2, final int graphSize, final Font font, final String text, Font fontTwo,
			String textTwo, float offset) {
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

}
