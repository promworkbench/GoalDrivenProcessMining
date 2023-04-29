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
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;

public class _Cl0456MakeHighLevelLog extends DataChainLinkComputationAbstract {

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Making high level log...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Make high level log";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.selected_unique_values, IvMObject.imlog_activity_filtered,
				GoalDrivenObject.selected_classifier1, IvMObject.selected_miner };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] {  };
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- Cl0456");
		XLog log = inputs.get(IvMObject.imlog_activity_filtered).toXLog();
		String selectedAttribute = inputs.get(GoalDrivenObject.selected_classifier1).toString();
		/*********/
		AttributeClassifier[] sValues = inputs.get(GoalDrivenObject.selected_unique_values);
		String[] selectedValues = new String[sValues.length];
		for (int i = 0; i < sValues.length; i++) {
			selectedValues[i] = sValues[i].toString();
		}

		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		for (XTrace tr : log) {
			XTraceImpl newTr = new XTraceImpl(aMap);
			for (XEvent ev : tr) {
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
		XLifeCycleClassifier lifeCycleClassifier = inputs.get(IvMObject.selected_miner).getLifeCycleClassifier();

		IMLog imLog = new IMLogImpl(newLog, performanceClassifier.getActivityClassifier(), lifeCycleClassifier);
		XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(log, performanceClassifier.getActivityClassifier());
		XLogInfo xLogInfoPerformance = XLogInfoFactory.createLogInfo(log, performanceClassifier);

		return new IvMObjectValues().//
//				s(GoalDrivenObject.im_log_high_level, imLog).//
				s(IvMObject.xlog_info, xLogInfo).//
				s(IvMObject.xlog_info_performance, xLogInfoPerformance);
	}

}
