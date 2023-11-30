package graph.controls;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

import graph.GoalDrivenDFGUtils;
import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class GraphObjectClickControl extends ControlAdapter {
	private final InGroupPredicate edgeFilter = new InGroupPredicate(GraphConstants.EDGE_GROUP);
	private final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);

	private DataChain<GoalDrivenConfiguration> chain;
	private BasicStroke curStroke = new BasicStroke();
	private Font curFont;
	private Boolean isHighLevel;

	public GraphObjectClickControl() {
	}

	public GraphObjectClickControl(DataChain<GoalDrivenConfiguration> chain, Boolean isHighLevel) {
		this.chain = chain;
		this.isHighLevel = isHighLevel;
	}

	public void itemClicked(VisualItem item, MouseEvent e) {
		if (!e.isControlDown() && e.getButton() != MouseEvent.BUTTON3) {
			if (edgeFilter.getBoolean(item)) {
				// find the source and end of edge
				EdgeItem edge = (EdgeItem) item;
				String sourceNode = edge.getSourceItem().getString(GraphConstants.LABEL_FIELD)
						.equals(GraphConstants.BEGIN_NODE_NAME)
								? "begin"
								: edge.getSourceItem().getString(GraphConstants.LABEL_FIELD)
										.equals(GraphConstants.END_NODE_NAME) ? "end"
												: edge.getSourceItem().getString(GraphConstants.LABEL_FIELD);
				String targetNode = edge.getTargetItem().getString(GraphConstants.LABEL_FIELD)
						.equals(GraphConstants.BEGIN_NODE_NAME)
								? "begin"
								: edge.getTargetItem().getString(GraphConstants.LABEL_FIELD)
										.equals(GraphConstants.END_NODE_NAME) ? "end"
												: edge.getTargetItem().getString(GraphConstants.LABEL_FIELD);

				// only highlight edge when not in selected mode
				if (!GoalDrivenDFGUtils.isInSelectActMode) {
					item.setStrokeColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);
				}

				HashMap<String, Object> passValues = new HashMap<String, Object>();
				passValues.put("source", sourceNode);
				passValues.put("target", targetNode);
				if (this.chain != null) {
					if (isHighLevel) {
						this.chain.setObject(GoalDrivenObject.selected_source_target_node, passValues);
						this.chain.setObject(GoalDrivenObject.selected_path_from_high, passValues);
					} else {
						this.chain.setObject(GoalDrivenObject.selected_path_from_low, passValues);
					}
				}

			} else if (nodeFilter.getBoolean(item)) {
				if (item.getString(GraphConstants.NODE_TYPE_FIELD).equals("ACT_NODE")) {
					if (this.chain != null) {
						this.chain.setObject(GoalDrivenObject.selected_node,
								item.getString(GraphConstants.LABEL_FIELD));
					}
				}
			}
		}
		item.getVisualization().repaint();
	}

	public void itemEntered(VisualItem item, java.awt.event.MouseEvent e) {
		if (!GoalDrivenDFGUtils.isInSelectActMode) {
			if (edgeFilter.getBoolean(item)) {
				item.setStrokeColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);
				this.curStroke = item.getStroke();
				this.curFont = item.getFont();
				item.setStroke(new BasicStroke(this.curStroke.getLineWidth() + 5));
				item.setFont(new Font(curFont.getFontName(), curFont.getStyle(), this.curFont.getSize() + 10));
				item.setFillColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);
			} else if (nodeFilter.getBoolean(item)) {
				item.setStrokeColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);

			}

			item.getVisualization().repaint();
		}
	}

	public void itemExited(VisualItem item, java.awt.event.MouseEvent e) {
		if (!GoalDrivenDFGUtils.isInSelectActMode) {
			if (edgeFilter.getBoolean(item)) {
				item.setStrokeColor(item.getInt(GraphConstants.EDGE_FILL_COLOR_FIELD));
				item.setFillColor(item.getInt(GraphConstants.EDGE_FILL_COLOR_FIELD));
				item.setStroke(this.curStroke);
				item.setFont(this.curFont);

			} else if (nodeFilter.getBoolean(item)) {
				item.setStrokeColor(item.getInt(GraphConstants.NODE_STROKE_COLOR_FIELD));
			}
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
