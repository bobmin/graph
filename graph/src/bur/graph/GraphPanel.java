package bur.graph;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphPanel extends JPanel implements Runnable, KeyListener {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 300;
	public static final int SCALE = 1;

	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	private BufferedImage image;
	private Graphics2D g;

	private GraphManager gm;

	public GraphPanel() {
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (null == thread) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}

	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		gm = new GraphManager();
	}

	@Override
	public void run() {
		init();
		long start, elapsed, wait;
		while (running) {
			start = System.nanoTime();
			update();
			draw();
			drawToScreen();
			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;
			try {
				Thread.sleep(0 > wait ? 5 : wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		gm.update();
	}

	private void draw() {
		gm.draw(g);
	}

	private void drawToScreen() {
		Graphics2D g2 = (Graphics2D) getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		gm.keyPressed(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		gm.keyReleased(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
