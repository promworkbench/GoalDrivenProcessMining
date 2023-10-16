package graph.utils.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

import graph.GoalDrivenDFG;
import graph.GraphConstants;
import graph.controls.DragMultipleNodesControl;
import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.visual.VisualItem;

public class GraphNodeUtils {

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

	public static VisualItem getVisualItemByLabel(Visualization vis, String label) {
		for (VisualItem visualItem : getAllNodes(vis)) {
			if (visualItem.getString(GraphConstants.LABEL_FIELD).equals(label)) {
				return visualItem;
			}
		}
		return null;
	}

	public static void addNewEdgesWithOriginalName(GoalDrivenDFG goalDrivenDFG, List<EdgeObject> listEdges) {
		Graph g = goalDrivenDFG.getGraph();
		for (EdgeObject edge : listEdges) {
			String source = edge.getNode1().getOriginalName();
			String target = edge.getNode2().getOriginalName();
			Node node1;
			Node node2;
			if (source.equals("begin")) {
				node1 = g.getNode(goalDrivenDFG.getBeginNodeRow());
			} else if (source.equals("end")) {
				node1 = g.getNode(goalDrivenDFG.getEndNodeRow());
			} else {
				node1 = goalDrivenDFG.getNodeByLabel(g, source);
			}
			if (target.equals("begin")) {
				node2 = g.getNode(goalDrivenDFG.getBeginNodeRow());
			} else if (target.equals("end")) {
				node2 = g.getNode(goalDrivenDFG.getEndNodeRow());
			} else {
				node2 = goalDrivenDFG.getNodeByLabel(g, target);
			}
			Edge e = g.addEdge(node1, node2);
			goalDrivenDFG.configEdge(e, edge);
		}
	}

	public static void addNewEdgesWithCurrentName(GoalDrivenDFG goalDrivenDFG, List<EdgeObject> listEdges) {
		Graph g = goalDrivenDFG.getGraph();
		for (EdgeObject edge : listEdges) {
			String source = edge.getNode1().getCurrentName();
			String target = edge.getNode2().getCurrentName();
			Node node1;
			Node node2;
			if (source.equals("begin")) {
				node1 = g.getNode(goalDrivenDFG.getBeginNodeRow());
			} else if (source.equals("end")) {
				node1 = g.getNode(goalDrivenDFG.getEndNodeRow());
			} else {
				node1 = goalDrivenDFG.getNodeByLabel(g, source);
			}
			if (target.equals("begin")) {
				node2 = g.getNode(goalDrivenDFG.getBeginNodeRow());
			} else if (target.equals("end")) {
				node2 = g.getNode(goalDrivenDFG.getEndNodeRow());
			} else {
				node2 = goalDrivenDFG.getNodeByLabel(g, target);
			}
			Edge e = g.addEdge(node1, node2);
			goalDrivenDFG.configEdge(e, edge);
		}
	}

	public static void addNewGroupNodes(GoalDrivenDFG goalDrivenDFG, List<String> newLabels) {
		for (String newLabel : newLabels) {
			Node node1 = null;
			node1 = goalDrivenDFG.getGraph().addNode();
			goalDrivenDFG.configNode(node1, newLabel, true);
		}
	}

	public static void addNewActNodes(GoalDrivenDFG goalDrivenDFG, List<String> newLabels) {
		for (String newLabel : newLabels) {
			Node node1 = null;
			node1 = goalDrivenDFG.getGraph().addNode();
			goalDrivenDFG.configNode(node1, newLabel, false);
		}
	}

	public static void removeNodeByLabel(GoalDrivenDFG goalDrivenDFG, String label) {
		Graph graph = goalDrivenDFG.getGraph();
		// Get the node edge table from the graph
		Table nodeTable = graph.getNodeTable();
		Table edgeTable = graph.getEdgeTable();

		// Find the node with the specified label
		List<Node> listNodeToRemove = new ArrayList<>();
		for (int i = 0; i < nodeTable.getMaximumRow(); i++) {
			if (nodeTable.isValidRow(i)) {
				Node node = graph.getNode(i);
				if (node.getString(GraphConstants.LABEL_FIELD).equals(label)) {
					listNodeToRemove.add(node);
				}
			}
		}
		// If the node is found, remove it along with its associated edges
		for (Node nodeToRemove : listNodeToRemove) {
			// Remove associated edges
			int nodeIdToRemove = nodeToRemove.getRow();
			for (int i = 0; i < edgeTable.getMaximumRow(); i++) {
				if (edgeTable.isValidRow(i)) {
					int sourceId = graph.getEdge(i).getSourceNode().getRow();
					int targetId = graph.getEdge(i).getTargetNode().getRow();
					if (sourceId == nodeIdToRemove || targetId == nodeIdToRemove) {
						// Remove the edge from the edge table
						edgeTable.removeRow(i);
					}
				}
			}
			// Remove node from map invi node
			VisualItem visualItem = GraphNodeUtils.getVisualItemByLabel(goalDrivenDFG.getVisualization(), label);
			if (DragMultipleNodesControl.mapInvisibleNodes.containsKey(visualItem)
					|| DragMultipleNodesControl.mapInvisibleNodes.containsValue(visualItem)) {
				DragMultipleNodesControl.mapInvisibleNodes.remove(visualItem);
			}
			if (DragMultipleNodesControl.mapAffectedNodes.containsKey(visualItem)) {
				DragMultipleNodesControl.mapAffectedNodes.remove(visualItem);
			}
			for (VisualItem vItem : DragMultipleNodesControl.mapAffectedNodes.keySet()) {
				if (DragMultipleNodesControl.mapAffectedNodes.get(vItem).contains(visualItem)) {
					DragMultipleNodesControl.mapAffectedNodes.remove(vItem);
				}
			}

			// Remove the node from the node table
			nodeTable.removeRow(nodeIdToRemove);
		}
	}

}
