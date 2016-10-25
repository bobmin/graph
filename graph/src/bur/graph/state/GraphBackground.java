package bur.graph.state;

import java.awt.Graphics2D;

import bur.graph.GraphConstants;
import bur.graph.GraphManager;
import bur.graph.GraphPanel;
import bur.graph.GraphManager.GraphState;

public class GraphBackground implements GraphState {

	public GraphBackground() {
	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(GraphConstants.getBackgroundColor());
		g.fillRect(0, 0, GraphPanel.WIDTH, GraphPanel.HEIGHT);
		g.setColor(GraphConstants.getTextColor());
		g.drawString("Hallo Welt!", 5, 15);
	}

}
