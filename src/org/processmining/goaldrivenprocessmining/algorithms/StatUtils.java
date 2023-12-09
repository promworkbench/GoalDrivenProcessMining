package org.processmining.goaldrivenprocessmining.algorithms;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.goaldrivenprocessmining.algorithms.chain.CONFIG_Update;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;

import graph.GraphConstants;
import prefuse.data.Graph;
import prefuse.data.util.TableIterator;

public class StatUtils {

	/*--------------------------------------------------------------------------*/
	/* Calculate stat for act */
	public static Map<String, String> getFrequencyStatForAct(String act) {
		int freq = 0;
		Set<Integer> affectedCases = new HashSet();

		EdgeHashTable originalEdgeHashTable = Cl01GatherAttributes.originalEdgeHashTable;
		for (EdgeObject edgeObject : originalEdgeHashTable.getEdgeTable().keySet()) {
			if (edgeObject.getNode1().equals(act) || edgeObject.getNode2().equals(act)) {
				Map<Integer, List<Integer[]>> allPos = originalEdgeHashTable.getEdgePositions(edgeObject);
				// case loop -> x2
				if (edgeObject.getNode1().equals(edgeObject.getNode2())) {
					for (Map.Entry<Integer, List<Integer[]>> entry : allPos.entrySet()) {
						// freq
						freq += entry.getValue().size() * 2;
					}
				} else {
					for (Map.Entry<Integer, List<Integer[]>> entry : allPos.entrySet()) {
						// freq
						freq += entry.getValue().size();

					}
				}
				// case
				affectedCases.removeAll(allPos.keySet());
				affectedCases.addAll(allPos.keySet());
			}
		}
		Map<String, String> res = new HashMap<>();
		res.put("Occurence", Integer.toString(freq / 2));
		res.put("Case", Integer.toString(affectedCases.size()));

		return res;
	}

	/* Calculate waiting and leading acts */
	public static List<List<Object[]>> getThroughputStatForAct(String act) {
		List<List<Object[]>> res = new ArrayList<>();

		List<Object[]> waitingActsData = new ArrayList<>();
		List<Object[]> leadingActsData = new ArrayList<>();

		Map<EdgeObject, ThroughputTimeObject> mapEdgeThroughputTime;
		EdgeHashTable edgeHashTable;
		// check if act in high or low
		if (Arrays.asList(CONFIG_Update.currentConfig.getHighActs()).contains(act)) {
			mapEdgeThroughputTime = HIGH_MakeHighLevelLog.currentMapEdgeThroughputTime;
			edgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;
		} else {
			mapEdgeThroughputTime = Cl01GatherAttributes.originalMapEdgeThroughputTime;
			edgeHashTable = Cl01GatherAttributes.originalEdgeHashTable;
		}
		// find the affected edge
		for (EdgeObject edgeObject : mapEdgeThroughputTime.keySet()) {
			ThroughputTimeObject time = mapEdgeThroughputTime.get(edgeObject);
			// calculate the frequency
			int freq = 0;
			Map<Integer, List<Integer[]>> allPos = edgeHashTable.getEdgePositions(edgeObject);
			for (List<Integer[]> value : allPos.values()) {
				freq += value.size();
			}

			if (edgeObject.getNode2().equals(act)) {

				Object[] data = new Object[] { edgeObject.getNode1(), Integer.toString(freq),
						StatUtils.getDurationString(time.getMean()), StatUtils.getDurationString(time.getMedian()),
						StatUtils.getDurationString(time.getMin()), StatUtils.getDurationString(time.getMax()) };
				waitingActsData.add(data);
			}
			if (edgeObject.getNode1().equals(act)) {
				Object[] data = new Object[] { edgeObject.getNode2(), Integer.toString(freq),
						StatUtils.getDurationString(time.getMean()), StatUtils.getDurationString(time.getMedian()),
						StatUtils.getDurationString(time.getMin()), StatUtils.getDurationString(time.getMax()) };
				leadingActsData.add(data);
			}
		}
		res.add(waitingActsData);
		res.add(leadingActsData);

		return res;
	}

	/* Calculate stat for path */
	public static Map<String, String> getFrequencyStatForPath(EdgeObject edgeObject) {
		int freq = 0;
		int caseNum = 0;
		EdgeHashTable edgeHashTable = null;

		if (HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable.getEdgeTable().containsKey(edgeObject)) {
			edgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;
		} else if (Cl01GatherAttributes.originalEdgeHashTable.getEdgeTable().containsKey(edgeObject)) {
			edgeHashTable = Cl01GatherAttributes.originalEdgeHashTable;
		}
		if (edgeHashTable != null) {
			Map<Integer, List<Integer[]>> allPos = edgeHashTable.getEdgePositions(edgeObject);
			for (Map.Entry<Integer, List<Integer[]>> entry : allPos.entrySet()) {
				freq += entry.getValue().size();
			}
			caseNum = allPos.keySet().size();
		}

		Map<String, String> res = new HashMap<>();
		res.put("Occurence", Integer.toString(freq));
		res.put("Case", Integer.toString(caseNum));

		return res;
	}

	/* Calculate waiting and leading acts */
	public static Map<String, String> getThroughputStatForPath(EdgeObject edgeObject) {
		Map<EdgeObject, ThroughputTimeObject> mapEdgeThroughputTime = null;
		if (HIGH_MakeHighLevelLog.currentMapEdgeThroughputTime.containsKey(edgeObject)) {
			mapEdgeThroughputTime = HIGH_MakeHighLevelLog.currentMapEdgeThroughputTime;
		} else if (Cl01GatherAttributes.originalMapEdgeThroughputTime.containsKey(edgeObject)) {
			mapEdgeThroughputTime = Cl01GatherAttributes.originalMapEdgeThroughputTime;
		}
		Map<String, String> res = new HashMap<>();
		if (mapEdgeThroughputTime != null) {
			ThroughputTimeObject time = mapEdgeThroughputTime.get(edgeObject);
			res.put("Mean", StatUtils.getDurationString(time.getMean()));
			res.put("Median", StatUtils.getDurationString(time.getMedian()));
			res.put("Min", StatUtils.getDurationString(time.getMin()));
			res.put("Max", StatUtils.getDurationString(time.getMax()));

		}

		return res;
	}

	/*--------------------------------------------------------------------------*/
	/* Calculate trace attributes */
	public static Map<String, Map<String, Integer>> getMapCaseAttribute(List<Integer> caseIndex) {
		Map<String, Map<String, Integer>> res = new HashMap<>();

		List<TraceSkeleton> log = Cl01GatherAttributes.originalLog;
		for (Integer caseNum : caseIndex) {
			TraceSkeleton trace = log.get(caseNum);
			for (Map.Entry<String, Object> entry : trace.getAttributes().entrySet()) {
				if (entry.getValue() != null) {
					if (res.containsKey(entry.getKey())) {
						Map<String, Integer> map = res.get(entry.getKey());
						if (map.get(entry.getValue().toString()) == null) {
							map.put(entry.getValue().toString(), 1);
						} else {
							map.replace(entry.getValue().toString(), map.get(entry.getValue().toString()) + 1);
						}
					} else {
						Map<String, Integer> map = new HashMap<>();
						map.put(entry.getValue().toString(), 1);
						res.put(entry.getKey(), map);
					}
				}
			}
		}

		return res;
	}

	/*--------------------------------------------------------------------------*/
	/* Long time -> Readable string */
	public static String convertMillisToDateString(long milliseconds) {
		// Convert milliseconds to LocalDateTime
		Instant instant = Instant.ofEpochMilli(milliseconds);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());

		// Format LocalDateTime to a String
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTime.format(formatter);
	}

	public static String getDurationString(long time) {
		String res = "";
		if (time != -1) {
			long year = time / 1000 / 60 / 60 / 24 / 30 / 12;
			long month = time / 1000 / 60 / 60 / 24 / 30;
			long day = time / 1000 / 60 / 60 / 24;
			long hour = time / 1000 / 60 / 60;

			if (year != 0) {
				if (year >= 3) {
					double comma = ((double) time) / 1000 / 60 / 60 / 24 / 30.41 / 12;
					res = String.format("%.1f yrs", comma);
				} else {
					double comma = ((double) time) / 1000 / 60 / 60 / 24 / 30.41;
					res = String.format("%.1f mo", comma);
				}
			} else {
				if (month != 0) {
					if (month >= 3) {
						double comma = ((double) time) / 1000 / 60 / 60 / 24 / 30.41;
						res = String.format("%.1f mo", comma);
					} else {
						float comma = ((float) time) / 1000 / 60 / 60 / 24;
						res = String.format("%.1f d", comma);
					}
				} else {
					if (day != 0) {
						if (day >= 3) {
							float comma = ((float) time) / 1000 / 60 / 60 / 24;
							res = String.format("%.1f d", comma);
						} else {
							float comma = ((float) time) / 1000 / 60 / 60;
							res = String.format("%.1f hrs", comma);
						}
					} else {
						if (hour != 0) {
							if (hour >= 2) {
								float comma = ((float) time) / 1000 / 60 / 60;
								res = String.format("%.1f hrs", comma);
							} else {
								float comma = ((float) time) / 1000 / 60;
								res = String.format("%.1f mins", comma);
								;
							}
						} else {
							float comma = ((float) time) / 1000 / 60;
							res = String.format("%.1f mins", comma);
							;
						}
					}
				}
			}
		}
		return res;
	}

	public static double convertTimeStringToSeconds(String value) {
		// Extract numeric part and string
		Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*([a-zA-Z]+)");
		Matcher matcher = pattern.matcher(value);

		if (matcher.find()) {
			double number = Double.parseDouble(matcher.group(1));

			String unit = matcher.group(2).toLowerCase();
			// Map of time units to seconds
			int secondsInMinute = 60;
			int secondsInHour = 60 * secondsInMinute;
			int secondsInDay = 24 * secondsInHour;
			int secondsInMonth = (int) (30.41 * secondsInDay); // Assuming an average month
			int secondsInYear = 12 * secondsInMonth; // Assuming an average year

			// Convert to seconds
			switch (unit) {
				case "mins" :
					return number * secondsInMinute;
				case "hrs" :
					return number * secondsInHour;
				case "d" :
					return number * secondsInDay;
				case "mo" :
					return number * secondsInMonth;
				case "yrs" :
					return number * secondsInYear;
				default :
					// Handle unknown units or return 0
					return 0;
			}
		}

		return 0; // Return 0 if no match is found
	}

	public static void main(String[] args) {
		System.out.println(StatUtils.getDurationString(566092800000l));
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(false);
		String formattedValue2 = numberFormat
				.format(StatUtils.convertTimeStringToSeconds(StatUtils.getDurationString(566092800000l)));
		System.out.println(formattedValue2);
	}
	/*--------------------------------------------------------------------------*/

	/*--------------------------------------------------------------------------*/
	/* Calculate disconnected acts */
	public static DefaultDirectedGraph<String, DefaultEdge> getJGraphTFromGraph(Graph graph) {
		List<String> allActs = new ArrayList<>();
		DefaultDirectedGraph<String, DefaultEdge> graphT = new DefaultDirectedGraph<>(DefaultEdge.class);

		TableIterator nodes = graph.getNodeTable().iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (graph.getNodeTable().isValidRow(row)) {
				if (graph.getNodeTable().getBoolean(row, GraphConstants.IS_DISPLAY)) {
					String act = graph.getNodeTable().getString(row, GraphConstants.LABEL_FIELD);
					act = act.equals("**BEGIN**") ? "begin" : act;
					act = act.equals("**END**") ? "end" : act;
					allActs.add(act);
					graphT.addVertex(act);
				}

			}
		}
		TableIterator edges = graph.getEdgeTable().iterator();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (graph.getEdgeTable().isValidRow(row)) {
				if (graph.getEdgeTable().getBoolean(row, GraphConstants.IS_DISPLAY)) {
					int source = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
					int target = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
					String sString = graph.getNodeTable().getString(source, GraphConstants.LABEL_FIELD);
					String tString = graph.getNodeTable().getString(target, GraphConstants.LABEL_FIELD);
					sString = sString.equals("**BEGIN**") ? "begin" : sString;
					tString = tString.equals("**END**") ? "end" : tString;
					graphT.addEdge(sString, tString);
				}
			}
		}
		return graphT;
	}

	public static List<String> getDisconnectedBeginToActsFromDFG(Graph graph) {
		List<String> result = new ArrayList<String>();
		List<String> allActs = new ArrayList<>();
		DefaultDirectedGraph<String, DefaultEdge> graphT = getJGraphTFromGraph(graph);
		TableIterator nodes = graph.getNodeTable().iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (graph.getNodeTable().isValidRow(row)) {
				if (graph.getNodeTable().getBoolean(row, GraphConstants.IS_DISPLAY)) {
					String act = graph.getNodeTable().getString(row, GraphConstants.LABEL_FIELD);
					act = act.equals("**BEGIN**") ? "begin" : act;
					act = act.equals("**END**") ? "end" : act;
					allActs.add(act);
				}
			}
		}
		for (String act : allActs) {
			if (!act.equals("begin") && !act.equals("end")) {
				List<DefaultEdge> pathBegin = DijkstraShortestPath.findPathBetween(graphT, "begin", act);
				if (pathBegin == null) {
					result.add(act);
				}
			}
		}

		return result;
	}

	public static List<String> getDisconnectedActsToEndFromDFG(Graph graph) {
		List<String> result = new ArrayList<String>();
		List<String> allActs = new ArrayList<>();
		DefaultDirectedGraph<String, DefaultEdge> graphT = getJGraphTFromGraph(graph);
		TableIterator nodes = graph.getNodeTable().iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (graph.getNodeTable().isValidRow(row)) {
				if (graph.getNodeTable().getBoolean(row, GraphConstants.IS_DISPLAY)) {
					String act = graph.getNodeTable().getString(row, GraphConstants.LABEL_FIELD);
					act = act.equals("**BEGIN**") ? "begin" : act;
					act = act.equals("**END**") ? "end" : act;
					allActs.add(act);
				}
			}
		}
		for (String act : allActs) {
			if (!act.equals("begin") && !act.equals("end")) {
				List<DefaultEdge> pathEnd = DijkstraShortestPath.findPathBetween(graphT, act, "end");
				if (pathEnd == null) {
					result.add(act);
				}
			}
		}

		return result;
	}
	/*--------------------------------------------------------------------------*/
}
