package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_GROUP_Cl02MakeGroupedHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLog afterGroupingHighLevelLog;

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return " log after select nodes from GUI...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "log after select nodes from GUI";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.batch_selected_nodes, GoalDrivenObject.high_level_log };
	}

	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.after_grouping_high_level_log };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_GROUP_Cl02MakeGroupedHighLevelLog ---");
		List<String> selectedNodes = inputs.get(GoalDrivenObject.batch_selected_nodes).getListNodeLabel();
		String groupName = inputs.get(GoalDrivenObject.batch_selected_nodes).getGroupName();
		GDPMLog log;
		if (this.afterGroupingHighLevelLog == null) {
			log = inputs.get(GoalDrivenObject.high_level_log);
		} else {
			log = this.afterGroupingHighLevelLog;
		}
		GDPMLog newLog = LogUtils.replaceSetActivitiesInLog(log.getLog(), selectedNodes, log.getIndirectedEdges(),
				groupName);
		LogUtils.setUpMapNodeType(newLog, Arrays.asList(groupName));
		this.afterGroupingHighLevelLog = newLog;

		return new IvMObjectValues().//
				s(GoalDrivenObject.after_grouping_high_level_log, newLog);
	}

	
	

}
