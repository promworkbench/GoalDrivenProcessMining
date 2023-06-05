package org.processmining.goaldrivenprocessmining.algorithms.chain;

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
		return new IvMObject<?>[] {GoalDrivenObject.selected_node, GoalDrivenObject.stat};

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.stat_selected_node };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		String selectedAct = inputs.get(GoalDrivenObject.selected_node);
		MapStatObject mapStatObject = inputs.get(GoalDrivenObject.stat);
		StatNodeObject statNodeObject = mapStatObject.getMapStatNode().get(selectedAct);
		
		return new IvMObjectValues().//
				s(GoalDrivenObject.stat_selected_node, statNodeObject);//
	}
}
