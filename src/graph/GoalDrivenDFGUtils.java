package graph;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.algorithms.chain.CONFIG_Update;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupState;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.util.TableIterator;
import prefuse.visual.VisualItem;

public class GoalDrivenDFGUtils {

	public static Boolean isInSelectActMode = false;
	public static String selectingAct;

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
		// run the action
		GoalDrivenDFGUtils.display(goalDrivenDFG);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	/*
	 * Create new nodes for the group -> create new edges for the group -> hide
	 * all the children acts and groups
	 */
	public static void displayCollapseGroup(GoalDrivenDFG goalDrivenDFG, List<GroupSkeleton> groupSkeletons) {
		HashMap<String, Integer> newFrequencyNode = new HashMap<String, Integer>();
		newFrequencyNode.putAll(goalDrivenDFG.getFrequencyNode());
		HashMap<EdgeObject, Integer> newFrequencyEdge = new HashMap<EdgeObject, Integer>();
		newFrequencyEdge.putAll(goalDrivenDFG.getFrequencyEdge());
		// default display all act
		for (String act : goalDrivenDFG.getFrequencyNode().keySet()) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				if (node.get(GraphConstants.NODE_TYPE_FIELD) == NodeType.ACT_NODE) {
					goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), node);
				}
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
			// create new edges if necessary
			HashMap<EdgeObject, Integer> frequencyEdgeProjectingGroup = new HashMap<EdgeObject, Integer>();
			for (EdgeObject edgeObject : newFrequencyEdge.keySet()) {
				String source = edgeObject.getNode1();
				String target = edgeObject.getNode2();
				// check if edge relate to the act
				if (combinedList.contains(source) || combinedList.contains(target)) {
					String newSource = combinedList.contains(source) ? groupName : source;
					String newTarget = combinedList.contains(target) ? groupName : target;
					EdgeObject edgeO = new EdgeObject(newSource, newTarget, edgeObject.getIsIndirected());

					if (!newFrequencyEdge.containsKey(edgeO)) {
						if (frequencyEdgeProjectingGroup.containsKey(edgeO)) {
							frequencyEdgeProjectingGroup.replace(edgeO,
									frequencyEdgeProjectingGroup.get(edgeO) + newFrequencyEdge.get(edgeObject));
						} else {
							frequencyEdgeProjectingGroup.put(edgeO, newFrequencyEdge.get(edgeObject));
						}
					}

					Node node1 = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), newSource);
					Node node2 = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), newTarget);
					if (goalDrivenDFG.getGraph().getEdge(node1.getRow(), node2.getRow()) == -1) {
						// create new edge 
						Edge e = goalDrivenDFG.getGraph().addEdge(node1, node2);
						goalDrivenDFG.configEdge(e, edgeO);
					}
				} else {
					frequencyEdgeProjectingGroup.put(edgeObject, newFrequencyEdge.get(edgeObject));
				}
			}

			newFrequencyEdge.putAll(frequencyEdgeProjectingGroup);
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
				String curName = groupNode.getString(GraphConstants.DISPLAY_LABEL_FIELD);
				String newLabel = "";
				if (curName.split("\n").length == 2) {
					newLabel = curName.split("\n")[0];
				} else if (curName.split("\n").length == 3) {
					newLabel = curName.split("\n")[0] + "\n" + curName.split("\n")[1];
				} else {
					newLabel = curName;
				}
				newLabel += "\n" + newFreq;
				groupNode.setString(GraphConstants.DISPLAY_LABEL_FIELD, newLabel);
			}

		}
		goalDrivenDFG.setFrequencyEdge(newFrequencyEdge);
		for (Map.Entry<String, Integer> entry : goalDrivenDFG.getCurrentFrequencyNode().entrySet()) {
			newFrequencyNode.put(entry.getKey(), entry.getValue());
		}

		goalDrivenDFG.setFrequencyNode(newFrequencyNode);

		// collapse group
		for (GroupSkeleton group : groupSkeletons) {
			GoalDrivenDFGUtils.collapseGroup(goalDrivenDFG, group);
		}
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
			List<String> acts = groupSkeleton.getListAct();
			List<GroupSkeleton> groups = groupSkeleton.getListGroup();

			// hide group node
			Node nodeToHide = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), groupName);
			if (nodeToHide != null) {
				goalDrivenDFG.hideNode(goalDrivenDFG.getGraph(), nodeToHide);
			}
			// relabel the children group nodes
			for (String act : acts) {
				Node n = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
				if (n != null) {
					String curLabel = n.getString(GraphConstants.DISPLAY_LABEL_FIELD);
					String newLabel = "";
					String freq = "";
					if (curLabel.split("\n").length == 2) {
						newLabel = curLabel.split("\n")[0];
						freq = curLabel.split("\n")[1];
					} else if (curLabel.split("\n").length == 3) {
						newLabel = curLabel.split("\n")[0];
						freq = curLabel.split("\n")[2];
					}
					n.setString(GraphConstants.DISPLAY_LABEL_FIELD,
							newLabel + "\n" + "Parent group: " + groupName + "\n" + freq);
				}

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

		for (String act : goalDrivenDFG.getCurrentFrequencyNode().keySet()) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), node);
			}

		}
	}

	/* Run all the action so that the dfg can be displayed */
	public static void display(GoalDrivenDFG goalDrivenDFG) {
		Table nodeTable = goalDrivenDFG.getGraph().getNodeTable();
		Table edgeTable = goalDrivenDFG.getGraph().getEdgeTable();
		goalDrivenDFG.setCurrentFrequencyNode(new HashMap<>());
		goalDrivenDFG.setCurrentFrequencyEdge(new HashMap<>());

		// calculate the current frequency node
		TableIterator nodes = nodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (nodeTable.isValidRow(row)) {
				Node node = goalDrivenDFG.getGraph().getNode(row);
				if (!node.getBoolean(GraphConstants.BEGIN_FIELD) && !node.getBoolean(GraphConstants.END_FIELD)) {
					String label = node.getString(GraphConstants.LABEL_FIELD);
					if (node.getBoolean(GraphConstants.IS_DISPLAY)) {
						goalDrivenDFG.getCurrentFrequencyNode().put(label, goalDrivenDFG.getFrequencyNode().get(label));
					}
				}
			}
		}
		// calculate the current frequency edge
		TableIterator edges = edgeTable.iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (edgeTable.isValidRow(row)) {
				Edge edge = goalDrivenDFG.getGraph().getEdge(row);
				if (edge.getBoolean(GraphConstants.IS_DISPLAY)) {
					String source = edge.getSourceNode().getString(GraphConstants.LABEL_FIELD);
					String target = edge.getTargetNode().getString(GraphConstants.LABEL_FIELD);
					source = source.equals("**BEGIN**") ? "begin" : source;
					target = target.equals("**END**") ? "end" : target;

					for (EdgeObject edgeObject : goalDrivenDFG.getFrequencyEdge().keySet()) {
						if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
							goalDrivenDFG.getCurrentFrequencyEdge().put(edgeObject,
									goalDrivenDFG.getFrequencyEdge().get(edgeObject));
							break;
						}
					}
				}
			}
		}
		// run the action
		goalDrivenDFG.runCustomColorNodeFillAction(goalDrivenDFG.getCurrentFrequencyNode());
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(goalDrivenDFG.getCurrentFrequencyEdge());

		goalDrivenDFG.setDefaultNodeStrokeWidth();
		goalDrivenDFG.setDefaultTextColorSizeAndFont();
		goalDrivenDFG.setDefaultNodeStrokeColor();
		goalDrivenDFG.setDefaultArrowFillColor();
		goalDrivenDFG.setDefaultEdgeColor();
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
		// add the group name in the labels of the children nodes
		for (Node child : childrenNodes) {
			String currentLabel = child.getString(GraphConstants.DISPLAY_LABEL_FIELD);
			String nodeName = currentLabel.split("\n")[0];
			String freq = currentLabel.split("\n")[1];
			currentLabel = nodeName + "\n" + "Parent group: " + groupSkeleton.getGroupName() + "\n" + freq;
			child.setString(GraphConstants.DISPLAY_LABEL_FIELD, currentLabel);
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
				Node nodeToDisplay = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
				if (nodeToDisplay != null) {
					String curLabel = nodeToDisplay.getString(GraphConstants.DISPLAY_LABEL_FIELD);
					String name = curLabel.split("\n")[0];
					String freq = curLabel.split("\n")[2];
					nodeToDisplay.setString(GraphConstants.DISPLAY_LABEL_FIELD, name + "\n" + freq);
					goalDrivenDFG.displayNode(goalDrivenDFG.getGraph(), nodeToDisplay);
				}

			}
			// rename group node of the children
			for (GroupSkeleton group : groupSkeleton.getListGroup()) {
				Node n = goalDrivenDFG.getMapGroupNode().get(group.getGroupName());
				String curLabel = n.getString(GraphConstants.DISPLAY_LABEL_FIELD);
				String name = curLabel.split("\n")[0];
				String freq = curLabel.split("\n")[2];
				n.setString(GraphConstants.DISPLAY_LABEL_FIELD, name + "\n" + freq);
			}
		}
	}

	/*----------------------------------------------------------------*/

	/*----------------------------------------------------------------*/
	/* Highlight the selected edge */
	public static void highlightSelectedEdge(GoalDrivenDFG goalDrivenDFG, String[] edge) {
		Table nodeTable = goalDrivenDFG.getGraph().getNodeTable();
		Table edgeTable = goalDrivenDFG.getGraph().getEdgeTable();
		List<Integer> highlightEdges = new ArrayList<Integer>();
		List<Integer> unhighlightEdges = new ArrayList<Integer>();
		String source = edge[0];
		String target = edge[1];
		// reset graph
		resetColorAndStroke(goalDrivenDFG);
		// set in select mode
		isInSelectActMode = true;

		TableIterator edges = edgeTable.iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (edgeTable.isValidRow(row)) {
				int sourceRow = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
				int targetRow = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
				String sourceNode = nodeTable.getString(sourceRow, GraphConstants.LABEL_FIELD).equals("**BEGIN**")
						? "begin"
						: nodeTable.getString(sourceRow, GraphConstants.LABEL_FIELD);
				String targetNode = nodeTable.getString(targetRow, GraphConstants.LABEL_FIELD).equals("**END**") ? "end"
						: nodeTable.getString(targetRow, GraphConstants.LABEL_FIELD);
				// find the edge match the affected edge
				if (source.equals(sourceNode) && target.equals(targetNode)) {
					highlightEdges.add(row);
					break;
				}
			}
		}
		// find the unhighlighted edges
		edges = edgeTable.iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (edgeTable.isValidRow(row)) {
				if (!highlightEdges.contains(row)) {
					unhighlightEdges.add(row);
				}
			}
		}

		// unhighlight all nodes
		List<Integer> unhighlightNode = new ArrayList<Integer>();
		List<Integer> highlightNode = new ArrayList<Integer>();
		TableIterator nodes = nodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (nodeTable.isValidRow(row)) {
				if (nodeTable.getString(row, GraphConstants.LABEL_FIELD).equals(source)
						|| nodeTable.getString(row, GraphConstants.LABEL_FIELD).equals(target)) {
					highlightNode.add(row);
				} else {
					unhighlightNode.add(row);
				}
			}
		}
		for (Integer i : highlightNode) {
			VisualItem nodeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
					edgeTable.getTuple(i));
			highlightItem(nodeItem);
		}
		for (Integer i : unhighlightNode) {
			VisualItem nodeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
					edgeTable.getTuple(i));
			unhighlightItem(nodeItem);
		}
		// highlight edges
		for (Integer i : highlightEdges) {
			VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
					edgeTable.getTuple(i));
			highlightItem(edgeItem);
		}
		for (Integer i : unhighlightEdges) {
			VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
					edgeTable.getTuple(i));
			unhighlightItem(edgeItem);
		}

	}

	/* Highlight the selected act */
	public static void highlightSelectedAct(GoalDrivenDFG goalDrivenDFG, String act) {
		// reset graph
		resetColorAndStroke(goalDrivenDFG);
		Config config = CONFIG_Update.currentConfig;
		selectingAct = act;
		Boolean isActInHigh = false;
		Boolean isHighGraph = goalDrivenDFG.getIsHighLevel();
		for (String highAct : config.getHighActs()) {
			if (act.equals(highAct)) {
				isActInHigh = true;
				break;
			}
		}
		// set in select mode
		isInSelectActMode = true;

		if (isActInHigh && isHighGraph) {
			// highlight node and its related edges
			highlightNode(goalDrivenDFG, act);
		} else if (!isActInHigh && isHighGraph) {
			List<EdgeObject> listAffectedEdge = config.getMapActEdgeInHighLevel().get(act);
			if (listAffectedEdge != null) {
				highlightEdge(goalDrivenDFG, act, listAffectedEdge);
			}
		} else if (!isActInHigh && !isHighGraph) {
			// highlight node and its related edges
			highlightNode(goalDrivenDFG, act);
		} else {
			// highlight node and its related edges
			highlightNode(goalDrivenDFG, act);
		}

	}

	// highlight the edge representing the act
	public static void highlightEdge(GoalDrivenDFG goalDrivenDFG, String act, List<EdgeObject> listAffectedEdge) {
		Table nodeTable = goalDrivenDFG.getGraph().getNodeTable();
		Table edgeTable = goalDrivenDFG.getGraph().getEdgeTable();
		List<Integer> highlightEdges = new ArrayList<Integer>();
		List<Integer> unhighlightEdges = new ArrayList<Integer>();
		// recalculate the label for the highlighted edge: display the frequency of the act happening on that edge.
		Map<EdgeObject, Integer> frequencyOfActInEdge = LogSkeletonUtils.getFrequencyOfActInEdge(act, listAffectedEdge);

		for (EdgeObject edgeObject : listAffectedEdge) {
			TableIterator edges = edgeTable.iterator();
			while (edges.hasNext()) {
				int row = edges.nextInt();
				if (edgeTable.isValidRow(row)) {
					int sourceRow = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
					int targetRow = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
					String source = nodeTable.getString(sourceRow, GraphConstants.LABEL_FIELD).equals("**BEGIN**")
							? "begin"
							: nodeTable.getString(sourceRow, GraphConstants.LABEL_FIELD);
					String target = nodeTable.getString(targetRow, GraphConstants.LABEL_FIELD).equals("**END**") ? "end"
							: nodeTable.getString(targetRow, GraphConstants.LABEL_FIELD);
					// find the edge match the affected edge
					if (source.equals(edgeObject.getNode1()) && target.equals(edgeObject.getNode2())) {
						highlightEdges.add(row);
						break;
					}
				}
			}
		}
		// find the unhighlighted edges
		TableIterator edges = edgeTable.iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (edgeTable.isValidRow(row)) {
				if (!highlightEdges.contains(row)) {
					unhighlightEdges.add(row);
				}
			}
		}

		// unhighlight all nodes
		List<Integer> unhighlightNode = new ArrayList<Integer>();
		TableIterator nodes = nodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			unhighlightNode.add(row);
		}
		for (Integer i : unhighlightNode) {
			VisualItem nodeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
					edgeTable.getTuple(i));
			unhighlightItem(nodeItem);
		}
		// highlight edges
		for (Integer i : highlightEdges) {
			VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
					edgeTable.getTuple(i));
			highlightItem(edgeItem);
		}
		for (Integer i : unhighlightEdges) {
			VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
					edgeTable.getTuple(i));
			unhighlightItem(edgeItem);
		}
		// change customized label 
		for (Map.Entry<EdgeObject, Integer> entry : frequencyOfActInEdge.entrySet()) {
			goalDrivenDFG.getEdgeRenderer().getCustomizedFrequencyEdge().put(entry.getKey(),
					Integer.toString(entry.getValue()));
		}
	}

	// highlight the node representing the act
	public static void highlightNode(GoalDrivenDFG goalDrivenDFG, String act) {
		List<Integer> unhighlightNodes = new ArrayList<Integer>();
		List<Integer> highlightEdges = new ArrayList<Integer>();
		List<Integer> unhighlightEdges = new ArrayList<Integer>();
		Table nodeTable = goalDrivenDFG.getGraph().getNodeTable();
		Table edgeTable = goalDrivenDFG.getGraph().getEdgeTable();
		// find the node
		Node highlightNode = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);

		if (highlightNode != null) {
			// find all edges relates to this node
			TableIterator edges = edgeTable.iterator();
			while (edges.hasNext()) {
				int row = edges.nextInt();
				if (edgeTable.isValidRow(row)) {
					int source = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
					int target = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
					if (source == highlightNode.getRow() || target == highlightNode.getRow()) {
						highlightEdges.add(row);
					} else {
						unhighlightEdges.add(row);
					}
				}
			}
			TableIterator nodes = nodeTable.iterator();
			while (nodes.hasNext()) {
				int row = nodes.nextInt();
				if (row != highlightNode.getRow()) {
					unhighlightNodes.add(row);
				}
			}
			// highlight nodes and edges
			// unhighlight nodes and edges
			VisualItem nodeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
					nodeTable.getTuple(highlightNode.getRow()));
			highlightItem(nodeItem);

			for (Integer i : unhighlightNodes) {
				nodeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
						nodeTable.getTuple(i));
				unhighlightItem(nodeItem);
			}

			for (Integer i : highlightEdges) {
				VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
						edgeTable.getTuple(i));
				highlightItem(edgeItem);
			}
			for (Integer i : unhighlightEdges) {
				VisualItem edgeItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
						edgeTable.getTuple(i));
				unhighlightItem(edgeItem);
			}
		}

	}

	public static void highlightItem(VisualItem item) {
		item.setFillColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);
		item.setStrokeColor(GraphConstants.HIGHLIGHT_STROKE_COLOR);
		item.getVisualization().repaint();
	}

	public static void unhighlightItem(VisualItem item) {
		item.setFillColor(GraphConstants.UNHIGHLIGHT_STROKE_COLOR);
		item.setStrokeColor(GraphConstants.UNHIGHLIGHT_STROKE_COLOR);
		item.getVisualization().repaint();
	}

	/*
	 * Reset color and stroke of graph
	 * 
	 */
	public static void resetColorAndStroke(GoalDrivenDFG goalDrivenDFG) {
		selectingAct = null;
		Table nodeTable = goalDrivenDFG.getGraph().getNodeTable();
		Table edgeTable = goalDrivenDFG.getGraph().getEdgeTable();
		// set select mode false
		GoalDrivenDFGUtils.isInSelectActMode = false;
		// reset label for edges
		goalDrivenDFG.getEdgeRenderer().setCustomizedFrequencyEdge(new HashMap<>());
		// reset all nodes
		TableIterator nodes = nodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (nodeTable.isValidRow(row)) {
				if (nodeTable.getBoolean(row, GraphConstants.IS_SELECTED)) {
					nodeTable.setBoolean(row, GraphConstants.IS_SELECTED, false);
				}
				VisualItem item = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP,
						nodeTable.getTuple(row));
				if (item.getBoolean(GraphConstants.BEGIN_FIELD) || item.getBoolean(GraphConstants.END_FIELD)) {
					item.setFillColor(GraphConstants.BEGIN_END_NODE_COLOR);
				} else {
					item.setFillColor(item.getInt(GraphConstants.NODE_FILL_COLOR_FIELD));
				}
				item.setStrokeColor(GraphConstants.NODE_STROKE_COLOR);
			}
		}
		//reset all edges 
		TableIterator edges = edgeTable.iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (edgeTable.isValidRow(row)) {
				VisualItem item = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.EDGE_GROUP,
						edgeTable.getTuple(row));
				item.setStrokeColor(item.getInt(GraphConstants.EDGE_FILL_COLOR_FIELD));
				item.setFillColor(item.getInt(GraphConstants.EDGE_FILL_COLOR_FIELD));
			}
		}
		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}
	/*----------------------------------------------------------------*/

	/*----------------------------------------------------------------*/
	/*
	 * Process graph based on the mode view: frequency, throughput, desirability
	 * priority: 1) Choose the map to compute for node and edge 2) Rerun the
	 * corresponding action for node fill color, edge stroke color, edge stroke
	 * width
	 */
	// mode frequency: display on node and edges the frequency of the acts and paths
	public static void displayModeFrequency(GoalDrivenDFG goalDrivenDFG) {
		// color fill node based on the frequency
		goalDrivenDFG.runCustomColorNodeFillAction(goalDrivenDFG.getCurrentFrequencyNode());
		// label node with frequency 
		// edge stroke color default
		goalDrivenDFG.setDefaultEdgeColor();
		// edge stroke width based on freq
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(goalDrivenDFG.getCurrentFrequencyEdge());
		// edge label to frequency
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (EdgeObject edgeObject : goalDrivenDFG.getCurrentFrequencyEdge().keySet()) {
			mapEdgeLabel.put(edgeObject, Integer.toString(goalDrivenDFG.getCurrentFrequencyEdge().get(edgeObject)));
		}
		goalDrivenDFG.getEdgeRenderer().setMapEdgeLabel(mapEdgeLabel);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();

	}

	// mode mean throughput: display on edge the mean throughput
	public static void displayModeMeanThroughput(GoalDrivenDFG goalDrivenDFG) {
		HashMap<EdgeObject, ThroughputTimeObject> mapThroughput = goalDrivenDFG.getCurrentThroughputEdge();
		HashMap<EdgeObject, Long> mapMeanThroughput = new HashMap<>();

		for (Map.Entry<EdgeObject, ThroughputTimeObject> entry : mapThroughput.entrySet()) {
			mapMeanThroughput.put(entry.getKey(), entry.getValue().getMean());
		}

		// color fill node default for time
		goalDrivenDFG.setNodeFillColorWith(GraphConstants.NODE_TIME_DEFAULT_COLOR);
		// edge stroke color
		goalDrivenDFG.runCustomEdgeColorAction(mapMeanThroughput);
		// edge stroke width
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(mapMeanThroughput);
		// edge label to mean throughput
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (EdgeObject edgeObject : mapThroughput.keySet()) {
			mapEdgeLabel.put(edgeObject, StatUtils.getDurationString(mapMeanThroughput.get(edgeObject)));
		}
		goalDrivenDFG.getEdgeRenderer().setMapEdgeLabel(mapEdgeLabel);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	// mode median throughput: display on edge the mean throughput
	public static void displayModeMedianThroughput(GoalDrivenDFG goalDrivenDFG) {
		HashMap<EdgeObject, ThroughputTimeObject> mapThroughput = goalDrivenDFG.getCurrentThroughputEdge();
		HashMap<EdgeObject, Long> mapMedianThroughput = new HashMap<>();

		for (Map.Entry<EdgeObject, ThroughputTimeObject> entry : mapThroughput.entrySet()) {
			mapMedianThroughput.put(entry.getKey(), entry.getValue().getMedian());
		}

		// color fill node default for time
		goalDrivenDFG.setNodeFillColorWith(GraphConstants.NODE_TIME_DEFAULT_COLOR);
		// edge stroke color
		goalDrivenDFG.runCustomEdgeColorAction(mapMedianThroughput);
		// edge stroke width
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(mapMedianThroughput);
		// edge label to mean throughput
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (EdgeObject edgeObject : mapThroughput.keySet()) {
			mapEdgeLabel.put(edgeObject, StatUtils.getDurationString(mapMedianThroughput.get(edgeObject)));
		}
		goalDrivenDFG.getEdgeRenderer().setMapEdgeLabel(mapEdgeLabel);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	// mode min throughput: display on edge the mean throughput
	public static void displayModeMinThroughput(GoalDrivenDFG goalDrivenDFG) {
		HashMap<EdgeObject, ThroughputTimeObject> mapThroughput = goalDrivenDFG.getCurrentThroughputEdge();
		HashMap<EdgeObject, Long> mapMinThroughput = new HashMap<>();

		for (Map.Entry<EdgeObject, ThroughputTimeObject> entry : mapThroughput.entrySet()) {
			mapMinThroughput.put(entry.getKey(), entry.getValue().getMin());
		}

		// color fill node default for time
		goalDrivenDFG.setNodeFillColorWith(GraphConstants.NODE_TIME_DEFAULT_COLOR);
		// edge stroke color
		goalDrivenDFG.runCustomEdgeColorAction(mapMinThroughput);
		// edge stroke width
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(mapMinThroughput);
		// edge label to mean throughput
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (EdgeObject edgeObject : mapThroughput.keySet()) {
			mapEdgeLabel.put(edgeObject, StatUtils.getDurationString(mapMinThroughput.get(edgeObject)));
		}
		goalDrivenDFG.getEdgeRenderer().setMapEdgeLabel(mapEdgeLabel);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	// mode max throughput: display on edge the mean throughput
	public static void displayModeMaxThroughput(GoalDrivenDFG goalDrivenDFG) {
		HashMap<EdgeObject, ThroughputTimeObject> mapThroughput = goalDrivenDFG.getCurrentThroughputEdge();
		HashMap<EdgeObject, Long> mapMaxThroughput = new HashMap<>();

		for (Map.Entry<EdgeObject, ThroughputTimeObject> entry : mapThroughput.entrySet()) {
			mapMaxThroughput.put(entry.getKey(), entry.getValue().getMax());
		}

		// color fill node default for time
		goalDrivenDFG.setNodeFillColorWith(GraphConstants.NODE_TIME_DEFAULT_COLOR);
		// edge stroke color
		goalDrivenDFG.runCustomEdgeColorAction(mapMaxThroughput);
		// edge stroke width
		goalDrivenDFG.runCustomEdgeStrokeWidthAction(mapMaxThroughput);
		// edge label to mean throughput
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (EdgeObject edgeObject : mapThroughput.keySet()) {
			mapEdgeLabel.put(edgeObject, StatUtils.getDurationString(mapMaxThroughput.get(edgeObject)));
		}
		goalDrivenDFG.getEdgeRenderer().setMapEdgeLabel(mapEdgeLabel);

		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	// display additional desire
	public static void displayDesirability(GoalDrivenDFG goalDrivenDFG, String[] highActs, String[] lowActs) {
		displayNoneAdditional(goalDrivenDFG);
		for (String act : highActs) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				VisualItem visualItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, node);
				visualItem.setStroke(GraphConstants.HIGHLIGHT_STROKE);
				visualItem.setStrokeColor(GraphConstants.NODE_HIGH_DESIRED_STROKE_COLOR);
				visualItem.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD,
						GraphConstants.NODE_HIGH_DESIRED_STROKE_COLOR);
			}
		}
		for (String act : lowActs) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				VisualItem visualItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, node);
				visualItem.setStroke(GraphConstants.HIGHLIGHT_STROKE);
				visualItem.setStrokeColor(GraphConstants.NODE_LOW_DESIRED_STROKE_COLOR);
				visualItem.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD, GraphConstants.NODE_LOW_DESIRED_STROKE_COLOR);
			}
		}
		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();

	}

	// display additional priority
	public static void displayPriority(GoalDrivenDFG goalDrivenDFG, String[] highActs, String[] lowActs) {
		displayNoneAdditional(goalDrivenDFG);
		for (String act : highActs) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				VisualItem visualItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, node);
				visualItem.setStroke(GraphConstants.HIGHLIGHT_STROKE);
				visualItem.setStrokeColor(GraphConstants.NODE_HIGH_PRIORITY_STROKE_COLOR);
				visualItem.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD,
						GraphConstants.NODE_HIGH_PRIORITY_STROKE_COLOR);
			}
		}
		for (String act : lowActs) {
			Node node = goalDrivenDFG.getNodeByLabelInGraph(goalDrivenDFG.getGraph(), act);
			if (node != null) {
				VisualItem visualItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, node);
				visualItem.setStroke(GraphConstants.HIGHLIGHT_STROKE);
				visualItem.setStrokeColor(GraphConstants.NODE_LOW_PRIORITY_STROKE_COLOR);
				visualItem.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD,
						GraphConstants.NODE_LOW_PRIORITY_STROKE_COLOR);
			}
		}
		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	// display additional none
	public static void displayNoneAdditional(GoalDrivenDFG goalDrivenDFG) {
		List<VisualItem> vList = new ArrayList<VisualItem>();
		TableIterator nodes = goalDrivenDFG.getGraph().getNodeTable().iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (goalDrivenDFG.getGraph().getNodeTable().isValidRow(row)) {
				Tuple node = goalDrivenDFG.getGraph().getNodeTable().getTuple(row);
				VisualItem visualItem = goalDrivenDFG.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, node);
				vList.add(visualItem);
			}
		}
		for (VisualItem visualItem : vList) {
			visualItem.setStroke(new BasicStroke(2));
			visualItem.setStrokeColor(GraphConstants.NODE_STROKE_COLOR);
			visualItem.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD, GraphConstants.NODE_STROKE_COLOR);
		}
		goalDrivenDFG.revalidate();
		goalDrivenDFG.repaint();
	}

	/*----------------------------------------------------------------*/

}
