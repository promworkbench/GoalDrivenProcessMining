package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.HashMap;

public class MapStatObject implements Serializable {
	private HashMap<String, ThroughputTimeObject> mapStatNode;
	private HashMap<EdgeObject, StatEdgeObject> mapStatEdge;

	public MapStatObject() {
		this.mapStatNode = new HashMap<>();
		this.mapStatEdge = new HashMap<>();
	}

	public HashMap<EdgeObject, StatEdgeObject> getMapStatEdge() {
		return mapStatEdge;
	}

	public void setMapStatEdge(HashMap<EdgeObject, StatEdgeObject> mapStatEdge) {
		this.mapStatEdge = mapStatEdge;
	}

	public HashMap<String, ThroughputTimeObject> getMapStatNode() {
		return mapStatNode;
	}

	public void setMapStatNode(HashMap<String, ThroughputTimeObject> mapStatNode) {
		this.mapStatNode = mapStatNode;
	}

	public String toString() {
		return "MapNodeStatObject [mapStatNode=" + mapStatNode + ", mapStatEdge=" + mapStatEdge + "]";
	}

}
