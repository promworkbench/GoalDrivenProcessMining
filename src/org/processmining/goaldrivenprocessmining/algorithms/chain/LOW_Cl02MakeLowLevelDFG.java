package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;

public class LOW_Cl02MakeLowLevelDFG<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton, };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_Cl02MakeLowLevelDFG");
		GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.low_level_log_skeleton));
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_dfg, dfg);
	}


}
