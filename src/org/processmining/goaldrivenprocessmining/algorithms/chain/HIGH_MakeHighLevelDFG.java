package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.controls.GraphObjectClickControl;

public class HIGH_MakeHighLevelDFG<C> extends DataChainLinkComputationAbstract<C> {
	
	@Override
	public String getName() {
		return "Mine high-level dfg";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining high-level dfg..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_MakeHighLevelDFG");
		GDPMLogSkeleton gdpmLogSkeleton = inputs.get(GoalDrivenObject.high_level_log_skeleton);
		GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLogSkeleton, true);
		dfg.setEdgeClickControl(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.addControlListener(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
//		GoalDrivenDFGUtils.updateDfg(dfg);
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg)
				;
	}

	
}
