package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper._GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class LogSkeletonUtils {

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

	public static _GDPMLog setUpMapNodeType(_GDPMLog gdpmLog, List<String> listGroupActivities) {
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

	public static GDPMLogSkeleton restrictLogFrom2Activities(GDPMLogSkeleton logSkeleton, String source, String target,
			String[] activities) throws Exception {

		int sourceIndex = logSkeleton.getActivityIndexMapper().getIndexFromActivity(source);
		int targetIndex = logSkeleton.getActivityIndexMapper().getIndexFromActivity(target);

		HashMap<Integer, List<Integer>> newLogSkeleton = new HashMap<Integer, List<Integer>>();

		for (Map.Entry<Integer, List<Integer>> entry : logSkeleton.getLogSkeleton().entrySet()) {
			int newIndex = 0;
			List<Integer> events = entry.getValue();
			List<Integer> newEvents = new ArrayList<Integer>();
			if (events.contains(sourceIndex) && events.contains(targetIndex)) {
				Boolean sourceFound = false;
				for (Integer value : events) {
					if (!sourceFound) {
						if (value == sourceIndex) {
							sourceFound = true;
							newEvents.add(value);
						}
					} else {
						if (value == targetIndex) {
							newEvents.add(value);
							newLogSkeleton.put(newIndex, newEvents);
							newIndex++;
							newEvents = new ArrayList<Integer>();
							if (value == sourceIndex) {
								sourceFound = true;
								newEvents.add(value);
							} else {
								sourceFound = false;
							}
						} else {
							if (Arrays.asList(activities)
									.contains(logSkeleton.getActivityIndexMapper().getActivityFromIndex(value))) {
								newEvents.add(value);
							}

						}

					}
				}
			}
		}
		// update log skeleton
		logSkeleton.setLogSkeleton(newLogSkeleton);
		// 
		
		
		return logSkeleton;
	}

	public static GDPMLogSkeleton replaceSetActivitiesInLog(GDPMLogSkeleton logSkeleton, List<String> activities,
			String groupName) {

		ActivityHashTable activityHashTable = logSkeleton.getActivityHashTable();
		// update map act index
		logSkeleton.getActivityIndexMapper().assignActivity(Arrays.asList(groupName));
		// replace in log skeleton
		for (String act : activities) {
			Map<Integer, List<Integer>> positions = activityHashTable.getActivityPositions(act);
			for (Integer key : positions.keySet()) {
				for (Integer value : positions.get(key)) {
					try {
						logSkeleton.getLogSkeleton().get(key).set(value,
								logSkeleton.getActivityIndexMapper().getIndexFromActivity(groupName));
					} catch (Exception e) {
						throw new RuntimeException("Can not replace act name");
					}
				}
			}
			// update hash table
			logSkeleton.getActivityHashTable().getActivityTable().put(groupName, positions);
			logSkeleton.getActivityHashTable().getActivityTable().remove(act);
		}

		return logSkeleton;
	}

	public static GDPMLogSkeleton removeActivitiesInLog(GDPMLogSkeleton logSkeleton, String[] activities) {
		ActivityHashTable newActivityHashTable = (ActivityHashTable) logSkeleton.getActivityHashTable().clone();
		IndirectedEdgeCarrierObject indirectedEdges = new IndirectedEdgeCarrierObject();
		HashMap<Integer, List<Integer>> removeActInCaseMap = new HashMap<Integer, List<Integer>>();

		for (String act : activities) {
			Map<Integer, List<Integer>> allPosMap = newActivityHashTable.getActivityPositions(act);
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
			int traceLength = logSkeleton.getMapTraceLength().get(index);
			List<Integer> removeActs = removeActInCaseMap.get(index);
			Collections.sort(removeActs);
			if (traceLength > removeActs.size()) {
				List<List<Integer>> listConsecutiveNumber = LogSkeletonUtils.groupConsecutiveNumbers(removeActs);
				for (List<Integer> list : listConsecutiveNumber) {
					int start = list.get(0);
					int end = list.get(list.size() - 1);
					String node1 = "";
					String node2 = "";
					if (start == 0) {
						node1 = "begin";
					} else {
						node1 = logSkeleton.getActNameAtPosition(index, start - 1);
					}
					if (end == traceLength - 1) {
						node2 = "end";
					} else {
						node2 = logSkeleton.getActNameAtPosition(index, end + 1);
					}
					indirectedEdges.addEdge(new EdgeObject(node1, node2));
				}
			}

		}
		logSkeleton.setListIndirectedEdge(indirectedEdges.getListIndirectedEdge());

		// remove act from traces
		for (Integer posTrace : removeActInCaseMap.keySet()) {
			for (int i = 0; i < removeActInCaseMap.get(posTrace).size(); i++) {
				int posEvent = removeActInCaseMap.get(posTrace).get(i);
				logSkeleton.getLogSkeleton().get(posTrace).remove(posEvent - i);
				logSkeleton.getTimeSkeleton().get(posTrace).remove(posEvent - i);

			}
		}
		return logSkeleton;
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
