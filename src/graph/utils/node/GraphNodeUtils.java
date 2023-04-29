package graph.utils.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.visual.VisualItem;

public class GraphNodeUtils {

	public static List<VisualItem> getAllNodes(Visualization vis, Table table) {
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

	public static HashMap<String, Integer> getNodeFillColor(FrequencyNodeObject frequencyNodes,
			HashMap<Integer[], Integer> mapFreqColor) {
		HashMap<String, Integer> res = new HashMap<>();
		HashMap<String, Integer> frequencyActs = frequencyNodes.getFrequencyActivity();
		for (String act : frequencyActs.keySet()) {
			for (Integer[] range : mapFreqColor.keySet()) {
				if (frequencyActs.get(act) >= range[0] && frequencyActs.get(act) <= range[1]) {
					res.put(act, mapFreqColor.get(range));
					break;
				}
			}
		}
		return res;
	}

	public static HashMap<Integer[], Integer> getMapFreqColor(FrequencyNodeObject frequencyNodes) {
		HashMap<Integer[], Integer> res = new HashMap<>();
		HashMap<String, Integer> frequencyActivity = frequencyNodes.getFrequencyActivity();
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (String act : frequencyActivity.keySet()) {
			if (frequencyActivity.get(act) >= maxFreq) {
				maxFreq = frequencyActivity.get(act);
			}
			if (frequencyActivity.get(act) <= minFreq) {
				minFreq = frequencyActivity.get(act);
			}
		}

		if (maxFreq == minFreq) {
			res.put(new Integer[] { minFreq, maxFreq }, 4);
		} else {
			int distance = maxFreq - minFreq;
			int numNode = distance / 5;
			if (numNode <= 1) {
				res.put(new Integer[] { minFreq, minFreq }, 0);
				res.put(new Integer[] { minFreq, maxFreq }, 4);
			} else {
				int cur = minFreq;
				for (int i = 0; i < 5; i++) {
					int lower = cur;
					int upper = lower + numNode - 1;
					if (i == 4) {
						upper = maxFreq;
					}
					cur = upper + 1;
					res.put(new Integer[] { lower, upper }, i);
				}
			}
		}
		return res;
	}

}
