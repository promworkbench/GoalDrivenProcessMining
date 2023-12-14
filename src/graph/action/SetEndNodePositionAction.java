package graph.action;

import prefuse.action.Action;
import prefuse.visual.VisualItem;

public class SetEndNodePositionAction extends Action {

	private VisualItem item;

	public SetEndNodePositionAction(VisualItem item) {
		this.item = item;
	}

	public void run(double frac) {
		item.setY(item.getY() + 600);
	}

}
