package org.processmining.goaldrivenprocessmining.plugins;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfigurationDefault;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenController;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenLauncher;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;

public class GoalDrivenPlugin {

	@Plugin(
			name = "Goal Driven Visualizer", 
			returnLabels = { "Goal Driven visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "Inductive visual Miner launcher", "canceller" }, 
			userAccessible = true
			)
	@Visualizer
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public JComponent visualise(final PluginContext context, final GoalDrivenLauncher launcher, ProMCanceller canceller) throws UnknownTreeNodeException {
		//set launcher non-favourite
		if (context instanceof UIPluginContext) {
			((UIPluginContext) context).getGlobalContext().getResourceManager().getResourceForInstance(launcher)
					.setFavorite(false);
		}

		XLog log = launcher.xLog.get();
		if (log == null) {
			throw new RuntimeException("The log has been removed by garbage collection.");
		}

		//initialise configuration and controller
		GoalDrivenConfigurationDefault configuration = new GoalDrivenConfigurationDefault(canceller, context.getExecutor());
		GoalDrivenController controller = new GoalDrivenController(context, configuration, log,
				canceller);

		return controller.getPanel();
	}

	@Plugin(name = "111AAA", returnLabels = {
			"Goal-driven User-defined Process Mining" }, returnTypes = { GoalDrivenLauncher.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Discovery,
							PluginCategory.Analytics, }, help = "Discover a directly follows model interactively. (DFvM)")

	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public GoalDrivenLauncher mineGuiProcessTree(PluginContext context, XLog xLog) {
		GoalDrivenLauncher launcher = GoalDrivenLauncher.launcher(xLog);
		launcher.setMiner(new DfgMiner());
		return launcher;
	}
}
