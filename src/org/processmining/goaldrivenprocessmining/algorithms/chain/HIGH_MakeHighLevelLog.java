package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLogSkeleton currentHighLogSkeleton = null;

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
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.config };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.high_level_log_skeleton };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_MakeHighLevelLog");
		//		Config config = inputs.get(GoalDrivenObject.config);
		UpdateConfig update = CONFIG_Update.currentUpdateConfig;
		GDPMLogSkeleton gdpmLog = currentHighLogSkeleton == null ? inputs.get(GoalDrivenObject.full_log_skeleton)
				: currentHighLogSkeleton;

		if (update.getUpdateType() != null) {
			switch (update.getUpdateType()) {
				case SELECTED_ACT :
					HashMap<String, String[]> selectedActMap = (HashMap<String, String[]>) update.getUpdateObject();
					// Select/Unselect
					String[] selectedActs = selectedActMap.get("High");
					// use filter to unselect selected acts
					String[] unselectedActs = selectedActMap.get("Low");

					gdpmLog = LogSkeletonUtils.addActivitiesInLog(gdpmLog, selectedActs);
					gdpmLog = LogSkeletonUtils.removeActivitiesInLog(gdpmLog, unselectedActs);
					break;
				case GROUP :
					switch (update.getUpdateAction()) {
						case ADD :

							GroupSkeleton newGroupActObject = (GroupSkeleton) update.getUpdateObject();

							//							Boolean isNewGroup = true;
							//							for (GroupSkeleton group : CONFIG_Update.currentConfig.getListGroupSkeletons()) {
							//								if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
							//									isNewGroup = false;
							//									break;
							//								}
							//							}
							//							if (isNewGroup) {
							//								gdpmLog = LogSkeletonUtils.replaceSetActivitiesInLog(gdpmLog,
							//										newGroupActObject.getListAct(), newGroupActObject.getGroupName());
							//							} else {
							//								for (GroupSkeleton group : CONFIG_Update.currentConfig.getListGroupSkeletons()) {
							//									if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
							//										gdpmLog = LogSkeletonUtils.replaceSetActivitiesInLog(gdpmLog,
							//												group.getListAct(), group.getGroupName());
							//										break;
							//									}
							//								}
							//
							//							}
							gdpmLog = LogSkeletonUtils.replaceSetActivitiesInLog(gdpmLog, newGroupActObject);

							break;
						case REMOVE :
							Class<?> objClass = update.getUpdateObject().getClass();
							if (objClass.isArray()) {
								String[] array = (String[]) update.getUpdateObject();
								String groupName = array[0];
								String removeAct = array[1];
								gdpmLog = LogSkeletonUtils.removeActInGroup(gdpmLog, groupName, removeAct);

							} else {
								String groupName = (String) update.getUpdateObject();
								gdpmLog = LogSkeletonUtils.ungroupGroupInLog(gdpmLog,
										gdpmLog.getLogSkeleton().getGroupConfig().get(groupName));
							}

							break;
					}
					break;
				case CATEGORY :
					switch (update.getUpdateAction()) {
						case ADD :
							break;
						case REMOVE :
							break;
					}
					break;
				case FILTER :
					break;

			}
		}

		currentHighLogSkeleton = gdpmLog;
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, gdpmLog);
	}

}
