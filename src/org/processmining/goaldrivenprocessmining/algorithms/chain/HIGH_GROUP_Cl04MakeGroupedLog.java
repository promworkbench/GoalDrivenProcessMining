package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedNodeGroupObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.controls.EdgeClickControl;

public class HIGH_GROUP_Cl04MakeGroupedLog<C> extends DataChainLinkComputationAbstract<C> {
	public String getName() {
		return "Make the log for the selected nodes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Making the log for the selected nodes";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.batch_selected_nodes, GoalDrivenObject.high_level_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.grouped_log, GoalDrivenObject.grouped_dfg };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_Cl07MakeGroupedLog");
		XLog log = inputs.get(GoalDrivenObject.high_level_log).getLog();
		SelectedNodeGroupObject selectedNode = inputs.get(GoalDrivenObject.batch_selected_nodes);
		GDPMLog gdpmLog = LogUtils.projectLogOnSetActivities(log, selectedNode.getListNodeLabel());
		LogUtils.setUpMapNodeType(gdpmLog, Arrays.asList());
		XLog newLog = gdpmLog.getLog();
		IndirectedEdgeCarrierObject indirectedEdges = gdpmLog.getIndirectedEdges();
		FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(newLog,
				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(newLog,
				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLog, frequencyEdge, frequencyNode);
		dfg.setEdgeClickControl(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		dfg.addControlListener(new EdgeClickControl(((GoalDrivenConfiguration) configuration).getChain()));
		return new IvMObjectValues().//
				s(GoalDrivenObject.grouped_log, gdpmLog). 
				s(GoalDrivenObject.grouped_dfg, dfg);
	}
}
