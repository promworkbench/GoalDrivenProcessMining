package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.List;

public class FilterEdgeConfig {
	private EdgeObject edgeObject;
	private Integer[] threshold;
	private Boolean isHideIsolateActs;
	private List<Object[]> persistentPaths;

	public FilterEdgeConfig(EdgeObject edgeObject, Integer[] threshold, Boolean isHideIsolateActs,
			List<Object[]> persistentPaths) {
		this.edgeObject = edgeObject;
		this.threshold = threshold;
		this.isHideIsolateActs = isHideIsolateActs;
		this.persistentPaths = persistentPaths;
	}

	public EdgeObject getEdgeObject() {
		return edgeObject;
	}

	public void setEdgeObject(EdgeObject edgeObject) {
		this.edgeObject = edgeObject;
	}

	public Integer[] getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer[] threshold) {
		this.threshold = threshold;
	}

	public Boolean getIsHideIsolateActs() {
		return isHideIsolateActs;
	}

	public void setIsHideIsolateActs(Boolean isHideIsolateActs) {
		this.isHideIsolateActs = isHideIsolateActs;
	}

	public List<Object[]> getPersistentPaths() {
		return persistentPaths;
	}

	public void setPersistentPaths(List<Object[]> persistentPaths) {
		this.persistentPaths = persistentPaths;
	}
	
}
