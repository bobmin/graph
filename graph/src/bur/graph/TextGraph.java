package bur.graph;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Zeigt eine Zeile mit großer Schrift und zwei Zeilen mit kleiner Schrift.
 *  
 * @author maik@btmx.net
 *
 */
public class TextGraph extends AbstractGraph {
	
	private String bigValue = null;
	
	private String smallValue = null;

	@Override
	public BufferedImage createGraph(int graphSize) {
		final BufferedImage image = createEmptyImage(graphSize);
		final Graphics2D g2 = (Graphics2D) image.getGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		paintString(g2, graphSize, bigFont, "1586", null, null, 0);
		paintString(g2, graphSize, smallFont, "Vorgänge", null, null, 1);
		paintString(g2, graphSize, smallFont, "Verfahren 2", null, null, 2);
		
		return image;
	}

}
