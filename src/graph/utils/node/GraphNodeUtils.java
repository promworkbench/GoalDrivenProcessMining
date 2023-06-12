package graph.utils.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.visual.VisualItem;

public class GraphNodeUtils {

	public static List<VisualItem> getAllNodes(Visualization vis) {
		List<VisualItem> res = new ArrayList<>();
		Iterator<Node> nodeIter = ((Graph) vis.getGroup("graph")).nodes();
		while (nodeIter.hasNext()) {
			Node node = nodeIter.next();
			VisualItem visualItem = vis.getVisualItem("graph.nodes", node);
			res.add(visualItem);
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

}
