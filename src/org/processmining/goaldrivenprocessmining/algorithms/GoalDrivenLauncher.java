package org.processmining.goaldrivenprocessmining.algorithms;

import java.lang.ref.SoftReference;

import org.deckfour.xes.model.XLog;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;

public class GoalDrivenLauncher {
	public final SoftReference<XLog> xLog;
	public final SoftReference<EfficientTree> preMinedTree;
	public final SoftReference<DirectlyFollowsModel> preMinedDfg;
	private VisualMinerWrapper miner;
	private final GoalDrivenConfigurationDefault configuration;

	private GoalDrivenLauncher(SoftReference<XLog> xLog, SoftReference<EfficientTree> preMinedTree,
			SoftReference<DirectlyFollowsModel> preMinedDfg, GoalDrivenConfigurationDefault configuration) {
		this.xLog = xLog;
		this.preMinedTree = preMinedTree;
		this.preMinedDfg = preMinedDfg;
		this.configuration = configuration;
	}

	public VisualMinerWrapper getMiner() {
		return miner;
	}

	public static GoalDrivenLauncher launcher(XLog log, GoalDrivenConfigurationDefault configuration) {
		return new GoalDrivenLauncher(new SoftReference<>(log), null, null, configuration);
	}

	public static GoalDrivenLauncher launcher(XLog xLog) {
		return new GoalDrivenLauncher(new SoftReference<>(xLog), null, null, null);
	}

	public static GoalDrivenLauncher launcher(XLog xLog, DirectlyFollowsModel preMinedDfg) {
		return new GoalDrivenLauncher(new SoftReference<>(xLog), null, new SoftReference<>(preMinedDfg),
				null);
	}

	public void setMiner(VisualMinerWrapper miner) {
		this.miner = miner;
	}

	public SoftReference<DirectlyFollowsModel> getPreMinedDfg() {
		return preMinedDfg;
	}

	public GoalDrivenConfigurationDefault getConfiguration() {
		return configuration;
	}
	
	
}
