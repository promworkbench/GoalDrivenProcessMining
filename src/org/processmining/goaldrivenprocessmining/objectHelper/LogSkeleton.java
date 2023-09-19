package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogSkeleton implements Serializable {
	private ActivityHashTable activityHashTable;
	private EdgeHashTable edgeHashTable;
	private List<TraceSkeleton> log;
	private HashMap<String, GroupSkeleton> groupConfig;
	private List<EdgeObject> listIndirectedEdges;

	public LogSkeleton() {
		this.log = new ArrayList<>();
		this.groupConfig = new HashMap<>();
		this.activityHashTable = new ActivityHashTable();
		this.edgeHashTable = new EdgeHashTable();
	}

	public void addGroup(GroupSkeleton groupSkeleton) {
		if (this.groupConfig.keySet().contains(groupSkeleton.getGroupName())) {
			this.groupConfig.replace(groupSkeleton.getGroupName(), groupSkeleton);
		} else {
			this.groupConfig.put(groupSkeleton.getGroupName(), groupSkeleton);
		}
	}

	public Boolean isInGroup(String act) {
		for (String key : this.groupConfig.keySet()) {
			if (this.groupConfig.get(key).getListAct().contains(act)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getAllActivitiesInGroup(GroupSkeleton groupSkeleton) {
		List<String> result = new ArrayList<>();
		result.addAll(groupSkeleton.getListAct());
		
		for (GroupSkeleton group: groupSkeleton.getListGroup()) {
			result.addAll(getAllActivitiesInGroup(group));
		}
		
		return result;
	}

	public ActivityHashTable getActivityHashTable() {
		return activityHashTable;
	}

	public void setActivityHashTable(ActivityHashTable activityHashTable) {
		this.activityHashTable = activityHashTable;
	}

	public List<TraceSkeleton> getLog() {
		return log;
	}

	public void setLog(List<TraceSkeleton> log) {
		this.log = log;
	}

	public EdgeHashTable getEdgeHashTable() {
		return edgeHashTable;
	}

	public void setEdgeHashTable(EdgeHashTable edgeHashTable) {
		this.edgeHashTable = edgeHashTable;
	}

	public HashMap<String, GroupSkeleton> getGroupConfig() {
		return groupConfig;
	}

	public void setGroupConfig(HashMap<String, GroupSkeleton> groupConfig) {
		this.groupConfig = groupConfig;
	}

	public String toString() {
		return "LogSkeleton [activityHashTable=" + activityHashTable + ", edgeHashTable=" + edgeHashTable + ", log="
				+ log + ", groupConfig=" + groupConfig + "]";
	}

}
