package bur.graph;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;

/**
 * Zeichnet einen Kreis und den Wert.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class LineGraph extends JComponent {

	/** Schrift für große Nummern */
	public static final Font ROBOTO_REGULAR = createFontFromTTF("/roboto/Roboto-Regular.ttf", 38f);

	final static BasicStroke stroke = new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	public LineGraph() {
		final Dimension size = new Dimension(100, 100);
		setMinimumSize(size);
		setPreferredSize(size);
//		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final Insets border = getInsets();

		final int width = getWidth();
		final int height = getHeight();

		final int x1 = 0 + border.left;
		final int y1 = 0 + border.top;

		final int x2 = width - border.right;
		final int y2 = height - border.bottom;

		g.setColor(GraphConstants.COLOR_FUENF);
		g.fillRect(x1, y1, x2, y2);

		g2.setStroke(stroke);

		g.setColor(GraphConstants.COLOR_VIER);

		g2.fill(new RoundRectangle2D.Double(x1 + 10, y1 + 10,
				(x2 - x1 - 20),
				(y2 - y1 - 20),
				10, 10));

		g.setColor(GraphConstants.COLOR_EINS);

//		g2.draw(new Arc2D.Double(x1 + 10, y1 + 10,
//				(x2 - x1 - 20),
//				(y2 - y1 - 20),
//				90, 290,
//				Arc2D.OPEN));

//		final String text = "87%";
//
//		g.setFont(ROBOTO_REGULAR);
//
//		g2.drawString(text, x1 + 25, y1 + 65);

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
	public static Font createFontFromTTF(final String path, final float size) {
		Font x = null;
		final InputStream ttf = LineGraph.class.getResourceAsStream(path);
		if (null != ttf) {
			try {
				x = Font.createFont(Font.TRUETYPE_FONT, ttf);
				x = x.deriveFont(size);
			} catch (FontFormatException | IOException ex) {
				ex.printStackTrace();
			}
		}
		return x;
	}

}
