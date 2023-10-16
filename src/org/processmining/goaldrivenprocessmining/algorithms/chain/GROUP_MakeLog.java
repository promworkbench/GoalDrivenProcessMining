package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedGroup;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.GraphConstants;
import graph.utils.node.GraphNodeUtils;
import prefuse.data.Node;
import prefuse.visual.VisualItem;

public class GROUP_MakeLog<C> extends DataChainLinkComputationAbstract<C> {

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Creating log for selected group";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Creating log for selected group";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.selected_group };
	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.selected_group_log_skeleton };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {

		SelectedGroup selectedGroup = inputs.get(GoalDrivenObject.selected_group);

		GDPMLogSkeleton gdpmLogSkeleton;
		GoalDrivenDFG goalDrivenDFG;
		if (selectedGroup.getIsHighLevel()) {
			gdpmLogSkeleton = HIGH_MakeHighLevelLog.currentHighLogSkeleton;
			goalDrivenDFG = HIGH_MakeHighLevelDFG.currentHighLevelDfg;
		} else {
			gdpmLogSkeleton = LOW_MakeLowLevelLog.currentLowLogSkeleton;
			goalDrivenDFG = LOW_MakeLowLevelDFG.currentLowLevelDfg;
		}
		if (selectedGroup.isExpanding()) {
			this.expandGroup(gdpmLogSkeleton, goalDrivenDFG, selectedGroup.getGroupSkeleton());
		} else {
			this.collapseGroup(gdpmLogSkeleton, goalDrivenDFG, selectedGroup.getGroupSkeleton());
		}

		return new IvMObjectValues().//
				s(GoalDrivenObject.selected_group_log_skeleton, gdpmLogSkeleton);
	}

	public void expandGroup(GDPMLogSkeleton gdpmLogSkeleton, GoalDrivenDFG goalDrivenDFG,
			GroupSkeleton selectedGroupSkeleton) {
		EdgeHashTable edgeHashTable = gdpmLogSkeleton.getLogSkeleton().getEdgeHashTable();
		List<String> affectedActs = selectedGroupSkeleton.getListAct();
		List<GroupSkeleton> affectedGroups = selectedGroupSkeleton.getListGroup();
		// add new nodes
		GraphNodeUtils.addNewActNodes(goalDrivenDFG, affectedActs);
		List<String> groupNames = affectedGroups.stream().map(GroupSkeleton::getGroupName).collect(Collectors.toList());
		GraphNodeUtils.addNewGroupNodes(goalDrivenDFG, groupNames);
		// add new edges
		List<EdgeObject> newEdges = new ArrayList<EdgeObject>();
		List<GroupSkeleton> newGroups = new ArrayList<GroupSkeleton>();
		for (GroupSkeleton group : newGroups) {
			if (!group.equals(selectedGroupSkeleton)) {
				newGroups.add(group);
			}
		}
		for (EdgeObject edge : edgeHashTable.getEdgeTable().keySet()) {
			String trueSourceLabel = LogSkeletonUtils.getTrueActivityLabel(gdpmLogSkeleton, newGroups,
					edge.getNode1().getOriginalName());
			String trueTargeLabel = LogSkeletonUtils.getTrueActivityLabel(gdpmLogSkeleton, newGroups,
					edge.getNode2().getOriginalName());
			if (affectedActs.contains(trueSourceLabel) || affectedActs.contains(trueTargeLabel)
					|| groupNames.contains(trueSourceLabel) || groupNames.contains(trueTargeLabel)) {
				newEdges.add(edge);
			}

		}
		GraphNodeUtils.addNewEdgesWithOriginalName(goalDrivenDFG, newEdges);
		// save the removed node
		VisualItem removingNode = GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(),
				selectedGroupSkeleton.getGroupName());
		double removingNodeX = removingNode.getX();
		double removingNodeY = removingNode.getY();

		// remove all nodes
		GraphNodeUtils.removeNodeByLabel(goalDrivenDFG, selectedGroupSkeleton.getGroupName());

		// reposition newly added nodes
		List<VisualItem> newAddedNodes = new ArrayList<VisualItem>();
		for (String act : affectedActs) {
			VisualItem vItem = GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(), act);
			if (vItem != null) {
				newAddedNodes.add(vItem);
			}
		}
		for (String act : groupNames) {
			VisualItem vItem = GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(), act);
			if (vItem != null) {
				newAddedNodes.add(vItem);
			}
		}
		double[] midPos = GraphNodeUtils.repositionNodes(newAddedNodes, goalDrivenDFG.getVisualization(), removingNodeX,
				removingNodeY);
		// create invisible node
		Node inviNode = null;
		inviNode = goalDrivenDFG.getGraph().addNode();
		goalDrivenDFG.configInvisibleNode(inviNode, selectedGroupSkeleton.getGroupName());
		inviNode.setBoolean(GraphConstants.IS_INVISIBLE_COLLAPSED, false);
		List<String> combinedList = new ArrayList<String>(affectedActs);
		combinedList.addAll(groupNames);
		// reposition the invisible node
		goalDrivenDFG.getDragMultipleNodesControl().addInvisibleNode(selectedGroupSkeleton.getGroupName(), combinedList,
				midPos);

		// assign 

		// run the renderer for color
		goalDrivenDFG.setNodeFillColorWithExpandedGroup(selectedGroupSkeleton);
		goalDrivenDFG.setDefaultNodeStrokeWidth();
		goalDrivenDFG.setDefaultTextColorAndSize();
		goalDrivenDFG.setDefaultNodeStrokeColor();
		goalDrivenDFG.setEdgeStrokeWidthWithExpandedGroup(selectedGroupSkeleton);
		goalDrivenDFG.setDefaultArrowFillColor();
		goalDrivenDFG.setDefaultEdgeStrokeColor();
		goalDrivenDFG.setDefaultNodeSize();

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	public void collapseGroup(GDPMLogSkeleton gdpmLogSkeleton, GoalDrivenDFG goalDrivenDFG,
			GroupSkeleton selectedGroupSkeleton) {
		EdgeHashTable edgeHashTable = gdpmLogSkeleton.getLogSkeleton().getEdgeHashTable();
		List<String> affectedActs = selectedGroupSkeleton.getListAct();
		List<GroupSkeleton> affectedGroups = selectedGroupSkeleton.getListGroup();
		List<String> groupNames = affectedGroups.stream().map(GroupSkeleton::getGroupName).collect(Collectors.toList());
		List<String> combinedList = new ArrayList<String>(affectedActs);
		combinedList.addAll(groupNames);

		// remove all nodes
		GraphNodeUtils.removeNodeByLabel(goalDrivenDFG, selectedGroupSkeleton.getGroupName());
		for (String act : combinedList) {
			GraphNodeUtils.removeNodeByLabel(goalDrivenDFG, act);
		}

		// add new nodes
		GraphNodeUtils.addNewActNodes(goalDrivenDFG, Arrays.asList(selectedGroupSkeleton.getGroupName()));
		// add new edges
		List<EdgeObject> newEdges = new ArrayList<EdgeObject>();
		List<GroupSkeleton> newGroups = gdpmLogSkeleton.getLogSkeleton().getConfig().getListGroupSkeletons();
		for (EdgeObject edge : edgeHashTable.getEdgeTable().keySet()) {
			String trueSourceLabel = LogSkeletonUtils.getTrueActivityLabel(gdpmLogSkeleton, newGroups,
					edge.getNode1().getOriginalName());
			String trueTargeLabel = LogSkeletonUtils.getTrueActivityLabel(gdpmLogSkeleton, newGroups,
					edge.getNode2().getOriginalName());
			if (trueSourceLabel.equals(selectedGroupSkeleton.getGroupName())
					|| trueTargeLabel.equals(selectedGroupSkeleton.getGroupName())) {
				newEdges.add(edge);
			}
			//			if (edge.getNode1().getCurrentName().equals(selectedGroupSkeleton.getGroupName())
			//					|| edge.getNode2().getCurrentName().equals(selectedGroupSkeleton.getGroupName())) {
			//				newEdges.add(edge);
			//			}

		}
		GraphNodeUtils.addNewEdgesWithCurrentName(goalDrivenDFG, newEdges);
		// save the removed node
		VisualItem removingNode = GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(),
				selectedGroupSkeleton.getListAct().get(0));
		double removingNodeX = removingNode.getX();
		double removingNodeY = removingNode.getY();

		// reposition newly added nodes
		List<VisualItem> newAddedNodes = new ArrayList<VisualItem>();
		newAddedNodes.add(GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(),
				selectedGroupSkeleton.getGroupName()));
		double[] midPos = GraphNodeUtils.repositionNodes(newAddedNodes, goalDrivenDFG.getVisualization(), removingNodeX,
				removingNodeY);
		// create invisible node
		Node inviNode = null;
		inviNode = goalDrivenDFG.getGraph().addNode();
		goalDrivenDFG.configInvisibleNode(inviNode, selectedGroupSkeleton.getGroupName());
		inviNode.setBoolean(GraphConstants.IS_INVISIBLE_COLLAPSED, true);

		// reposition the invisible node
		goalDrivenDFG.getDragMultipleNodesControl().addInvisibleNode(selectedGroupSkeleton.getGroupName(), combinedList,
				midPos);

		// assign 

		// run the renderer for color
		goalDrivenDFG.setNodeFillColorWithExpandedGroup(null);
		goalDrivenDFG.setDefaultNodeStrokeWidth();
		goalDrivenDFG.setDefaultTextColorAndSize();
		goalDrivenDFG.setDefaultNodeStrokeColor();
		goalDrivenDFG.setEdgeStrokeWidthWithExpandedGroup(null);
		goalDrivenDFG.setDefaultArrowFillColor();
		goalDrivenDFG.setDefaultEdgeStrokeColor();
		goalDrivenDFG.setDefaultNodeSize();

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

}
