package graph.action;

import java.util.Iterator;

import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

import graph.GraphConstants;
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
			if (node.get(GraphConstants.NODE_TYPE_FIELD).equals(NodeType.GROUP_NODE)) {
//				node.setEndSize(10);
				node.setBounds(0, 0, 1000000, 1000000);
			} else {
				node.setSize(this.size);
			}
			
		}
	}

	public void setSize(Double size) {
		this.size = size;
	}

}
