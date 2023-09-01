package org.processmining.goaldrivenprocessmining.algorithms;

import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

public interface GoalDrivenConfiguration {

//
//	/**
//	 * Set up the JComponent panel.
//	 * 
//	 * @param context
//	 * @param state
//	 * @param discoveryTechniques
//	 * @param canceller
//	 * @return
//	 */
	public GoalDrivenPanel getPanel();

	/**
	 * Set up the chain (DAG) of steps (chain links) that should be executed in
	 * the background and to update the gui.
	 * 
	 * @return
	 */
	public DataChain<GoalDrivenConfiguration> getChain();


}