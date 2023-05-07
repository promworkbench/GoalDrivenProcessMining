package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;

public class LOW_Cl03UpdateCategoryLowLevelDFG<C> extends DataChainLinkComputationAbstract<C> {
	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Updating low level DFG with selected mode...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Update low level DFG";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log, GoalDrivenObject.selected_mode_category,
				GoalDrivenObject.map_activity_category, GoalDrivenObject.low_frequency_edge,
				GoalDrivenObject.low_frequency_node };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- LOW_Cl03UpdateCategoryLowLevelDFG");
		XLog log = inputs.get(GoalDrivenObject.low_level_log).getLog();
		FrequencyEdgeObject frequencyEdge = inputs.get(GoalDrivenObject.low_frequency_edge);
		FrequencyNodeObject frequencyNode = inputs.get(GoalDrivenObject.low_frequency_node);
		GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.low_level_log), frequencyEdge, frequencyNode);
		//		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.repaintNodeStrokeColor(
				dfg.getNodeStrokeColorFromMapActCat(inputs.get(GoalDrivenObject.map_activity_category),
						inputs.get(GoalDrivenObject.selected_mode_category)));
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_dfg, dfg);//
	}
}
