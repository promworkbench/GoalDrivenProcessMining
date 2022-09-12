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
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerParameters;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;

import com.google.gwt.dev.util.collect.HashMap;

public class Cl055MineEdges<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "mine";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.selected_classifier1, IvMObject.input_log,
				GoalDrivenObject.selected_source_target_node, IvMObject.selected_miner, IvMObject.selected_noise_threshold };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.model_edge, GoalDrivenObject.xlog_info_edge,
				GoalDrivenObject.xlog_info_performance_edge, GoalDrivenObject.log_edge };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("Get model edge");
		XLog log = inputs.get(IvMObject.input_log);

		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");
		AttributeClassifier[] sValues = (AttributeClassifier[]) passValues.get("unique_values");
		String[] selectedValues = new String[sValues.length];
		for (int i = 0; i < sValues.length; i++) {
			selectedValues[i] = sValues[i].toString();
		}

		String selectedAttribute = inputs.get(GoalDrivenObject.selected_classifier1).toString();
		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		XTraceImpl newTr = new XTraceImpl(aMap);
		for (XTrace tr : log) {
			Boolean sourceFound = source.replaceAll(" ", "").equals("") ? true : false;
			for (XEvent ev : tr) {
				String value = ev.getAttributes().get(selectedAttribute).toString();
				if (!sourceFound) {
					if (value.equals(source)) {
						sourceFound = true;
						newTr.add(ev);
					}
				} else {
					if (value.equals(target)) {
						newTr.add(ev);
						newLog.add(newTr);						
						newTr = new XTraceImpl(aMap);
						if (value.equals(source)) {
							sourceFound = true;
							newTr.add(ev);
						} else {
							sourceFound = false;
						}
					} else {
						if (!Arrays.asList(selectedValues).contains(value)) {
							newTr.add(ev);
						}

					}

				}

			}
			if (!newTr.isEmpty() && target.replaceAll(" ", "").equals("")) {
				newLog.add(newTr);
				newTr = new XTraceImpl(aMap);
			} else {
				newTr = new XTraceImpl(aMap);
			}

		}
		System.out.println("####");
		for (XTrace tr : newLog) {
			System.out.println("@@");
			for (XEvent ev : tr) {
				System.out.println(ev.getAttributes().get(selectedAttribute).toString());
			}
		}
		System.out.println("####");
		XEventPerformanceClassifier performanceClassifier = new XEventPerformanceClassifier(
				AttributeClassifiers.constructClassifier(inputs.get(GoalDrivenObject.selected_classifier1)));
		XLifeCycleClassifier lifeCycleClassifier = inputs.get(IvMObject.selected_miner).getLifeCycleClassifier();

		IMLog iLog = new IMLogImpl(newLog, performanceClassifier.getActivityClassifier(), lifeCycleClassifier);
		IMLog2IMLogInfo miner1 = inputs.get(IvMObject.selected_miner).getLog2logInfo();
		IMLogInfo imLogInfo = miner1.createLogInfo(iLog);
		System.out.println(imLogInfo.getNumberOfTraces());
		VisualMinerWrapper miner = inputs.get(IvMObject.selected_miner);
		double noise_threshold = inputs.get(IvMObject.selected_noise_threshold);

		VisualMinerParameters minerParameters = new VisualMinerParameters(noise_threshold);

		IvMModel model = miner.mine(iLog, imLogInfo, minerParameters, canceller);
		XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(newLog, performanceClassifier.getActivityClassifier());
		XLogInfo xLogInfoPerformance = XLogInfoFactory.createLogInfo(newLog, performanceClassifier);
		return new IvMObjectValues().//
				s(GoalDrivenObject.model_edge, model).s(GoalDrivenObject.log_edge, newLog).s(GoalDrivenObject.xlog_info_edge, xLogInfo)
				.s(GoalDrivenObject.xlog_info_performance_edge, xLogInfoPerformance);
	}

}
