package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class LogUtils {

	public static GDPMLog setUpMapNodeType(GDPMLog gdpmLog, List<String> listGroupActivities) {
		Map<String, NodeType> mapNodeType = new HashMap<>();
		XLog log = gdpmLog.getLog();
		List<String> allActivities = getAllUniqueActivities(log);
		if (!listGroupActivities.isEmpty()) {
			for (String act : allActivities) {
				if (listGroupActivities.contains(act)) {
					mapNodeType.put(act, NodeType.GROUP_NODE);
				} else {
					mapNodeType.put(act, NodeType.ACT_NODE);
				}
			}
		} else {
			for (String act : allActivities) {
				mapNodeType.put(act, NodeType.ACT_NODE);
			}
		}
		if (gdpmLog.getMapNodeType() == null) {
			gdpmLog.setMapNodeType(mapNodeType);
		} else {
			for (String key : mapNodeType.keySet()) {
				if (mapNodeType.keySet().contains(key)) {
					gdpmLog.getMapNodeType().replace(key, mapNodeType.get(key));
				} else {
					gdpmLog.getMapNodeType().put(key, mapNodeType.get(key));
				}
			}
		}

		return gdpmLog;
	}

	public static GDPMLog replaceSetActivitiesInLog(XLog log, List<String> activities,
			IndirectedEdgeCarrierObject indirectedEdgesObject, String groupName) {
		List<EdgeObject> indirectedEdges = indirectedEdgesObject.getListIndirectedEdge();
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		newLog.getClassifiers().addAll(log.getClassifiers());
		XEvent groupNodeEvent = new XEventImpl();
		groupNodeEvent.getAttributes().put(classifier, new XAttributeLiteralImpl(classifier, groupName));
		for (XTrace tr : log) {
			XTraceImpl newTr = new XTraceImpl(tr.getAttributes());
			for (int i = 0; i < tr.size(); i++) {
				XEvent ev = tr.get(i);
				String evValue = ev.getAttributes().get(classifier).toString();
				if (activities.contains(evValue)) {
					newTr.add(groupNodeEvent);
					if (i != 0) {
						XEvent evPrev = tr.get(i - 1);
						String valuePrev = evPrev.getAttributes().get(classifier).toString();
						if (indirectedEdges.contains(new EdgeObject(valuePrev, evValue))) {
							indirectedEdges.add(new EdgeObject(valuePrev, groupName));
						}
					}
				} else {
					newTr.add(ev);
				}
			}
			if (!newTr.isEmpty()) {
				newLog.add(newTr);
			}
		}
		indirectedEdgesObject.setListIndirectedEdge(indirectedEdges);
		return new GDPMLog(newLog, indirectedEdgesObject);
	}

	public static GDPMLog projectLogOnSetActivities(XLog log, List<String> activities) {
		String[] activitiesArray = new String[activities.size()];
		for (int i = 0; i < activitiesArray.length; i++) {
			activitiesArray[i] = activities.get(i);
		}
		return projectLogOnSetActivities(log, activitiesArray);
	}

	public static GDPMLog projectLogOnSetActivities(XLog log, String[] activities) {
		String selectedAttribute = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		newLog.getClassifiers().addAll(log.getClassifiers());
		IndirectedEdgeCarrierObject indirectedEdges = new IndirectedEdgeCarrierObject();
		for (XTrace tr : log) {
			XTraceImpl newTr = new XTraceImpl(tr.getAttributes());
			for (int i = 0; i < tr.size(); i++) {
				XEvent ev = tr.get(i);
				String value = ev.getAttributes().get(selectedAttribute).toString();

				if (Arrays.asList(activities).contains(value)) {
					if (i != 0) {
						XEvent evPrev = tr.get(i - 1);
						String valuePrev = evPrev.getAttributes().get(selectedAttribute).toString();
						if (!Arrays.asList(activities).contains(valuePrev)) {
							if (newTr.size() == 0) {
								EdgeObject obj = new EdgeObject("begin", value);
								if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
									indirectedEdges.addEdge(obj);
								}

							} else {
								EdgeObject obj = new EdgeObject(
										newTr.get(newTr.size() - 1).getAttributes().get(selectedAttribute).toString(),
										value);
								if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
									indirectedEdges.addEdge(obj);
								}
							}
						}
					}

					newTr.add(ev);
				} else {
					if (i == tr.size() - 1) {
						if (!newTr.isEmpty()) {
							EdgeObject obj = new EdgeObject(
									newTr.get(newTr.size() - 1).getAttributes().get(selectedAttribute).toString(),
									"end");
							if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
								indirectedEdges.addEdge(obj);
							}
						}
					}
				}
			}
			if (!newTr.isEmpty()) {
				newLog.add(newTr);
			}
		}
		return new GDPMLog(newLog, indirectedEdges);
	}

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

	public static List<String> getAllUniqueActivities(XLog log) {
		List<String> res = new ArrayList<>();
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String value = event.getAttributes().get(classifier).toString();
				if (!res.contains(value)) {
					res.add(value);
				}
			}
		}
		return res;
	}
}
