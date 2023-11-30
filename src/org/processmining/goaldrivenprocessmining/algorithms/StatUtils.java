package org.processmining.goaldrivenprocessmining.algorithms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import graph.GraphConstants;
import prefuse.data.Graph;
import prefuse.data.util.TableIterator;

public class StatUtils {

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
					float comma = ((float) time) / 1000 / 60 / 60 / 24 / 30 / 12;
					res = Float.toString(Math.round(comma * 10) / 10f) + " yrs";
				} else {
					float comma = ((float) time) / 1000 / 60 / 60 / 24 / 30;
					res = Float.toString(Math.round(comma * 10) / 10f) + " mo";
				}
			} else {
				if (month != 0) {
					if (month >= 3) {
						float comma = ((float) time) / 1000 / 60 / 60 / 24 / 30;
						res = Float.toString(Math.round(comma * 10) / 10f) + " mo";
					} else {
						float comma = ((float) time) / 1000 / 60 / 60 / 24;
						res = Float.toString(Math.round(comma * 10) / 10f) + " d";
					}
				} else {
					if (day != 0) {
						if (day >= 3) {
							float comma = ((float) time) / 1000 / 60 / 60 / 24;
							res = Float.toString(Math.round(comma * 10) / 10f) + " d";
						} else {
							float comma = ((float) time) / 1000 / 60 / 60;
							res = Float.toString(Math.round(comma * 10) / 10f) + " hrs";
						}
					} else {
						if (hour != 0) {
							if (hour >= 2) {
								float comma = ((float) time) / 1000 / 60 / 60;
								res = Float.toString(Math.round(comma * 10) / 10f) + " hrs";
							} else {
								float comma = ((float) time) / 1000 / 60;
								res = Float.toString(Math.round(comma * 10) / 10f) + " mins";
							}
						} else {
							float comma = ((float) time) / 1000 / 60;
							res = Float.toString(Math.round(comma * 10) / 10f) + " mins";
						}
					}
				}
			}
		}
		return res;
	}

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

}
