package org.processmining.goaldrivenprocessmining.algorithms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StatUtils {

//	public static MapStatObject getStat(LogSkeleton logSkeleton) {
//		ActivityHashTable activityHashTable = logSkeleton.getActivityHashTable();
//		EdgeHashTable edgeHashTable = logSkeleton.getEdgeHashTable();
//		MapStatObject statObject = new MapStatObject();
//		HashMap<String, Long> mapNodeTotalTime = new HashMap<>();
//		HashMap<String, Integer> mapGroupNodeTotalOccurence = new HashMap<>();
//
//		// stat node
//		for (Map.Entry<String, Map<Integer, List<Integer>>> entry : activityHashTable.getActivityTable().entrySet()) {
//			String act = entry.getKey();
//			Map<Integer, List<Integer>> value = entry.getValue();
//			int total = 0;
//			for (Map.Entry<Integer, List<Integer>> entry1 : value.entrySet()) {
//				total += entry1.getValue().size();
//			}
//			Boolean isGroupNode = false;
//			String groupName = "";
//			for (GroupSkeleton group : logSkeleton.getConfig().getListGroupSkeletons()) {
//				if (group.getListAct().contains(act)) {
//					isGroupNode = true;
//					groupName = group.getGroupName();
//					if (mapGroupNodeTotalOccurence.keySet().contains(groupName)) {
//						mapGroupNodeTotalOccurence.replace(groupName,
//								mapGroupNodeTotalOccurence.get(groupName) + total);
//					} else {
//						mapGroupNodeTotalOccurence.put(groupName, total);
//					}
//				}
//			}
//			if (!isGroupNode) {
//				statObject.getMapStatNode().put(act,
//						new StatNodeObject(total, (float) total / (float) logSkeleton.getLog().size()));
//			} else {
//				statObject.getMapStatNode().put(groupName, new StatNodeObject(mapGroupNodeTotalOccurence.get(groupName),
//						(float) mapGroupNodeTotalOccurence.get(groupName) / (float) logSkeleton.getLog().size()));
//			}
//
//		}
//		// stat edge
//		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : edgeHashTable.getEdgeTable().entrySet()) {
//			EdgeObject edge = entry.getKey();
//			String source = edge.getNode1().getCurrentName();
//			String target = edge.getNode2().getCurrentName();
//			Map<Integer, List<Integer[]>> value = entry.getValue();
//			int total = 0;
//			for (Map.Entry<Integer, List<Integer[]>> entry1 : value.entrySet()) {
//				total += entry1.getValue().size();
//
//				if (!source.equals("begin") && !target.equals("end")) {
//					int traceNum = entry1.getKey();
//					for (Integer[] pos : entry1.getValue()) {
//						int posSource = pos[0];
//						int posTarget = pos[1];
//						LocalDateTime startTime = StatUtils
//								.getDateTime(logSkeleton.getLog().get(traceNum).getTrace().get(posSource).getTime());
//						LocalDateTime endTime = StatUtils
//								.getDateTime(logSkeleton.getLog().get(traceNum).getTrace().get(posTarget).getTime());
//						long different = StatUtils.getDifferenceBetween2DateTime(startTime, endTime);
//						if (!mapNodeTotalTime.keySet().contains(source)) {
//							mapNodeTotalTime.put(source, different);
//						} else {
//							mapNodeTotalTime.replace(source, mapNodeTotalTime.get(source) + different);
//						}
//					}
//				}
//			}
//
////			statObject.getMapStatEdge().put(edge, new StatEdgeObject(total));
//		}
//		for (Map.Entry<String, Long> entry : mapNodeTotalTime.entrySet()) {
//			StatNodeObject statNodeObject = statObject.getMapStatNode().get(entry.getKey());
//			statNodeObject.setAvgThroughputTime(
//					StatUtils.getDateString(entry.getValue() / statNodeObject.getTotalOccurences()));
//			statObject.getMapStatNode().replace(entry.getKey(), statNodeObject);
//		}
//		return statObject;
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
	//		HashMap<String, Object> llHashMap = StatUtils.processLog(log, "time:timestamp");
	//		System.out.println(llHashMap);
	//	}

}
