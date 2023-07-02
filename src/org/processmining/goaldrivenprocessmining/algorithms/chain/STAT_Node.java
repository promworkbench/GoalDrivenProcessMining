package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapStatObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class STAT_Node<C> extends DataChainLinkComputationAbstract<C> {
	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Calculating statistics for node";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Calculate statistic for node";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] {GoalDrivenObject.selected_node, GoalDrivenObject.config};

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.stat_selected_node };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		String selectedAct = inputs.get(GoalDrivenObject.selected_node);
		GDPMLogSkeleton curLog;
		if (Arrays.asList(inputs.get(GoalDrivenObject.config).getSelectedActs()).contains(selectedAct)) {
			curLog = HIGH_MakeHighLevelLog.currentHighLogSkeleton;
		} else if (Arrays.asList(inputs.get(GoalDrivenObject.config).getUnselectedActs()).contains(selectedAct)) {
			curLog = LOW_MakeLowLevelLog.currentLowLogSkeleton;
		} else {
			throw new IllegalArgumentException("No such activity");
		}
		MapStatObject mapStatObject = curLog.getStatObject();
		StatNodeObject statNodeObject = mapStatObject.getMapStatNode().get(selectedAct);
		
		return new IvMObjectValues().//
				s(GoalDrivenObject.stat_selected_node, statNodeObject);//
	}
}
