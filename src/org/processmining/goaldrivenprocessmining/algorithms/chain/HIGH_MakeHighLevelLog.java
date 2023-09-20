package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
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
		GDPMLogSkeleton gdpmLog = currentHighLogSkeleton == null ? inputs.get(GoalDrivenObject.full_log_skeleton)
				: currentHighLogSkeleton;
		// for config 
		Config updatedConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		UpdateConfig update = inputs.get(GoalDrivenObject.update_config_object);
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
					// for config 
					updatedConfig.setSelectedActs(selectedActMap.get("High"));
					updatedConfig.setUnselectedActs(selectedActMap.get("Low"));
					break;
				case GROUP :
					switch (update.getUpdateAction()) {
						case ADD :

							GroupSkeleton newGroupActObject = (GroupSkeleton) update.getUpdateObject();
							gdpmLog = LogSkeletonUtils.groupActivitiesInLog(gdpmLog, newGroupActObject);

							// for config
							Boolean isNewGroup = true;
							for (GroupSkeleton group : updatedConfig.getListGroupSkeletons()) {
								if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
									isNewGroup = false;
									break;
								}
							}
							if (isNewGroup) {
								updatedConfig.addGroup(newGroupActObject);
							} else {
								for (GroupSkeleton group : updatedConfig.getListGroupSkeletons()) {
									if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
										group.getListAct().addAll(newGroupActObject.getListAct());
										group.getListGroup().addAll(newGroupActObject.getListGroup());
										break;
									}
								}
							}

							break;
						case REMOVE :
							Class<?> objClass = update.getUpdateObject().getClass();
							if (objClass.isArray()) {
								String[] array = (String[]) update.getUpdateObject();
								GroupSkeleton selectedGroup = null;
								outerloop: for (String groupName : gdpmLog.getLogSkeleton().getGroupConfig().keySet()) {
									if (groupName.equals(array[0])) {
										GroupSkeleton group = gdpmLog.getLogSkeleton().getGroupConfig().get(array[0]);
										if (group.getListAct().contains(array[1])) {
											gdpmLog = LogSkeletonUtils.removeActInGroup(gdpmLog, groupName, array[1]);
										} else {
											for (GroupSkeleton groupItem : group.getListGroup()) {
												if (groupItem.getGroupName().equals(array[1])) {
													selectedGroup = groupItem;
													break outerloop;
												}
											}

										}

									}
								}
								if (selectedGroup != null) {
									gdpmLog = LogSkeletonUtils.ungroupGroupInLog(gdpmLog, selectedGroup);
									gdpmLog = updateLogWithRemovingGroup(gdpmLog, selectedGroup);
									
									// for config 
									List<GroupSkeleton> groups = new ArrayList<GroupSkeleton>();
									for (GroupSkeleton group : gdpmLog.getLogSkeleton().getGroupConfig().values()) {
										groups.add(group);
									}
									updatedConfig.setListGroupSkeletons(groups);
								}

							} else {
								String groupName = (String) update.getUpdateObject();
								GroupSkeleton selectedGroup = null;
								for (GroupSkeleton group : gdpmLog.getLogSkeleton().getGroupConfig().values()) {
									if (group.getGroupName().equals(groupName)) {
										selectedGroup = group;
										break;
									}
								}
								gdpmLog = LogSkeletonUtils.ungroupGroupInLog(gdpmLog, selectedGroup);
								gdpmLog = updateLogWithRemovingGroup(gdpmLog, selectedGroup);
								
								// for config 
								List<GroupSkeleton> groups = new ArrayList<GroupSkeleton>();
								for (GroupSkeleton group : gdpmLog.getLogSkeleton().getGroupConfig().values()) {
									groups.add(group);
								}
								updatedConfig.setListGroupSkeletons(groups);
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
		CONFIG_Update.currentConfig = updatedConfig;
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log_skeleton, gdpmLog).s(GoalDrivenObject.config, updatedConfig);
	}

	private GDPMLogSkeleton updateLogWithRemovingGroup(GDPMLogSkeleton gdpmLogSkeleton, GroupSkeleton removingGroup) {
		// update all groups
		for (GroupSkeleton group : gdpmLogSkeleton.getLogSkeleton().getGroupConfig().values()) {
			if (group.getListGroup().contains(removingGroup)) {
				group.getListAct().addAll(removingGroup.getListAct());
				group.getListGroup().addAll(removingGroup.getListGroup());
				group.getListGroup().remove(removingGroup);
				gdpmLogSkeleton = LogSkeletonUtils.groupActivitiesInLog(gdpmLogSkeleton, group);
			}
		}
		return gdpmLogSkeleton;
	}

}
