package graph.action;

import java.awt.Color;
import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

import graph.GraphConstants;
import prefuse.action.assignment.ColorAction;
import prefuse.data.Edge;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class CustomEdgeFillColorAction extends ColorAction {
	private HashMap<EdgeObject, Color> mapEdgeColor;

	public CustomEdgeFillColorAction(String group, HashMap<EdgeObject, Color> mapEdgeColor) {
		super(group, VisualItem.FILLCOLOR);
		this.mapEdgeColor = mapEdgeColor;
	}

	public int getColor(VisualItem item) {
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
			for (EdgeObject edgeObject : this.mapEdgeColor.keySet()) {
				if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
					item.setInt(GraphConstants.EDGE_FILL_COLOR_FIELD,
							ColorLib.color(this.mapEdgeColor.get(edgeObject)));
					return ColorLib.color(this.mapEdgeColor.get(edgeObject));
				}
			}
			return GraphConstants.EDGE_STROKE_COLOR;
		} else {
			return super.getColor(item);
		}

	}
}
