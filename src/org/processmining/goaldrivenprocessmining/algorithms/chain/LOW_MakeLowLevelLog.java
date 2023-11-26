package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class LOW_MakeLowLevelLog<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.selected_source_target_node };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_MakeLowLevelLog");
		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");

		/*--------------------------*/

		/*--------------------------*/
		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		Config config = CONFIG_Update.currentConfig;
		List<GroupSkeleton> groups = config.getListGroupSkeletons();

		List<String> listSources = new ArrayList<String>();
		List<String> listTargets = new ArrayList<String>();
		// check if source is a group
		for (GroupSkeleton groupSkeleton : groups) {
			if (groupSkeleton.getGroupName().equals(source)) {
				listSources.addAll(this.getAllActsInGroup(groupSkeleton));
			}
		}
		// otherwise
		if (listSources.isEmpty()) {
			listSources.add(source);
		}
		// check if target is a group
		for (GroupSkeleton groupSkeleton : groups) {
			if (groupSkeleton.getGroupName().equals(target)) {
				listTargets.addAll(this.getAllActsInGroup(groupSkeleton));
			}
		}
		// otherwise
		if (listTargets.isEmpty()) {
			listTargets.add(target);
		}
		for (String s : listSources) {
			for (String t : listTargets) {
				// create log based on the source and target
				LogSkeletonUtils.setupEdgeHashTableForLowLevelLog(newGdpmLog, inputs.get(GoalDrivenObject.full_log_skeleton), s, t);
			}
		}

		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, newGdpmLog);

	}

	private List<String> getAllActsInGroup(GroupSkeleton groupSkeleton) {
		List<String> res = new ArrayList<>();
		res.addAll(groupSkeleton.getListAct());
		for (GroupSkeleton groupSkeleton2 : groupSkeleton.getListGroup()) {
			res.addAll(this.getAllActsInGroup(groupSkeleton2));
		}
		return res;
	}

	

}
