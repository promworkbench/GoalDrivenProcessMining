package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupState;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.visual.VisualItem;

public class GoalDrivenDFGUtils {

	public static List<GroupState> groupStates = new ArrayList<GroupState>();

	public static List<VisualItem> getAllNodes(Visualization vis) {
		List<VisualItem> res = new ArrayList<>();
		if (vis.getGroup("graph") != null) {
			Iterator<Node> nodeIter = ((Graph) vis.getGroup("graph")).nodes();
			while (nodeIter.hasNext()) {
				Node node = nodeIter.next();
				VisualItem visualItem = vis.getVisualItem("graph.nodes", node);
				res.add(visualItem);
			}
		}

		return res;

	}

	public static List<VisualItem> getSelectedNodes(Visualization vis, Table table) {
		List<VisualItem> res = new ArrayList<>();
		Iterator<Node> nodeIter = ((Graph) vis.getGroup("graph")).nodes();
		while (nodeIter.hasNext()) {
			Node node = nodeIter.next();
			VisualItem visualItem = vis.getVisualItem("graph.nodes", node);
			if (table.getBoolean(visualItem.getRow(), "select")) {
				res.add(visualItem);
			}
		}
		return res;
	}

	/*----------------------------------------------------------------*/
	/* Update group state -> Update dfg -> Calculate stat (frequency, time) */
	/* Add */
	public static void addGroupState(GroupSkeleton groupSkeleton) {
		// default is display and collapse
		GroupState newGroupState = new GroupState(groupSkeleton);
		GoalDrivenDFGUtils.groupStates.add(newGroupState);
	}

	/* Expand group */
	public static void setGroupStateExpanded(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		for (GroupState groupStateInGraph : groupStates) {
			if (groupStateInGraph.getGroupSkeleton().equals(groupSkeleton)) {
				groupStateInGraph.setIsDisplay(true);
				groupStateInGraph.setIsCollapse(false);
				// check for its child group
				for (GroupSkeleton groupSkeleton2 : groupStateInGraph.getGroupSkeleton().getListGroup()) {
					GoalDrivenDFGUtils.setGroupStateCollapsed(goalDrivenDFG, groupSkeleton2);
				}
			}
		}
	}

	/* Collapse group */
	public static void setGroupStateCollapsed(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		for (GroupState groupStateInGraph : groupStates) {
			if (groupStateInGraph.getGroupSkeleton().equals(groupSkeleton)) {
				groupStateInGraph.setIsDisplay(true);
				groupStateInGraph.setIsCollapse(true);
				// check for its child group
				for (GroupSkeleton groupSkeleton2 : groupStateInGraph.getGroupSkeleton().getListGroup()) {
					GoalDrivenDFGUtils.setGroupStateHidden(goalDrivenDFG, groupSkeleton2);
				}
			}
		}
	}

	/* Hide group */
	public static void setGroupStateHidden(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		for (GroupState groupStateInGraph : groupStates) {
			if (groupStateInGraph.getGroupSkeleton().equals(groupSkeleton)) {
				groupStateInGraph.setIsDisplay(false);
				groupStateInGraph.setIsCollapse(true);
				// make is_hidden = true in the graph table
				GoalDrivenDFGUtils.hideGroup(goalDrivenDFG, groupSkeleton);
				// check for its child group
				for (GroupSkeleton groupSkeleton2 : groupStateInGraph.getGroupSkeleton().getListGroup()) {
					GoalDrivenDFGUtils.setGroupStateHidden(goalDrivenDFG, groupSkeleton2);
				}
			}
		}
	}

	/* Edit group */
	public static void editGroupState(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		List<GroupState> newGroupStates = new ArrayList<GroupState>();
		for (GroupState groupState : groupStates) {
			if (groupState.getGroupSkeleton().getGroupName().equals(groupSkeleton.getGroupName())) {
				GroupState newGroupState = new GroupState(groupSkeleton);
				newGroupState.setIsCollapse(groupState.getIsCollapse());
				newGroupState.setIsDisplay(groupState.getIsDisplay());
				newGroupStates.add(newGroupState);
			} else {
				newGroupStates.add(groupState);
			}
		}
		groupStates = newGroupStates;
	}

	/* Remove group completely */
	public static void removeGroupState(GoalDrivenDFG goalDrivenDFG, GroupSkeleton deletingGroup,
			GroupSkeleton parentGroup) {
		List<GroupState> newGroupStates = new ArrayList<>();
		for (GroupState groupState : groupStates) {
			if (groupState.getGroupSkeleton().equals(parentGroup)) {
				// take over all children acts and groups of the deleted group
				groupState.getGroupSkeleton().getListAct().addAll(deletingGroup.getListAct());
				groupState.getGroupSkeleton().getListGroup().addAll(deletingGroup.getListGroup());
				// erase the deleted group in the children groups of the parent group
				groupState.getGroupSkeleton().getListGroup().remove(deletingGroup);
			}
			if (!groupState.getGroupSkeleton().equals(deletingGroup)) {
				newGroupStates.add(groupState);
			}
		}
		groupStates = newGroupStates;
		GoalDrivenDFGUtils.removeGroup(goalDrivenDFG, deletingGroup);
		for (GroupSkeleton childGroup : deletingGroup.getListGroup()) {
			// set to collapsed
			GoalDrivenDFGUtils.setGroupStateCollapsed(goalDrivenDFG, childGroup);
		}
	}

	/* Remove group from parent group */
	public static void removeGroupStateFromGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton deletingGroup,
			GroupSkeleton parentGroup) {
		List<GroupState> newGroupStates = new ArrayList<>();
		for (GroupState groupState : groupStates) {
			if (groupState.getGroupSkeleton().getGroupName().equals(parentGroup.getGroupName())) {
				// a new parent group without the deleting group
				GroupState newGroupState = new GroupState(parentGroup);
				newGroupState.setIsCollapse(groupState.getIsCollapse());
				newGroupState.setIsDisplay(groupState.getIsDisplay());
				newGroupStates.add(newGroupState);
			} else {
				newGroupStates.add(groupState);
			}
		}
		groupStates = newGroupStates;
		GoalDrivenDFGUtils.removeGroup(goalDrivenDFG, parentGroup);
		for (GroupSkeleton childGroup : deletingGroup.getListGroup()) {
			// set to collapsed
			GoalDrivenDFGUtils.setGroupStateCollapsed(goalDrivenDFG, childGroup);
		}
	}

	/*----------------------------------------------------------------*/
	/* Methods to update dfg from group, filter states */
	public static void updateDfg(GoalDrivenDFG goalDrivenDFG) {
		// group state
		List<GroupSkeleton> displayCollapseGroups = new ArrayList<>();
		List<GroupSkeleton> displayExpandGroups = new ArrayList<>();
		for (GroupState groupState : GoalDrivenDFGUtils.groupStates) {
			if (groupState.getIsDisplay()) {
				if (groupState.getIsCollapse()) {
					displayCollapseGroups.add(groupState.getGroupSkeleton());
				} else {
					displayExpandGroups.add(groupState.getGroupSkeleton());
				}
			}
		}
		if (!displayCollapseGroups.isEmpty()) {
			GoalDrivenDFGUtils.displayCollapseGroup(goalDrivenDFG, displayCollapseGroups);
		}
		if (!displayExpandGroups.isEmpty()) {
			GoalDrivenDFGUtils.displayExpandGroup(goalDrivenDFG, displayExpandGroups);
		}
		if (displayCollapseGroups.isEmpty() && displayExpandGroups.isEmpty()) {
			GoalDrivenDFGUtils.displayDefault(goalDrivenDFG);
		}

		// filter state

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	/*
	 * Create new nodes for the group -> create new edges for the group -> hide
	 * all the children acts and groups
	 */
	public static void displayCollapseGroup(GoalDrivenDFG goalDrivenDFG, List<GroupSkeleton> groupSkeletons) {
		HashMap<String, Integer> newFrequencyNode = new HashMap<String, Integer>();
		HashMap<EdgeObject, Integer> newFrequencyEdge = new HashMap<EdgeObject, Integer>();
		newFrequencyEdge.putAll(goalDrivenDFG.getFrequencyEdge());
		// default display all act
		for (String act : goalDrivenDFG.getFrequencyNode().keySet()) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), node);
			}
		}
		// collapse all groups
		for (GroupSkeleton groupSkeleton : groupSkeletons) {
			String groupName = groupSkeleton.getGroupName();
			List<String> acts = groupSkeleton.getListAct();
			List<GroupSkeleton> groups = groupSkeleton.getListGroup();
			List<String> groupNames = groups.stream().map(GroupSkeleton::getGroupName).collect(Collectors.toList());
			List<String> combinedList = new ArrayList<String>(acts);
			combinedList.addAll(groupNames);
			// create group node if necessary
			if (goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), groupName) == null) {
				GoalDrivenDFGUtils.addGroupNodes(goalDrivenDFG, groupSkeleton);
			}
			// create new edges
			HashMap<EdgeObject, Integer> frequencyEdgeProjectingGroup = new HashMap<EdgeObject, Integer>();
			for (EdgeObject edgeObject : newFrequencyEdge.keySet()) {
				String source = edgeObject.getNode1();
				String target = edgeObject.getNode2();
				// check if edge relate to the act
				if (combinedList.contains(source) || combinedList.contains(target)) {
					String newSource = combinedList.contains(source) ? groupName : source;
					String newTarget = combinedList.contains(target) ? groupName : target;
					Node node1 = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), newSource);
					Node node2 = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), newTarget);
					// create new edge 
					Edge e = goalDrivenDFG.getGraph().addEdge(node1, node2);
					EdgeObject edgeO = new EdgeObject(newSource, newTarget, edgeObject.getIsIndirected());
					goalDrivenDFG.configEdge(e, edgeO);

					if (frequencyEdgeProjectingGroup.containsKey(edgeO)) {
						frequencyEdgeProjectingGroup.replace(edgeO,
								frequencyEdgeProjectingGroup.get(edgeO) + newFrequencyEdge.get(edgeObject));
					} else {
						frequencyEdgeProjectingGroup.put(edgeO, newFrequencyEdge.get(edgeObject));
					}
				} else {
					frequencyEdgeProjectingGroup.put(edgeObject, newFrequencyEdge.get(edgeObject));
				}
			}
			newFrequencyEdge = frequencyEdgeProjectingGroup;
			// calculate new frequency for node
			int newFreq = 0;
			for (String act : acts) {
				if (goalDrivenDFG.getFrequencyNode().containsKey(act)) {
					newFreq += goalDrivenDFG.getFrequencyNode().get(act);
				}
			}
			for (GroupSkeleton group : groups) {
				if (newFrequencyNode.containsKey(group.getGroupName())) {
					newFreq += newFrequencyNode.get(group.getGroupName());
				}
			}
			newFrequencyNode.put(groupSkeleton.getGroupName(), newFreq);
			// set label of the group name to name + freq 
			Node groupNode = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), groupName);
			if (groupNode != null) {
				groupNode.setString(GraphConstants.DISPLAY_LABEL_FIELD, groupName + "\n" + newFreq);
			}

		}
		goalDrivenDFG.setCurrentFrequencyEdge(newFrequencyEdge);
		for (Map.Entry<String, Integer> entry : goalDrivenDFG.getCurrentFrequencyNode().entrySet()) {
			newFrequencyNode.put(entry.getKey(), entry.getValue());
		}

		goalDrivenDFG.setCurrentFrequencyNode(newFrequencyNode);

		// collapse group
		for (GroupSkeleton group : groupSkeletons) {
			GoalDrivenDFGUtils.collapseGroup(goalDrivenDFG, group);
		}
		// run the action
		GoalDrivenDFGUtils.display(goalDrivenDFG);
	}

	/* Hide all group nodes -> display children act nodes and edges */
	public static void displayExpandGroup(GoalDrivenDFG goalDrivenDFG, List<GroupSkeleton> groupSkeletons) {

		List<String> allDisplayActs = new ArrayList<String>();
		for (GroupSkeleton groupSkeleton : groupSkeletons) {
			allDisplayActs.addAll(groupSkeleton.getListAct());
		}

		// default display all act
		for (String act : goalDrivenDFG.getFrequencyNode().keySet()) {
			if (allDisplayActs.contains(act)) {
				Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
				if (node != null) {
					goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), node);
				}
			}
		}
		// hide all group nodes + display collapse children group nodes
		for (GroupSkeleton groupSkeleton : groupSkeletons) {
			String groupName = groupSkeleton.getGroupName();
			List<GroupSkeleton> groups = groupSkeleton.getListGroup();

			// hide group node
			Node nodeToHide = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), groupName);
			if (nodeToHide != null) {
				goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodeToHide);
			}
			// display collapse or expand children group
			for (GroupSkeleton childGroupSkeleton : groups) {
				for (GroupState groupState : GoalDrivenDFGUtils.groupStates) {
					if (groupState.getGroupSkeleton().equals(childGroupSkeleton)) {
						List<GroupSkeleton> cGroup = new ArrayList<>();
						cGroup.add(childGroupSkeleton);
						if (groupState.getIsCollapse()) {
							// set group state to collapse
							GoalDrivenDFGUtils.displayCollapseGroup(goalDrivenDFG, cGroup);
						} else {
							// set group state to expand
							GoalDrivenDFGUtils.displayExpandGroup(goalDrivenDFG, cGroup);
						}
					}
				}

			}

		}
	}

	/* Display all plain act nodes */
	public static void displayDefault(GoalDrivenDFG goalDrivenDFG) {
		//		goalDrivenDFG.setCurrentFrequencyNode(goalDrivenDFG.getFrequencyNode());
		//		goalDrivenDFG.setCurrentFrequencyEdge(goalDrivenDFG.getFrequencyEdge());

		for (String act : goalDrivenDFG.getCurrentFrequencyNode().keySet()) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), node);
			}

		}
		GoalDrivenDFGUtils.display(goalDrivenDFG);
	}

	/* Run all the action so that the dfg can be displayed */
	public static void display(GoalDrivenDFG goalDrivenDFG) {

		// run the action
		goalDrivenDFG.runCustomColorNodeFillAction();
		goalDrivenDFG.runCustomEdgeStrokeWidthAction();

		goalDrivenDFG.setDefaultNodeStrokeWidth();
		goalDrivenDFG.setDefaultTextColorAndSize();
		goalDrivenDFG.setDefaultNodeStrokeColor();
		goalDrivenDFG.setDefaultArrowFillColor();
		goalDrivenDFG.setDefaultEdgeStrokeColor();
		goalDrivenDFG.setDefaultNodeSize();

	}
	/*----------------------------------------------------------------*/

	/* Methods to calculate stat from group states */
	/*----------------------------------------------------------------*/

	public static void addGroupNodes(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// add node for dashed box
		//		Node node1 = null;
		//		node1 = goalDrivenDFG.getInviGraph().addNode();
		//		goalDrivenDFG.configInvisibleNode(node1, groupSkeleton.getGroupName());
		// add node for group
		Node node2 = null;
		node2 = goalDrivenDFG.getGraph().addNode();
		goalDrivenDFG.configGroupNode(node2, groupSkeleton.getGroupName());

		/*-------------*/
		// find the children nodes
		List<Node> childrenNodes = new ArrayList<Node>();
		for (String act : groupSkeleton.getListAct()) {
			Node n = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (n != null) {
				childrenNodes.add(n);
			}
		}
		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
			Node n = goalDrivenDFG.getMapGroupNode().get(group.getGroupName());
			if (n != null) {
				childrenNodes.add(n);
			}
		}
		// reposition the group node to be in the middle of the children nodes
		GoalDrivenDFGUtils.repositionGroupNodes(goalDrivenDFG, node2, childrenNodes);
		/*-------------*/
		goalDrivenDFG.getMapGroupNode().put(groupSkeleton.getGroupName(), node2);
	}

	public static void repositionGroupNodes(GoalDrivenDFG goalDrivenDFG, Node groupNode, List<Node> childrenNodes) {
		double totalX = 0;
		double totalY = 0;
		for (Node n : childrenNodes) {
			totalX += goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, n).getX();
			totalY += goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, n).getY();
		}
		goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, groupNode)
				.setX(totalX / childrenNodes.size());
		goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, groupNode)
				.setY(totalY / childrenNodes.size());
	}

	public static void collapseGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		List<String> allActs = groupSkeleton.getListAct();
		List<GroupSkeleton> allGroups = groupSkeleton.getListGroup();
		// display the group node 
		Node groupNode = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), groupNode);
		// hide the children act node
		for (String act : allActs) {
			Node nodeToHide = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (nodeToHide != null) {
				goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodeToHide);
			}

		}
		// hide the children group node
		for (GroupSkeleton groupSkeleton2 : allGroups) {
			GoalDrivenDFGUtils.hideGroup(goalDrivenDFG, groupSkeleton2);
		}
	}

	public static void expandGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// hide the group node and its invi square
		Node groupNode = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		// hide group node in graph
		goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), groupNode);
		// display the children act node
		for (String act : groupSkeleton.getListAct()) {
			Node nodeToDisplay = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (nodeToDisplay != null) {
				goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), nodeToDisplay);
			}
		}
		// display the children group node and collapse themselves
		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
			GoalDrivenDFGUtils.collapseGroup(goalDrivenDFG, group);
		}
	}

	public static void hideGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// hide the group node
		Node groupNode = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		// hide group node in graph
		goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), groupNode);
		// hide all children act nodes
		for (String act : groupSkeleton.getListAct()) {
			Node nodeToHide = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (nodeToHide != null) {
				goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodeToHide);
			}

		}
		// hide all children group
		for (GroupSkeleton groupSkeleton2 : groupSkeleton.getListGroup()) {
			GoalDrivenDFGUtils.hideGroup(goalDrivenDFG, groupSkeleton2);
		}

	}

	public static void removeGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// remove all group nodes
		Node groupNode = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		if (groupNode != null) {
			goalDrivenDFG.removeNode(goalDrivenDFG.getGraph(), groupNode);
			// display node of the children act 
			for (String act : groupSkeleton.getListAct()) {
				Node nodeToHide = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
				if (nodeToHide != null) {
					goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), nodeToHide);
				}

			}
		}
	}

	/*----------------------------------------------------------------*/

}
