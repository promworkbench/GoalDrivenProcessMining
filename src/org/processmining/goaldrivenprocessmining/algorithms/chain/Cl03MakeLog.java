package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
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

public class Cl03MakeLog<C> extends DataChainLinkComputationAbstract<C> {

	public String getName() {
		return "make log";
	}

	public String getStatusBusyMessage() {
		return "Making log..";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.sorted_log, GoalDrivenObject.selected_classifier1, IvMObject.selected_miner,
			GoalDrivenObject.selected_unique_values };
	}

	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.imlog, IvMObject.imlog_info, IvMObject.xlog_info,
				IvMObject.xlog_info_performance };
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("Get log");
		XLog log = inputs.get(IvMObject.sorted_log);
		String selectedAttribute = inputs.get(GoalDrivenObject.selected_classifier1).toString();
		AttributeClassifier[] sValues = inputs.get(GoalDrivenObject.selected_unique_values);
		String[] selectedValues = new String[sValues.length];
		for (int i = 0; i < sValues.length; i++) {
			selectedValues[i] = sValues[i].toString();
		}
		
		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		for (XTrace tr: log) {
			XTraceImpl newTr = new XTraceImpl(aMap);
			for (XEvent ev: tr) {				
				String value = ev.getAttributes().get(selectedAttribute).toString();
				if (Arrays.asList(selectedValues).contains(value)) {
					newTr.add(ev);
				}
			}
			if (!newTr.isEmpty()) {
				newLog.add(newTr);
			}
			
		}
		
		
		XEventPerformanceClassifier performanceClassifier = new XEventPerformanceClassifier(
				AttributeClassifiers.constructClassifier(inputs.get(GoalDrivenObject.selected_classifier1)));
		IMLog2IMLogInfo miner = inputs.get(IvMObject.selected_miner).getLog2logInfo();
		XLifeCycleClassifier lifeCycleClassifier = inputs.get(IvMObject.selected_miner).getLifeCycleClassifier();

		IMLog imLog = new IMLogImpl(newLog, performanceClassifier.getActivityClassifier(), lifeCycleClassifier);
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
