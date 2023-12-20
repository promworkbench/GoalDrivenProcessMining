package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.concurrent.Executor;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.panelHelper.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

/**
 * IvM configuration that contains the chainlink. To extend, please use the
 * InductiveVisualMinerConfigurationDefault class. This one is not guaranteed to
 * be stable.
 * 
 * @author sander
 *
 */
public abstract class GoalDrivenConfigurationAbstract implements GoalDrivenConfiguration {

	private final DataChain<GoalDrivenConfiguration> chain;
	private final GoalDrivenPanel panel;

	public GoalDrivenConfigurationAbstract(ProMCanceller canceller, Executor executor) {

		panel = createPanel(canceller);
		chain = createChain(panel, canceller, executor);
	}

	protected abstract GoalDrivenPanel createPanel(ProMCanceller canceller);


	protected abstract DataChain<GoalDrivenConfiguration> createChain(GoalDrivenPanel panel,
			ProMCanceller canceller, Executor executor);

	@Override
	final public DataChain<GoalDrivenConfiguration> getChain() {
		return chain;
	}

	@Override
	final public GoalDrivenPanel getPanel() {
		return panel;
	}


}