package bur.graph;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class ColorDemo extends JFrame {

	/** der Vergrößerungsfaktor */
	double zoom = 1.0;

	/** die Themenauswahltasten */
	private static List<Character> THEME_CHARS = new ArrayList<>();

	static {
		THEME_CHARS.add('!');
		THEME_CHARS.add('"');
		THEME_CHARS.add('§');
		THEME_CHARS.add('$');
		THEME_CHARS.add('%');
		THEME_CHARS.add('&');
		System.out.println("themes: " + THEME_CHARS);
	}

	/** die Themenauswahl */
	int theme = 0;

	/** die Kantenglättung */
	boolean antialiasing = true;

	/** die Maskierung */
	boolean mask = false;

	private static Color[] colors = Arrays.copyOf(GraphConstants.COLORS, 6);

	/** die Farbauswahl */
	int selection = 0;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new ColorDemo().setVisible(true);
			}
		});
	}

	public ColorDemo() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("ColorDemo v0.1");
		// Panel
		getContentPane().add(new Panel());
		pack();
		setLocationRelativeTo(null);
		// Steuerung
		addKeyListener(new KeyListener());
	}

	private void zoom(final double value) {
		this.zoom *= value;
		repaint();
	}

	private void toggleAntialiasing() {
		this.antialiasing = !antialiasing;
		repaint();
	}

	private void toggleMask() {
		this.mask = !mask;
		repaint();
	}

	private void select(final int index) {
		this.selection = index;
		repaint();
	}

	private void color(final int rgb, final int step) {
		int red = colors[selection].getRed();
		int green = colors[selection].getGreen();
		int blue = colors[selection].getBlue();
		if (0 == rgb) {
			red += step;
			green += step;
			blue += step;
		} else if (1 == rgb) {
			red += step;
		} else if (2 == rgb) {
			green += step;
		} else if (3 == rgb) {
			blue += step;
		}
		red = check(red);
		green = check(green);
		blue = check(blue);
		colors[selection] = new Color(red, green, blue);
		System.out.println("color assigned: " + red + ":" + green + ":" + blue);
		repaint();
	}

	private int check(final int x) {
		if (0 > x) {
			return 255;
		} else if (255 < x) {
			return 0;
		}
		return x;
	}

	private void theme(final char key) {
		this.theme = THEME_CHARS.indexOf(Character.valueOf(key));
		GraphConstants.setTheme(theme);
		colors = Arrays.copyOf(GraphConstants.COLORS, 6);
		repaint();
	}

	private class Panel extends JPanel {

		public Panel() {
			setPreferredSize(new Dimension(500, 500));
		}

		@Override
		protected void paintComponent(Graphics g) {
			final Graphics2D g2 = (Graphics2D) g;

			// Hintergrund

			g2.setColor(GraphConstants.getBackgroundColor());
			g2.fillRect(0, 0, getWidth(), getHeight());

			// hints

			if (antialiasing) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			} else {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// zoom

			final AffineTransform tx1 = new AffineTransform();
			// tx1.translate(110, 22);
			tx1.scale(zoom, zoom);
			g2.setTransform(tx1);

			// kombinieren

			g2.setComposite(AlphaComposite.SrcOver);
			final int colorSize = GraphConstants.COLORS.length;
			int x = 10;

			// schräg

			for (int i = 0; i < colorSize; i++) {
				g2.setColor(colors[i]);
				g2.fillRect(x + i * 5, x + i * 5, 10, 10);
			}

			// überlagernd ohne Abstand

			x += colorSize * 5 + 10;

			g2.setColor(colors[GraphConstants.COLOR_TEXT]);
			g2.fillRect(x, 10, 10, 10 * colorSize);

			for (int i = 0; i < colorSize; i++) {
				g2.setColor(colors[i]);
				g2.fillRect(x, 10 + i * 10, 10, 10);
			}

			// überlagernd mit Abstand

			x += 20;

			g2.setColor(colors[GraphConstants.COLOR_TEXT]);
			g2.fillRect(x, 10, 10, 10 * colorSize);

			for (int i = 0; i < colorSize; i++) {
				g2.setColor(colors[i]);
				g2.fillRect(x, 10 + i * 10 + 1, 10, 8);
			}

			// per Strich

			final BasicStroke colorStroke = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
			g2.setStroke(colorStroke);

			g2.setColor(colors[GraphConstants.COLOR_TEXT]);
			g2.drawLine(10, 80, 80, 80);

			for (int i = 0; i < colorSize; i++) {
				g2.setColor(colors[i]);
				g2.drawLine(10 + i * 10, 80, 10 + i * 10 + 10, 80);
			}

			if (mask) {
				final BasicStroke maskStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
				g2.setStroke(maskStroke);
				g2.setColor(Color.BLACK);
				g2.draw(new Rectangle2D.Double(9, 74, 71, 11));
			}

			// Farbauswahl

			x += 15;
			final int y = (10 * (selection + 1) + 1);
			g2.setColor(GraphConstants.getBlueColor());
			g2.fill(new Ellipse2D.Double(x, y, 8, 8));

			// Texte

			g2.setColor(GraphConstants.getTextColor());
			g2.setFont(GraphConstants.ROBOTO_REGULAR.deriveFont(12.0f));
			final FontMetrics fm = g2.getFontMetrics();
			final int textHeight = fm.getHeight();
			x = 100;
			g2.drawString("Antialiasing [a]: " + String.valueOf(antialiasing).toUpperCase(), 10, (x += textHeight));
			g2.drawString("Maske [m]: " + String.valueOf(mask).toUpperCase(), 10, (x += textHeight));
			g2.drawString("Zoom [+/-]: " + String.valueOf(zoom), 10, (x += textHeight));
			g2.drawString("Schema [SHIFT + 1.." + THEME_CHARS.size() + "]: " + String.valueOf(theme + 1), 10,
					(x += textHeight));
			g2.drawString("Farbe [1..5]: " + String.valueOf(selection + 1), 10, (x += textHeight));

			// aufräumen

			g2.dispose();
		}

	}

	private class KeyListener extends KeyAdapter {

		@Override
		public void keyTyped(KeyEvent e) {
			final char cmd = e.getKeyChar();
			if ('a' == cmd) {
				System.out.println("antialiasing...");
				toggleAntialiasing();
			} else if ('m' == cmd) {
				System.out.println("mask...");
				toggleMask();
			} else if ('+' == cmd) {
				System.out.println("zoom in...");
				zoom(1.1);
			} else if ('-' == cmd) {
				System.out.println("zoom out...");
				zoom(0.9);
			} else if ('1' == cmd || '2' == cmd || '3' == cmd || '4' == cmd || '5' == cmd || '6' == cmd) {
				System.out.println("color: " + cmd);
				select(Integer.parseInt(String.valueOf(cmd)) - 1);
			} else if ('x' == cmd) {
				System.out.println("rgb +");
				color(0, +1);
			} else if ('X' == cmd) {
				System.out.println("rgb -");
				color(0, -1);
			} else if ('r' == cmd) {
				System.out.println("red +");
				color(1, +1);
			} else if ('R' == cmd) {
				System.out.println("red -");
				color(1, -1);
			} else if ('g' == cmd) {
				System.out.println("green +");
				color(2, +1);
			} else if ('G' == cmd) {
				System.out.println("green -");
				color(2, -1);
			} else if ('b' == cmd) {
				System.out.println("blue +");
				color(3, +1);
			} else if ('B' == cmd) {
				System.out.println("blue -");
				color(3, -1);
			} else if (-1 < THEME_CHARS.indexOf(Character.valueOf(cmd))) {
				System.out.println("theme " + cmd);
				theme(cmd);
			}
		}

	}

}
