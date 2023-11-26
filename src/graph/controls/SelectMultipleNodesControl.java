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
//				if (!item.getBoolean(GraphConstants.IS_INVISIBLE)) {
					if (item.getBoolean(GraphConstants.IS_SELECTED)) {
						this.nodeTable.set(item.getRow(), GraphConstants.IS_SELECTED, false);
						if (item.getBoolean("begin") || item.getBoolean("end")) {
							item.setFillColor(GraphConstants.BEGIN_END_NODE_COLOR);
						} else {
							item.setFillColor(item.getInt(GraphConstants.NODE_FILL_COLOR_FIELD));
						}
					} else {
						this.nodeTable.set(item.getRow(), GraphConstants.IS_SELECTED, true);
						item.setFillColor(GraphConstants.SELECTED_NODE_FILL_COLOR);
					}
					display.revalidate();
					display.repaint();
//				} 
//				else {
//					List<VisualItem> vItems = DragMultipleNodesControl.mapAffectedNodes.get(item);
//					for (VisualItem vItem: vItems) {
//						if (vItem.getBoolean(GraphConstants.SELECT_FIELD)) {
//							this.nodeTable.set(vItem.getRow(), GraphConstants.SELECT_FIELD, false);
//							if (vItem.getBoolean("begin") || vItem.getBoolean("end")) {
//								vItem.setFillColor(GraphConstants.BEGIN_END_NODE_COLOR);
//							} else {
//								vItem.setFillColor(item.getInt(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD));
//							}
//						} else {
//							this.nodeTable.set(vItem.getRow(), GraphConstants.SELECT_FIELD, true);
//							vItem.setFillColor(GraphConstants.SELECTED_NODE_FILL_COLOR);
//						}
//					}
//					display.revalidate();
//					display.repaint();
//				}
			}
		}

	}
}
