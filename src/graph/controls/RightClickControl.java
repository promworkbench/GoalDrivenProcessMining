package graph.controls;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupState;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.goaldrivenprocessmining.panelHelper.PopupPanel;

import graph.GoalDrivenDFG;
import graph.GoalDrivenDFGUtils;
import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class RightClickControl extends ControlAdapter {

	private GoalDrivenDFG display;
	private final InGroupPredicate edgeFilter = new InGroupPredicate(GraphConstants.EDGE_GROUP);
	private final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);

	public RightClickControl(GoalDrivenDFG display) {
		this.display = display;

	}

	@Override
	public void itemClicked(VisualItem item, MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && item != null) {
			if (nodeFilter.getBoolean(item)) {
				boolean isSelected = item.getTable().getBoolean(item.getRow(), GraphConstants.IS_SELECTED);
				if (isSelected && item.getTable().get(item.getRow(), GraphConstants.NODE_TYPE_FIELD)
						.equals(NodeType.ACT_NODE)) {

					PopupPanel.showGroupPopupPanel(this.display, e.getPoint());
				} else if (item.get(GraphConstants.NODE_TYPE_FIELD) == NodeType.GROUP_NODE) {
					GroupSkeleton selectedGroup = this.display.getLog().getGroupSkeletonByGroupName(
							item.getTable().getString(item.getRow(), GraphConstants.LABEL_FIELD));
					Boolean isCollapsed = true;
					for (GroupState groupState : GoalDrivenDFGUtils.groupStates) {
						if (groupState.getGroupSkeleton().equals(selectedGroup)) {
							isCollapsed = groupState.getIsCollapse();
							break;
						}
					}
					PopupPanel.showDisplayGroupPopupPanel(this.display, e.getPoint(), selectedGroup,
							this.display.getIsHighLevel(), isCollapsed);
				}

				else {
					Boolean hasSelect = false;
					if (hasSelect) {
						PopupPanel.showGroupPopupPanel(this.display, e.getPoint());
					} else {
						GroupSkeleton selectedGroup = this.display.getLog().getGroupSkeletonByGroupName(
								item.getTable().getString(item.getRow(), GraphConstants.LABEL_FIELD));
						PopupPanel.showDisplayGroupPopupPanel(this.display, e.getPoint(), selectedGroup,
								this.display.getIsHighLevel(), item.getBoolean(GraphConstants.IS_INVISIBLE_COLLAPSED));
					}
				}
			} else {
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
				HashMap<String, Object> passValues = new HashMap<String, Object>();
				passValues.put("source", sourceNode);
				passValues.put("target", targetNode);

				if (display.getIsHighLevel()) {
					if (SwingUtilities.isRightMouseButton(e)) {
						PopupPanel.showEdgePopupMenu(this.display, e.getPoint(),sourceNode, targetNode, true);
					}
				} else {
				}
			}
		}

	}
}
