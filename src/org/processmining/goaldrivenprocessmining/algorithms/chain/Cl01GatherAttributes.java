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

public class Cl01GatherAttributes extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	@Override
	public String getName() {
		return "gather attributes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Gathering attributes..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { 
			IvMObject.input_log
			};
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { 
			GoalDrivenObject.full_xlog,
			GoalDrivenObject.all_unique_values,
			GoalDrivenObject.selected_unique_values,
			GoalDrivenObject.unselected_unique_values
			};
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		System.out.println("Cl01 - gather att");
		XLog log = inputs.get(IvMObject.input_log);
		AttributeClassifier classifier = new AttributeClassifier(log.getClassifiers().get(0));
		return new IvMObjectValues().//
				s(GoalDrivenObject.full_xlog, log).
				s(GoalDrivenObject.unselected_unique_values, new AttributeClassifier[0]).
				s(GoalDrivenObject.selected_unique_values, getAllUniqueValues(log, classifier)).// 
				s(GoalDrivenObject.all_unique_values, getAllUniqueValues(log, classifier));
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
