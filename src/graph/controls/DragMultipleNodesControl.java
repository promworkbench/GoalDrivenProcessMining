package graph.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import graph.GraphConstants;
import graph.utils.node.GraphNodeUtils;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.data.event.EventConstants;
import prefuse.data.event.TableListener;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class DragMultipleNodesControl extends ControlAdapter implements TableListener {
	private VisualItem activeItem;
	protected String action;
	protected Point2D down = new Point2D.Double();
	protected Point2D temp = new Point2D.Double();
	protected boolean dragged, wasFixed, resetItem;
	private boolean fixOnMouseOver = true;
	protected boolean repaint = true;
	final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);

	private Display display;

	public static Map<VisualItem, VisualItem> mapInvisibleNodes = new HashMap<>();
	public static Map<VisualItem, List<VisualItem>> mapAffectedNodes = new HashMap<>();

	public DragMultipleNodesControl(Display display) {
		this.display = display;
	}

	public void initInvisibleNodes() {
		List<VisualItem> allNodes = GraphNodeUtils.getAllNodes(this.display.getVisualization());
		for (VisualItem invisibleNode : allNodes) {
			if (invisibleNode.getBoolean(GraphConstants.IS_INVISIBLE)) {
				for (VisualItem item2 : allNodes) {
					if (invisibleNode.getString(GraphConstants.LABEL_FIELD)
							.equals(item2.getString(GraphConstants.LABEL_FIELD))) {
						invisibleNode.setX(item2.getX());
						invisibleNode.setY(item2.getY());
						mapInvisibleNodes.put(item2, invisibleNode);
						if (mapAffectedNodes.containsKey(invisibleNode)) {
							List<VisualItem> vItems = mapAffectedNodes.get(invisibleNode);
							vItems.add(item2);
							mapAffectedNodes.replace(invisibleNode, vItems);
						} else {
							List<VisualItem> vItems = new ArrayList<>();
							vItems.add(item2);
							mapAffectedNodes.put(invisibleNode, vItems);
						}
						break;
					}
				}
			}
		}
	}

	public void addInvisibleNode(String invisibleNodeName, List<String> affectedNodes, double[] midPos) {
		List<VisualItem> allNodes = GraphNodeUtils.getAllNodes(this.display.getVisualization());
		for (VisualItem invisibleNode : allNodes) {
			if (invisibleNode.getBoolean(GraphConstants.IS_INVISIBLE)
					&& invisibleNode.getString(GraphConstants.LABEL_FIELD).equals(invisibleNodeName)) {
				for (VisualItem node : allNodes) {
					if (affectedNodes.contains(node.getString(GraphConstants.LABEL_FIELD))) {
						mapInvisibleNodes.put(node, invisibleNode);
						if (mapAffectedNodes.containsKey(invisibleNode)) {
							List<VisualItem> vItems = mapAffectedNodes.get(invisibleNode);
							vItems.add(node);
							mapAffectedNodes.replace(invisibleNode, vItems);
						} else {
							List<VisualItem> vItems = new ArrayList<>();
							vItems.add(node);
							mapAffectedNodes.put(invisibleNode, vItems);
						}
					}
				}
				invisibleNode.setX(midPos[0]);
				invisibleNode.setY(midPos[1]);
				break;
			}
		}
	}

	public void setFixPositionOnMouseOver(boolean s) {
		fixOnMouseOver = s;
	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	public void itemEntered(VisualItem item, MouseEvent e) {
		Display d = (Display) e.getSource();
		d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		activeItem = item;
		if (fixOnMouseOver) {
			wasFixed = item.isFixed();
			resetItem = true;
			item.setFixed(true);
			item.getTable().addTableListener(this);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	public void itemExited(VisualItem item, MouseEvent e) {
		if (activeItem == item) {
			activeItem = null;
			item.getTable().removeTableListener(this);
			if (resetItem)
				item.setFixed(wasFixed);
		}
		Display d = (Display) e.getSource();
		d.setCursor(Cursor.getDefaultCursor());
	} //

	/**
	 * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	public void itemPressed(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (!fixOnMouseOver) {
			wasFixed = item.isFixed();
			resetItem = true;
			item.setFixed(true);
			item.getTable().addTableListener(this);
		}
		dragged = false;
		Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), down);
	}

	/**
	 * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	public void itemReleased(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (dragged) {
			activeItem = null;
			item.getTable().removeTableListener(this);
			if (resetItem)
				item.setFixed(wasFixed);
			dragged = false;

		}
	}

	public void itemDragged(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		dragged = true;
		Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), temp);
		double dx = temp.getX() - down.getX();
		double dy = temp.getY() - down.getY();
		if (nodeFilter.getBoolean(item)) {
			if (item.getBoolean(GraphConstants.SELECT_FIELD)) {
				List<VisualItem> listSelectedNodes = GraphNodeUtils
						.getSelectedNodes(((Display) e.getComponent()).getVisualization(), item.getTable());
				for (VisualItem selectedItem : listSelectedNodes) {
					this.updatePosNode(selectedItem, dx, dy);
					if (this.mapInvisibleNodes.containsKey(selectedItem)) {
						VisualItem inviNode = this.mapInvisibleNodes.get(selectedItem);
						this.updatePosNode(inviNode, dx, dy);
					}
				}

			} else if (item.getBoolean(GraphConstants.IS_INVISIBLE)) {
				List<VisualItem> vItems = DragMultipleNodesControl.mapAffectedNodes.get(item);
				for (VisualItem vItem : vItems) {
					if (vItem.getBoolean(GraphConstants.SELECT_FIELD)) {
						List<VisualItem> listSelectedNodes = GraphNodeUtils
								.getSelectedNodes(((Display) e.getComponent()).getVisualization(), item.getTable());
						for (VisualItem selectedItem : listSelectedNodes) {
							this.updatePosNode(selectedItem, dx, dy);
							if (this.mapInvisibleNodes.containsKey(selectedItem)) {
								VisualItem inviNode = this.mapInvisibleNodes.get(selectedItem);
								this.updatePosNode(inviNode, dx, dy);
							}
						}
					} else {
						this.updatePosNode(vItem, dx, dy);
					}
				}
				this.updatePosNode(item, dx, dy);

			}

			else {
				this.updatePosNode(item, dx, dy);
				if (this.mapAffectedNodes.containsKey(item)) {
					List<VisualItem> vItems = this.mapAffectedNodes.get(item);
					for (VisualItem vItem : vItems) {
						this.updatePosNode(vItem, dx, dy);
					}
				}
			}
		}

		if (repaint)
			item.getVisualization().repaint();

		down.setLocation(temp);
		if (action != null)
			d.getVisualization().run(action);

	}

	private void updatePosNode(VisualItem item, double dx, double dy) {
		double x = item.getX();
		double y = item.getY();
		item.setStartX(x);
		item.setStartY(y);
		item.setX(x + dx);
		item.setY(y + dy);
		item.setEndX(x + dx);
		item.setEndY(y + dy);
	}

	public void tableChanged(Table t, int start, int end, int col, int type) {
		if (activeItem == null || type != EventConstants.UPDATE || col != t.getColumnNumber(VisualItem.FIXED))
			return;
		int row = activeItem.getRow();
		if (row >= start && row <= end)
			resetItem = false;
	}

}
