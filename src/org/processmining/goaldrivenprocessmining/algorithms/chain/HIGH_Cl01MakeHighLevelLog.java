package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl01MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { GoalDrivenObject.full_xlog, GoalDrivenObject.config,
				GoalDrivenObject.act_hash_table };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl01MakeHighLevelLog");
		Config config = inputs.get(GoalDrivenObject.config);
		XLog fullLog = inputs.get(GoalDrivenObject.full_xlog);
		XLog newLog = (XLog) fullLog.clone();
		ActivityHashTable activityHashTable = inputs.get(GoalDrivenObject.act_hash_table);
		// compute for updated combined high level log

		// apply filter

		// apply group
		List<GroupActObject> groups = config.getListGroupActObjects();
		for (GroupActObject groupActObject : groups) {
			newLog = LogUtils.replaceSetActivitiesInLog(newLog, activityHashTable, groupActObject.getListAct(), groupActObject.getGroupName());
		}
		// apply selected activities
		String[] unselectedActivities = config.getUnselectedActs();
		GDPMLog newLogObject = LogUtils.removeActivitiesInLog(newLog, activityHashTable, unselectedActivities);
		// apply category

		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log, newLogObject);
	}

}
