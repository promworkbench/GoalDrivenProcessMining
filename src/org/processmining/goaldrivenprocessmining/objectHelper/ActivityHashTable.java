package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityHashTable {
	private Map<String, Map<Integer, List<Integer>>> activityTable;

    public ActivityHashTable() {
        activityTable = new HashMap<>();
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

	public String toString() {
		return "ActivityHashTable [activityTable=" + activityTable + "]";
	}
    
}
