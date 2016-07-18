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

	// Background, Text, Blau, Grün, Gelb, Rot
	private static final String[][] THEMES = new String[][] {
			{ "141f2e", "c0c0c0", "375780", "80ff80", "ffcc33", "f05050" },
			{ "404040", "F6F6E8", "00ACE9", "6A9A1F", "cfd427", "D43F3F" },
			{ "323232", "F0F0F0", "0092CC", "779933", "DCD427", "FF3333" },
			{ "282828", "F0F0F0", "087099", "5C7829", "B7B327", "CC3333" },
			{ "1d2126", "b6babf", "5f88b0", "70bf53", "d89b28", "eb5368" },
			{ "303030", "cacaca", "0065a2", "00a1b5", "b8af28", "b82828" }, };

	public static final Color[] COLORS = new Color[6];

	public static void setTheme(final int index) {
		for (int color = 0; color < 6; color++) {
			final String c = THEMES[index][color];
			final int r = Integer.valueOf(c.substring(0, 2), 16);
			final int g = Integer.valueOf(c.substring(2, 4), 16);
			final int b = Integer.valueOf(c.substring(4, 6), 16);
			COLORS[color] = new Color(r, g, b);
		}
	}

	static {
		setTheme(0);
	}

	public static Color getBackgroundColor() {
		return COLORS[0];
	}

	public static Color getTextColor() {
		return COLORS[1];
	}

	public static Color getBlueColor() {
		return COLORS[2];
	}

	public static Color getGreenColor() {
		return COLORS[3];
	}

	public static Color getYellowColor() {
		return COLORS[4];
	}

	public static Color getRedColor() {
		return COLORS[5];
	}

	/** die Schrift für große Nummern */
	public static final Font ROBOTO_BOLD = createFontFromTTF("/roboto/Roboto-Bold.ttf");

	/** die Schrift für Maßeinheiten */
	public static final Font ROBOTO_REGULAR = createFontFromTTF("/roboto/Roboto-Regular.ttf");

	public static final int COLOR_BACKGROUND = 0;

	public static final int COLOR_TEXT = 1;

	public static final int COLOR_BLUE = 2;

	public static final int COLOR_GREEN = 3;

	public static final int COLOR_YELLOW = 4;

	public static final int COLOR_RED = 5;

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

	/**
	 * Liefert die "erste Farbe" für Hilfslinien.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public static Color debugColorOne() {
		return Color.yellow;
	}

	/**
	 * Liefert die "zweite Farbe" für Hilfslinien.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public static Color debugColorTwo() {
		return Color.green;
	}

}
