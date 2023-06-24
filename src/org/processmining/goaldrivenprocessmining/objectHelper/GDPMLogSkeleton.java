package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class GDPMLogSkeleton implements Serializable {
	// activity hash table
	private ActivityHashTable activityHashTable;
	// [trace pos -> [index of act]]
	private HashMap<Integer, List<Integer>> logSkeleton;
	// [trace pos -> [time]]
	private HashMap<Integer, List<String>> timeSkeleton;
	// trace length
	private HashMap<Integer, Integer> mapTraceLength;
	// activity index map
	private ActivityIndexMapper activityIndexMapper;
	private List<EdgeObject> listIndirectedEdge;
	private Map<String, NodeType> mapNodeType;
	// stat object
	private MapStatObject statObject;

	public GDPMLogSkeleton() {
		this.logSkeleton = new HashMap<>();
		this.listIndirectedEdge = new ArrayList<>();
		this.mapNodeType = new HashMap<>();
		this.activityHashTable = new ActivityHashTable();
		this.mapTraceLength = new HashMap<>();
		this.activityIndexMapper = new ActivityIndexMapper();
		this.statObject = new MapStatObject();
		this.timeSkeleton = new HashMap<>();
	}

	public String getActNameAtPosition(int posTrace, int posEvent) {
		try {
			return this.activityIndexMapper.getActivityFromIndex(this.logSkeleton.get(posTrace).get(posEvent));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Error getting activity name from index");
		}
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}

	public HashMap<Integer, List<Integer>> getLogSkeleton() {
		return logSkeleton;
	}

	public void setLogSkeleton(HashMap<Integer, List<Integer>> logSkeleton) {
		this.logSkeleton = logSkeleton;
	}

	public List<EdgeObject> getListIndirectedEdge() {
		return listIndirectedEdge;
	}

	public void setListIndirectedEdge(List<EdgeObject> listIndirectedEdge) {
		this.listIndirectedEdge = listIndirectedEdge;
	}

	public Map<String, NodeType> getMapNodeType() {
		return mapNodeType;
	}

	public void setMapNodeType(Map<String, NodeType> mapNodeType) {
		this.mapNodeType = mapNodeType;
	}

	public ActivityHashTable getActivityHashTable() {
		return activityHashTable;
	}

	public void setActivityHashTable(ActivityHashTable activityHashTable) {
		this.activityHashTable = activityHashTable;
	}

	public HashMap<Integer, Integer> getMapTraceLength() {
		return mapTraceLength;
	}

	public void setMapTraceLength(HashMap<Integer, Integer> mapTraceLength) {
		this.mapTraceLength = mapTraceLength;
	}

	public ActivityIndexMapper getActivityIndexMapper() {
		return activityIndexMapper;
	}

	public void setActivityIndexMapper(ActivityIndexMapper activityIndexMapper) {
		this.activityIndexMapper = activityIndexMapper;
	}

	public void addEdge(EdgeObject tupleNode) {
		if (!listIndirectedEdge.contains(tupleNode)) {
			this.listIndirectedEdge.add(tupleNode);
		}
	}

	public MapStatObject getStatObject() {
		return statObject;
	}

	public void setStatObject(MapStatObject statObject) {
		this.statObject = statObject;
	}

	public HashMap<Integer, List<String>> getTimeSkeleton() {
		return timeSkeleton;
	}

	public void setTimeSkeleton(HashMap<Integer, List<String>> timeSkeleton) {
		this.timeSkeleton = timeSkeleton;
	}

	public String toString() {
		return "GDPMLogSkeleton [logSkeleton=" + logSkeleton + ", listIndirectedEdge=" + listIndirectedEdge
				+ ", mapNodeType=" + mapNodeType + "]";
	}

}
