package bur.graph;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bur.graph.GraphConstants.FontStyle;

/**
 * Zeigt eine Laufschrift.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class MarqueeGraph extends AbstractGraph {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(MarqueeGraph.class.getName());

	/** der Speicher für weitere Textzeilen */
	private Map<Integer, String[]> moreLines = null;

	/** der Speicher für Textstile */
	private Map<Integer, FontStyle[]> lineStyles = null;

	/** der Speicher für Textfarben */
	private Map<Integer, Color[]> lineColors = null;

	private Alignment alignment = MarqueeGraph.Alignment.LEFT;

	private final int highlighterFactor = 4;

	/**
	 * Instanziiert das Objekt mit halber Höhe.
	 */
	public MarqueeGraph() {
		this(MarqueeGraph.Alignment.LEFT);
	}

	/**
	 * Instanziiert das Objekt mit halber Höhe und legt die horizontale
	 * Textausrichtung fest.
	 */
	public MarqueeGraph(final Alignment alignment) {
		super(GraphConfig.HALF);
		this.alignment = alignment;
	}

	@Override
	public void createGraph(Graphics2D g2) {

		final double ankerHeight = graphHeight * 0.5;

		final int lineIndex = (int) Math.ceil(((double) highlighter) / highlighterFactor) - 1;

		if (null != line(lineIndex, 0)) {
			final int lineLength = getLineLength(lineIndex);

			int ankerWidth = 0;

			if (Alignment.CENTER == alignment) {
				int ankerSum = 0;
				for (int valueIndex = 0; valueIndex < lineLength; valueIndex++) {
					final FontStyle style = style(lineIndex, valueIndex);
					if (FontStyle.BOLD == style) {
						g2.setFont(bigBoldFont);
					} else {
						g2.setFont(bigRegularFont);
					}
					final FontMetrics bigFontMetrics = g2.getFontMetrics();
					final String value = line(lineIndex, valueIndex);
					ankerSum += bigFontMetrics.stringWidth(value);
				}
				ankerWidth = (int) ((graphWidth * 0.5) - (ankerSum * 0.5) - margin);
			}

			for (int valueIndex = 0; valueIndex < lineLength; valueIndex++) {
				final FontStyle style = style(lineIndex, valueIndex);
				if (FontStyle.BOLD == style) {
					g2.setFont(bigBoldFont);
				} else {
					g2.setFont(bigRegularFont);
				}
				final Color color = color(lineIndex, valueIndex);
				if (null != color) {
					g2.setColor(color);
				} else {
					g2.setColor(GraphConstants.getTextColor());
				}
				final FontMetrics bigFontMetrics = g2.getFontMetrics();
				final double bigFontHeight = bigFontMetrics.getAscent() * 0.72;
				final double halfheight = bigFontHeight * 0.5;
				final String value = line(lineIndex, valueIndex);
				g2.drawString(value, (int) (margin * 2) + ankerWidth, (int) (ankerHeight + halfheight));
				ankerWidth += bigFontMetrics.stringWidth(value);
			}
		}

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("highlighter = " + highlighter + ", lenght = " + getLength());
		}

	}

	private int getLineLength(final int lineIndex) {
		int x = 0;
		if (0 == lineIndex) {
			x = getTextsLenght();
		} else {
			final Integer key = Integer.valueOf(lineIndex);
			if (moreLines.containsKey(key)) {
				x = moreLines.get(key).length;
			}
		}
		return x;
	}

	@Override
	public int getLength() {
		final int l = (null == moreLines ? 1 : ((moreLines.size() + 1) * 3));
		return l;
	}

	/**
	 * Speichert die Texte pro Zeile.
	 * 
	 * @param lineIndex
	 *            der Zeilenindex
	 * @param values
	 *            die Texte
	 */
	public void setTexts(final int lineIndex, final String... values) {
		if (0 == lineIndex) {
			setTexts(values);
		} else {
			if (null == moreLines) {
				moreLines = new LinkedHashMap<>();
			}
			if (null == values) {
				moreLines.remove(Integer.valueOf(lineIndex));
			} else {
				moreLines.put(Integer.valueOf(lineIndex), Arrays.copyOf(values, values.length));
			}
		}
	}

	private String line(final int lineIndex, final int valueIndex) {
		String x = GraphConstants.UNKNOWN;
		if (0 == lineIndex) {
			x = string(valueIndex);
		} else {
			final Integer key = Integer.valueOf(lineIndex);
			if (moreLines.containsKey(key)) {
				x = moreLines.get(key)[valueIndex];
			}
		}
		return x;
	}

	private FontStyle style(final int lineIndex, final int valueIndex) {
		FontStyle x = null;
		final Integer key = Integer.valueOf(lineIndex);
		if (null != lineStyles && lineStyles.containsKey(key)) {
			final FontStyle[] styles = lineStyles.get(key);
			if (valueIndex < styles.length) {
				x = styles[valueIndex];
			}
		}
		return x;
	}

	private Color color(final int lineIndex, final int valueIndex) {
		Color x = null;
		final Integer key = Integer.valueOf(lineIndex);
		if (null != lineColors && lineColors.containsKey(key)) {
			final Color[] colors = lineColors.get(key);
			if (valueIndex < colors.length) {
				x = colors[valueIndex];
			}
		}
		return x;
	}

	public void setStyles(final int lineIndex, final FontStyle... values) {
		if (null == lineStyles) {
			lineStyles = new LinkedHashMap<>();
		}
		if (null == values) {
			lineStyles.remove(Integer.valueOf(lineIndex));
		} else {
			lineStyles.put(Integer.valueOf(lineIndex), Arrays.copyOf(values, values.length));
		}
	}

	public void setColors(final int lineIndex, final Color... values) {
		if (null == lineColors) {
			lineColors = new LinkedHashMap<>();
		}
		if (null == values) {
			lineColors.remove(Integer.valueOf(lineIndex));
		} else {
			lineColors.put(Integer.valueOf(lineIndex), Arrays.copyOf(values, values.length));
		}
	}

	/**
	 * Die horizontale Ausrichtung vom Text.
	 */
	public enum Alignment {
		LEFT, CENTER
	}

}
