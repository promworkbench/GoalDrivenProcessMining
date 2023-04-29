package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;

public class _Cl03MakeLog<C> extends DataChainLinkComputationAbstract<C> {

	public String getName() {
		return "make log";
	}

	public String getStatusBusyMessage() {
		return "Making log..";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.sorted_log, GoalDrivenObject.selected_classifier1, IvMObject.selected_miner,
			};
	}

	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.imlog, IvMObject.imlog_info, IvMObject.xlog_info,
				IvMObject.xlog_info_performance };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		XLog log = inputs.get(IvMObject.sorted_log);
		XEventPerformanceClassifier performanceClassifier = new XEventPerformanceClassifier(
				AttributeClassifiers.constructClassifier(inputs.get(GoalDrivenObject.selected_classifier1)));
		IMLog2IMLogInfo miner = inputs.get(IvMObject.selected_miner).getLog2logInfo();
		XLifeCycleClassifier lifeCycleClassifier = inputs.get(IvMObject.selected_miner).getLifeCycleClassifier();

		IMLog imLog = new IMLogImpl(log, performanceClassifier.getActivityClassifier(), lifeCycleClassifier);
		IMLogInfo imLogInfo = miner.createLogInfo(imLog);
		XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(log, performanceClassifier.getActivityClassifier());
		XLogInfo xLogInfoPerformance = XLogInfoFactory.createLogInfo(log, performanceClassifier);

		return new IvMObjectValues().//
				s(IvMObject.imlog, imLog).//
				s(IvMObject.imlog_info, imLogInfo).//
				s(IvMObject.xlog_info, xLogInfo).//
				s(IvMObject.xlog_info_performance, xLogInfoPerformance);
	}
}
