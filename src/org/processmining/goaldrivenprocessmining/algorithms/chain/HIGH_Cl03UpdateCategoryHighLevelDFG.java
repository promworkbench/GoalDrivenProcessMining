package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.controls.EdgeClickControl;

public class HIGH_Cl03UpdateCategoryHighLevelDFG<C> extends DataChainLinkComputationAbstract<C> {
	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Updating high level DFG with selected mode...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Update high level DFG";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { 
			GoalDrivenObject.high_level_xlog, 
			GoalDrivenObject.selected_mode_category,
			GoalDrivenObject.map_activity_category, 
			};

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl03UpdateCategoryHighLevelDFG");
		XLog log = inputs.get(GoalDrivenObject.high_level_xlog);
		GoalDrivenDFG dfg = new GoalDrivenDFG(log);
		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.repaintNodeStrokeColor(dfg.getNodeStrokeColorFromMapActCat(inputs.get(GoalDrivenObject.map_activity_category),
				inputs.get(GoalDrivenObject.selected_mode_category)));
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg);//
	}
}
