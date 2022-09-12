package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class Cl012UniqueValues extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	@Override
	public String getName() {
		return "unique values";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Gathering attributes..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.input_log, GoalDrivenObject.selected_classifier1 };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.unique_values, GoalDrivenObject.selected_unique_values,
			GoalDrivenObject.unique_values_for_gui, GoalDrivenObject.unselected_unique_values };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		XLog log = inputs.get(IvMObject.input_log);
		AttributeClassifier selected_classifier = inputs.get(GoalDrivenObject.selected_classifier1);
		System.out.println("execute cl012 unique values");
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
		return new IvMObjectValues().//
				s(GoalDrivenObject.unique_values, arrAtt).//
				s(GoalDrivenObject.selected_unique_values, arrAtt).//
				s(GoalDrivenObject.unique_values_for_gui, arrAtt).
				s(GoalDrivenObject.unselected_unique_values, new AttributeClassifier[0]);

	}

}
