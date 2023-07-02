package graph.controls;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class GraphObjectClickControl extends ControlAdapter {
	private final InGroupPredicate edgeFilter = new InGroupPredicate(GraphConstants.EDGE_GROUP);
	private final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);

	private DataChain<GoalDrivenConfiguration> chain;

	public GraphObjectClickControl() {
	}

	public GraphObjectClickControl(DataChain<GoalDrivenConfiguration> chain) {
		this.chain = chain;
	}

	public void itemClicked(VisualItem item, java.awt.event.MouseEvent e) {
		if (!e.isControlDown()) {
			if (edgeFilter.getBoolean(item)) {
				item.setStrokeColor(GraphConstants.CLICK_EDGE_STROKE_COLOR);
				EdgeItem edge = (EdgeItem) item;
				String sourceNode = edge.getSourceItem().getString(GraphConstants.LABEL_FIELD);
				String targetNode = edge.getTargetItem().getString(GraphConstants.LABEL_FIELD);
				HashMap<String, Object> passValues = new HashMap<String, Object>();
				passValues.put("source", sourceNode);
				passValues.put("target", targetNode);
				if (this.chain != null) {
					this.chain.setObject(GoalDrivenObject.selected_source_target_node, passValues);
				}
			}
			if (nodeFilter.getBoolean(item)) {
				if (item.getString(GraphConstants.NODE_TYPE_FIELD).equals("ACT_NODE")) {
					if (this.chain != null) {
						this.chain.setObject(GoalDrivenObject.selected_node, item.getString(GraphConstants.LABEL_FIELD));
					}
				} else if (item.getString(GraphConstants.NODE_TYPE_FIELD).equals("GROUP_NODE")) {
					if (this.chain != null) {
						chain.setObject(GoalDrivenObject.selected_group, item.getString(GraphConstants.LABEL_FIELD));
					}
				}
			}
		}
		item.getVisualization().repaint();
	}

	public void itemEntered(VisualItem item, java.awt.event.MouseEvent e) {
		if (edgeFilter.getBoolean(item)) {
			item.setStrokeColor(GraphConstants.CLICK_EDGE_STROKE_COLOR);
			item.getVisualization().repaint();
		}
	}

	public void itemExited(VisualItem item, java.awt.event.MouseEvent e) {
		if (edgeFilter.getBoolean(item)) {
			item.setStrokeColor(GraphConstants.EDGE_STROKE_COLOR);
			item.getVisualization().repaint();
		}
	}

	public DataChain<GoalDrivenConfiguration> getChain() {
		return chain;
	}

	public void setChain(DataChain<GoalDrivenConfiguration> chain) {
		this.chain = chain;
	}

}
