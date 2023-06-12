package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
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
		return new IvMObject<?>[] { GoalDrivenObject.is_in_group_mode };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_GROUP_Cl05UngroupHighLevelDFG");
		if (inputs.get(GoalDrivenObject.is_in_group_mode)) {
			GDPMLog gdpmLog = HIGH_Cl01MakeHighLevelLog.originalHighLog;
			String classifier = gdpmLog.getLog().getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
			LogUtils.setUpMapNodeType(gdpmLog, Arrays.asList(""));
			HIGH_Cl01MakeHighLevelLog.currentHighLog = gdpmLog;

			FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(gdpmLog.getLog(), classifier);
			FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(gdpmLog.getLog(), classifier);
			GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLog, frequencyEdge, frequencyNode);
			dfg.setEdgeClickControl(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
			dfg.addControlListener(new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain()));
			GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
					((GoalDrivenConfiguration) configuration).getChain());
			dfg.setGroupNodeControl(groupNodeControl);
			dfg.addControlListener(groupNodeControl);

			return new IvMObjectValues().//
					s(GoalDrivenObject.high_level_dfg, dfg);
		} else {
			return null;
		}

	}

}
