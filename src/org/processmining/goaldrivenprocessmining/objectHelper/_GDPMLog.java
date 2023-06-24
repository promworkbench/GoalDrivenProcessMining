package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

public class _GDPMLog {
	private XLog log;
	private IndirectedEdgeCarrierObject indirectedEdges;
	private Map<String, NodeType> mapNodeType;
	
	public _GDPMLog(XLog log) {
		this.log = log;
		this.indirectedEdges = new IndirectedEdgeCarrierObject();
		this.mapNodeType = new HashMap<>();
	}
	public _GDPMLog(XLog log, IndirectedEdgeCarrierObject indirectedEdges) {
		this.log = log;
		this.indirectedEdges = indirectedEdges;
		this.mapNodeType = new HashMap<>();
	}
	public XLog getLog() {
		return log;
	}
	public void setLog(XLog log) {
		this.log = log;
	}
	public IndirectedEdgeCarrierObject getIndirectedEdges() {
		return indirectedEdges;
	}
	public void setIndirectedEdges(IndirectedEdgeCarrierObject indirectedEdges) {
		this.indirectedEdges = indirectedEdges;
	}
	public Map<String, NodeType> getMapNodeType() {
		return mapNodeType;
	}
	public void setMapNodeType(Map<String, NodeType> mapNodeType) {
		this.mapNodeType = mapNodeType;
	}
	
}
