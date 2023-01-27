package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.MapValueGroupObject;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMTrace;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.IvMFilterTree;
import org.processmining.plugins.inductiveVisualMiner.logFiltering.FilterLeastOccurringActivities;

public class Cl04FilterLogOnActivities extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	public String getName() {
		return "filter log";
	}

	public String getStatusBusyMessage() {
		return "Filtering activities..";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.imlog, IvMObject.imlog_info, IvMObject.selected_activities_threshold,
				IvMObject.selected_miner, IvMObject.pre_mining_filter_tree_event,
				IvMObject.pre_mining_filter_tree_trace, GoalDrivenObject.selected_classifier1,};
	}

	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.imlog_activity_filtered, IvMObject.imlog_info_activity_filtered,
				IvMObject.filtered_activities, GoalDrivenObject.all_unique_values,
				GoalDrivenObject.selected_unique_values, GoalDrivenObject.unselected_unique_values, GoalDrivenObject.map_value_group };
	}

	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		System.out.println("---- Cl04");
		IMLog imLog = inputs.get(IvMObject.imlog);
		IMLogInfo imLogInfo = inputs.get(IvMObject.imlog_info);
		double activitiesThreshold = inputs.get(IvMObject.selected_activities_threshold);
		IMLog2IMLogInfo log2logInfo = inputs.get(IvMObject.selected_miner).getLog2logInfo();
		@SuppressWarnings("unchecked")
		IvMFilterTree<XEvent> filterEvent = inputs.get(IvMObject.pre_mining_filter_tree_event);
		@SuppressWarnings("unchecked")
		IvMFilterTree<IMTrace> filterTrace = inputs.get(IvMObject.pre_mining_filter_tree_trace);
		AttributeClassifier classifier = inputs.get(GoalDrivenObject.selected_classifier1);
		if (filterEvent.couldSomethingBeFiltered() || filterTrace.couldSomethingBeFiltered()
				|| activitiesThreshold < 1.0) {
			IMLog newLog = imLog.clone();
			Set<XEventClass> removedActivities = new HashSet<>();

			//apply activities slider
			if (activitiesThreshold < 1.0) {
				removedActivities = FilterLeastOccurringActivities.filter(newLog, imLogInfo, activitiesThreshold,
						log2logInfo);
			}

			//apply pre-mining filters
			filterTrace.filter(newLog.iterator(), canceller);

			if (filterEvent.couldSomethingBeFiltered()) {
				for (IMTrace trace : newLog) {
					filterEvent.filter(trace.iterator(), canceller);
				}
			}

			IMLogInfo filteredLogInfo = log2logInfo.createLogInfo(newLog);

			return new IvMObjectValues().// 
					s(GoalDrivenObject.map_value_group, new MapValueGroupObject()).
					s(GoalDrivenObject.unselected_unique_values, new AttributeClassifier[0]).// 
					s(GoalDrivenObject.selected_unique_values, getAllUniqueValues(newLog.toXLog(), classifier)).// 
					s(GoalDrivenObject.all_unique_values, getAllUniqueValues(newLog.toXLog(), classifier)).// 
					s(IvMObject.imlog_activity_filtered, newLog).// 
					s(IvMObject.imlog_info_activity_filtered, filteredLogInfo).//
					s(IvMObject.filtered_activities, removedActivities);
		} else {
			return  new IvMObjectValues().// 
					s(GoalDrivenObject.map_value_group, new MapValueGroupObject()).
					s(GoalDrivenObject.unselected_unique_values, new AttributeClassifier[0]).
					s(GoalDrivenObject.selected_unique_values, getAllUniqueValues(imLog.toXLog(), classifier)).// 
					s(GoalDrivenObject.all_unique_values, getAllUniqueValues(imLog.toXLog(), classifier)).// 
					s(IvMObject.imlog_activity_filtered, imLog).// 
					s(IvMObject.imlog_info_activity_filtered, imLogInfo).//
					s(IvMObject.filtered_activities, new HashSet<XEventClass>());
		}
	}

	private AttributeClassifier[] getAllUniqueValues(XLog log, AttributeClassifier classifier) {
		AttributeClassifier selected_classifier = classifier;
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < log.size(); i++) {
			XTrace tr = log.get(i);
			for (int j = 0; j < tr.size(); j++) {
				XEvent e = tr.get(j);
				if (!l.contains(e.getAttributes().get(selected_classifier.toString()).toString())) {

					l.add(e.getAttributes().get(selected_classifier.toString()).toString());
				}
			}
		}
		AttributeClassifier[] arrAtt = new AttributeClassifier[l.size()];
		for (int i = 0; i < l.size(); i++) {
			arrAtt[i] = new AttributeClassifier(l.get(i));

		}
		return arrAtt;
	}
}
