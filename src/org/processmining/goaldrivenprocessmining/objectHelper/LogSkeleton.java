package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogSkeleton implements Serializable {
	private ActivityHashTable activityHashTable;
	private EdgeHashTable edgeHashTable;
	private List<TraceSkeleton> log;
	private HashMap<String, List<String>> groupConfig;

	public LogSkeleton() {
		this.log = new ArrayList<>();
		this.groupConfig = new HashMap<>();
		this.activityHashTable = new ActivityHashTable();
		this.edgeHashTable = new EdgeHashTable();
	}

	public void addGroup(String groupName, List<String> listActivities) {
		List<String> newListActivies = new ArrayList<>();
		for (String act : listActivities) {
			if (this.groupConfig.keySet().contains(act)) {
				newListActivies.addAll(this.groupConfig.get(act));
				this.groupConfig.remove(act);
			} else {
				newListActivies.add(act);
			}
		}
		this.groupConfig.put(groupName, newListActivies);
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

	public HashMap<String, List<String>> getGroupConfig() {
		return groupConfig;
	}

	public void setGroupConfig(HashMap<String, List<String>> groupConfig) {
		this.groupConfig = groupConfig;
	}

	public String toString() {
		return "LogSkeleton [activityHashTable=" + activityHashTable + ", edgeHashTable=" + edgeHashTable + ", log="
				+ log + ", groupConfig=" + groupConfig + "]";
	}

	
}
