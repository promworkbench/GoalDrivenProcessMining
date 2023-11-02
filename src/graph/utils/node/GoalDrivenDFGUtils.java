package graph.utils.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupState;

import graph.GoalDrivenDFG;
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

	public static double[] repositionNodes(List<VisualItem> nodes, Visualization vis, double x, double y) {
		int numNodes = nodes.size();
		int numCol = (int) Math.sqrt(numNodes);
		int numRow = numNodes % numCol == 0 ? numNodes / numCol : numNodes / numCol + 1;
		int vItemIndex = 0;
		double curX = x;
		double curY = y;
		int widthMargin = 160;
		int heightMargin = 160;
		outer: for (int row = 0; row < numRow; row++) {
			for (int col = 0; col < numCol; col++) {
				if (vItemIndex < numNodes) {
					VisualItem vItem = nodes.get(vItemIndex);
					vItem.setX(curX);
					vItem.setY(curY);
					if (row % 2 == 0) {
						// go to right
						if (col + 1 < numCol) {
							curX += widthMargin;
						}
					} else {
						// go to left
						if (col + 1 < numCol) {
							curX -= widthMargin;
						}
					}
					vItemIndex++;
				} else {
					break outer;
				}
			}
			curY += heightMargin;
		}

		double midX = x + (numCol / 2) * 100;
		double midY = y + (numRow / 2) * 100;
		double[] midNodePos = new double[] { midX, midY };
		return midNodePos;

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

		}
		if (displayCollapseGroups.isEmpty() && displayExpandGroups.isEmpty()) {
			GoalDrivenDFGUtils.displayDefault(goalDrivenDFG);
		}

		// filter state
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
				newFreq += goalDrivenDFG.getFrequencyNode().get(act);
			}
			for (GroupSkeleton group : groups) {
				if (newFrequencyNode.containsKey(group.getGroupName())) {
					newFreq += newFrequencyNode.get(group.getGroupName());
				}
			}
			newFrequencyNode.put(groupSkeleton.getGroupName(), newFreq);
		}
		goalDrivenDFG.setCurrentFrequencyEdge(newFrequencyEdge);
		newFrequencyNode.putAll(goalDrivenDFG.getCurrentFrequencyNode());
		goalDrivenDFG.setCurrentFrequencyNode(newFrequencyNode);
		
		// collapse group
		for (GroupSkeleton group : groupSkeletons) {
			        GoalDrivenDFGUtils.collapseGroup(goalDrivenDFG, group);
		}
		// run the action
		GoalDrivenDFGUtils.display(goalDrivenDFG);
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
		Node node1 = null;
		node1 = goalDrivenDFG.getInviGraph().addNode();
		goalDrivenDFG.configInvisibleNode(node1, groupSkeleton.getGroupName());
		// add node for group
		Node node2 = null;
		node2 = goalDrivenDFG.getGraph().addNode();
		goalDrivenDFG.configGroupNode(node2, groupSkeleton.getGroupName());
		HashMap<Graph, Node> res = new HashMap<Graph, Node>();
		res.put(goalDrivenDFG.getGraph(), node2);
		res.put(goalDrivenDFG.getInviGraph(), node1);

		goalDrivenDFG.getMapGroupNode().put(groupSkeleton.getGroupName(), res);
	}

	public static void collapseGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		List<String> allActs = groupSkeleton.getListAct();
		List<GroupSkeleton> allGroups = groupSkeleton.getListGroup();
		// display the group node 
		HashMap<Graph, Node> nodes = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), nodes.get(goalDrivenDFG.getGraph()));
		goalDrivenDFG.displayNode(goalDrivenDFG.getInviGraph(), nodes.get(goalDrivenDFG.getInviGraph()));
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

	public static void displayAndExpandGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// hide the group node and its invi square
		HashMap<Graph, Node> nodes = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		// hide group node in graph
		goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodes.get(goalDrivenDFG.getGraph()));
		// hide invi group node in inviGraph
		goalDrivenDFG.hideNode(goalDrivenDFG.getInviGraph(), nodes.get(goalDrivenDFG.getInviGraph()));
		// display the children act node
		for (String act : groupSkeleton.getListAct()) {
			goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(),
					goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act));
		}
		// display the children group node and collapse themselves
		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
			GoalDrivenDFGUtils.collapseGroup(goalDrivenDFG, group);
		}
	}

	public static void hideGroup(GoalDrivenDFG goalDrivenDFG, GroupSkeleton groupSkeleton) {
		// hide the group node
		HashMap<Graph, Node> nodes = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		// hide group node in graph
		goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodes.get(goalDrivenDFG.getGraph()));
		// hide invi group node in inviGraph
		goalDrivenDFG.hideNode(goalDrivenDFG.getInviGraph(), nodes.get(goalDrivenDFG.getInviGraph()));
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
		HashMap<Graph, Node> nodes = goalDrivenDFG.getMapGroupNode().get(groupSkeleton.getGroupName());
		if (nodes != null) {
			goalDrivenDFG.removeNode(goalDrivenDFG.getGraph(), nodes.get(goalDrivenDFG.getGraph()));
			goalDrivenDFG.removeNode(goalDrivenDFG.getInviGraph(), nodes.get(goalDrivenDFG.getInviGraph()));
			// display node of the children act 
			for (String act : groupSkeleton.getListAct()) {
				goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(),
						goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act));
			}
		}
	}

	/*----------------------------------------------------------------*/

}
