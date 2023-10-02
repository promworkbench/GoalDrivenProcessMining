package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LogSkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5522692751620232352L;
	private ActivityHashTable activityHashTable;
	private EdgeHashTable edgeHashTable;
	private List<TraceSkeleton> log;
	private List<EdgeObject> listIndirectedEdges;
	private Config config;

	public LogSkeleton() {
		this.log = new ArrayList<>();
		//		this.groupConfig = new HashMap<>();
		this.activityHashTable = new ActivityHashTable();
		this.edgeHashTable = new EdgeHashTable();
		this.config = new Config();
	}

	public void addGroup(GroupSkeleton groupSkeleton) {
		List<GroupSkeleton> newGroupSkeletons = new ArrayList<GroupSkeleton>();
		Boolean isAdded = false;
		for (GroupSkeleton group : this.config.getListGroupSkeletons()) {
			if (group.getGroupName().equals(groupSkeleton.getGroupName())) {
				newGroupSkeletons.add(groupSkeleton);
				isAdded = true;
			} else {
				newGroupSkeletons.add(group);
			}
		}
		if (!isAdded) {
			newGroupSkeletons.add(groupSkeleton);
		}
		this.config.setListGroupSkeletons(newGroupSkeletons);

	}

	public Boolean isInGroup(String act) {
		for (GroupSkeleton group : this.config.getListGroupSkeletons()) {
			if (group.getListAct().contains(act)) {
				return true;
			}
		}
		return false;
	}

	public GroupSkeleton getGroupSkeletonByGroupName(String act) {
		for (GroupSkeleton group : this.config.getListGroupSkeletons()) {
			if (group.getGroupName().equals(act)) {
				return group;
			}
		}
		return null;
	}

	public Boolean isAGroupSkeleton(String act) {
		for (GroupSkeleton group : this.config.getListGroupSkeletons()) {
			if (group.getGroupName().equals(act)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getAllActivitiesInGroup(GroupSkeleton groupSkeleton) {
		List<String> result = new ArrayList<>();
		result.addAll(groupSkeleton.getListAct());

		for (GroupSkeleton group : groupSkeleton.getListGroup()) {
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

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public String toString() {
		return "LogSkeleton [activityHashTable=" + activityHashTable + ", edgeHashTable=" + edgeHashTable + ", log="
				+ log + ", listIndirectedEdges=" + listIndirectedEdges + ", config=" + config + "]";
	}

	//	public HashMap<String, GroupSkeleton> getGroupConfig() {
	//		return groupConfig;
	//	}
	//
	//	public void setGroupConfig(HashMap<String, GroupSkeleton> groupConfig) {
	//		this.groupConfig = groupConfig;
	//	}

}
