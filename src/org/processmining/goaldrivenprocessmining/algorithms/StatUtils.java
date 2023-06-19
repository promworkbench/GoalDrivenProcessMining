package org.processmining.goaldrivenprocessmining.algorithms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.MapStatObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;

public class StatUtils {
	public static HashMap<String, Object> processLog(XLog log, String timeClassifier) {
		String classifier = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		// act hash table
		ActivityHashTable activityHashTable = new ActivityHashTable();
		// stat
		MapStatObject statObject = new MapStatObject();
		HashMap<String, Integer> mapActOccurence = new HashMap<String, Integer>();
		HashMap<String, Long> mapActTime = new HashMap<String, Long>();
		int posTrace = 0;
		for (XTrace trace : log) {
			int posEvent = 0;
			for (int i = 0; i < trace.size(); i++) {
				XEvent event = trace.get(i);
				String actname = event.getAttributes().get(classifier).toString();
				//**** compute act hash table ****
				activityHashTable.addActivity(actname, posTrace, posEvent);
				posEvent++;
				//********************************
				LocalDateTime time = StatUtils.getDateTime(event.getAttributes().get(timeClassifier).toString());

				// update act occurence
				if (mapActOccurence.containsKey(actname)) {
					mapActOccurence.replace(actname, mapActOccurence.get(actname) + 1);
				} else {
					mapActOccurence.put(actname, 1);
				}

				// update act time
				XEvent nextEvent = null;
				LocalDateTime nextTime = null;
				if (i < trace.size() - 1) {
					nextEvent = trace.get(i + 1);
					nextTime = StatUtils.getDateTime(nextEvent.getAttributes().get(timeClassifier).toString());

					if (mapActTime.containsKey(actname)) {
						mapActTime.replace(actname,
								mapActTime.get(actname) + StatUtils.getDifferenceBetween2DateTime(time, nextTime));
					} else {
						mapActTime.put(actname, StatUtils.getDifferenceBetween2DateTime(time, nextTime));
					}
				}
				
			}
			posTrace++;
		}
		HashMap<String, StatNodeObject> mapStatNode = statObject.getMapStatNode();
		int numCase = log.size();
		for (String act : mapActOccurence.keySet()) {
			StatNodeObject statNodeObject = new StatNodeObject();
			String avgTime = StatUtils.getDateString(mapActTime.containsKey(act) ? mapActTime.get(act) / numCase : -1);
			statNodeObject.addStatValue(avgTime, Integer.toString(mapActOccurence.get(act)),
					Float.toString(((float) mapActOccurence.get(act)) / numCase));
			statNodeObject.setNodeName(act);
			mapStatNode.put(act, statNodeObject);
		}
		statObject.setMapStatNode(mapStatNode);
		HashMap<String, Object> res = new HashMap<>();
		res.put("Hash", activityHashTable);
		res.put("Stat", statObject);
		return res;

	}

//	public static MapStatObject computeStatNodeFromLog(XLog log, String classifier, String timeClassifier) {
//		MapStatObject result = new MapStatObject();
//		result.setLog(log);
//		HashMap<String, Integer> mapActOccurence = new HashMap<String, Integer>();
//		HashMap<String, Long> mapActTime = new HashMap<String, Long>();
//
//		for (XTrace trace : log) {
//			for (int i = 0; i < trace.size(); i++) {
//				XEvent event = trace.get(i);
//				String act = event.getAttributes().get(classifier).toString();
//				LocalDateTime time = StatUtils.getDateTime(event.getAttributes().get(timeClassifier).toString());
//
//				// update act occurence
//				if (mapActOccurence.containsKey(act)) {
//					mapActOccurence.replace(act, mapActOccurence.get(act) + 1);
//				} else {
//					mapActOccurence.put(act, 1);
//				}
//
//				// update act time
//				XEvent nextEvent = null;
//				LocalDateTime nextTime = null;
//				if (i < trace.size() - 1) {
//					nextEvent = trace.get(i + 1);
//					nextTime = StatUtils.getDateTime(nextEvent.getAttributes().get(timeClassifier).toString());
//
//					if (mapActTime.containsKey(act)) {
//						mapActTime.replace(act,
//								mapActTime.get(act) + StatUtils.getDifferenceBetween2DateTime(time, nextTime));
//					} else {
//						mapActTime.put(act, StatUtils.getDifferenceBetween2DateTime(time, nextTime));
//					}
//				}
//
//			}
//		}
//		HashMap<String, StatNodeObject> mapStatNode = result.getMapStatNode();
//		int numCase = log.size();
//		for (String act : mapActOccurence.keySet()) {
//			StatNodeObject statNodeObject = new StatNodeObject();
//			String avgTime = StatUtils.getDateString(mapActTime.containsKey(act) ? mapActTime.get(act) / numCase : -1);
//			statNodeObject.addStatValue(avgTime, Integer.toString(mapActOccurence.get(act)),
//					Float.toString(((float) mapActOccurence.get(act)) / numCase));
//			statNodeObject.setNodeName(act);
//			mapStatNode.put(act, statNodeObject);
//		}
//		result.setMapStatNode(mapStatNode);
//		return result;
//	}

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
//		System.out.println(StatUtils.computeStatNodeFromLog(log, "Activity", "time:timestamp"));
//
//	}

}
