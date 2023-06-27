package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapGroupLogObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;

public class HIGH_Cl01MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLogSkeleton currentHighLogSkeleton = null;
	private MapGroupLogObject mapGroupLogObject = new MapGroupLogObject();

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Making high level event log...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Make high level log";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.config };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton, GoalDrivenObject.map_group_log };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl01MakeHighLevelLog");
		Config config = inputs.get(GoalDrivenObject.config);
		GDPMLogSkeleton fullLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
		GDPMLogSkeleton newLogSkeleton = (GDPMLogSkeleton) fullLogSkeleton.clone();

		// compute for updated combined high level log

		// apply filter

		// apply group
		List<GroupActObject> groups = config.getListGroupActObjects();
		for (GroupActObject groupActObject : groups) {
			// update on high level log
			newLogSkeleton = LogSkeletonUtils.replaceSetActivitiesInLog(newLogSkeleton, groupActObject.getListAct(),
					groupActObject.getGroupName());
			// update map all group log
			if (!this.mapGroupLogObject.getMapGroupLog().keySet().contains(groupActObject.getGroupName())) {
				GDPMLogSkeleton groupLogSkeleton = (GDPMLogSkeleton) fullLogSkeleton.clone();
				List<String> toRemoveAct = new ArrayList<String>();
				for (String act : groupLogSkeleton.getActivityHashTable().getActivityTable().keySet()) {
					if (!groupActObject.getListAct().contains(act)) {
						toRemoveAct.add(act);
					}
				}
				groupLogSkeleton = LogSkeletonUtils.removeActivitiesInLog(groupLogSkeleton, toRemoveAct);
				this.mapGroupLogObject.getMapGroupLog().put(groupActObject.getGroupName(), groupLogSkeleton);
			}

		}
		// apply selected activities
		String[] unselectedActivities = config.getUnselectedActs();
		GDPMLogSkeleton newLogObject = LogSkeletonUtils.removeActivitiesInLog(newLogSkeleton, unselectedActivities);
		// apply for grouped log
		for (String act : this.mapGroupLogObject.getMapGroupLog().keySet()) {
			this.mapGroupLogObject.getMapGroupLog().replace(act, LogSkeletonUtils
					.removeActivitiesInLog(this.mapGroupLogObject.getMapGroupLog().get(act), unselectedActivities));
			StatUtils.updateStat(this.mapGroupLogObject.getMapGroupLog().get(act));
			GoalDrivenDFG dfg = new GoalDrivenDFG(this.mapGroupLogObject.getMapGroupLog().get(act));
			this.mapGroupLogObject.getMapGroupDfg().put(act, dfg);
		}
		// apply category

		// recalculate the stat of new log
		StatUtils.updateStat(newLogObject);
		// update global var
		currentHighLogSkeleton = newLogObject;
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, newLogObject)
				.s(GoalDrivenObject.map_group_log, this.mapGroupLogObject);
	}

}
