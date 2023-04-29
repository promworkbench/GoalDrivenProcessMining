package graph.controls;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

import com.google.gwt.dev.util.collect.HashMap;

import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class EdgeClickControl extends ControlAdapter {
	private final InGroupPredicate edgeFilter = new InGroupPredicate(GraphConstants.EDGE_GROUP);

	private DataChain<GoalDrivenConfiguration> chain;
	
	public EdgeClickControl() {
	}

	public EdgeClickControl(DataChain<GoalDrivenConfiguration> chain) {
		this.chain = chain;
	}

	public void itemClicked(VisualItem item, java.awt.event.MouseEvent e) {
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
			item.getVisualization().repaint();
		}
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
