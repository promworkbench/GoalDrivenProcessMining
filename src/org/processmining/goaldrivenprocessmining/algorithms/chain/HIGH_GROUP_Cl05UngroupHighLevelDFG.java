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
import graph.controls.GraphObjectClickControl;
import graph.controls.GroupNodeControl;

public class HIGH_GROUP_Cl05UngroupHighLevelDFG<C> extends DataChainLinkComputationAbstract<C> {

	public String getName() {
		return "ungroup dfg";
	}

	@Override
	public String getStatusBusyMessage() {
		return " ungrouping the dfg";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.is_in_group_mode, GoalDrivenObject.high_level_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_GROUP_Cl05UngroupHighLevelDFG");
		if (inputs.get(GoalDrivenObject.is_in_group_mode)) {
			XLog log = inputs.get(GoalDrivenObject.high_level_log).getLog();
			FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(log,
					log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
			FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(log,
					log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
			GoalDrivenDFG dfg = new GoalDrivenDFG(inputs.get(GoalDrivenObject.high_level_log), frequencyEdge, frequencyNode);
			dfg.setEdgeClickControl(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
			dfg.addControlListener(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
			GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
					((GoalDrivenConfiguration) configuration).getChain());
			dfg.setGroupNodeControl(groupNodeControl);
			dfg.addControlListener(groupNodeControl);
			HIGH_GROUP_Cl02MakeGroupedHighLevelLog.afterGroupingHighLevelLog = null;
			
			return new IvMObjectValues().//
					s(GoalDrivenObject.high_level_dfg, dfg);
		} else {
			return null;
		}
		
	}

}
