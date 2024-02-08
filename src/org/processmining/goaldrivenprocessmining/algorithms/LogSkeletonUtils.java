package org.processmining.goaldrivenprocessmining.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EventSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;

public class LogSkeletonUtils {

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\D\\data\\BPI\\BPI-2019\\BPI_Challenge_2019_3wayAfter_EC.xes");

		// Create an input stream for the XES file
		InputStream is = new FileInputStream(file);

		// Create a parser for XES files
		XesXmlParser parser = new XesXmlParser();

		XLog log = parser.parse(is).get(0);

		System.out.println(log.get(0).get(0).getAttributes().keySet());

	}

	private static final String TIME_CLASSIFIER = "time:timestamp";

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
		List<String> checkedUnselectedActs = new ArrayList<>();
		for (String act : unselectedActs) {
			List<EdgeObject> actLeft = new ArrayList<>();
			List<EdgeObject> actRight = new ArrayList<>();
			List<EdgeObject> removedEdges = new ArrayList<>();
			// remove self loop edges in calculation
			removeSelfLoopEdges(affectedEdges, act, checkedUnselectedActs);

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
								int s = posEdgeLeft[1];
								int e = posEdgeRight[0];
								// check if the 2 edges happen sequentially
								if (s == e) {
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
			checkedUnselectedActs.add(act);
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
		// calculate throughput for each edge
		calculateThroughputForEachEdge(gdpmLogSkeleton);

		setupMapActToEdgeHighLevel(newEdgeHashTable, config);
		/*-----------------*/
	}

	public static void setupEdgeHashTableForLowLevelLog(GDPMLogSkeleton newGdpmLog, GDPMLogSkeleton fullLogSkeleton,
			String source, String target) {
		/*------------------*/
		EdgeHashTable highLevelEdgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;
		EdgeHashTable newEdgeHashTable = new EdgeHashTable();
		Map<EdgeObject, Map<Integer, List<Integer[]>>> edgeTable = highLevelEdgeHashTable.getEdgeTable();
		for (EdgeObject edgeObject : edgeTable.keySet()) {
			if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
				Map<Integer, List<Integer[]>> mapCasePos = edgeTable.get(edgeObject);
				for (Map.Entry<Integer, List<Integer[]>> entry : mapCasePos.entrySet()) {
					TraceSkeleton trace = fullLogSkeleton.getLog().get(entry.getKey());

					for (Integer[] pos : entry.getValue()) {
						if (pos[0] == -1) {
							String sourceAct = "begin";
							String targetAct = trace.getTrace().get(0).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), -1, 0);
						} else {
							String sourceAct = "begin";
							String targetAct = trace.getTrace().get(pos[0]).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), -1, pos[0]);
						}
						if (pos[1] == -2) {
							String sourceAct = trace.getTrace().get(trace.getTrace().size() - 1).getActivity();
							String targetAct = "end";
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), trace.getTrace().size() - 1, -2);
						} else {
							String sourceAct = trace.getTrace().get(pos[1]).getActivity();
							String targetAct = "end";
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), pos[1], -2);
						}
						int startIndex = pos[0] == -1 ? 0 : pos[0];
						int targetIndex = pos[1] == -2 ? trace.getTrace().size() - 1 : pos[1];

						for (int index = startIndex; index < targetIndex; index++) {
							String sourceAct = trace.getTrace().get(index).getActivity();
							String targetAct = trace.getTrace().get(index + 1).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), index, index + 1);
						}

					}
				}

				break;
			}
		}
		/*------------------*/
		newGdpmLog.setEdgeHashTable(newEdgeHashTable);
		// calculate throughput for each edge
		calculateThroughputForEachEdge(newGdpmLog);
	}

	public static void calculateThroughputForEachEdge(GDPMLogSkeleton gdpmLogSkeleton) {
		// calculate throughput time for each edge
		Map<EdgeObject, ThroughputTimeObject> mapEdgeThroughputTime = new HashMap<>();
		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : gdpmLogSkeleton.getEdgeHashTable()
				.getEdgeTable().entrySet()) {
			EdgeObject edgeObject = entry.getKey();
			if (edgeObject.getNode1().equals("begin") || edgeObject.getNode2().equals("end")) {
				// case when it is the start act or end act, then the throughput time is 0
				mapEdgeThroughputTime.put(edgeObject, new ThroughputTimeObject(0, 0, 0, 0));
			} else {
				Map<Integer, List<Integer[]>> allTracePos = entry.getValue();
				long min = Long.MAX_VALUE;
				long max = Long.MIN_VALUE;
				List<Long> listThroughput = new ArrayList<>();
				BigInteger sum = BigInteger.ZERO;
				for (Map.Entry<Integer, List<Integer[]>> entry1 : allTracePos.entrySet()) {
					int traceNum = entry1.getKey();
					// get the trace from the log
					TraceSkeleton traceSkeleton = Cl01GatherAttributes.originalLog.get(traceNum);
					List<Integer[]> allPos = entry1.getValue();
					for (Integer[] pos : allPos) {
						int sourceIndex = pos[0];
						int targetIndex = pos[1];
						long sourceTime = traceSkeleton.getTrace().get(sourceIndex).getTime();
						long targetTime = traceSkeleton.getTrace().get(targetIndex).getTime();
						long difTime = targetTime - sourceTime;
						// check for max
						if (difTime >= max) {
							max = difTime;
						}
						// check for min
						if (difTime <= min) {
							min = difTime;
						}
						// sum
						BigInteger bigIntegerNumber = BigInteger.valueOf(difTime);
						sum = sum.add(bigIntegerNumber);
						// list for median
						listThroughput.add(difTime);
					}
				}
				// mean
				BigInteger result = sum.divide(BigInteger.valueOf(listThroughput.size()));
				long mean = result.longValue();
				// median
				long median;
				if (listThroughput.size() % 2 == 1) {
					median = listThroughput.get((listThroughput.size() + 1) / 2 - 1);
				} else {
					long lower = listThroughput.get(listThroughput.size() / 2 - 1);
					long upper = listThroughput.get(listThroughput.size() / 2);
					median = (upper + lower) / 2;
				}
				mapEdgeThroughputTime.put(edgeObject, new ThroughputTimeObject(min, max, median, mean));
			}

		}
		gdpmLogSkeleton.setEdgeThroughputTime(mapEdgeThroughputTime);
	}

	public static void removeSelfLoopEdges(EdgeHashTable affectedEdges, String act,
			List<String> checkedUnselectedActs) {
		List<EdgeObject> loopEdges = new ArrayList<>();
		// fix the self loop edge: (x,x) -> (a,x) + (x,b) 
		// find the self loop edge
		List<EdgeObject> edges = new ArrayList<>();
		for (EdgeObject edge : affectedEdges.getEdgeTable().keySet()) {
			edges.add(edge);
		}
		for (EdgeObject edge : edges) {
			if (edge.getNode1().equals(edge.getNode2()) && edge.getNode1().equals(act)) {
				loopEdges.add(edge);
				//merge all consecutive loop sequence in a trace
				Map<Integer, List<Integer[]>> mapAllPosLoopEdge = affectedEdges.getEdgePositions(edge);
				Map<Integer, List<Integer[]>> newMap = new HashMap<>();
				for (Integer trace : mapAllPosLoopEdge.keySet()) {
					List<Integer[]> allPos = mapAllPosLoopEdge.get(trace);
					List<Integer[]> newPos = new ArrayList<>();
					allPos.sort(Comparator.comparing(array -> array[0]));
					int curS = allPos.get(0)[0];
					int curE = allPos.get(0)[1];
					if (allPos.size() == 1) {
						newPos.add(new Integer[] { curS, curE });
					} else {
						for (int i = 1; i < allPos.size(); i++) {
							if (curE == allPos.get(i)[0]) {
								curE = allPos.get(i)[1];
							} else {
								newPos.add(new Integer[] { curS, curE });
								curS = allPos.get(i)[0];
								curE = allPos.get(i)[1];
							}
							if (i == allPos.size() - 1) {
								newPos.add(new Integer[] { curS, curE });
							}
						}
					}

					newMap.put(trace, newPos);
				}
				affectedEdges.getEdgeTable().remove(edge);
				affectedEdges.addEdge(edge, newMap);
			}
		}

		// for each merge loop edge, find its connecting edge to the left and to the right
		// keep the left, replace the right
		EdgeHashTable newMap = new EdgeHashTable();
		for (EdgeObject edge : loopEdges) {
			String start = edge.getNode1();
			Map<Integer, List<Integer[]>> mapAllPosLoopEdge = affectedEdges.getEdgePositions(edge);
			for (Integer trace : mapAllPosLoopEdge.keySet()) {
				TraceSkeleton traceSkeleton = Cl01GatherAttributes.originalLog.get(trace);
				for (Integer[] pos : mapAllPosLoopEdge.get(trace)) {
					int endPos = pos[1];
					int endIndex = pos[1] + 1;
					String end = "";
					if (endPos + 1 == traceSkeleton.getTrace().size()) {
						end = "end";
						endIndex = -2;
					} else {
						for (int i = endPos + 1; i < traceSkeleton.getTrace().size(); i++) {
							if (!checkedUnselectedActs.contains(traceSkeleton.getTrace().get(i).getActivity())) {
								end = traceSkeleton.getTrace().get(i).getActivity();
								endIndex = i;
								break;
							}
						}
						if (end.isEmpty()) {
							end = "end";
							endIndex = -2;
						}
					}
					EdgeObject newEdge = new EdgeObject(start, end, true);
					newMap.addEdge(newEdge, trace, pos[0], endIndex);
				}
			}
		}
		for (EdgeObject edge : loopEdges) {
			affectedEdges.getEdgeTable().remove(edge);
		}
		for (EdgeObject edge : newMap.getEdgeTable().keySet()) {
			affectedEdges.addEdge(edge, newMap.getEdgePositions(edge));
		}
	}

	public static void setupMapActToEdgeHighLevel(EdgeHashTable edgeHashTable, Config config) {
		// calculate the map of activities to each edge in the high level graph
		Map<String, List<EdgeObject>> mapActToEdgeHighLevel = new HashMap<String, List<EdgeObject>>();
		for (EdgeObject edgeObject : edgeHashTable.getEdgeTable().keySet()) {
			if (edgeObject.getIsIndirected()) {
				Map<Integer, List<Integer[]>> mapCasePos = edgeHashTable.getEdgeTable().get(edgeObject);
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

	public static Set<String> getTraceAttributes(XLog log) {
		return log.get(0).getAttributes().keySet();
	}

	public static Set<String> getEventAttributes(XLog log) {
		return log.get(0).get(0).getAttributes().keySet();
	}

	public static GDPMLogSkeleton getLogSkeleton(XLog log) {
		GDPMLogSkeleton logSkeleton = new GDPMLogSkeleton();
		Set<String> traceAttributes = getTraceAttributes(log);
		Set<String> eventAttributes = getEventAttributes(log);
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		String classifier = LogSkeletonUtils.getLogClassifier(log);

		int traceNum = 0;
		for (XTrace trace : log) {
			TraceSkeleton traceSkeleton = new TraceSkeleton();
			HashMap<String, Object> traceAttribute = new HashMap<String, Object>();
			// add trace attributes
			for (String attribute : traceAttributes) {
				traceAttribute.put(attribute, trace.getAttributes().get(attribute));
			}
			traceSkeleton.setAttributes(traceAttribute);

			// add events to trace
			for (int i = 0; i < trace.size(); i++) {
				XEvent event = trace.get(i);
				String act = event.getAttributes().get(classifier).toString();
				Long time = ((XAttributeTimestamp) event.getAttributes().get(TIME_CLASSIFIER)).getValueMillis();
				EventSkeleton eventSkeleton = new EventSkeleton(act, time, true);
				HashMap<String, Object> eventAttribute = new HashMap<String, Object>();
				for (String attribute : eventAttributes) {
					eventAttribute.put(attribute, event.getAttributes().get(attribute));
				}
				eventSkeleton.setAttributes(eventAttribute);
				// add event attributes

				// add event
				traceSkeleton.getTrace().add(eventSkeleton);
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
		logSkeleton.setEdgeHashTable(edgeHashTable);
		return logSkeleton;
	}

}
