package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.List;
import java.util.concurrent.Executor;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.export.IvMExporter;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;

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
	private final List<IvMExporter> exporters;
	private final IvMDecoratorI decorator;

	public GoalDrivenConfigurationAbstract(ProMCanceller canceller, Executor executor) {
		exporters = createExporters();
		decorator = createDecorator();

		panel = createPanel(canceller);
		chain = createChain(panel, canceller, executor);
	}

	protected abstract List<IvMExporter> createExporters();

	protected abstract GoalDrivenPanel createPanel(ProMCanceller canceller);

	protected abstract IvMDecoratorI createDecorator();

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

	@Override
	final public List<IvMExporter> getExporters() {
		return exporters;
	}

	@Override
	final public IvMDecoratorI getDecorator() {
		return decorator;
	}
}