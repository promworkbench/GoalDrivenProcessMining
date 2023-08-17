package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class CONFIG_Update extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	public static Config currentConfig = null;
	public static UpdateConfig currentUpdateConfig = null;

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
		return new IvMObject<?>[] { GoalDrivenObject.update_config_object };
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
					updatedConfig.setSelectedActs(selectedActMap.get("High"));
					updatedConfig.setUnselectedActs(selectedActMap.get("Low"));
					break;
				case GROUP :
					switch (update.getUpdateAction()) {
						case ADD :

							GroupActObject newGroupActObject = (GroupActObject) update.getUpdateObject();
							Boolean isNewGroup = true;
							for (GroupActObject group : updatedConfig.getListGroupActObjects()) {
								if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
									isNewGroup = false;
									break;
								}
							}
							if (isNewGroup) {
								updatedConfig.addGroup(newGroupActObject);
							} else {
								for (GroupActObject group : updatedConfig.getListGroupActObjects()) {
									if (group.getGroupName().equals(newGroupActObject.getGroupName())) {
										group.getListAct().addAll(newGroupActObject.getListAct());
										break;
									}
								}
							}

							break;
						case REMOVE :
							Class<?> objClass = update.getUpdateObject().getClass();
							if (objClass.isArray()) {
								String[] array = (String[]) update.getUpdateObject();
								for (GroupActObject group : updatedConfig.getListGroupActObjects()) {
									if (group.getGroupName().equals(array[0])) {
										group.getListAct().remove(array[1]);
									}
								}

							} else {
								String groupName = (String) update.getUpdateObject();
								List<GroupActObject> newGroupActObjects = new ArrayList<>();
								for (GroupActObject group : updatedConfig.getListGroupActObjects()) {
									if (!group.getGroupName().equals(groupName)) {
										newGroupActObjects.add(group);
									}
								}
								updatedConfig.setListGroupActObjects(newGroupActObjects);
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

		currentConfig = updatedConfig;
		currentUpdateConfig = update;
		return new IvMObjectValues().//
				s(GoalDrivenObject.config, updatedConfig);
	}

}
