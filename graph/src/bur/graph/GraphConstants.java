package bur.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class GraphConstants {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(GraphConstants.class.getName());

	// #00ACE9
	public static final Color COLOR_BLUE = new Color(Integer.valueOf("00", 16), Integer.valueOf("AC", 16),
			Integer.valueOf("E9", 16));

	// #D43F3F
	public static final Color COLOR_RED = new Color(Integer.valueOf("D4", 16), Integer.valueOf("3F", 16),
			Integer.valueOf("3F", 16));

	// #6A9A1F
	public static final Color COLOR_GREEN = new Color(Integer.valueOf("6A", 16), Integer.valueOf("9A", 16),
			Integer.valueOf("1F", 16));

	// #F6F6E8
	public static final Color COLOR_TEXT = new Color(Integer.valueOf("F6", 16), Integer.valueOf("F6", 16),
			Integer.valueOf("E8", 16));

	// #404040
	public static final Color COLOR_GRAY = new Color(Integer.valueOf("40", 16), Integer.valueOf("40", 16),
			Integer.valueOf("40", 16));

	/** die Schrift für große Nummern */
	public static final Font ROBOTO_BOLD = createFontFromTTF("/roboto/Roboto-Bold.ttf");

	/** die Schrift für Maßeinheiten */
	public static final Font ROBOTO_REGULAR = createFontFromTTF("/roboto/Roboto-Regular.ttf");

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

}
