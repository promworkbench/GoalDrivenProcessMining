package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.Arrays;
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

public class HIGH_MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
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
		List<GroupActObject> groups = config.getListGroupActObjects();
		GDPMLogSkeleton newLogSkeleton;

		// analyze the configuration
		if (config.getListGroupActObjects().isEmpty()) {
			newLogSkeleton = (GDPMLogSkeleton) inputs.get(GoalDrivenObject.full_log_skeleton).clone();

		} else {
			//			GDPMLogSkeleton fullLogSkeleton = currentHighLogSkeleton == null
			//					? inputs.get(GoalDrivenObject.full_log_skeleton)
			//					: currentHighLogSkeleton;
			//			newLogSkeleton = (GDPMLogSkeleton) fullLogSkeleton.clone();
			newLogSkeleton = (GDPMLogSkeleton) inputs.get(GoalDrivenObject.full_log_skeleton).clone();
			// apply group

			for (GroupActObject groupActObject : groups) {
				// update on high level log
				newLogSkeleton = LogSkeletonUtils.replaceSetActivitiesInLog(newLogSkeleton, groupActObject.getListAct(),
						groupActObject.getGroupName());
				// update map all group log
				if (!this.mapGroupLogObject.getMapGroupLog().keySet().contains(groupActObject.getGroupName())) {
					GDPMLogSkeleton groupLogSkeleton = (GDPMLogSkeleton) inputs.get(GoalDrivenObject.full_log_skeleton)
							.clone();
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
		}
		// apply selected activities
		List<String> unselectedActivities = new ArrayList<String>();
		for (GroupActObject groupActObject : groups) {
			if (Arrays.asList(config.getUnselectedActs()).containsAll(groupActObject.getListAct())) {
				unselectedActivities.add(groupActObject.getGroupName());
			}
		}
		for (String act : config.getUnselectedActs()) {
			unselectedActivities.add(act);
		}
		newLogSkeleton = LogSkeletonUtils.removeActivitiesInLog(newLogSkeleton, unselectedActivities);

		// apply filter

		// apply for grouped log
		if (!config.getListGroupActObjects().isEmpty()) {
			for (String act : this.mapGroupLogObject.getMapGroupLog().keySet()) {

				this.mapGroupLogObject.getMapGroupLog().replace(act, LogSkeletonUtils
						.removeActivitiesInLog(this.mapGroupLogObject.getMapGroupLog().get(act), unselectedActivities));
				StatUtils.updateStat(this.mapGroupLogObject.getMapGroupLog().get(act));
				GoalDrivenDFG dfg = new GoalDrivenDFG(this.mapGroupLogObject.getMapGroupLog().get(act));
				this.mapGroupLogObject.getMapGroupDfg().put(act, dfg);
			}
		}

		// apply category

		// recalculate the stat of new log
		StatUtils.updateStat(newLogSkeleton);
		// update global var
		currentHighLogSkeleton = newLogSkeleton;
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, newLogSkeleton)
				.s(GoalDrivenObject.map_group_log, this.mapGroupLogObject);
	}

}
