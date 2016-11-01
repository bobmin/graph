package bur.graph;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import bur.graph.state.BarState;
import bur.graph.state.GraphBackground;

public class GraphManager {

	private final List<GraphState> states;

	public GraphManager() {
		states = new LinkedList<>();
		states.add(new GraphBackground());
		states.add(new BarState(100, 100));
	}

	public void update() {
		for (GraphState x : states) {
			x.update();
		}
	}

	public void draw(Graphics2D g) {
		for (GraphState x : states) {
			x.draw(g);
		}
	}

	public void keyPressed(int keyCode) {
		for (GraphState x : states) {
			x.keyPressed(keyCode);
		}
	}

	public void keyReleased(int keyCode) {
		for (GraphState x : states) {
			x.keyReleased(keyCode);
		}
	}

	public interface GraphState {

		void update();

		void draw(Graphics2D g);

		void keyPressed(int keyCode);

		void keyReleased(int keyCode);

	}

}
