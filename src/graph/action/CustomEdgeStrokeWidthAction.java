package graph.action;

import java.awt.BasicStroke;
import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

import graph.GraphConstants;
import prefuse.action.assignment.StrokeAction;
import prefuse.data.Edge;
import prefuse.visual.VisualItem;

public class CustomEdgeStrokeWidthAction extends StrokeAction {
	private HashMap<EdgeObject, Float> mapEdgeStrokeWidth;

	public CustomEdgeStrokeWidthAction(String group, HashMap<EdgeObject, Float> mapEdgeStrokeWidth) {
		super(group);
		this.mapEdgeStrokeWidth = mapEdgeStrokeWidth;
	}

	public BasicStroke getStroke(VisualItem item) {
		if (item instanceof Edge) {
			Edge edge = (Edge) item;
			// Get the source and target nodes of the edge
			String source;
			String target;
			if (edge.getSourceNode().getRow() == 0) {
				source = "begin";
			} else if (edge.getSourceNode().getRow() == 1) {
				source = "end";
			} else {
				source = edge.getSourceNode().getString(GraphConstants.LABEL_FIELD);
			}
			if (edge.getTargetNode().getRow() == 0) {
				target = "begin";
			} else if (edge.getTargetNode().getRow() == 1) {
				target = "end";
			} else {
				target = edge.getTargetNode().getString(GraphConstants.LABEL_FIELD);
			}
			for (EdgeObject edgeObject : this.mapEdgeStrokeWidth.keySet()) {
				if (edgeObject.getNode1().equals(source)
						&& edgeObject.getNode2().equals(target)) {
					return new BasicStroke(this.mapEdgeStrokeWidth.get(edgeObject));
				}
			}
			return new BasicStroke(0);

		} else {
			return super.getStroke(item);
		}

	}
}
