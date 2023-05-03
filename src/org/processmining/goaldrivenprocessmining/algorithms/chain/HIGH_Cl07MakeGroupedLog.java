package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl07MakeGroupedLog<C> extends DataChainLinkComputationAbstract<C> {
	public String getName() {
		return "Make the log for the selected nodes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Making the log for the selected nodes";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.batch_selected_nodes, GoalDrivenObject.high_level_xlog };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.grouped_xlog };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
