package graph.controls;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

import graph.GraphConstants;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class GroupNodeControl extends ControlAdapter {
	private final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP);
	private Table nodeTable;
	private DataChain<GoalDrivenConfiguration> chain;

	public GroupNodeControl(Table nodeTable, DataChain<GoalDrivenConfiguration> chain) {
		this.nodeTable = nodeTable;
		this.chain = chain;
	}

	public void itemClicked(VisualItem item, java.awt.event.MouseEvent e) {
		if (nodeFilter.getBoolean(item)) {
			System.out.println(nodeTable.get(item.getRow(), GraphConstants.NODE_TYPE_FIELD));
			if (nodeTable.get(item.getRow(), GraphConstants.NODE_TYPE_FIELD).equals(NodeType.GROUP_NODE)) {
				chain.setObject(GoalDrivenObject.selected_group, item.getString(GraphConstants.LABEL_FIELD));
			}
		}
	}

	public DataChain<GoalDrivenConfiguration> getChain() {
		return chain;
	}

	public void setChain(DataChain<GoalDrivenConfiguration> chain) {
		this.chain = chain;
	}

}
