package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class CONFIG_Update extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	private Config currentConfig = null;

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
		Config updatedConfig = this.currentConfig == null ? new Config() : this.currentConfig;
		UpdateConfig update = inputs.get(GoalDrivenObject.update_config_object);
		switch (update.getUpdateType()) {
			case SELECTED_ACT:
				HashMap<String, String[]> selectedActMap = (HashMap<String, String[]>) update.getUpdateObject();
				updatedConfig.setSelectedActs(selectedActMap.get("High"));
				updatedConfig.setUnselectedActs(selectedActMap.get("Low"));
				break;
			case GROUP :
				GroupActObject groupActObject =  (GroupActObject) update.getUpdateObject();
				switch (update.getUpdateAction()) {
					case ADD :
						updatedConfig.addGroup(groupActObject);
						break;
					case REMOVE :
						updatedConfig.removeGroup(groupActObject);
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
		return new IvMObjectValues().//
				s(GoalDrivenObject.config, updatedConfig);
	}

}
