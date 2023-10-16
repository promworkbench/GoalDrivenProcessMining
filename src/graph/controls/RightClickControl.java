package graph.controls;

import java.awt.event.MouseEvent;
import java.util.List;

import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.goaldrivenprocessmining.panelHelper.PopupPanel;

import graph.GoalDrivenDFG;
import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

public class RightClickControl extends ControlAdapter {

	private GoalDrivenDFG display;

	public RightClickControl(GoalDrivenDFG display) {
		this.display = display;

	}

	@Override
	public void itemClicked(VisualItem item, MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && item != null) {
			boolean isSelected = item.getTable().getBoolean(item.getRow(), GraphConstants.SELECT_FIELD);
			if (isSelected
					&& item.getTable().get(item.getRow(), GraphConstants.NODE_TYPE_FIELD).equals(NodeType.ACT_NODE)) {

				PopupPanel.showGroupPopupPanel(this.display, e.getPoint());
			} else if (item.getBoolean(GraphConstants.IS_INVISIBLE)) {
				List<VisualItem> vItems = DragMultipleNodesControl.mapAffectedNodes.get(item);
				Boolean hasSelect = false;
				for (VisualItem vItem : vItems) {
					if (vItem.getBoolean(GraphConstants.SELECT_FIELD)) {
						hasSelect = true;
						break;
					}
				}
				if (hasSelect) {
					PopupPanel.showGroupPopupPanel(this.display, e.getPoint());
				} else {
					GroupSkeleton selectedGroup = this.display.getLog().getLogSkeleton().getGroupSkeletonByGroupName(
							item.getTable().getString(item.getRow(), GraphConstants.LABEL_FIELD));
					PopupPanel.showDisplayGroupPopupPanel(this.display, e.getPoint(), selectedGroup,
							this.display.getIsHighLevel(), item.getBoolean(GraphConstants.IS_INVISIBLE_COLLAPSED));
				}
			}
		}
	}
}
