package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.Map;
import java.util.Objects;

public class MapGroupLogObject {
	private Map<SelectedNodeGroupObject, GDPMLog> mapGroupLog;

	public Map<SelectedNodeGroupObject, GDPMLog> getMapGroupLog() {
		return mapGroupLog;
	}

	public void setMapGroupLog(Map<SelectedNodeGroupObject, GDPMLog> mapGroupLog) {
		this.mapGroupLog = mapGroupLog;
	}

	public int hashCode() {
		return Objects.hash(mapGroupLog);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapGroupLogObject other = (MapGroupLogObject) obj;
		return Objects.equals(mapGroupLog, other.mapGroupLog);
	}

	
}
