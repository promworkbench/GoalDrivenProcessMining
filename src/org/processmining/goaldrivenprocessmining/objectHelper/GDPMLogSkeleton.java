package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class GDPMLogSkeleton implements Serializable {

	private LogSkeleton logSkeleton;
	private List<EdgeObject> listIndirectedEdge;
	private Map<String, NodeType> mapNodeType;
	// stat object
	private MapStatObject statObject;

	public GDPMLogSkeleton() {
		this.logSkeleton = new LogSkeleton();
		this.listIndirectedEdge = new ArrayList<>();
		this.mapNodeType = new HashMap<>();
		this.statObject = new MapStatObject();
	}

	public GDPMLogSkeleton(XLog log) {
		this.logSkeleton = LogSkeletonUtils.getLogSkeleton(log);
		this.listIndirectedEdge = new ArrayList<>();
		this.mapNodeType = new HashMap<>();
		this.statObject = StatUtils.getStat(logSkeleton);
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}

	public LogSkeleton getLogSkeleton() {
		return logSkeleton;
	}

	public void setLogSkeleton(LogSkeleton logSkeleton) {
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

	public String toString() {
		return "GDPMLogSkeleton [logSkeleton=" + logSkeleton + ", listIndirectedEdge=" + listIndirectedEdge
				+ ", mapNodeType=" + mapNodeType + ", statObject=" + statObject + "]";
	}

}
