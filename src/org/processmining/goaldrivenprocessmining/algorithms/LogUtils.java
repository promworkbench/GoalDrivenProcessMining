package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class LogUtils {

	//	public static void main(String[] args) throws Exception {
	//		File file = new File("C:\\D\\data\\ma_test.xes");
	//
	//		// Create an input stream for the XES file
	//		InputStream is = new FileInputStream(file);
	//
	//		// Create a parser for XES files
	//		XesXmlParser parser = new XesXmlParser();
	//
	//		XLog log = parser.parse(is).get(0);
	//		ActivityHashTable aht = LogUtils.getActivityHashTable(log);
	//		GDPMLog l = LogUtils.removeActivitiesInLog(log, aht, Arrays.asList("a", "e"));
	//		System.out.println("done");
	//	}

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
				if (gdpmLog.getMapNodeType().keySet().contains(key)) {
					gdpmLog.getMapNodeType().replace(key, mapNodeType.get(key));
				} else {
					gdpmLog.getMapNodeType().put(key, mapNodeType.get(key));
				}
			}
		}

		return gdpmLog;
	}

	public static XLog replaceSetActivitiesInLog(XLog log, ActivityHashTable activityHashTable, List<String> activities,
			String groupName) {
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		XLog newLog = (XLog) log.clone();
		for (String act : activities) {
			Map<Integer, List<Integer>> positions = activityHashTable.getActivityPositions(act);
			for (Integer i : positions.keySet()) {
				XTrace trace = newLog.get(i);
				for (Integer j : positions.get(i)) {
					XEvent event = trace.get(j);
					XAttributeMap attributes = event.getAttributes();
					attributes.replace(classifier, new XAttributeLiteralImpl(classifier, groupName));
					event.setAttributes(attributes);
				}
			}
		}

		return newLog;
	}

	public static GDPMLog removeActivitiesInLog(XLog log, ActivityHashTable activityHashTable,
			List<String> activities) {
		String[] activitiesArray = new String[activities.size()];
		for (int i = 0; i < activitiesArray.length; i++) {
			activitiesArray[i] = activities.get(i);
		}
		return removeActivitiesInLog(log, activityHashTable, activitiesArray);
	}

	public static GDPMLog removeActivitiesInLog(XLog log, ActivityHashTable activityHashTable, String[] activities) {
		XLog newLog = (XLog) log.clone();
		IndirectedEdgeCarrierObject indirectedEdges = new IndirectedEdgeCarrierObject();
		HashMap<Integer, List<Integer>> removeActInCaseMap = new HashMap<Integer, List<Integer>>();
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		for (String act : activities) {
			Map<Integer, List<Integer>> allPosMap = activityHashTable.getActivityPositions(act);
			for (Integer i : allPosMap.keySet()) {
				for (int j = 0; j < allPosMap.get(i).size(); j++) {
					if (removeActInCaseMap.keySet().contains(i)) {
						List<Integer> removeActs = removeActInCaseMap.get(i);
						removeActs.add(allPosMap.get(i).get(j));
						removeActInCaseMap.replace(i, removeActs);
					} else {
						List<Integer> removeActs = new ArrayList<>();
						removeActs.add(allPosMap.get(i).get(j));
						removeActInCaseMap.put(i, removeActs);
					}
				}
			}
		}
		// record indirected edges
		for (Integer index : removeActInCaseMap.keySet()) {
			XTrace trace = log.get(index);
			List<Integer> removeActs = removeActInCaseMap.get(index);
			Collections.sort(removeActs);
			if (trace.size() > removeActs.size()) {
				List<List<Integer>> listConsecutiveNumber = LogUtils.groupConsecutiveNumbers(removeActs);
				for (List<Integer> list : listConsecutiveNumber) {
					int start = list.get(0);
					int end = list.get(list.size() - 1);
					String node1 = "";
					String node2 = "";
					if (start != end) {
						if (start == 0) {
							node1 = "begin";
						} else {
							node1 = trace.get(start - 1).getAttributes().get(classifier).toString();
						}
						if (end == trace.size() - 1) {
							node2 = "end";
						} else {
							node2 = trace.get(end + 1).getAttributes().get(classifier).toString();
						}
					} else {
						node1 = trace.get(start - 1).getAttributes().get(classifier).toString();
						node2 = trace.get(start + 1).getAttributes().get(classifier).toString();
					}
					indirectedEdges.addEdge(new EdgeObject(node1, node2));
				}
			}

		}

		// remove act from traces
		for (Integer index : removeActInCaseMap.keySet()) {
			List<XEvent> toRemove = new ArrayList<>();
			for (Integer i : removeActInCaseMap.get(index)) {
				toRemove.add(newLog.get(index).get(i));
			}
			newLog.get(index).removeAll(toRemove);
		}
		return new GDPMLog(newLog, indirectedEdges);
	}
	
	public static List<List<Integer>> groupConsecutiveNumbers(List<Integer> numbers) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentGroup = new ArrayList<>();

        for (int i = 0; i < numbers.size(); i++) {
            if (i == 0 || numbers.get(i) - numbers.get(i - 1) != 1) {
                if (!currentGroup.isEmpty()) {
                    result.add(currentGroup);
                    currentGroup = new ArrayList<>();
                }
            }
            currentGroup.add(numbers.get(i));
        }

        if (!currentGroup.isEmpty()) {
            result.add(currentGroup);
        }

        return result;
    }

	public static FrequencyEdgeObject getFrequencyEdges(XLog log, String classifier) {
		FrequencyEdgeObject res = new FrequencyEdgeObject();
		List<EdgeObject> listEdges = new ArrayList<>();
		for (XTrace trace : log) {
			for (int i = 0; i < trace.size(); i++) {
				XEvent ev1 = trace.get(i);
				String val1 = ev1.getAttributes().get(classifier).toString();
				if (i + 1 < trace.size()) {
					XEvent ev2 = trace.get(i + 1);
					String val2 = ev2.getAttributes().get(classifier).toString();
					EdgeObject edge = new EdgeObject(val1, val2);
					if (!listEdges.contains(edge)) {
						listEdges.add(edge);
						res.getFrequencyEdge().put(edge, 1);
					} else {
						int curFreq = res.getFrequencyEdge().get(edge);
						res.getFrequencyEdge().replace(edge, curFreq + 1);
					}

				}
				if (i == 0) {
					val1 = "begin";
					String val2 = ev1.getAttributes().get(classifier).toString();
					EdgeObject edge = new EdgeObject(val1, val2);
					if (!listEdges.contains(edge)) {
						listEdges.add(edge);
						res.getFrequencyEdge().put(edge, 1);
					} else {
						int curFreq = res.getFrequencyEdge().get(edge);
						res.getFrequencyEdge().replace(edge, curFreq + 1);
					}
				}
				if (i == trace.size() - 1) {
					val1 = ev1.getAttributes().get(classifier).toString();
					String val2 = "end";
					EdgeObject edge = new EdgeObject(val1, val2);
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
