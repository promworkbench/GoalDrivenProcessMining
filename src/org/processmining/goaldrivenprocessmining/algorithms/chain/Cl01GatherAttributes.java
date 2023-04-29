package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapEdgeStrokeWidth;
import org.processmining.goaldrivenprocessmining.objectHelper.MapNodeFillColor;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.utils.edge.GraphEdgeUtils;
import graph.utils.node.GraphNodeUtils;

public class Cl01GatherAttributes extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	@Override
	public String getName() {
		return "gather attributes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Gathering attributes..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.input_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.full_xlog, GoalDrivenObject.all_unique_values,
				GoalDrivenObject.selected_unique_values, GoalDrivenObject.unselected_unique_values,
				GoalDrivenObject.map_node_fill_color, GoalDrivenObject.map_edge_stroke_width };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		System.out.println("Cl01 - gather att");
		XLog log = inputs.get(IvMObject.input_log);
		AttributeClassifier classifier = new AttributeClassifier(
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0]);
		List<AttributeClassifier[]> valuesDistribution = this.getAllUniqueValues(log, classifier);
		
		FrequencyEdgeObject frequencyEdges = this.getFrequencyEdges(log, classifier.toString());
		FrequencyNodeObject frequencyNode = this.getFrequencyNodes(log, classifier.toString());
		MapNodeFillColor mapNodeFillColor = new MapNodeFillColor();
		mapNodeFillColor.setMapNodeFillColor(GraphNodeUtils.getMapFreqColor(frequencyNode));
		MapEdgeStrokeWidth mapEdgeStrokeWidth = new MapEdgeStrokeWidth();	
		mapEdgeStrokeWidth.setMapEdgeStrokeWidth(GraphEdgeUtils.getMapFreqStrokeWidth(frequencyEdges));
		return new IvMObjectValues().//
				s(GoalDrivenObject.full_xlog, log)
				.s(GoalDrivenObject.unselected_unique_values, valuesDistribution.get(1))
				.s(GoalDrivenObject.selected_unique_values, valuesDistribution.get(0)).// 
				s(GoalDrivenObject.all_unique_values, valuesDistribution.get(2)).
				s(GoalDrivenObject.map_node_fill_color, mapNodeFillColor).
				s(GoalDrivenObject.map_edge_stroke_width, mapEdgeStrokeWidth);
	}

	private List<AttributeClassifier[]> getAllUniqueValues(XLog log, AttributeClassifier classifier) {
		HashMap<String, Integer> mapActFreq = new HashMap<>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String value = event.getAttributes().get(classifier.toString()).toString();
				if (!mapActFreq.keySet().contains(value)) {
					mapActFreq.put(value, 1);
				} else {
					int curFreq = mapActFreq.get(value);
					mapActFreq.replace(value, curFreq + 1);
				}
			}
		}

		int maxFreq = 0;
		for (String key : mapActFreq.keySet()) {
			if (mapActFreq.get(key) >= maxFreq) {
				maxFreq = mapActFreq.get(key);
			}
		}
		int threshold = (int) (0.3 * maxFreq);
		List<String> l = new ArrayList<>();
		List<String> allL = new ArrayList<>();
		for (String key : mapActFreq.keySet()) {
			allL.add(key);
			if (mapActFreq.get(key) >= threshold) {
				l.add(key);
			}
		}
		AttributeClassifier[] arrAtt = new AttributeClassifier[allL.size()];
		for (int i = 0; i < allL.size(); i++) {
			arrAtt[i] = new AttributeClassifier(allL.get(i));
		}
		AttributeClassifier[] selectedArrAtt = new AttributeClassifier[l.size()];
		for (int i = 0; i < l.size(); i++) {
			selectedArrAtt[i] = new AttributeClassifier(l.get(i));
		}
		List<String> unselectedL = new ArrayList<>();
		for (String key : mapActFreq.keySet()) {
			if (!l.contains(key)) {
				unselectedL.add(key);
			}
		}
		AttributeClassifier[] unselectedArrAtt = new AttributeClassifier[unselectedL.size()];
		for (int i = 0; i < unselectedL.size(); i++) {
			unselectedArrAtt[i] = new AttributeClassifier(unselectedL.get(i));
		}
		List<AttributeClassifier[]> res = new ArrayList<>();
		res.add(0, selectedArrAtt);
		res.add(1, unselectedArrAtt);
		res.add(2, arrAtt);
		return res;
	}

	private FrequencyEdgeObject getFrequencyEdges(XLog log, String classifier) {
		FrequencyEdgeObject res = new FrequencyEdgeObject();
		List<EdgeObject> listEdges = new ArrayList<>();
		for (XTrace trace : log) {
			for (int i = 0; i < trace.size() - 1; i++) {
				XEvent ev1 = trace.get(i);
				XEvent ev2 = trace.get(i + 1);
				String val1 = ev1.getAttributes().get(classifier).toString();
				String val2 = ev2.getAttributes().get(classifier).toString();
				EdgeObject edge = new EdgeObject(val1, val2);
				if (!listEdges.contains(edge)) {
					listEdges.add(edge);
					res.getFrequencyEdge().put(edge, 1);
				} else {
					int curFreq = res.getFrequencyEdge().get(edge);
					res.getFrequencyEdge().replace(edge, curFreq + 1);
				}
				if (i == 0) {
					val1 = "begin";
					val2 = ev1.getAttributes().get(classifier).toString();
					edge = new EdgeObject(val1, val2);
					if (!listEdges.contains(edge)) {
						listEdges.add(edge);
						res.getFrequencyEdge().put(edge, 1);
					} else {
						int curFreq = res.getFrequencyEdge().get(edge);
						res.getFrequencyEdge().replace(edge, curFreq + 1);
					}
				}
				if (i == trace.size() - 2) {
					val1 = ev2.getAttributes().get(classifier).toString();
					val2 = "end";
					edge = new EdgeObject(val1, val2);
					if (!listEdges.contains(edge)) {
						listEdges.add(edge);
						res.getFrequencyEdge().put(edge, 1);
					} else {
						int curFreq = res.getFrequencyEdge().get(edge);
						res.getFrequencyEdge().replace(edge, curFreq + 1);
					}
				}

			}
		}
		return res;
	}

	private FrequencyNodeObject getFrequencyNodes(XLog log, String classifier) {
		FrequencyNodeObject res = new FrequencyNodeObject();
		List<String> listNodes = new ArrayList<>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				if (!listNodes.contains(event.getAttributes().get(classifier).toString())) {
					listNodes.add(event.getAttributes().get(classifier).toString());
					res.getFrequencyActivity().put(event.getAttributes().get(classifier).toString(), 1);
				} else {
					int curFreq = res.getFrequencyActivity().get(event.getAttributes().get(classifier).toString());
					res.getFrequencyActivity().replace(event.getAttributes().get(classifier).toString(), curFreq + 1);
				}
			}
		}
		return res;
	}

}
