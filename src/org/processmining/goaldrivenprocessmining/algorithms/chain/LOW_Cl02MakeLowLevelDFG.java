package org.processmining.goaldrivenprocessmining.algorithms.chain;
import org.deckfour.xes.model.XLog;
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
		return new IvMObject<?>[] { 
			GoalDrivenObject.low_level_xlog, 
			};
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { 
			GoalDrivenObject.low_level_dfg
			};
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		XLog log = inputs.get(GoalDrivenObject.low_level_xlog);
		System.out.println("--- LOW_Cl02MakeLowLevelDFG");

		GoalDrivenDFG dfg = new GoalDrivenDFG(log);
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_dfg, dfg);
	}

}
