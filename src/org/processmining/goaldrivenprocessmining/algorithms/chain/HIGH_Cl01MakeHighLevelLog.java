package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl01MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.config
				};

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton };
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
			newLogSkeleton = LogSkeletonUtils.replaceSetActivitiesInLog(newLogSkeleton, groupActObject.getListAct(),
					groupActObject.getGroupName());
		}
		// apply selected activities
		String[] unselectedActivities = config.getUnselectedActs();
		GDPMLogSkeleton newLogObject = LogSkeletonUtils.removeActivitiesInLog(newLogSkeleton, unselectedActivities);
		// apply category

		// recalculate the stat of new log
		StatUtils.updateStat(newLogObject);
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, newLogObject);
	}

}
