package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_Cl01MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_Cl02MakeHighLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_Cl03UpdateCategoryHighLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_Cl04MakeGroupedHighLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_Cl05MakeGroupedHighLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_Cl01MakeLowLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_Cl02MakeLowLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_Cl03UpdateCategoryLowLevelDFG;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputer;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputerImpl;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainImplNonBlocking;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.export.IvMExporter;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorDefault;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;

public class GoalDrivenConfigurationDefault extends GoalDrivenConfigurationAbstract {

//	protected Cl02SortEvents<GoalDrivenConfiguration> sortEvents;

	public GoalDrivenConfigurationDefault(ProMCanceller canceller, Executor executor) {
		super(canceller, executor);
	}



	@Override
	protected List<IvMExporter> createExporters() {
		return new ArrayList<>(Arrays.asList(new IvMExporter[] { //
				
		}));
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
		chain.register(new HIGH_Cl01MakeHighLevelLog<GoalDrivenConfiguration>());
		chain.register(new HIGH_Cl02MakeHighLevelDFG<GoalDrivenConfiguration>());
		chain.register(new HIGH_Cl03UpdateCategoryHighLevelDFG<GoalDrivenConfiguration>());
		chain.register(new HIGH_Cl04MakeGroupedHighLevelLog<GoalDrivenConfiguration>());
		chain.register(new HIGH_Cl05MakeGroupedHighLevelDFG<GoalDrivenConfiguration>());
		chain.register(new LOW_Cl01MakeLowLevelLog<GoalDrivenConfiguration>());
		chain.register(new LOW_Cl02MakeLowLevelDFG<GoalDrivenConfiguration>());
		chain.register(new LOW_Cl03UpdateCategoryLowLevelDFG<GoalDrivenConfiguration>());
		return chain;
	}

	protected AlignmentComputer createAlignmentComputer() {
		return new AlignmentComputerImpl();
	}

	protected IvMDecoratorI createDecorator() {
		return new IvMDecoratorDefault();
	}

	
}