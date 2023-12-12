package org.processmining.goaldrivenprocessmining.plugins;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfigurationDefault;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenController;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenLauncher;
import org.processmining.goaldrivenprocessmining.algorithms.chain.CONFIG_Update;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

import graph.GoalDrivenDFGUtils;

public class GoalDrivenPlugin {

	@Plugin(name = "Goal Driven Visualizer", returnLabels = { "Goal Driven visualization" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Inductive visual Miner launcher",
					"canceller" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public JComponent visualise(final PluginContext context, final GoalDrivenLauncher launcher, ProMCanceller canceller)
			throws UnknownTreeNodeException {
		XLog log = launcher.xLog.get();
		// reset global variables
		CONFIG_Update.currentConfig = null;
		GoalDrivenDFGUtils.groupStates = new ArrayList<>();
		Cl01GatherAttributes.originalLog = new ArrayList<>();
		Cl01GatherAttributes.originalEdgeHashTable = new EdgeHashTable();
		Cl01GatherAttributes.originalMapEdgeThroughputTime = new HashMap<>();
		//initialise configuration and controller
		GoalDrivenConfigurationDefault configuration = new GoalDrivenConfigurationDefault(canceller,
				context.getExecutor());
		GoalDrivenController controller = new GoalDrivenController(context, configuration, log, canceller);

		return controller.getPanel();
	}

	@Plugin(name = "ProDUG: User-guided interactive process discovery tool", returnLabels = {
			"Goal-driven User-defined Process Mining" }, returnTypes = { GoalDrivenLauncher.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Discovery,
							PluginCategory.Analytics, }, help = "Discover a directly follows model interactively. (DFvM)")

	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public GoalDrivenLauncher mineGuiProcessTree(PluginContext context, XLog xLog) {
		GoalDrivenLauncher launcher = GoalDrivenLauncher.launcher(xLog);
		return launcher;
	}
}
