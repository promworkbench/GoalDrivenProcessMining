package graph.action;

import java.awt.Color;
import java.util.HashMap;

import graph.GraphConstants;
import prefuse.action.assignment.ColorAction;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class CustomColorNodeFillAction extends ColorAction {
	private HashMap<String, Color> mapActColor;

	public CustomColorNodeFillAction(String group, HashMap<String, Color> mapActColor) {
		super(group, VisualItem.FILLCOLOR);
		this.mapActColor = mapActColor;
	}

	public int getColor(VisualItem item) {
		if (item instanceof Node) {
			if (!item.getBoolean(GraphConstants.IS_INVISIBLE)) {
				Table table = (Table) item.getVisualization().getSourceData(this.m_group);
				int rowIndex = item.getRow();
				if (rowIndex == 0 || rowIndex == 1) {
					table.set(rowIndex, GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD,
							GraphConstants.BEGIN_END_NODE_COLOR);
					return GraphConstants.BEGIN_END_NODE_COLOR;
				} else {
					String label = table.getString(rowIndex, GraphConstants.LABEL_FIELD);
					table.set(rowIndex, GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD,
							ColorLib.color(mapActColor.get(label)));
					return ColorLib.color(mapActColor.get(label));
				}
			} else {
				return super.getColor(item);
			}

		} else {
			return super.getColor(item);
		}

	}
}
