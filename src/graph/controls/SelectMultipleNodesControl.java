package graph.controls;

import java.awt.event.MouseEvent;

import graph.GraphConstants;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class SelectMultipleNodesControl extends ControlAdapter {

	private Table nodeTable;

	private Display display;

	final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);

	public SelectMultipleNodesControl(Table nodeTable, Display display) {
		this.nodeTable = nodeTable;
		this.display = display;
	}

	public void itemPressed(VisualItem item, MouseEvent e) {
		if (e.isControlDown()) {
			if (nodeFilter.getBoolean(item)) {
				if (this.nodeTable.getBoolean(item.getRow(), GraphConstants.SELECT_FIELD)) {
					this.nodeTable.set(item.getRow(), GraphConstants.SELECT_FIELD, false);
					if (item.getBoolean("begin") || item.getBoolean("end")) {
						item.setFillColor(GraphConstants.BEGIN_END_NODE_COLOR);
					} else {
						item.setFillColor(item.getInt(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD));
					}
				} else {
					this.nodeTable.set(item.getRow(), GraphConstants.SELECT_FIELD, true);
					item.setFillColor(GraphConstants.SELECTED_NODE_FILL_COLOR);
				}
				display.revalidate();
				display.repaint();

			}
		}

	}
}
