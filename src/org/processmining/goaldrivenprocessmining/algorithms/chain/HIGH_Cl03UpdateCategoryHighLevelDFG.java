package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;

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
			GoalDrivenObject.high_level_log, 
			GoalDrivenObject.selected_mode_category,
			GoalDrivenObject.map_activity_category, 
			GoalDrivenObject.high_frequency_edge,
			GoalDrivenObject.high_frequency_node
			};

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl03UpdateCategoryHighLevelDFG");
		FrequencyEdgeObject frequencyEdge = inputs.get(GoalDrivenObject.high_frequency_edge);
		FrequencyNodeObject frequencyNode = inputs.get(GoalDrivenObject.high_frequency_node);
		GoalDrivenDFG  dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.high_level_log), frequencyEdge, frequencyNode);
//		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.repaintNodeStrokeColor(dfg.getNodeStrokeColorFromMapActCat(inputs.get(GoalDrivenObject.map_activity_category),
				inputs.get(GoalDrivenObject.selected_mode_category)));
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg);//
		
	}
}
