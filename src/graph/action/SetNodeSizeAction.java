package graph.action;

import java.util.Iterator;

import prefuse.action.Action;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

public class SetNodeSizeAction extends Action {

	private Double size;

	public SetNodeSizeAction(Double size) {
		this.size = size;
	}

	public void run(double frac) {
		TupleSet nodes = m_vis.getVisualGroup("graph.nodes");
		for (Iterator<VisualItem> iter = nodes.tuples(); iter.hasNext();) {
			VisualItem node = (VisualItem) iter.next();
			node.setSize(this.size);
		}
	}

	public void setSize(Double size) {
		this.size = size;
	}

}
