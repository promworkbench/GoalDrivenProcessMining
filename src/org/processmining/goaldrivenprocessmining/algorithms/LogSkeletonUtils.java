package org.processmining.goaldrivenprocessmining.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivitySkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EventSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
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
		//		GDPMLogSkeleton newLog = LogSkeletonUtils.restrictLogFrom2Activities(gdpmLog, "register request",
		//				"check ticket");
		gdpmLog = LogSkeletonUtils.removeActivitiesInLog(gdpmLog, Arrays.asList("register request", "check ticket"));
		GDPMLogSkeleton newLog = LogSkeletonUtils.getDisplayedLogSkeleton(gdpmLog);

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

	public static GDPMLogSkeleton getDisplayedLogSkeleton(GDPMLogSkeleton gdpmLog) {
		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		LogSkeleton newLogSkeleton = new LogSkeleton();
		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();

		for (TraceSkeleton traceSkeleton : logSkeleton.getLog()) {
			TraceSkeleton newTraceSkeleton = new TraceSkeleton();
			for (EventSkeleton eventSkeleton : traceSkeleton.getTrace()) {
				if (eventSkeleton.getIsDisplayed()) {
					newTraceSkeleton.getTrace().add(new EventSkeleton(eventSkeleton));
				}
			}
			newLogSkeleton.getLog().add(newTraceSkeleton);
		}
		// update edge hash table
		newLogSkeleton.setEdgeHashTable(LogSkeletonUtils.getEdgeHashTableForDisplayedLogSkeleton(logSkeleton));
		// update activity hash table
		newLogSkeleton.setActivityHashTable(LogSkeletonUtils.getActivityHashTable(newLogSkeleton));
		newLogSkeleton.setGroupConfig(logSkeleton.getGroupConfig());
		newGdpmLog.setLogSkeleton(newLogSkeleton);
		newGdpmLog.setStatObject(StatUtils.getStat(newLogSkeleton));
		return newGdpmLog;
	}

	public static GDPMLogSkeleton restrictLogFrom2Activities(GDPMLogSkeleton gdpmLog, List<String> sources,
			List<String> targets, List<String> blockedActivities) {
		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		// block activities
		Map<Integer, List<Integer>> blockedActivitiesPos = new HashMap<Integer, List<Integer>>();
		// set same group
		newGdpmLog.getLogSkeleton().setGroupConfig(gdpmLog.getLogSkeleton().getGroupConfig());
		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		// setup blocked act pos
		for (String act : blockedActivities) {
			for (Map.Entry<Integer, List<Integer>> entry : gdpmLog.getLogSkeleton().getActivityHashTable()
					.getActivityPositions(act).entrySet()) {
				if (blockedActivitiesPos.containsKey(entry.getKey())) {
					blockedActivitiesPos.get(entry.getKey()).addAll(entry.getValue());
				} else {
					blockedActivitiesPos.put(entry.getKey(), entry.getValue());
				}
			}
		}

		for (String source : sources) {
			for (String target : targets) {
				Map<Integer, List<Integer>> allPosSource = logSkeleton.getActivityHashTable()
						.getActivityPositions(source);
				Map<Integer, List<Integer>> allPosTarget = logSkeleton.getActivityHashTable()
						.getActivityPositions(target);

				if (source.equals(target)) {
					for (int traceNum : allPosSource.keySet()) {
						List<Integer> posSource = allPosSource.get(traceNum);
						List<TraceSkeleton> traces = LogSkeletonUtils.getTracesFrom1ListPosWithBlockedActivities(
								logSkeleton, traceNum, posSource, blockedActivitiesPos.get(traceNum));
						if (!traces.isEmpty()) {
							newGdpmLog.getLogSkeleton().getLog().addAll(traces);
						}
					}
				} else {
					Set<Integer> result = new HashSet<Integer>();

					if (allPosSource == null) {
						if (allPosTarget == null) {
							for (int i = 0; i < gdpmLog.getLogSkeleton().getLog().size(); i++) {
								result.add(i);
							}
						} else {
							result = allPosTarget.keySet();
						}
					} else {
						if (allPosTarget == null) {
							result = allPosSource.keySet();
						} else {
							result = allPosSource.keySet().stream().distinct().filter(allPosTarget.keySet()::contains)
									.collect(Collectors.toSet());
						}
					}

					for (Integer i : result) {
						List<Integer> posSource = allPosSource == null ? Arrays.asList(-1) : allPosSource.get(i);
						List<Integer> posTarget = allPosTarget == null ? Arrays.asList(-2) : allPosTarget.get(i);
						List<TraceSkeleton> traces = LogSkeletonUtils.getTracesFrom2ListPosWithBlockedActivities(
								logSkeleton, i, posSource, posTarget, blockedActivitiesPos.get(i));
						if (!traces.isEmpty()) {
							newGdpmLog.getLogSkeleton().getLog().addAll(traces);
						}

					}
				}

			}
		}
		newGdpmLog.setLogSkeleton(newGdpmLog.getLogSkeleton());
		newGdpmLog.getLogSkeleton()
				.setActivityHashTable(LogSkeletonUtils.getActivityHashTable(newGdpmLog.getLogSkeleton()));
		newGdpmLog.getLogSkeleton().setEdgeHashTable(
				LogSkeletonUtils.getEdgeHashTableForDisplayedLogSkeleton(newGdpmLog.getLogSkeleton()));
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
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUnselection(logSkeleton, Arrays.asList(activities));
		gdpmLog.setLogSkeleton(logSkeleton);
		return gdpmLog;
	}

	public static GDPMLogSkeleton addActivitiesInLog(GDPMLogSkeleton gdpmLog, List<String> activities) {
		String[] activitiesArray = new String[activities.size()];
		for (int i = 0; i < activitiesArray.length; i++) {
			activitiesArray[i] = activities.get(i);
		}
		return LogSkeletonUtils.addActivitiesInLog(gdpmLog, activitiesArray);
	}

	public static GDPMLogSkeleton addActivitiesInLog(GDPMLogSkeleton gdpmLog, String[] activities) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithSelection(logSkeleton, Arrays.asList(activities));
		logSkeleton = LogSkeletonUtils.setupListIndirectedEdgesFromLogSkeleton(logSkeleton);
		gdpmLog.setLogSkeleton(logSkeleton);

		// change list indirected edge
		return gdpmLog;
	}

	public static GDPMLogSkeleton replaceSetActivitiesInLog(GDPMLogSkeleton gdpmLog, GroupSkeleton groupSkeleton) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithGroup(logSkeleton, groupSkeleton);
		gdpmLog.setLogSkeleton(logSkeleton);

		return gdpmLog;
	}

	public static GDPMLogSkeleton removeActInGroup(GDPMLogSkeleton gdpmLog, String groupName, String act) {

		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUngroupForGroupElement(logSkeleton, groupName, act);
		gdpmLog.setLogSkeleton(logSkeleton);

		return gdpmLog;
	}

	public static GDPMLogSkeleton ungroupGroupInLog(GDPMLogSkeleton gdpmLog, GroupSkeleton groupSkeleton) {
		LogSkeleton logSkeleton = gdpmLog.getLogSkeleton();
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUngroup(logSkeleton, groupSkeleton);
		gdpmLog.setLogSkeleton(logSkeleton);
		return gdpmLog;
	}

	public static LogSkeleton changeLogSkeletonWithUngroupForGroupElement(LogSkeleton logSkeleton, String groupName,
			String act) {
		if (logSkeleton.getActivityHashTable().getActivityTable().containsKey(act)) {
			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, act);
			// remove in group
			logSkeleton.getGroupConfig().get(groupName).getListAct().remove(act);
		} else if (logSkeleton.getGroupConfig().containsKey(act)) {
			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, logSkeleton.getGroupConfig().get(act));
			// remove group in group config and in log
			logSkeleton.getGroupConfig().get(groupName).getListGroup().remove(logSkeleton.getGroupConfig().get(act));
			logSkeleton.getGroupConfig().remove(act);
		}

		return logSkeleton;
	}

	public static LogSkeleton changeLogSkeletonWithUngroup(LogSkeleton logSkeleton, GroupSkeleton groupSkeleton) {
		// change activity name
		for (String affectedAct : groupSkeleton.getListAct()) {
			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, affectedAct);
		}
		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
			changeLogSkeletonWithUngroup(logSkeleton, group);
		}
		// remove group in group config
		logSkeleton.getGroupConfig().remove(groupSkeleton.getGroupName());
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithUngroup(LogSkeleton logSkeleton, String act) {
		Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			for (Integer pos : entry.getValue()) {
				logSkeleton.getLog().get(entry.getKey()).getTrace().get(pos).getActivity().setCurrentName(
						logSkeleton.getLog().get(entry.getKey()).getTrace().get(pos).getActivity().getOriginalName());
			}
		}
		return logSkeleton;
	}

	public static LogSkeleton changeLogSkeletonWithGroup(LogSkeleton logSkeleton, GroupSkeleton groupSkeleton) {

		logSkeleton.addGroup(groupSkeleton);

		for (String act : groupSkeleton.getListAct()) {
			logSkeleton = changeLogSkeletonWithGroup(logSkeleton, act, groupSkeleton.getGroupName());

		}
		for (GroupSkeleton affectedGroup : groupSkeleton.getListGroup()) {
			logSkeleton = changeLogSkeletonWithGroup(logSkeleton, affectedGroup, groupSkeleton.getGroupName());
		}

		return logSkeleton;

	}

	private static LogSkeleton changeLogSkeletonWithGroup(LogSkeleton logSkeleton, String act, String groupName) {
		Map<Integer, List<Integer>> allPositions = logSkeleton.getActivityHashTable().getActivityPositions(act);
		if (allPositions != null) {
			for (Map.Entry<Integer, List<Integer>> entry : allPositions.entrySet()) {
				int traceNum = entry.getKey();
				List<Integer> positions = entry.getValue();
				for (Integer position : positions) {
					logSkeleton.getLog().get(traceNum).getTrace().get(position).getActivity().setCurrentName(groupName);
				}
			}
		}
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithGroup(LogSkeleton logSkeleton, GroupSkeleton oldGroup,
			String groupName) {
		for (String act : oldGroup.getListAct()) {
			logSkeleton = changeLogSkeletonWithGroup(logSkeleton, act, groupName);
		}
		for (GroupSkeleton group : oldGroup.getListGroup()) {
			logSkeleton = changeLogSkeletonWithGroup(logSkeleton, group, groupName);
		}
		return logSkeleton;
	}

	public static LogSkeleton changeLogSkeletonWithUnselection(LogSkeleton logSkeleton,
			List<String> unselectedActivities) {
		for (String act : unselectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
			} else if (logSkeleton.getGroupConfig().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, logSkeleton.getGroupConfig().get(act));
			} else {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithUnselection(LogSkeleton logSkeleton, String act) {
		Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			for (Integer i : entry.getValue()) {
				logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(false);
			}

		}
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithUnselection(LogSkeleton logSkeleton, GroupSkeleton oldGroup) {
		for (String act : oldGroup.getListAct()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
		}
		for (GroupSkeleton groupSkeleton : oldGroup.getListGroup()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, groupSkeleton);
		}
		return logSkeleton;
	}

	public static LogSkeleton changeLogSkeletonWithSelection(LogSkeleton logSkeleton, List<String> selectedActivities) {
		for (String act : selectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithSelection(logSkeleton, act);
			} else if (logSkeleton.getGroupConfig().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithSelection(logSkeleton, logSkeleton.getGroupConfig().get(act));
			} else if (!act.equals("begin") && !act.equals("end")) {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithSelection(LogSkeleton logSkeleton, String act) {
		Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			for (Integer i : entry.getValue()) {
				logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(true);
			}

		}
		return logSkeleton;
	}

	private static LogSkeleton changeLogSkeletonWithSelection(LogSkeleton logSkeleton, GroupSkeleton oldGroup) {
		for (String act : oldGroup.getListAct()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
		}
		for (GroupSkeleton groupSkeleton : oldGroup.getListGroup()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, groupSkeleton);
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
				EventSkeleton eventSkeleton = new EventSkeleton(new ActivitySkeleton(act, act), time, true);
				traceSkeleton.getTrace().add(eventSkeleton);
				//act hash table
				activityHashTable.addActivity(act, traceNum, i);

				//edge hash table
				if (i == 0) {
					EdgeObject edgeObject = new EdgeObject(new ActivitySkeleton("begin", "begin"),
							new ActivitySkeleton(act, act));
					edgeHashTable.addEdge(edgeObject, traceNum, -1, i);
				}
				if (i == trace.size() - 1) {
					EdgeObject edgeObject = new EdgeObject(new ActivitySkeleton(act, act),
							new ActivitySkeleton("end", "end"));
					edgeHashTable.addEdge(edgeObject, traceNum, i, -2);
				} else {
					XEvent nextEvent = trace.get(i + 1);
					EdgeObject edgeObject = new EdgeObject(new ActivitySkeleton(act, act),
							new ActivitySkeleton(nextEvent.getAttributes().get(classifier).toString(),
									nextEvent.getAttributes().get(classifier).toString()));
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

	public static EdgeHashTable getEdgeHashTableForDisplayedLogSkeleton(LogSkeleton logSkeleton) {
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		List<EdgeObject> addedEdges = new ArrayList<EdgeObject>();

		for (int traceNum = 0; traceNum < logSkeleton.getLog().size(); traceNum++) {
			TraceSkeleton traceSkeleton = logSkeleton.getLog().get(traceNum);
			ActivitySkeleton act1 = new ActivitySkeleton("begin", "begin");
			int act1Index = -1;
			int oldAct1Index = -1;
			ActivitySkeleton act2 = new ActivitySkeleton("", "");

			for (int eventNum = 0; eventNum < traceSkeleton.getTrace().size(); eventNum++) {
				EventSkeleton eventSkeleton = traceSkeleton.getTrace().get(eventNum);
				if (eventSkeleton.getIsDisplayed()) {
					act2 = eventSkeleton.getActivity();
					EdgeObject edge = new EdgeObject(act1, act2);
					if (oldAct1Index != eventNum - 1) {
						edge.setIsIndirected(true);
						if (addedEdges.contains(edge)) {
							Map<Integer, List<Integer[]>> allPos = edgeHashTable.getEdgePositions(edge);
							edgeHashTable.getEdgeTable().remove(edge);
							edgeHashTable.addEdge(edge, allPos);
						}
					}

					edgeHashTable.addEdge(edge, traceNum, act1Index, act1Index + 1);
					addedEdges.add(edge);
					addedEdges = addedEdges.stream().distinct().collect(Collectors.toList());

					act1 = eventSkeleton.getActivity();
					if (oldAct1Index != eventNum - 1) {
						oldAct1Index = eventNum;
					} else {
						oldAct1Index += 1;
					}
					act1Index += 1;

				}
				if (eventNum == traceSkeleton.getTrace().size() - 1) {
					act2 = new ActivitySkeleton("end", "end");
					EdgeObject edge = new EdgeObject(act1, act2);
					if (oldAct1Index != traceSkeleton.getTrace().size() - 1) {
						edge.setIsIndirected(true);
						if (addedEdges.contains(edge)) {
							Map<Integer, List<Integer[]>> allPos = edgeHashTable.getEdgePositions(edge);
							edgeHashTable.getEdgeTable().remove(edge);
							edgeHashTable.addEdge(edge, allPos);
						}
					}
					edgeHashTable.addEdge(edge, traceNum, act1Index, -2);
					addedEdges.add(edge);
					addedEdges = addedEdges.stream().distinct().collect(Collectors.toList());
				}

			}
		}
		return edgeHashTable;
	}

	public static ActivityHashTable getActivityHashTable(LogSkeleton logSkeleton) {
		ActivityHashTable activityHashTable = new ActivityHashTable();

		for (int traceNum = 0; traceNum < logSkeleton.getLog().size(); traceNum++) {
			TraceSkeleton traceSkeleton = logSkeleton.getLog().get(traceNum);
			for (int eventNum = 0; eventNum < traceSkeleton.getTrace().size(); eventNum++) {
				EventSkeleton eventSkeleton = traceSkeleton.getTrace().get(eventNum);
				activityHashTable.addActivity(eventSkeleton.getActivity().getOriginalName(), traceNum, eventNum);
			}
		}

		return activityHashTable;
	}

	public static void setupEdgeHashTable(LogSkeleton logSkeleton) {
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		for (int traceNum = 0; traceNum < logSkeleton.getLog().size(); traceNum++) {
			TraceSkeleton traceSkeleton = logSkeleton.getLog().get(traceNum);
			ActivitySkeleton act1 = new ActivitySkeleton("begin", "begin");
			int act1Index = -1;
			ActivitySkeleton act2 = new ActivitySkeleton("", "");

			for (int eventNum = 0; eventNum < traceSkeleton.getTrace().size(); eventNum++) {
				EventSkeleton eventSkeleton = traceSkeleton.getTrace().get(eventNum);
				if (eventSkeleton.getIsDisplayed()) {
					act2 = eventSkeleton.getActivity();
					EdgeObject edge = new EdgeObject(act1, act2);
					if (act1Index != eventNum - 1) {
						edge.setIsIndirected(true);
					}
					edgeHashTable.addEdge(edge, traceNum, act1Index, eventNum);
					act1 = eventSkeleton.getActivity();
					act1Index = eventNum;
				}
				if (eventNum == traceSkeleton.getTrace().size() - 1) {
					act2 = new ActivitySkeleton("end", "end");
					EdgeObject edge = new EdgeObject(act1, act2);
					if (act1Index != traceSkeleton.getTrace().size() - 1) {
						edge.setIsIndirected(true);
					}
					edgeHashTable.addEdge(edge, traceNum, act1Index, -2);
				}

			}
		}

		Set<EdgeObject> allEdgeObjects = edgeHashTable.getEdgeTable().keySet();

		for (EdgeObject edgeObject : allEdgeObjects) {
			if (!edgeObject.getIsIndirected()) {
				for (EdgeObject edgeObject2 : allEdgeObjects) {
					if (edgeObject2.equals(edgeObject)) {
						if (edgeObject2.getIsIndirected()) {
							// move all pos from false -> true
							Map<Integer, List<Integer[]>> allFalsePos = edgeHashTable.getEdgePositions(edgeObject);
							edgeHashTable.addEdge(edgeObject2, allFalsePos);
							edgeHashTable.getEdgeTable().remove(edgeObject);
						}
					}
				}
			}
		}

		logSkeleton.setEdgeHashTable(edgeHashTable);
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

	public static LogSkeleton setupListIndirectedEdgesFromLogSkeleton(LogSkeleton logSkeleton) {
		List<EdgeObject> listIndirectedEdges = new ArrayList<EdgeObject>();

		for (TraceSkeleton trace : logSkeleton.getLog()) {
			LogSkeletonUtils.addIndirectedEdgesFromTrace(trace, listIndirectedEdges);
		}

		for (EdgeObject edge : logSkeleton.getEdgeHashTable().getEdgeTable().keySet()) {
			if (listIndirectedEdges.contains(edge)) {
				edge.setIsIndirected(true);
			}
		}

		return logSkeleton;
	}

	public static List<TraceSkeleton> getTracesFrom2ListPosWithBlockedActivities(LogSkeleton logSkeleton, int traceNum,
			List<Integer> posSource, List<Integer> posTarget, List<Integer> posBlocked) {
		Boolean hasBlockedAct = posBlocked != null;
		List<TraceSkeleton> traces = new ArrayList<>();
		TraceSkeleton trace = logSkeleton.getLog().get(traceNum);
		Collections.sort(posSource);
		Collections.sort(posTarget);
		if (hasBlockedAct) {
			Collections.sort(posBlocked);
		}
		int startIndex = 0;
		int endIndex = -1; // not valid found

		// source = begin node
		if (posSource.get(0) == -1) {
			startIndex = 0;
			if (posTarget.get(0) == -2) {
				if (!hasBlockedAct) {
					endIndex = trace.getTrace().size() - 1;
				}
			} else {
				if ((hasBlockedAct && posTarget.get(0) < posBlocked.get(0)) || !hasBlockedAct) {
					endIndex = posTarget.get(0);
				}
			}
			if (endIndex != -1) {
				TraceSkeleton addTrace = new TraceSkeleton();
				for (int k = startIndex; k <= endIndex; k++) {
					addTrace.getTrace().add(trace.getTrace().get(k));
				}
				traces.add(addTrace);
			}

		} else {
			if (posTarget.get(0) == -2) {
				startIndex = posSource.get(posSource.size() - 1);

				if ((hasBlockedAct && startIndex > posBlocked.get(posBlocked.size() - 1)) || !hasBlockedAct) {
					endIndex = trace.getTrace().size() - 1;
					TraceSkeleton addTrace = new TraceSkeleton();
					for (int k = startIndex; k <= endIndex; k++) {
						addTrace.getTrace().add(trace.getTrace().get(k));
					}
					traces.add(addTrace);
					return traces;
				}

			} else {
				for (int i = 0; i < posSource.size(); i++) {
					startIndex = posSource.get(i);
					endIndex = -1; // not found
					if (i + 1 < posSource.size()) {
						int nextStartIndex = posSource.get(i + 1);
						for (Integer j : posTarget) {
							if (j > startIndex && j < nextStartIndex) {
								Boolean isBlocked = false;
								if (hasBlockedAct) {
									for (Integer k : posBlocked) {
										if (k > startIndex && k < j) {
											isBlocked = true;
											break;
										}
									}
								}

								if (!isBlocked) {
									endIndex = j;
								}
								break;

							}
						}
					} else {
						for (Integer j : posTarget) {
							if (j > startIndex) {
								Boolean isBlocked = false;
								if (hasBlockedAct) {
									for (Integer k : posBlocked) {
										if (k > startIndex && k < j) {
											isBlocked = true;
											break;
										}
									}
								}
								if (!isBlocked) {
									endIndex = j;
								}
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
			}
		}

		return traces;
	}

	public static List<TraceSkeleton> getTracesFrom1ListPosWithBlockedActivities(LogSkeleton logSkeleton, int traceNum,
			List<Integer> posSource, List<Integer> posBlocked) {
		Boolean hasBlockedAct = posBlocked != null;
		List<TraceSkeleton> traces = new ArrayList<>();
		TraceSkeleton trace = logSkeleton.getLog().get(traceNum);
		Collections.sort(posSource);
		if (hasBlockedAct) {
			Collections.sort(posBlocked);
		}
		int startIndex = 0;
		int endIndex = -1; // not valid found
		for (int i = 0; i < posSource.size(); i++) {
			startIndex = posSource.get(i);
			if (i + 1 < posSource.size()) {
				int j = posSource.get(i + 1);
				Boolean isBlocked = false;
				if (hasBlockedAct) {
					for (Integer k : posBlocked) {
						if (k > startIndex && k < j) {
							isBlocked = true;
							break;
						}
					}
				}
				if (!isBlocked) {
					endIndex = j;
				}
				if (endIndex != -1) {
					TraceSkeleton addTrace = new TraceSkeleton();
					for (int k = startIndex; k <= endIndex; k++) {
						addTrace.getTrace().add(trace.getTrace().get(k));
					}
					traces.add(addTrace);
				}
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
					node2 = event.getActivity().getCurrentName();
					EdgeObject toAdd = new EdgeObject(new ActivitySkeleton(node1, node1),
							new ActivitySkeleton(node2, node2));
					if (!listIndirectedEdges.contains(toAdd)) {
						listIndirectedEdges.add(toAdd);
					}
					isFound = false;
				}
				node1 = event.getActivity().getCurrentName();
			} else {
				isFound = true;
			}

		}
		if (isFound) {
			EdgeObject toAdd = new EdgeObject(new ActivitySkeleton(node1, node1), new ActivitySkeleton("end", "end"));
			if (!listIndirectedEdges.contains(toAdd)) {
				listIndirectedEdges.add(toAdd);
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
