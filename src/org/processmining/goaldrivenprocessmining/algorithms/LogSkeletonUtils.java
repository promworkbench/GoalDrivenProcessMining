package org.processmining.goaldrivenprocessmining.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
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
		setupMapActToEdgeHighLevel(newEdgeHashTable, config);
		/*-----------------*/
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

	public static GDPMLogSkeleton getLogSkeleton(XLog log) {
		GDPMLogSkeleton logSkeleton = new GDPMLogSkeleton();
		EdgeHashTable edgeHashTable = new EdgeHashTable();
		String classifier = LogSkeletonUtils.getLogClassifier(log);

		int traceNum = 0;
		for (XTrace trace : log) {
			TraceSkeleton traceSkeleton = new TraceSkeleton();
			for (int i = 0; i < trace.size(); i++) {
				XEvent event = trace.get(i);
				String act = event.getAttributes().get(classifier).toString();
				Long time = ((XAttributeTimestampImpl) event.getAttributes().get(TIME_CLASSIFIER)).getValueMillis();
				EventSkeleton eventSkeleton = new EventSkeleton(act, time, true);
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
