package bur.graph.draft;

import java.awt.Graphics2D;

import bur.graph.GraphConstants;

/**
 * Zeichnet ein Balkendiagramm.
 * 
 * @author maik@btmx.net
 *
 */
public class BarDraft {

	/** die Breite/Höhe der Zeichenfläche */
	private static final int SIZE = 100;

	private int count = 0;

	public BarDraft() {
	}

	public void update() {
		count++;
		if (count > SIZE) {
			count = 0;
		}
	}

	public void draw(final Graphics2D g, final int x, final int y, final int scale) {
		g.setColor(GraphConstants.getTextColor());
		g.drawRect(x, y, SIZE, SIZE);
		g.drawString("BALKEN", x + 5, y + 15);
		g.fillRect(x + count - 3, y + (int) (SIZE * 0.5), 6, 6);
	}

}
