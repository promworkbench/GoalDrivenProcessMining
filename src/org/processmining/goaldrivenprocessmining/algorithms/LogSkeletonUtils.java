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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EventSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;

public class LogSkeletonUtils {

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\D\\data\\running-example.xes");

		// Create an input stream for the XES file
		InputStream is = new FileInputStream(file);

		// Create a parser for XES files
		XesXmlParser parser = new XesXmlParser();

		XLog log = parser.parse(is).get(0);
		Consumer<XTrace> action = trace -> {
			for (XEvent event : trace) {
				System.out.println(event.getAttributes().get("concept:name"));
			}
			System.out.println(Thread.currentThread().getName());
		};
		log.parallelStream().forEach(action);
		//		GDPMLogSkeleton gdpmLog = new GDPMLogSkeleton(log);

		//		System.out.println(gdpmLog);

	}

	public static final String TIME_CLASSIFIER = "time:timestamp";

	public static String getLogClassifier(XLog log) {
		try {
			return log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		} catch (Exception e) {
			return "concept:name";
		}
	}

	public static List<String> getUsingActsInLog(GDPMLogSkeleton gdpmLogSkeleton) {
		EdgeHashTable edgeHashTable = gdpmLogSkeleton.getEdgeHashTable();

		// get all using nodes
		List<String> usingActs = new ArrayList<>();
		for (EdgeObject edgeObject : edgeHashTable.getEdgeTable().keySet()) {
			if (!usingActs.contains(edgeObject.getNode1())) {
				usingActs.add(edgeObject.getNode1());
			}
			if (!usingActs.contains(edgeObject.getNode2())) {
				usingActs.add(edgeObject.getNode2());
			}
		}
		return usingActs;
	}

	public static void setupEdgeHashTableForHighLevelAfterChangingDisplayedActs(GDPMLogSkeleton gdpmLogSkeleton,
			Config config, EdgeHashTable originalEdgeHashTable) {
		EdgeHashTable newEdgeHashTable = new EdgeHashTable();
		List<String> unselectedActs = new ArrayList<String>();
		EdgeHashTable affectedEdges = new EdgeHashTable();

		// get the unselected acts
		for (String act : config.getLowActs()) {
			unselectedActs.add(act);
		}
		// setup unaffected and affected edges
		for (EdgeObject edge : originalEdgeHashTable.getEdgeTable().keySet()) {
			if (unselectedActs.contains(edge.getNode1()) || unselectedActs.contains(edge.getNode2())) {
				affectedEdges.addEdge(edge, originalEdgeHashTable.getEdgePositions(edge));
			} else {
				newEdgeHashTable.addEdge(edge, originalEdgeHashTable.getEdgePositions(edge));
			}
		}

		// calculate affected edges for each activity
		for (String act : unselectedActs) {
			List<EdgeObject> actLeft = new ArrayList<>();
			List<EdgeObject> actRight = new ArrayList<>();
			List<EdgeObject> removedEdges = new ArrayList<>();
			// get the left edge and right edge to the activity L:(a, X), R:(X,a)
			for (EdgeObject edge : affectedEdges.getEdgeTable().keySet()) {
				if (edge.getNode2().equals(act)) {
					actLeft.add(edge);
				} else if (edge.getNode1().equals(act)) {
					actRight.add(edge);
				}
			}
			// start with the left edges
			for (EdgeObject edgeLeft : actLeft) {
				// get all pos of the left edge
				Map<Integer, List<Integer[]>> mapAllPosEdgeLeft = affectedEdges.getEdgePositions(edgeLeft);
				// check the right edges
				for (EdgeObject edgeRight : actRight) {
					// get the pos of right edge
					Map<Integer, List<Integer[]>> mapAllPosEdgeRight = affectedEdges.getEdgePositions(edgeRight);
					// find the common case where both left and right edges happened
					Set<Integer> affectedTraces = mapAllPosEdgeLeft.keySet().stream().distinct()
							.filter(mapAllPosEdgeRight.keySet()::contains).collect(Collectors.toSet());
					EdgeObject newEdge = null;
					for (Integer traceIndex : affectedTraces) {
						// find the pos of the edge happen in the trace
						List<Integer[]> listPosEdgeLeft = mapAllPosEdgeLeft.get(traceIndex);
						List<Integer[]> listPosEdgeRight = mapAllPosEdgeRight.get(traceIndex);
						for (Integer[] posEdgeLeft : listPosEdgeLeft) {
							for (Integer[] posEdgeRight : listPosEdgeRight) {
								// check if the 2 edges happen sequentially
								if (posEdgeLeft[1] == posEdgeRight[0]) {
									// create a new indirect edge
									newEdge = new EdgeObject(edgeLeft.getNode1(), edgeRight.getNode2(), true);
									affectedEdges.addEdge(newEdge, traceIndex, posEdgeLeft[0], posEdgeRight[1]);
									break;
								}
							}
						}
					}
					if (!removedEdges.contains(edgeRight)) {
						removedEdges.add(edgeRight);
					}
				}
				affectedEdges.getEdgeTable().remove(edgeLeft);
			}
			for (EdgeObject edge : removedEdges) {
				affectedEdges.getEdgeTable().remove(edge);
			}
		}

		for (EdgeObject edge : affectedEdges.getEdgeTable().keySet()) {
			if (newEdgeHashTable.getEdgeTable().keySet().contains(edge)) {
				edge.setIsIndirected(true);
				newEdgeHashTable.addEdge(edge, affectedEdges.getEdgePositions(edge));
				Map<Integer, List<Integer[]>> allPos = newEdgeHashTable.getEdgePositions(edge);
				newEdgeHashTable.getEdgeTable().remove(edge);
				newEdgeHashTable.getEdgeTable().put(edge, allPos);
			} else {
				newEdgeHashTable.addEdge(edge, affectedEdges.getEdgePositions(edge));
			}
		}
		gdpmLogSkeleton.setEdgeHashTable(newEdgeHashTable);

		/*-----------------*/
		// calculate the map of activities to each edge in the high level graph
		Map<String, List<EdgeObject>> mapActToEdgeHighLevel = new HashMap<String, List<EdgeObject>>();
		for (EdgeObject edgeObject : newEdgeHashTable.getEdgeTable().keySet()) {
			if (edgeObject.getIsIndirected()) {
				Map<Integer, List<Integer[]>> mapCasePos = newEdgeHashTable.getEdgeTable().get(edgeObject);
				for (Map.Entry<Integer, List<Integer[]>> entry : mapCasePos.entrySet()) {
					for (Integer[] pos : entry.getValue()) {
						TraceSkeleton trace = Cl01GatherAttributes.originalLog.get(entry.getKey());

						int startIndex = pos[0] == -1 ? 0 : pos[0];
						int targetIndex = pos[1] == -2 ? trace.getTrace().size() - 1 : pos[1];

						for (int index = startIndex; index <= targetIndex; index++) {
							String act = trace.getTrace().get(index).getActivity();
							mapActToEdgeHighLevel.computeIfAbsent(act, k -> new ArrayList<>());
							if (!mapActToEdgeHighLevel.get(act).contains(edgeObject)) {
								mapActToEdgeHighLevel.get(act).add(edgeObject);
							}
						}
					}
				}
			}
		}
		config.setMapActEdgeInHighLevel(mapActToEdgeHighLevel);
		/*-----------------*/
	}

	public static Map<EdgeObject, Integer> getFrequencyOfActInEdge(String act, List<EdgeObject> listAffectedEdges) {
		Map<EdgeObject, Integer> res = new HashMap<EdgeObject, Integer>();
		List<TraceSkeleton> originalLog = Cl01GatherAttributes.originalLog;
		EdgeHashTable currentHighLevelEdgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;

		for (EdgeObject edgeObject : listAffectedEdges) {
			// get the position of this edge from the current high level edge hash table
			Map<Integer, List<Integer[]>> mapPos = currentHighLevelEdgeHashTable.getEdgePositions(edgeObject);
			if (mapPos != null) {
				for (Map.Entry<Integer, List<Integer[]>> entry : mapPos.entrySet()) {
					int traceNum = entry.getKey();
					TraceSkeleton trace = originalLog.get(traceNum);
					List<Integer[]> positions = entry.getValue();
					for (Integer[] pos : positions) {
						int startIndex = pos[0] == -1 ? 0 : pos[0];
						int targetIndex = pos[1] == -2 ? trace.getTrace().size() - 1 : pos[1];
						for (int index = startIndex; index <= targetIndex; index++) {
							String a = trace.getTrace().get(index).getActivity();
							if (a.equals(act)) {
								// if contain act in this trace
								res.compute(edgeObject, (key, value) -> (value == null) ? 1 : value + 1);
							}
						}
					}
				}
			}
		}
		return res;
	}

	public static GDPMLogSkeleton removeActivitiesInLog(GDPMLogSkeleton logSkeleton, List<String> activities) {
		String[] activitiesArray = new String[activities.size()];
		for (int i = 0; i < activitiesArray.length; i++) {
			activitiesArray[i] = activities.get(i);
		}
		return LogSkeletonUtils.removeActivitiesInLog(logSkeleton, activitiesArray);
	}

	public static GDPMLogSkeleton removeActivitiesInLog(GDPMLogSkeleton gdpmLog, String[] activities) {

		GDPMLogSkeleton logSkeleton = gdpmLog;
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUnselection(logSkeleton, Arrays.asList(activities));
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

		GDPMLogSkeleton logSkeleton = gdpmLog;
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithSelection(logSkeleton, Arrays.asList(activities));
		logSkeleton = LogSkeletonUtils.setupListIndirectedEdgesFromLogSkeleton(logSkeleton);

		// change list indirected edge
		return gdpmLog;
	}

	public static GDPMLogSkeleton groupActivitiesInLog(GDPMLogSkeleton gdpmLog, GroupSkeleton groupSkeleton) {

		GDPMLogSkeleton logSkeleton = gdpmLog;
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithGroup(logSkeleton, groupSkeleton);

		return gdpmLog;
	}

	public static GDPMLogSkeleton removeActInGroup(GDPMLogSkeleton gdpmLog, String groupName, String act) {

		GDPMLogSkeleton logSkeleton = gdpmLog;
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUngroupForGroupElement(logSkeleton, groupName, act);

		return gdpmLog;
	}

	public static GDPMLogSkeleton ungroupGroupInLog(GDPMLogSkeleton gdpmLog, GroupSkeleton groupSkeleton) {
		GDPMLogSkeleton logSkeleton = gdpmLog;
		logSkeleton = LogSkeletonUtils.changeLogSkeletonWithUngroup(logSkeleton, groupSkeleton);
		return gdpmLog;
	}

	public static GDPMLogSkeleton changeLogSkeletonWithUngroupForGroupElement(GDPMLogSkeleton logSkeleton,
			String groupName, String act) {
		if (logSkeleton.getActivityHashTable().getActivityTable().containsKey(act)) {
			//			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, act);
			// remove in group
			if (logSkeleton.getGroupSkeletonByGroupName(groupName) != null) {
				logSkeleton.getGroupSkeletonByGroupName(groupName).getListAct().remove(act);
			}

		} else if (logSkeleton.isAGroupSkeleton(act)) {
			//			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, logSkeleton.getGroupSkeletonByGroupName(act));
			// remove group in group config and in log
			//			logSkeleton.getGroupConfig().get(groupName).getListGroup().remove(logSkeleton.getGroupConfig().get(act));
			//			logSkeleton.getGroupConfig().remove(act);
			logSkeleton.getGroupSkeletonByGroupName(groupName).getListGroup()
					.remove(logSkeleton.getGroupSkeletonByGroupName(act));
			logSkeleton.getConfig().getListGroupSkeletons().remove(logSkeleton.getGroupSkeletonByGroupName(act));
		}

		return logSkeleton;
	}

	public static GDPMLogSkeleton changeLogSkeletonWithUngroup(GDPMLogSkeleton logSkeleton,
			GroupSkeleton groupSkeleton) {
		// change activity name
		//		for (String affectedAct : groupSkeleton.getListAct()) {
		//			logSkeleton = changeLogSkeletonWithUngroup(logSkeleton, affectedAct);
		//		}
		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
			changeLogSkeletonWithUngroup(logSkeleton, group);
		}
		// remove group in group config
		//		logSkeleton.getGroupConfig().remove(groupSkeleton.getGroupName());
		logSkeleton.getConfig().getListGroupSkeletons().remove(groupSkeleton);
		return logSkeleton;
	}

	public static GDPMLogSkeleton changeLogSkeletonWithGroup(GDPMLogSkeleton logSkeleton, GroupSkeleton groupSkeleton) {

		logSkeleton.addGroup(groupSkeleton);

		return logSkeleton;

	}

	public static GDPMLogSkeleton changeLogSkeletonWithUnselection(GDPMLogSkeleton logSkeleton,
			List<String> unselectedActivities) {
		for (String act : unselectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
			} else if (logSkeleton.isAGroupSkeleton(act)) {
				//				logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, logSkeleton.getGroupConfig().get(act));
				logSkeleton = changeLogSkeletonWithUnselection(logSkeleton,
						logSkeleton.getGroupSkeletonByGroupName(act));
			} else {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		return logSkeleton;
	}

	private static GDPMLogSkeleton changeLogSkeletonWithUnselection(GDPMLogSkeleton logSkeleton, String act) {
		Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			for (Integer i : entry.getValue()) {
				logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(false);
			}

		}
		return logSkeleton;
	}

	private static GDPMLogSkeleton changeLogSkeletonWithUnselection(GDPMLogSkeleton logSkeleton,
			GroupSkeleton oldGroup) {
		for (String act : oldGroup.getListAct()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
		}
		for (GroupSkeleton groupSkeleton : oldGroup.getListGroup()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, groupSkeleton);
		}
		return logSkeleton;
	}

	public static GDPMLogSkeleton changeLogSkeletonWithSelection(GDPMLogSkeleton logSkeleton,
			List<String> selectedActivities) {
		for (String act : selectedActivities) {
			if (logSkeleton.getActivityHashTable().getActivityTable().keySet().contains(act)) {
				logSkeleton = changeLogSkeletonWithSelection(logSkeleton, act);
			} else if (logSkeleton.isAGroupSkeleton(act)) {
				//				logSkeleton = changeLogSkeletonWithSelection(logSkeleton, logSkeleton.getGroupConfig().get(act));
				logSkeleton = changeLogSkeletonWithSelection(logSkeleton, logSkeleton.getGroupSkeletonByGroupName(act));
			} else if (!act.equals("begin") && !act.equals("end")) {
				throw new IllegalStateException("Invalid activity in log");
			}

		}
		return logSkeleton;
	}

	private static GDPMLogSkeleton changeLogSkeletonWithSelection(GDPMLogSkeleton logSkeleton, String act) {
		Map<Integer, List<Integer>> allPos = logSkeleton.getActivityHashTable().getActivityPositions(act);
		for (Map.Entry<Integer, List<Integer>> entry : allPos.entrySet()) {
			for (Integer i : entry.getValue()) {
				logSkeleton.getLog().get(entry.getKey()).getTrace().get(i).setIsDisplayed(true);
			}

		}
		return logSkeleton;
	}

	private static GDPMLogSkeleton changeLogSkeletonWithSelection(GDPMLogSkeleton logSkeleton, GroupSkeleton oldGroup) {
		for (String act : oldGroup.getListAct()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, act);
		}
		for (GroupSkeleton groupSkeleton : oldGroup.getListGroup()) {
			logSkeleton = changeLogSkeletonWithUnselection(logSkeleton, groupSkeleton);
		}
		return logSkeleton;
	}

	public static GDPMLogSkeleton getLogSkeleton(XLog log) {
		GDPMLogSkeleton logSkeleton = new GDPMLogSkeleton();
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
				EventSkeleton eventSkeleton = new EventSkeleton(act, time, true);
				traceSkeleton.getTrace().add(eventSkeleton);
				//act hash table
				activityHashTable.addActivity(act, traceNum, i);
				EdgeObject edgeObject;
				//edge hash table
				if (i == 0) {
					edgeObject = new EdgeObject("begin", act);
					edgeHashTable.addEdge(edgeObject, traceNum, -1, i);

				} else {
					XEvent prevEvent = trace.get(i - 1);
					edgeObject = new EdgeObject(prevEvent.getAttributes().get(classifier).toString(), act);
					edgeHashTable.addEdge(edgeObject, traceNum, i - 1, i);

				}
				if (i == trace.size() - 1) {
					edgeObject = new EdgeObject(act, "end");
					edgeHashTable.addEdge(edgeObject, traceNum, i, -2);
				}

			}
			traceNum++;
			logSkeleton.getLog().add(traceSkeleton);
		}
		logSkeleton.setActivityHashTable(activityHashTable);
		logSkeleton.setEdgeHashTable(edgeHashTable);
		return logSkeleton;
	}

	public static EdgeHashTable getEdgeHashTableForDisplayedLogSkeleton(GDPMLogSkeleton logSkeleton) {
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		List<EdgeObject> addedEdges = new ArrayList<EdgeObject>();

		for (int traceNum = 0; traceNum < logSkeleton.getLog().size(); traceNum++) {
			TraceSkeleton traceSkeleton = logSkeleton.getLog().get(traceNum);
			String act1 = "begin";
			int act1Index = -1;
			int oldAct1Index = -1;
			String act2 = "";

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
					act2 = "end";
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

	public static ActivityHashTable getActivityHashTable(GDPMLogSkeleton logSkeleton) {
		ActivityHashTable activityHashTable = new ActivityHashTable();

		for (int traceNum = 0; traceNum < logSkeleton.getLog().size(); traceNum++) {
			TraceSkeleton traceSkeleton = logSkeleton.getLog().get(traceNum);
			for (int eventNum = 0; eventNum < traceSkeleton.getTrace().size(); eventNum++) {
				EventSkeleton eventSkeleton = traceSkeleton.getTrace().get(eventNum);
				activityHashTable.addActivity(eventSkeleton.getActivity(), traceNum, eventNum);
			}
		}

		return activityHashTable;
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

	public static GDPMLogSkeleton setupListIndirectedEdgesFromLogSkeleton(GDPMLogSkeleton logSkeleton) {
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

	public static List<TraceSkeleton> getTracesFrom2ListPosWithBlockedActivities(GDPMLogSkeleton logSkeleton,
			int traceNum, List<Integer> posSource, List<Integer> posTarget, List<Integer> posBlocked) {
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

	public static List<TraceSkeleton> getTracesFrom1ListPosWithBlockedActivities(GDPMLogSkeleton logSkeleton,
			int traceNum, List<Integer> posSource, List<Integer> posBlocked) {
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
					node2 = event.getActivity();
					EdgeObject toAdd = new EdgeObject(node1, node2);
					if (!listIndirectedEdges.contains(toAdd)) {
						listIndirectedEdges.add(toAdd);
					}
					isFound = false;
				}
				node1 = event.getActivity();
			} else {
				isFound = true;
			}

		}
		if (isFound) {
			EdgeObject toAdd = new EdgeObject(node1, "end");
			if (!listIndirectedEdges.contains(toAdd)) {
				listIndirectedEdges.add(toAdd);
			}
		}
	}

}
