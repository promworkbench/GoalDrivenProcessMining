package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import graph.GoalDrivenDFG;

public class MapGroupLogObject {
	private Map<String, _GDPMLog> mapGroupLog;
	private Map<String, GoalDrivenDFG> mapGroupDfg;
	

	public MapGroupLogObject() {
		this.mapGroupLog = new HashMap<>();
		this.mapGroupDfg = new HashMap<>();
	}

	public Map<String, _GDPMLog> getMapGroupLog() {
		return mapGroupLog;
	}

	public void setMapGroupLog(Map<String, _GDPMLog> mapGroupLog) {
		this.mapGroupLog = mapGroupLog;
	}

	public Map<String, GoalDrivenDFG> getMapGroupDfg() {
		return mapGroupDfg;
	}

	public void setMapGroupDfg(Map<String, GoalDrivenDFG> mapGroupDfg) {
		this.mapGroupDfg = mapGroupDfg;
	}

	public int hashCode() {
		return Objects.hash(mapGroupDfg, mapGroupLog);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapGroupLogObject other = (MapGroupLogObject) obj;
		return Objects.equals(mapGroupDfg, other.mapGroupDfg) && Objects.equals(mapGroupLog, other.mapGroupLog);
	}

	

	
}
