package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

public class ActivityHashTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1830613519325919243L;
	private Map<String, Map<Integer, List<Integer>>> activityTable;

	public ActivityHashTable() {
		this.activityTable = new HashMap<>();
	}

	public void addActivity(String activityName, int caseNumber, int position) {
		Map<Integer, List<Integer>> caseTable = activityTable.get(activityName);
		if (caseTable == null) {
			caseTable = new HashMap<>();
			activityTable.put(activityName, caseTable);
		}
		List<Integer> positions = caseTable.get(caseNumber);
		if (positions == null) {
			positions = new ArrayList<>();
			caseTable.put(caseNumber, positions);
		}
		positions.add(position);
	}

	public Map<Integer, List<Integer>> getActivityPositions(String activityName) {
		Map<Integer, List<Integer>> caseTable = activityTable.get(activityName);
		if (caseTable != null) {
			return caseTable;
		}
		return null;
	}

	public Map<String, Map<Integer, List<Integer>>> getActivityTable() {
		return activityTable;
	}

	public void setActivityTable(Map<String, Map<Integer, List<Integer>>> activityTable) {
		this.activityTable = activityTable;
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}

	public String toString() {
		return "ActivityHashTable [activityTable=" + activityTable + "]";
	}

}
