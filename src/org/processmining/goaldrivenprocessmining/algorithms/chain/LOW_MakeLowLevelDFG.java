package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.utils.node.GoalDrivenDFGUtils;

public class LOW_MakeLowLevelDFG<C> extends DataChainLinkComputationAbstract<C> {

	public static GoalDrivenDFG currentLowLevelDfg;
	
	@Override
	public String getName() {
		return "mine low dfg";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining low dfg..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_MakeLowLevelDFG");
		GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.low_level_log_skeleton), false);
		//		dfg.addControlListener(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		GoalDrivenDFGUtils.updateDfg(dfg);
		dfg.addSeeOnlyControls();
		currentLowLevelDfg = dfg; 
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_dfg, dfg);
	}

}
