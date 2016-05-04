package bur.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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

	/** die Schrift für große Nummern */
	public static final Font ROBOTO_BOLD = createFontFromTTF("/roboto/Roboto-Bold.ttf");

	/** die Schrift für Maßeinheiten */
	public static final Font ROBOTO_REGULAR = createFontFromTTF("/roboto/Roboto-Regular.ttf");

	/** die Standardgröße der Komponente */
	private static final Dimension SIZE = new Dimension(100, 100);

	/**
	 * Instanziiert das Objekt.
	 */
	public AbstractGraph() {
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
	}

	/**
	 * Liefert den Font zur TTF-Datei aus dem JAR. Kann der Font nicht geladen
	 * werden, wird <code>null</code> geliefert.
	 * 
	 * @param path
	 *            der Pfad innerhalb vom JAR
	 * @param size
	 *            die Schriftgröße
	 * @return ein Objekt oder <code>null</code>
	 */
	public static Font createFontFromTTF(final String path) {
		Font x = null;
		final InputStream ttf = PieGraph.class.getResourceAsStream(path);
		if (null != ttf) {
			try {
				x = Font.createFont(Font.TRUETYPE_FONT, ttf);
			} catch (FontFormatException | IOException ex) {
				LOG.severe(String.format("font currupt: %s", path));
			}
		}
		return x;
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final Insets border = getInsets();

		final int width = getWidth();
		final int height = getHeight();

//		final int x1 = 0 + border.left;
//		final int y1 = 0 + border.top;
//
//		final int x2 = width - border.right;
//		final int y2 = height - border.bottom;
//
//		g.setColor(GraphConstants.COLOR_FUENF);
//		g.fillRect(x1, y1, x2, y2);

		final BufferedImage image = createGraph(Math.min(width, height));

		g2.drawImage(image, border.left, border.top, image.getWidth(), image.getHeight(), null);

	}

	abstract public BufferedImage createGraph(final int size);

}
