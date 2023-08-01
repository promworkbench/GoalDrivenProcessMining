package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapGroupLogObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

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
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl01MakeHighLevelLog");

		GDPMLogSkeleton gdpmLog = inputs.get(GoalDrivenObject.full_log_skeleton);
		Config config = inputs.get(GoalDrivenObject.config);

		// Replace
		for (GroupActObject groupActObject : config.getListGroupActObjects()) {
			gdpmLog = LogSkeletonUtils.replaceSetActivitiesInLog(gdpmLog, groupActObject.getListAct(),
					groupActObject.getGroupName());
		}

		// Select/Unselect
		String[] selectedActs = config.getSelectedActs();
		// use filter to unselect selected acts
		String[] unselectedActs = config.getUnselectedActs();

		gdpmLog = LogSkeletonUtils.addActivitiesInLog(gdpmLog, selectedActs);
		gdpmLog = LogSkeletonUtils.removeActivitiesInLog(gdpmLog, unselectedActs);

		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, gdpmLog);
	}

}
