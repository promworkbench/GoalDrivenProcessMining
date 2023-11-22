package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;

public class GDPMLogSkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5522692751620232352L;
	private EdgeHashTable edgeHashTable;
	private Map<EdgeObject, ThroughputTimeObject> edgeThroughputTime;
	private List<TraceSkeleton> log;
	private Config config;

	public GDPMLogSkeleton() {
		this.log = new ArrayList<>();
		this.edgeHashTable = new EdgeHashTable();
		this.edgeThroughputTime = new HashMap<>();
		this.config = new Config();
	}

	public GDPMLogSkeleton(XLog log) {
		GDPMLogSkeleton clone = LogSkeletonUtils.getLogSkeleton(log);
		this.log = clone.getLog();
		this.edgeHashTable = clone.getEdgeHashTable();
		this.config = clone.getConfig();
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

	public Map<EdgeObject, ThroughputTimeObject> getEdgeThroughputTime() {
		return edgeThroughputTime;
	}

	public void setEdgeThroughputTime(Map<EdgeObject, ThroughputTimeObject> edgeThroughputTime) {
		this.edgeThroughputTime = edgeThroughputTime;
	}

	public String toString() {
		return "GDPMLogSkeleton [edgeHashTable=" + edgeHashTable + ", log=" + log + ", config=" + config + "]";
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}
}
