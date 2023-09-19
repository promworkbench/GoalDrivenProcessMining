package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class LOW_UpdateLowLevelLogUsingConfig<C> extends DataChainLinkComputationAbstract<C> {

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Updating low level log with config";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Update low level log with config";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.config };
	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		// TODO Auto-generated method stub
		GDPMLogSkeleton gdpmLog;
		if (LOW_MakeLowLevelLog.currentLowLogSkeleton == null) {
			gdpmLog = new GDPMLogSkeleton();
			return new IvMObjectValues().//
					s(GoalDrivenObject.low_level_log_skeleton, gdpmLog);
		} else {
			UpdateConfig update = CONFIG_Update.currentUpdateConfig;
			gdpmLog = LOW_MakeLowLevelLog.currentLowLogSkeleton;

			if (update.getUpdateType() != null) {
				switch (update.getUpdateType()) {
					case SELECTED_ACT :
						// clear low level panel
						LOW_MakeLowLevelLog.currentLowLogSkeleton = null;
						return new IvMObjectValues().//
								s(GoalDrivenObject.low_level_log_skeleton, new GDPMLogSkeleton());
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

			LOW_MakeLowLevelLog.currentLowLogSkeleton = gdpmLog;
			return new IvMObjectValues().//
					s(GoalDrivenObject.low_level_log_skeleton, gdpmLog);
		}

	}
}
