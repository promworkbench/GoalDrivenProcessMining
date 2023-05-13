package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.controls.EdgeClickControl;
import graph.controls.GroupNodeControl;

public class HIGH_Cl02MakeHighLevelDFG<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg, GoalDrivenObject.high_frequency_edge,
				GoalDrivenObject.high_frequency_node };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_Cl02MakeHighLevelDFG");
		XLog log = inputs.get(GoalDrivenObject.high_level_log).getLog();
		FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(log,
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(log,
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.high_level_log),  frequencyEdge, frequencyNode);
		dfg.setEdgeClickControl(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
				((GoalDrivenConfiguration) configuration).getChain());
		dfg.setGroupNodeControl(groupNodeControl);
		dfg.addControlListener(groupNodeControl);
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg).s(GoalDrivenObject.high_frequency_edge, frequencyEdge)
				.s(GoalDrivenObject.high_frequency_node, frequencyNode);
	}

	
}
