package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig.UpdateType;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class Cl01GatherAttributes extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	public static List<TraceSkeleton> originalLog;
	public static EdgeHashTable originalEdgeHashTable;

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
				GoalDrivenObject.selected_unique_values, GoalDrivenObject.unselected_unique_values, GoalDrivenObject.map_act_freq,
				GoalDrivenObject.config, GoalDrivenObject.update_config_object, GoalDrivenObject.full_log_skeleton };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		System.out.println("Cl01 - gather att");
		XLog log = inputs.get(IvMObject.input_log);
		AttributeClassifier classifier = new AttributeClassifier(LogSkeletonUtils.getLogClassifier(log));
		HashMap<String, Integer> mapActFreq = this.getMapActFreq(log, classifier);
		List<AttributeClassifier[]> valuesDistribution = this.getAllUniqueValues(mapActFreq);

		// string
		String[] values = new String[valuesDistribution.get(0).length];
		for (int i = 0; i < values.length; i++) {
			values[i] = valuesDistribution.get(0)[i].toString();
		}
		String[] values1 = new String[valuesDistribution.get(1).length];
		for (int i = 0; i < values1.length; i++) {
			values1[i] = valuesDistribution.get(1)[i].toString();
		}
		List<String> allAct = new ArrayList<String>();
		for (int i = 0; i < valuesDistribution.get(2).length; i++) {
			allAct.add(valuesDistribution.get(2)[i].toString());
		}
		Config config = new Config();
		config.setHighActs(values);
		config.setLowActs(values1);
		CONFIG_Update.currentConfig = config;

		HashMap<String, String[]> updateMap = new HashMap<String, String[]>();
		updateMap.put("High", values);
		updateMap.put("Low", values1);
		UpdateConfig updateConfig = new UpdateConfig(UpdateType.SELECTED_ACT, updateMap);

		GDPMLogSkeleton gdpmLogSkeleton = new GDPMLogSkeleton(log);
		originalLog = gdpmLogSkeleton.getLog();
		originalEdgeHashTable = gdpmLogSkeleton.getEdgeHashTable();

		return new IvMObjectValues().//
				s(GoalDrivenObject.full_xlog, log)
				.s(GoalDrivenObject.unselected_unique_values, valuesDistribution.get(1))
				.s(GoalDrivenObject.selected_unique_values, valuesDistribution.get(0))
				.s(GoalDrivenObject.map_act_freq, mapActFreq)
				.s(GoalDrivenObject.all_unique_values, valuesDistribution.get(2)).s(GoalDrivenObject.config, config)
				.s(GoalDrivenObject.full_log_skeleton, gdpmLogSkeleton)
				.s(GoalDrivenObject.update_config_object, updateConfig);
	}

	private List<AttributeClassifier[]> getAllUniqueValues(HashMap<String, Integer> mapActFreq) {
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

	private HashMap<String, Integer> getMapActFreq(XLog log, AttributeClassifier classifier) {
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
		return mapActFreq;
	}

}
