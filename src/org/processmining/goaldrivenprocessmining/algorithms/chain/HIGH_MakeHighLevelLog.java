package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;
import java.util.Map;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static EdgeHashTable currentHighLevelEdgeHashTable;
	public static Map<EdgeObject, ThroughputTimeObject> currentMapEdgeThroughputTime;

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Making high level event log...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Make high level log";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.update_config_object };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton, GoalDrivenObject.config };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_MakeHighLevelLog");
		GDPMLogSkeleton gdpmLog = new GDPMLogSkeleton();
		// for config 
		Config updatedConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		UpdateConfig update = inputs.get(GoalDrivenObject.update_config_object);

		HashMap<String, String[]> selectedActMap = (HashMap<String, String[]>) update.getUpdateObject();
		// for config 
		updatedConfig.setHighActs(selectedActMap.get("High"));
		updatedConfig.setLowActs(selectedActMap.get("Low"));
		gdpmLog.setConfig(updatedConfig);
		LogSkeletonUtils.setupEdgeHashTableForHighLevelAfterChangingDisplayedActs(gdpmLog, updatedConfig,
				Cl01GatherAttributes.originalEdgeHashTable);
		CONFIG_Update.currentConfig = updatedConfig;
		HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable = gdpmLog.getEdgeHashTable();
		HIGH_MakeHighLevelLog.currentMapEdgeThroughputTime = gdpmLog.getEdgeThroughputTime();
		
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, gdpmLog).s(GoalDrivenObject.config, updatedConfig);
	}

}
