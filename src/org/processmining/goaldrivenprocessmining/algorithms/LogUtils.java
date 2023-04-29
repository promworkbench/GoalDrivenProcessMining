package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;

public class LogUtils {
	public static FrequencyEdgeObject getFrequencyEdges(XLog log, String classifier) {
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

	public static FrequencyNodeObject getFrequencyNodeObject(XLog log, String classifier) {
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
