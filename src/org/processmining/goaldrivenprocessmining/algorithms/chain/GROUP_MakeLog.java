package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.List;

import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedGroup;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class GROUP_MakeLog<C> extends DataChainLinkComputationAbstract<C> {

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Creating log for selected group";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Creating log for selected group";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.selected_group };
	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.selected_group_log_skeleton };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		
		SelectedGroup group = inputs.get(GoalDrivenObject.selected_group);
		List<String> affectedActs = group.getGroupSkeleton().getListAct();
		List<GroupSkeleton> affectedGroups = group.getGroupSkeleton().getListGroup();
		GDPMLogSkeleton gdpmLogSkeleton;
		if (group.getIsHighLevel()) {
			gdpmLogSkeleton = HIGH_MakeHighLevelLog.currentHighLogSkeleton;
		} else {
			gdpmLogSkeleton = LOW_MakeLowLevelLog.currentLowLogSkeleton;
		}
		
		
		
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, null);
	}

}
