package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

import com.google.gwt.dev.util.collect.HashMap;

public class LOW_Cl01MakeLowLevelLog<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "Make low level log";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining low level log..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { 
			IvMObject.input_log,
			GoalDrivenObject.selected_source_target_node, 
			GoalDrivenObject.unselected_unique_values 
			};
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { 
			GoalDrivenObject.low_level_log
			};
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		XLog log = inputs.get(IvMObject.input_log);
		System.out.println("--- LOW_Cl01MakeLowLevelLog");

		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");
		AttributeClassifier[] uValues = inputs.get(GoalDrivenObject.unselected_unique_values);
		String[] unselectedValues = new String[uValues.length];
		for (int i = 0; i < uValues.length; i++) {
			unselectedValues[i] = uValues[i].toString();
		}

		String selectedAttribute = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		newLog.getClassifiers().addAll(log.getClassifiers());
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
						if (Arrays.asList(unselectedValues).contains(value)) {
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
		GDPMLog gdpmLog = new GDPMLog(newLog);
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log, gdpmLog);
	}

}
