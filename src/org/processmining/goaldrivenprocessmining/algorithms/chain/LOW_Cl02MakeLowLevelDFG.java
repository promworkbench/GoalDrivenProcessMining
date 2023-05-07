package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
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
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log, };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg,GoalDrivenObject.low_frequency_edge,
			GoalDrivenObject.low_frequency_node };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_Cl02MakeLowLevelDFG");
		GDPMLog gdpmLog = inputs.get(GoalDrivenObject.low_level_log);
		String classifier = gdpmLog.getLog().getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(gdpmLog.getLog(), classifier);
		FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(gdpmLog.getLog(), classifier);
		GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLog, frequencyEdge, frequencyNode);
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_dfg, dfg).
				s(GoalDrivenObject.low_frequency_edge, frequencyEdge).
				s(GoalDrivenObject.low_frequency_node, frequencyNode);
	}


}
