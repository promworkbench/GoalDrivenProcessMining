package graph.controls;

import java.awt.Color;
import java.awt.event.MouseEvent;

import graph.GraphConstants;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class BackgroundDoubleClickControl extends ControlAdapter {
	private Table nodeTable;

	public BackgroundDoubleClickControl(Table nodeTable) {
		this.nodeTable = nodeTable;
	}

	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2 && e.isConsumed() == false) {
			Display display = (Display) e.getComponent();
			for (int i = 0; i < nodeTable.getRowCount(); i++) {
				if (nodeTable.getBoolean(i, GraphConstants.SELECT_FIELD)) {
					nodeTable.setBoolean(i, GraphConstants.SELECT_FIELD, false);
					VisualItem item = display.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
							this.nodeTable.getTuple(i));
					if (item.getBoolean(GraphConstants.BEGIN_FIELD) || item.getBoolean(GraphConstants.END_FIELD)) {
						item.setFillColor(ColorLib.color(new Color(3, 218, 197)));
					} else {
						item.setFillColor(ColorLib.color(GraphConstants.GRADIENT_NODE_FILL_COLOR[item
								.getInt(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD)]));
					}

				}
			}
			display.validate();
			display.repaint();
		}
	}
}
