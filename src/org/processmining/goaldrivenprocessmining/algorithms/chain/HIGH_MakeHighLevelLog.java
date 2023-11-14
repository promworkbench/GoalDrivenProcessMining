package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {

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
		//		Config config = inputs.get(GoalDrivenObject.config);
//		GDPMLogSkeleton gdpmLog = inputs.get(GoalDrivenObject.full_log_skeleton);
		GDPMLogSkeleton gdpmLog = new GDPMLogSkeleton();
		// for config 
		Config updatedConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		UpdateConfig update = inputs.get(GoalDrivenObject.update_config_object);

		HashMap<String, String[]> selectedActMap = (HashMap<String, String[]>) update.getUpdateObject();
		// Select/Unselect
		String[] selectedActs = selectedActMap.get("High");
		String[] unselectedActs = selectedActMap.get("Low");
//		gdpmLog = LogSkeletonUtils.addActivitiesInLog(gdpmLog, selectedActs);
//		gdpmLog = LogSkeletonUtils.removeActivitiesInLog(gdpmLog, unselectedActs);
		// for config 
		updatedConfig.setSelectedActs(selectedActMap.get("High"));
		updatedConfig.setUnselectedActs(selectedActMap.get("Low"));
		gdpmLog.setConfig(updatedConfig);
		LogSkeletonUtils.setupEdgeHashTableForHighLevelAfterChangingDisplayedActs(gdpmLog, updatedConfig,
				Cl01GatherAttributes.originalEdgeHashTable);
		gdpmLog.setActivityHashTable(Cl01GatherAttributes.originalActivityHashTable);
		CONFIG_Update.currentConfig = updatedConfig;

		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, gdpmLog).s(GoalDrivenObject.config, updatedConfig);
	}

}
