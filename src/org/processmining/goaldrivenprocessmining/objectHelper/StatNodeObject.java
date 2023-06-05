package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;
import java.util.Objects;

public class StatNodeObject {
	
	public static final String AVG_THROUGHPUT_TIME = "Average throughput time";
	public static final String TOTAL_OCCURENCE = "Total number of occurrences";
	public static final String AVG_OCCURENCE = "Average number of occurrences per case";
	
	private String nodeName;
	private HashMap<String, String> statValueHashMap;
	
	public StatNodeObject() {
		this("", new HashMap<>());
	}
	public StatNodeObject(String nodeName, HashMap<String, String> statValueHashMap) {
		this.nodeName = nodeName;
		this.statValueHashMap = statValueHashMap;
	}
	public void addStatValue(String avgThroughputTime, String totalOccurence, String avgOccurence) {
		this.statValueHashMap.put(AVG_THROUGHPUT_TIME, avgThroughputTime);
		this.statValueHashMap.put(TOTAL_OCCURENCE, totalOccurence);
		this.statValueHashMap.put(AVG_OCCURENCE, avgOccurence);
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public HashMap<String, String> getStatValueHashMap() {
		return statValueHashMap;
	}
	public void setStatValueHashMap(HashMap<String, String> statValueHashMap) {
		this.statValueHashMap = statValueHashMap;
	}
	public String toString() {
		return "StatNode [nodeName=" + nodeName + ", statValueHashMap=" + statValueHashMap + "]";
	}
	public int hashCode() {
		return Objects.hash(nodeName, statValueHashMap);
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatNodeObject other = (StatNodeObject) obj;
		return Objects.equals(nodeName, other.nodeName) && Objects.equals(statValueHashMap, other.statValueHashMap);
	}
	
		
}
