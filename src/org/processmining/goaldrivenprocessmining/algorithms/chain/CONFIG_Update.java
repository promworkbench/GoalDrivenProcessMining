package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class CONFIG_Update extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	public static Config currentConfig = null;

	@Override
	public String getName() {
		return "update current configuration";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Updating configuration...";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] {  };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.config };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		Config updatedConfig = currentConfig == null ? new Config() : this.currentConfig;
		UpdateConfig update = inputs.get(GoalDrivenObject.update_config_object);
		if (update.getUpdateType() != null) {
			switch (update.getUpdateType()) {
				case SELECTED_ACT :
					HashMap<String, String[]> selectedActMap = (HashMap<String, String[]>) update.getUpdateObject();
					updatedConfig.setHighActs(selectedActMap.get("High"));
					updatedConfig.setLowActs(selectedActMap.get("Low"));
					break;
				case GROUP :
					switch (update.getUpdateAction()) {
						case ADD :
							GroupSkeleton newGroupActObject = (GroupSkeleton) update.getUpdateObject();
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

		currentConfig = updatedConfig;
		return new IvMObjectValues().//
				s(GoalDrivenObject.config, updatedConfig);
	}


}
