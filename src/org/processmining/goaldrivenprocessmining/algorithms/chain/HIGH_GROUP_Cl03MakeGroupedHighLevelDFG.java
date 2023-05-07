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

public class HIGH_GROUP_Cl03MakeGroupedHighLevelDFG<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "dfg after grouping nodes";
	}

	@Override
	public String getStatusBusyMessage() {
		return " dfg after grouping nodes..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.after_grouping_high_level_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] {GoalDrivenObject.high_level_dfg};
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_Cl05MakeHighLevelDFGGrouped");
		XLog log = inputs.get(GoalDrivenObject.after_grouping_high_level_log).getLog();
		FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(log,
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(log,
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.after_grouping_high_level_log), frequencyEdge, frequencyNode);
		dfg.setEdgeClickControl(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));

		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_dfg, dfg);
	}
}
