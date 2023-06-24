package org.processmining.goaldrivenprocessmining.algorithms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityIndexMapper;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapStatObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;

public class StatUtils {

	public static void updateStat(GDPMLogSkeleton log) throws Exception {
		HashMap<Integer, List<Integer>> logSkeleton = log.getLogSkeleton();
		HashMap<String, Integer> mapActOccurence = new HashMap<String, Integer>();
		HashMap<String, Long> mapActTime = new HashMap<String, Long>();
		List<EdgeObject> listEdges = new ArrayList<>();
		HashMap<EdgeObject, Integer> mapEdgeFrequency = new HashMap<>();

		for (Map.Entry<Integer, List<Integer>> entry : logSkeleton.entrySet()) {
			int key = entry.getKey();
			List<Integer> events = entry.getValue();
			for (int i = 0; i < events.size(); i++) {
				String eventName = log.getActivityIndexMapper().getActivityFromIndex(events.get(i));
				// update act occurence
				{
					if (mapActOccurence.containsKey(eventName)) {
						mapActOccurence.replace(eventName, mapActOccurence.get(eventName) + 1);
					} else {
						mapActOccurence.put(eventName, 1);
					}
				}
				// update act time
				{
					LocalDateTime time = StatUtils.getDateTime(log.getTimeSkeleton().get(key).get(i));
					LocalDateTime nextTime = null;
					if (i < events.size() - 1) {
						nextTime = StatUtils.getDateTime(log.getTimeSkeleton().get(key).get(i + 1));
						if (mapActTime.containsKey(eventName)) {
							mapActTime.replace(eventName, mapActTime.get(eventName)
									+ StatUtils.getDifferenceBetween2DateTime(time, nextTime));
						} else {
							mapActTime.put(eventName, StatUtils.getDifferenceBetween2DateTime(time, nextTime));
						}
					}
				}
				// stat edge
				{
					if (i + 1 < events.size()) {
						String val2 = log.getActivityIndexMapper().getActivityFromIndex(log.getLogSkeleton().get(key).get(i + 1));
						EdgeObject edge = new EdgeObject(eventName, val2);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}

					}
					if (i == 0) {
						String val1 = "begin";
						EdgeObject edge = new EdgeObject(val1, eventName);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}
					}
					if (i == events.size() - 1) {
						String val2 = "end";
						EdgeObject edge = new EdgeObject(eventName, val2);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}
					}
				}
				
			}

		}
		// Stat node
		MapStatObject statObject = new MapStatObject();
		int numCase = log.getMapTraceLength().size();
		for (String act : mapActOccurence.keySet()) {
			String avgTime = StatUtils.getDateString(mapActTime.containsKey(act) ? mapActTime.get(act) / numCase : -1);
			StatNodeObject statNodeObject = new StatNodeObject(avgTime, mapActOccurence.get(act),
					((float) mapActOccurence.get(act)) / numCase);
			statObject.getMapStatNode().put(act, statNodeObject);
		}
		// Stat edge
		for (EdgeObject edge : mapEdgeFrequency.keySet()) {
			StatEdgeObject statEdgeObject = new StatEdgeObject(mapEdgeFrequency.get(edge));
			statObject.getMapStatEdge().put(edge, statEdgeObject);
		}
		log.setStatObject(statObject);

	}

	public static GDPMLogSkeleton processLog(XLog log, String timeClassifier) {
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		// act hash table
		ActivityHashTable activityHashTable = new ActivityHashTable();
		// stat node
		MapStatObject statObject = new MapStatObject();
		HashMap<String, Integer> mapActOccurence = new HashMap<String, Integer>();
		HashMap<String, Long> mapActTime = new HashMap<String, Long>();
		// stat edge
		HashMap<EdgeObject, Integer> mapEdgeFrequency = new HashMap<>();
		// GDPMLogSkeleton
		GDPMLogSkeleton gdpmLogSkeleton = new GDPMLogSkeleton();
		List<String> allAct = LogSkeletonUtils.getAllUniqueActivities(log);
		ActivityIndexMapper activityIndexMapper = new ActivityIndexMapper();
		activityIndexMapper.assignActivity(allAct);
		gdpmLogSkeleton.setActivityIndexMapper(activityIndexMapper);

		int posTrace = 0;
		List<EdgeObject> listEdges = new ArrayList<>();
		for (XTrace trace : log) {
			int posEvent = 0;
			List<Integer> listEventSkeletons = new ArrayList<>();
			List<String> listTimeSkeletons = new ArrayList<>();
			for (int i = 0; i < trace.size(); i++) {
				XEvent event = trace.get(i);
				String eventName = event.getAttributes().get(classifier).toString();
				//compute act hash table 
				{
					activityHashTable.addActivity(eventName, posTrace, posEvent);

				}
				// update act occurence
				{
					if (mapActOccurence.containsKey(eventName)) {
						mapActOccurence.replace(eventName, mapActOccurence.get(eventName) + 1);
					} else {
						mapActOccurence.put(eventName, 1);
					}
				}
				// update act time
				{
					LocalDateTime time = StatUtils.getDateTime(event.getAttributes().get(timeClassifier).toString());
					XEvent nextEvent = null;
					LocalDateTime nextTime = null;
					if (i < trace.size() - 1) {
						nextEvent = trace.get(i + 1);
						nextTime = StatUtils.getDateTime(nextEvent.getAttributes().get(timeClassifier).toString());

						if (mapActTime.containsKey(eventName)) {
							mapActTime.replace(eventName, mapActTime.get(eventName)
									+ StatUtils.getDifferenceBetween2DateTime(time, nextTime));
						} else {
							mapActTime.put(eventName, StatUtils.getDifferenceBetween2DateTime(time, nextTime));
						}
					}
				}
				//GDPM log skeleton
				{
					try {
						listEventSkeletons.add(activityIndexMapper
								.getIndexFromActivity(event.getAttributes().get(classifier).toString()));
						listTimeSkeletons.add(event.getAttributes().get(timeClassifier).toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// stat edge
				{
					if (i + 1 < trace.size()) {
						XEvent ev2 = trace.get(i + 1);
						String val2 = ev2.getAttributes().get(classifier).toString();
						EdgeObject edge = new EdgeObject(eventName, val2);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}

					}
					if (i == 0) {
						String val1 = "begin";
						EdgeObject edge = new EdgeObject(val1, eventName);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}
					}
					if (i == trace.size() - 1) {
						String val2 = "end";
						EdgeObject edge = new EdgeObject(eventName, val2);
						if (!listEdges.contains(edge)) {
							listEdges.add(edge);
							mapEdgeFrequency.put(edge, 1);
						} else {
							int curFreq = mapEdgeFrequency.get(edge);
							mapEdgeFrequency.replace(edge, curFreq + 1);
						}
					}
				}
				posEvent++;

			}
			gdpmLogSkeleton.getLogSkeleton().put(posTrace, listEventSkeletons);
			gdpmLogSkeleton.getTimeSkeleton().put(posTrace, listTimeSkeletons);
			gdpmLogSkeleton.getMapTraceLength().put(posTrace, trace.size());
			posTrace++;
		}
		gdpmLogSkeleton.setActivityHashTable(activityHashTable);

		// Stat node
		HashMap<String, StatNodeObject> mapStatNode = statObject.getMapStatNode();
		int numCase = log.size();
		for (String act : mapActOccurence.keySet()) {
			String avgTime = StatUtils.getDateString(mapActTime.containsKey(act) ? mapActTime.get(act) / numCase : -1);
			StatNodeObject statNodeObject = new StatNodeObject(avgTime, mapActOccurence.get(act),
					((float) mapActOccurence.get(act)) / numCase);
			mapStatNode.put(act, statNodeObject);
		}
		statObject.setMapStatNode(mapStatNode);
		// Stat edge
		for (EdgeObject edge : mapEdgeFrequency.keySet()) {
			StatEdgeObject statEdgeObject = new StatEdgeObject(mapEdgeFrequency.get(edge));
			statObject.getMapStatEdge().put(edge, statEdgeObject);
		}

		gdpmLogSkeleton.setStatObject(statObject);

		return gdpmLogSkeleton;

	}

	public static String getDateString(long time) {
		String res = "";
		if (time != -1) {
			long day = time / 1000 / 60 / 60 / 24;
			long hour = (time - day * 1000 * 60 * 60 * 24) / 1000 / 60 / 60;
			long minute = (time - day * 1000 * 60 * 60 * 24 - hour * 1000 * 60 * 60) / 1000 / 60;
			long second = (time - day * 1000 * 60 * 60 * 24 - hour * 1000 * 60 * 60 - minute * 1000 * 60) / 1000;

			if (day != 0) {
				res += Long.toString(day) + " days ";
			}
			if (hour != 0) {
				res += Long.toString(hour) + " hours ";
			}
			if (minute != 0) {
				res += Long.toString(minute) + " minutes ";
			}
			if (second != 0) {
				res += Long.toString(second) + " seconds";
			}
		}
		return res;

	}

	public static long getDifferenceBetween2DateTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
		// Convert LocalDateTime to Instant.
		Instant instant1 = dateTime1.atZone(ZoneId.systemDefault()).toInstant();
		Instant instant2 = dateTime2.atZone(ZoneId.systemDefault()).toInstant();

		// Get the difference in milliseconds.
		return Math.abs(instant2.toEpochMilli() - instant1.toEpochMilli());

	}

	public static LocalDateTime getDateTime(String time) {
		for (String format : GoalDrivenConstants.DATA_TIME_FORMAT) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				LocalDateTime date = LocalDateTime.parse(time, formatter);
				return date;
			} catch (DateTimeParseException e) {
				// If the date doesn't match the format, continue with the next format.
			}
		}
		return null;
	}

	//	public static void main(String[] args) throws Exception {
	//		File file = new File("C:\\D\\data\\running-example.xes");
	//
	//		// Create an input stream for the XES file
	//		InputStream is = new FileInputStream(file);
	//
	//		// Create a parser for XES files
	//		XesXmlParser parser = new XesXmlParser();
	//
	//		XLog log = parser.parse(is).get(0);
	//
	//		HashMap<String, Object> llHashMap = StatUtils.processLog(log, "time:timestamp");
	//		System.out.println(llHashMap);
	//	}

}
