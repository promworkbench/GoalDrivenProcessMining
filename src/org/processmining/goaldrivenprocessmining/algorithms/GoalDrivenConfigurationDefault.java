package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.concurrent.Executor;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GROUP_MakeLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_UpdateCategoryHighLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_MakeLowLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_MakeLowLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_UpdateCategoryLowLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_UpdateLowLevelLogUsingConfig;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainImplNonBlocking;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;

public class GoalDrivenConfigurationDefault extends GoalDrivenConfigurationAbstract {

//	protected Cl02SortEvents<GoalDrivenConfiguration> sortEvents;

	public GoalDrivenConfigurationDefault(ProMCanceller canceller, Executor executor) {
		super(canceller, executor);
	}


	@Override
	protected GoalDrivenPanel createPanel(ProMCanceller canceller) {
		return new GoalDrivenPanel(this, canceller);
	}

	@Override
	public DataChain<GoalDrivenConfiguration> createChain(final GoalDrivenPanel panel,
			final ProMCanceller canceller, final Executor executor) {
		//set up the state
		DataState state = new DataState();

		//set up the chain
		final DataChainImplNonBlocking<GoalDrivenConfiguration, GoalDrivenPanel> chain = new DataChainImplNonBlocking<GoalDrivenConfiguration, GoalDrivenPanel>(
				state, canceller, executor, this, panel);

		chain.register(new Cl01GatherAttributes());
		chain.register(new HIGH_MakeHighLevelLog<GoalDrivenConfiguration>());
		chain.register(new HIGH_MakeHighLevelDFG<GoalDrivenConfiguration>());
		chain.register(new HIGH_UpdateCategoryHighLevelDFG<GoalDrivenConfiguration>());
		chain.register(new LOW_MakeLowLevelLog<GoalDrivenConfiguration>());
		chain.register(new LOW_MakeLowLevelDFG<GoalDrivenConfiguration>());
		chain.register(new LOW_UpdateCategoryLowLevelDFG<GoalDrivenConfiguration>());
		chain.register(new LOW_UpdateLowLevelLogUsingConfig<GoalDrivenConfiguration>());
		chain.register(new GROUP_MakeLog<GoalDrivenConfiguration>());
		return chain;
	}




	
}