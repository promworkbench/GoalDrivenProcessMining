package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
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
			GoalDrivenObject.high_level_xlog, 
			GoalDrivenObject.selected_mode_category,
			GoalDrivenObject.map_activity_category, 
			GoalDrivenObject.indirected_edges,
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
		XLog log = inputs.get(GoalDrivenObject.high_level_xlog);
		IndirectedEdgeCarrierObject indirectedEdges = inputs.get(GoalDrivenObject.indirected_edges);
		FrequencyEdgeObject frequencyEdge = inputs.get(GoalDrivenObject.high_frequency_edge);
		FrequencyNodeObject frequencyNode = inputs.get(GoalDrivenObject.high_frequency_node);
		GoalDrivenDFG  dfg = new GoalDrivenDFG(log, indirectedEdges, frequencyEdge, frequencyNode);
//		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.repaintNodeStrokeColor(dfg.getNodeStrokeColorFromMapActCat(inputs.get(GoalDrivenObject.map_activity_category),
				inputs.get(GoalDrivenObject.selected_mode_category)));
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg);//
		
	}
}
