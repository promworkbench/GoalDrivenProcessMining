package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
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
		return new IvMObject<?>[] { IvMObject.input_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.full_xlog, GoalDrivenObject.all_unique_values,
				GoalDrivenObject.selected_unique_values, GoalDrivenObject.unselected_unique_values,
				 GoalDrivenObject.stat };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		System.out.println("Cl01 - gather att");
		XLog log = inputs.get(IvMObject.input_log);
		AttributeClassifier classifier = new AttributeClassifier(
				log.getClassifiers().get(0).getDefiningAttributeKeys()[0]);
		List<AttributeClassifier[]> valuesDistribution = this.getAllUniqueValues(log, classifier);
		
		return new IvMObjectValues().//
				s(GoalDrivenObject.full_xlog, log).
				s(GoalDrivenObject.unselected_unique_values, valuesDistribution.get(1)).
				s(GoalDrivenObject.selected_unique_values, valuesDistribution.get(0)).// 
				s(GoalDrivenObject.all_unique_values, valuesDistribution.get(2)).
				s(GoalDrivenObject.stat, StatUtils.computeStatNodeFromLog(log, classifier.toString(), "time:timestamp"));
	}

	private List<AttributeClassifier[]> getAllUniqueValues(XLog log, AttributeClassifier classifier) {
		HashMap<String, Integer> mapActFreq = new HashMap<>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String value = event.getAttributes().get(classifier.toString()).toString();
				if (!mapActFreq.keySet().contains(value)) {
					mapActFreq.put(value, 1);
				} else {
					int curFreq = mapActFreq.get(value);
					mapActFreq.replace(value, curFreq + 1);
				}
			}
		}

		int maxFreq = 0;
		for (String key : mapActFreq.keySet()) {
			if (mapActFreq.get(key) >= maxFreq) {
				maxFreq = mapActFreq.get(key);
			}
		}
		int threshold = (int) (0.3 * maxFreq);
		List<String> l = new ArrayList<>();
		List<String> allL = new ArrayList<>();
		for (String key : mapActFreq.keySet()) {
			allL.add(key);
			if (mapActFreq.get(key) >= threshold) {
				l.add(key);
			}
		}
		AttributeClassifier[] arrAtt = new AttributeClassifier[allL.size()];
		for (int i = 0; i < allL.size(); i++) {
			arrAtt[i] = new AttributeClassifier(allL.get(i));
		}
		AttributeClassifier[] selectedArrAtt = new AttributeClassifier[l.size()];
		for (int i = 0; i < l.size(); i++) {
			selectedArrAtt[i] = new AttributeClassifier(l.get(i));
		}
		List<String> unselectedL = new ArrayList<>();
		for (String key : mapActFreq.keySet()) {
			if (!l.contains(key)) {
				unselectedL.add(key);
			}
		}
		AttributeClassifier[] unselectedArrAtt = new AttributeClassifier[unselectedL.size()];
		for (int i = 0; i < unselectedL.size(); i++) {
			unselectedArrAtt[i] = new AttributeClassifier(unselectedL.get(i));
		}
		List<AttributeClassifier[]> res = new ArrayList<>();
		res.add(0, selectedArrAtt);
		res.add(1, unselectedArrAtt);
		res.add(2, arrAtt);
		return res;
	}

}
