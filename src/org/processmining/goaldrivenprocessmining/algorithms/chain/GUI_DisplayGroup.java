package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkGuiAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import graph.GoalDrivenDFG;

public class GUI_DisplayGroup extends DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel> {
	public static Boolean isSelected = false;

	public String getName() {
		return "group log";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.selected_group, GoalDrivenObject.map_group_log };
	}

	public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
		System.out.println("--- group log ---");
		if (isSelected) {
			if (inputs.has(GoalDrivenObject.map_group_log)) {
				GoalDrivenDFG groupDfg = inputs.get(GoalDrivenObject.map_group_log).getMapGroupDfg()
						.get(inputs.get(GoalDrivenObject.selected_group));
				groupDfg.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
				groupDfg.addSeeOnlyControls();
				panel.getSidePanel().getStatisticPanel().getStatPane()
						.addTab(inputs.get(GoalDrivenObject.selected_group), groupDfg);
				panel.getSidePanel().revalidate();
				panel.getSidePanel().repaint();
				panel.revalidate();
				panel.repaint();
			}
		}
		isSelected = false;
	}

	public void invalidate(GoalDrivenPanel panel) {
	}

}
