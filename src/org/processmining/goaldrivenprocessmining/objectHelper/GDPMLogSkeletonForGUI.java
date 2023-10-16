package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.Map;

public class GDPMLogSkeletonForGUI {
	Map<String, Integer> mapNodesFreq;
	Map<String[], Integer> mapEdgesFreq;
	Map<String[], Long> mapEdgesThroughputTime;

	public GDPMLogSkeletonForGUI(Map<String, Integer> mapNodesFreq, Map<String[], Integer> mapEdgesFreq,
			Map<String[], Long> mapEdgesThroughputTime) {
		this.mapNodesFreq = mapNodesFreq;
		this.mapEdgesFreq = mapEdgesFreq;
		this.mapEdgesThroughputTime = mapEdgesThroughputTime;
	}

	public Map<String, Integer> getMapNodesFreq() {
		return mapNodesFreq;
	}

	public void setMapNodesFreq(Map<String, Integer> mapNodesFreq) {
		this.mapNodesFreq = mapNodesFreq;
	}

	public Map<String[], Integer> getMapEdgesFreq() {
		return mapEdgesFreq;
	}

	public void setMapEdgesFreq(Map<String[], Integer> mapEdgesFreq) {
		this.mapEdgesFreq = mapEdgesFreq;
	}

	public Map<String[], Long> getMapEdgesThroughputTime() {
		return mapEdgesThroughputTime;
	}

	public void setMapEdgesThroughputTime(Map<String[], Long> mapEdgesThroughputTime) {
		this.mapEdgesThroughputTime = mapEdgesThroughputTime;
	}

}
