package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class LOW_MakeLowLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLogSkeleton currentLowLogSkeleton = null;

	@Override
	public String getName() {
		return "Make low level log";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining low level log..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.selected_source_target_node};
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_Cl01MakeLowLevelLog");

		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");

		Config config = CONFIG_Update.currentConfig;
		GDPMLogSkeleton fullLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
		GDPMLogSkeleton newGdpmLog = (GDPMLogSkeleton) fullLogSkeleton.clone();
		
		List<String> displayedActs = new ArrayList<String>();
		for (String act: config.getUnselectedActs()) {
			displayedActs.add(act);
		}
		displayedActs.add(source);
		displayedActs.add(target);
		List<String> undisplayedActs = new ArrayList<String>();
		for (String act: config.getSelectedActs()){
			if (!act.equals(source) && !act.equals(target)) {
				undisplayedActs.add(act);
			}
		}
		
		newGdpmLog = LogSkeletonUtils.addActivitiesInLog(newGdpmLog, displayedActs);
		newGdpmLog = LogSkeletonUtils.removeActivitiesInLog(newGdpmLog, undisplayedActs);
		newGdpmLog = LogSkeletonUtils.restrictLogFrom2Activities(newGdpmLog, source, target);

		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, newGdpmLog);

	}
}
