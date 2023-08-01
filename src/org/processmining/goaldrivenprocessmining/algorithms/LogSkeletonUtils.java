package org.processmining.goaldrivenprocessmining.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EventSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.LogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper._GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class LogSkeletonUtils {

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\D\\data\\running-example.xes");

		// Create an input stream for the XES file
		InputStream is = new FileInputStream(file);

		// Create a parser for XES files
		XesXmlParser parser = new XesXmlParser();

		XLog log = parser.parse(is).get(0);
		//		Instant start = Instant.now();
		//		LogSkeleton logSkeleton = LogSkeletonUtils.getLogSkeleton(log);
		//		Instant end = Instant.now();
		//		System.out.println(Duration.between(start, end));
		//		start = Instant.now();
		// bpi
		//		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithGroup(logSkeleton, Arrays.asList("Record Goods Receipt",
		//				"Create Purchase Order Item", "Record Invoice Receipt", "Vendor creates invoice"), "g1_test");

		//		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithGroup(logSkeleton, Arrays.asList("check ticket", "decide"), "g1_test");
		//		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUnselection(logSkeleton, Arrays.asList("g1_test"));
		//		System.out.println(logSkeleton);
		//		end = Instant.now();
		//		System.out.println(Duration.between(start, end));

		GDPMLogSkeleton gdpmLog = new GDPMLogSkeleton(log);
		GDPMLogSkeleton newLog = LogSkeletonUtils.restrictLogFrom2Activities(gdpmLog, "register request",
				"check ticket");

		//		System.out.println(LogSkeletonUtils.getTracesFrom2ListPos(gdpmLog.getLogSkeleton(), 0, Arrays.asList(3, 4, 0),
		//				Arrays.asList(7, 2)));

		System.out.println(gdpmLog.getStatObject());

	}

	public static final String TIME_CLASSIFIER = "time:timestamp";

	public static String getLogClassifier(XLog log) {
		try {
			return log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		} catch (Exception e) {
			return "concept:name";
		}
	}

	public static GDPMLogSkeleton restrictLogFrom2Activities(GDPMLogSkeleton gdpmLog, String source, String target)
			throws Exception {
		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		ActivityHashTable activityHashTable = new ActivityHashTable();
		Map<Integer, List<Integer>> allPosSource = logSkeleton.getActivityHashTable().getActivityPositions(source);
		Map<Integer, List<Integer>> allPosTarget = logSkeleton.getActivityHashTable().getActivityPositions(target);

		Set<Integer> result = allPosSource.keySet().stream().distinct().filter(allPosTarget.keySet()::contains)
				.collect(Collectors.toSet());
		for (Integer i : result) {
			List<Integer> posSource = allPosSource.get(i);
			List<Integer> posTarget = allPosTarget.get(i);
			List<TraceSkeleton> traces = LogSkeletonUtils.getTracesFrom2ListPos(logSkeleton, i, posSource, posTarget);
			newGdpmLog.getLogSkeleton().getLog().addAll(traces);
		}

		// act hash table
		for (int traceNum = 0; traceNum < newGdpmLog.getLogSkeleton().getLog().size(); traceNum++) {
			for (int eventNum = 0; eventNum < newGdpmLog.getLogSkeleton().getLog().get(traceNum).getTrace()
					.size(); eventNum++) {
				activityHashTable.addActivity(
						newGdpmLog.getLogSkeleton().getLog().get(traceNum).getTrace().get(eventNum).getCurrentName(),
						traceNum, eventNum);
			}
		}
		newGdpmLog.getLogSkeleton().setActivityHashTable(activityHashTable);
		newGdpmLog.setStatObject(StatUtils.getStat(newGdpmLog.getLogSkeleton()));

		return newGdpmLog;
	}

	public static GDPMLogSkeleton removeActivitiesInLog(GDPMLogSkeleton logSkeleton, List<String> activities) {
		String[] activitiesArray = new String[activities.size()];
		for (int i = 0; i < activitiesArray.length; i++) {
			activitiesArray[i] = activities.get(i);
		}
		return LogSkeletonUtils.removeActivitiesInLog(logSkeleton, activitiesArray);
	}

	public static GDPMLogSkeleton removeActivitiesInLog(GDPMLogSkeleton gdpmLog, String[] activities) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		List<EdgeObject> listIndirectedEdges = new ArrayList<EdgeObject>();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUnselection(logSkeleton, Arrays.asList(activities));
		gdpmLog.setLogSkeleton(logSkeleton);

		for (String act : activities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
				listIndirectedEdges = LogSkeletonUtils.getListIndirectedEdgesFromMap(allPos, logSkeleton);
			} else if (logSkeleton.getGroupConfig().keySet().contains(act)) {
				for (String selectedAct : logSkeleton.getGroupConfig().get(act)) {
					Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable()
							.getActivityPositions(selectedAct);
					List<EdgeObject> listEdges = LogSkeletonUtils.getListIndirectedEdgesFromMap(allPos, logSkeleton);
					for (EdgeObject edge : listEdges) {
						if (!listIndirectedEdges.contains(edge)) {
							listIndirectedEdges.add(edge);
						}
					}
				}
			} else {
				throw new IllegalStateException("Invalid activity in log");
			}

		}

		for (EdgeObject edge : gdpmLog.getListIndirectedEdge()) {
			if (!Arrays.asList(activities).contains(edge.getNode1())
					&& !Arrays.asList(activities).contains(edge.getNode2())) {
				listIndirectedEdges.add(edge);
			}
		}
		gdpmLog.setListIndirectedEdge(listIndirectedEdges);
		return gdpmLog;
	}

	public static GDPMLogSkeleton addActivitiesInLog(GDPMLogSkeleton gdpmLog, String[] activities) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithSelection(logSkeleton, Arrays.asList(activities));
		gdpmLog.setLogSkeleton(logSkeleton);

		// change list indirected edge
		gdpmLog.setListIndirectedEdge(LogSkeletonUtils.getListIndirectedEdgesFromLogSkeleton(logSkeleton));
		return gdpmLog;
	}

	public static GDPMLogSkeleton replaceSetActivitiesInLog(GDPMLogSkeleton gdpmLog, List<String> activities,
			String groupName) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithGroup(logSkeleton, activities, groupName);
		gdpmLog.setLogSkeleton(logSkeleton);

		// change list indirected edge
		List<EdgeObject> listIndirectedEdges = new ArrayList<>();
		for (EdgeObject edge : gdpmLog.getListIndirectedEdge()) {
			String node1 = activities.contains(edge.getNode1()) ? groupName : edge.getNode1();
			String node2 = activities.contains(edge.getNode2()) ? groupName : edge.getNode2();
			listIndirectedEdges.add(new EdgeObject(node1, node2));
		}
		gdpmLog.setListIndirectedEdge(listIndirectedEdges);
		return gdpmLog;
	}

	public static LogSkeleton changeLogSkeletonWithGroup(LogSkeleton logSkeleton, List<String> listGroupActivities,
			String groupName) {

		logSkeleton.addGroup(groupName, listGroupActivities);

		List<String> affectedActs = logSkeleton.getGroupConfig().get(groupName);
		for (String act : affectedActs) {
			Map<Integer, List<Integer>> allPositions = logSkeleton.getActivityHashTable().getActivityPositions(act);
			for (Map.Entry<Integer, List<Integer>> entry : allPositions.entrySet()) {
				int traceNum = entry.getKey();
				List<Integer> positions = entry.getValue();
				for (Integer position : positions) {
					logSkeleton.getLog().get(traceNum).getTrace().get(position).setCurrentName(groupName);
				}
			}
		}

		return logSkeleton;

	}

	public static LogSkeleton changeLogSkeletonWithUnselection(LogSkeleton logSkeleton,
			List<String> unselectedActivities) {
		for (String act : unselectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
				for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
					for (Integer i : entry.getValue()) {
						logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(false);
					}

				}
			} else if (logSkeleton.getGroupConfig().keySet().contains(act)) {
				for (String selectedAct : logSkeleton.getGroupConfig().get(act)) {
					Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable()
							.getActivityPositions(selectedAct);
					for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
						for (Integer i : entry.getValue()) {
							logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(false);
						}
					}
				}
			} else {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		// update indirected 
		return logSkeleton;
	}

	public static LogSkeleton changeLogSkeletonWithSelection(LogSkeleton logSkeleton, List<String> selectedActivities) {
		for (String act : selectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
				for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
					for (Integer i : entry.getValue()) {
						logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(true);
					}
				}
			} else if (logSkeleton.getGroupConfig().keySet().contains(act)) {
				for (String selectedAct : logSkeleton.getGroupConfig().get(act)) {
					Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable()
							.getActivityPositions(selectedAct);
					for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
						for (Integer i : entry.getValue()) {
							logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(true);
						}
					}
				}
			} else {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		return logSkeleton;
	}

	public static LogSkeleton getLogSkeleton(XLog log) {
		LogSkeleton logSkeleton = new LogSkeleton();
		ActivityHashTable activityHashTable = new ActivityHashTable();
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		String classifier = LogSkeletonUtils.getLogClassifier(log);

		int traceNum = 0;
		for (XTrace trace : log) {
			TraceSkeleton traceSkeleton = new TraceSkeleton();
			for (int i = 0; i < trace.size(); i++) {
				XEvent event = trace.get(i);
				String act = event.getAttributes().get(classifier).toString();
				String time = event.getAttributes().get(TIME_CLASSIFIER).toString();
				EventSkeleton eventSkeleton = new EventSkeleton(act, act, time, true);
				traceSkeleton.getTrace().add(eventSkeleton);
				//act hash table
				activityHashTable.addActivity(act, traceNum, i);

				//edge hash table
				if (i == 0) {
					EdgeObject edgeObject = new EdgeObject("begin", act);
					edgeHashTable.addEdge(edgeObject, traceNum, -1, i);
				}
				if (i == trace.size() - 1) {
					EdgeObject edgeObject = new EdgeObject(act, "end");
					edgeHashTable.addEdge(edgeObject, traceNum, i, -2);
				} else {
					XEvent nextEvent = trace.get(i + 1);
					EdgeObject edgeObject = new EdgeObject(act, nextEvent.getAttributes().get(classifier).toString());
					edgeHashTable.addEdge(edgeObject, traceNum, i, i + 1);
				}
			}
			traceNum++;
			logSkeleton.getLog().add(traceSkeleton);
		}
		logSkeleton.setActivityHashTable(activityHashTable);
		logSkeleton.setEdgeHashTable(edgeHashTable);
		return logSkeleton;
	}

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

	public static List<EdgeObject> getListIndirectedEdgesFromMap(Map<Integer, List<Integer>> allPos,
			LogSkeleton logSkeleton) {
		List<EdgeObject> listIndirectedEdges = new ArrayList<EdgeObject>();
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			TraceSkeleton trace = logSkeleton.getLog().get(entry.getKey());
			LogSkeletonUtils.addIndirectedEdgesFromTrace(trace, listIndirectedEdges);
		}
		return listIndirectedEdges;
	}

	public static List<EdgeObject> getListIndirectedEdgesFromLogSkeleton(LogSkeleton logSkeleton) {
		List<EdgeObject> listIndirectedEdges = new ArrayList<EdgeObject>();

		for (TraceSkeleton trace : logSkeleton.getLog()) {
			LogSkeletonUtils.addIndirectedEdgesFromTrace(trace, listIndirectedEdges);
		}

		return listIndirectedEdges;
	}

	public static List<TraceSkeleton> getTracesFrom2ListPos(LogSkeleton logSkeleton, int traceNum,
			List<Integer> posSource, List<Integer> posTarget) {
		List<TraceSkeleton> traces = new ArrayList<>();
		TraceSkeleton trace = logSkeleton.getLog().get(traceNum);
		Collections.sort(posSource);
		Collections.sort(posTarget);
		for (int i = 0; i < posSource.size(); i++) {
			int startIndex = posSource.get(i);
			int endIndex = -1;
			if (i + 1 < posSource.size()) {
				int nextStartIndex = posSource.get(i + 1);

				for (Integer j : posTarget) {
					if (j > startIndex && j < nextStartIndex) {
						endIndex = j;
						break;
					}
				}
			} else {
				for (Integer j : posTarget) {
					if (j > startIndex) {
						endIndex = j;
						break;
					}
				}
			}
			if (endIndex != -1) {
				TraceSkeleton addTrace = new TraceSkeleton();
				for (int k = startIndex; k <= endIndex; k++) {
					addTrace.getTrace().add(trace.getTrace().get(k));
				}
				traces.add(addTrace);
			}
		}

		return traces;
	}

	public static void addIndirectedEdgesFromTrace(TraceSkeleton trace, List<EdgeObject> listIndirectedEdges) {
		String node1 = "begin";
		String node2 = "";
		Boolean isFound = false;
		for (int i = 0; i < trace.getTrace().size(); i++) {
			EventSkeleton event = trace.getTrace().get(i);
			if (event.getIsDisplayed()) {
				if (isFound) {
					node2 = event.getCurrentName();
					if (!listIndirectedEdges.contains(new EdgeObject(node1, node2))) {
						listIndirectedEdges.add(new EdgeObject(node1, node2));
					}
					isFound = false;
				}
				node1 = event.getCurrentName();
			} else {
				isFound = true;
			}

		}
		if (isFound) {
			if (!listIndirectedEdges.contains(new EdgeObject(node1, "end"))) {
				listIndirectedEdges.add(new EdgeObject(node1, "end"));
			}
		}
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
