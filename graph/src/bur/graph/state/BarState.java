package bur.graph.state;

import java.awt.Graphics2D;

import bur.graph.GraphManager.GraphState;
import bur.graph.draft.BarDraft;

public class BarState implements GraphState {

	private final int x;
	private final int y;

	private final BarDraft draft;

	public BarState(final int x, final int y) {
		this.x = x;
		this.y = y;
		this.draft = new BarDraft();
	}

	@Override
	public void update() {
		draft.update();
	}

	@Override
	public void draw(Graphics2D g) {
		draft.draw(g, x, y, 1);
	}

}
