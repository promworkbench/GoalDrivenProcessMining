package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;

import org.deckfour.xes.model.XLog;

public class MapStatObject {
	private XLog log;
	private HashMap<String, StatNodeObject> mapStatNode;
	private HashMap<String, StatEdgeObject> mapStatEdge;

	public MapStatObject() {
		this.mapStatNode = new HashMap<>();
		this.mapStatEdge = new HashMap<>();
	}

	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public HashMap<String, StatEdgeObject> getMapStatEdge() {
		return mapStatEdge;
	}

	public void setMapStatEdge(HashMap<String, StatEdgeObject> mapStatEdge) {
		this.mapStatEdge = mapStatEdge;
	}

	public HashMap<String, StatNodeObject> getMapStatNode() {
		return mapStatNode;
	}

	public void setMapStatNode(HashMap<String, StatNodeObject> mapStatNode) {
		this.mapStatNode = mapStatNode;
	}

	public String toString() {
		return "MapNodeStatObject [mapStatNode=" + mapStatNode + ", mapStatEdge=" + mapStatEdge + "]";
	}

}