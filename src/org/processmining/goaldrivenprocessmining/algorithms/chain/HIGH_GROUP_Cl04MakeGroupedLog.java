package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.MapGroupLogObject;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedNodeGroupObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;
import graph.controls.GraphObjectClickControl;
import graph.controls.GroupNodeControl;

public class HIGH_GROUP_Cl04MakeGroupedLog<C> extends DataChainLinkComputationAbstract<C> {
	private MapGroupLogObject mapGroupLogObject = null;
	private XLog currentLog = null;

	public String getName() {
		return "Make the log for the selected nodes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Making the log for the selected nodes";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.batch_selected_nodes,
				GoalDrivenObject.high_level_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.map_group_log };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_GROUP_Cl04MakeGroupedLog");
		XLog log = inputs.get(GoalDrivenObject.high_level_log).getLog();
		if (!log.equals(currentLog)) {
			
		}
		SelectedNodeGroupObject selectedNode = inputs.get(GoalDrivenObject.batch_selected_nodes);
		GDPMLog gdpmLog = LogUtils.projectLogOnSetActivities(log, selectedNode.getListNodeLabel());
		LogUtils.setUpMapNodeType(gdpmLog, Arrays.asList(""));
		XLog newLog = gdpmLog.getLog();
		FrequencyEdgeObject frequencyEdge = LogUtils.getFrequencyEdges(newLog,
				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		FrequencyNodeObject frequencyNode = LogUtils.getFrequencyNodeObject(newLog,
				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
		GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLog, frequencyEdge, frequencyNode);
		GraphObjectClickControl edgeClickControl = new GraphObjectClickControl(((GoalDrivenConfiguration) configuration).getChain());
		dfg.setEdgeClickControl(edgeClickControl);
		dfg.addControlListener(edgeClickControl);
		GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
				((GoalDrivenConfiguration) configuration).getChain());
		dfg.setGroupNodeControl(groupNodeControl);
		dfg.addControlListener(groupNodeControl);
		if (this.mapGroupLogObject == null) {
			this.mapGroupLogObject = new MapGroupLogObject();
		}
		this.mapGroupLogObject.getMapGroupLog().put(selectedNode.getGroupName(), gdpmLog);
		this.mapGroupLogObject.getMapGroupDfg().put(selectedNode.getGroupName(), dfg);

		return new IvMObjectValues().//
				s(GoalDrivenObject.map_group_log, this.mapGroupLogObject);
	}
}
