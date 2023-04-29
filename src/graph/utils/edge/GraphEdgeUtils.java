package graph.utils.edge;

import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;

import graph.GraphConstants;

public class GraphEdgeUtils {
	public static HashMap<EdgeObject, Float> getStrokeWidth(FrequencyEdgeObject frequencyEdge,
			HashMap<Integer[], Float> mapFreqStrokeWidth) {
		HashMap<EdgeObject, Float> res = new HashMap<>();
		HashMap<EdgeObject, Integer> mapFreqEdge = frequencyEdge.getFrequencyEdge();

		for (EdgeObject edge : mapFreqEdge.keySet()) {
			for (Integer[] range : mapFreqStrokeWidth.keySet()) {
				if (mapFreqEdge.get(edge) >= range[0] && mapFreqEdge.get(edge) <= range[1]) {
					res.put(edge, mapFreqStrokeWidth.get(range));
					break;
				}
			}
		}

		return res;
	}

	public static HashMap<Integer[], Float> getMapFreqStrokeWidth(FrequencyEdgeObject frequencyEdgeObject) {
		HashMap<Integer[], Float> res = new HashMap<>();
		HashMap<EdgeObject, Integer> frequencyEdge = frequencyEdgeObject.getFrequencyEdge();
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (EdgeObject edge : frequencyEdge.keySet()) {
			if (frequencyEdge.get(edge) >= maxFreq) {
				maxFreq = frequencyEdge.get(edge);
			}
			if (frequencyEdge.get(edge) <= minFreq) {
				minFreq = frequencyEdge.get(edge);
			}
		}
		if (maxFreq == minFreq) {
			res.put(new Integer[] { minFreq, maxFreq }, (float) 0.5 * GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH);
		} else {
			int distance = maxFreq - minFreq;
			int numEdge = distance / 7;
			if (numEdge <= 5) {
				res.put(new Integer[] { minFreq, minFreq }, GraphConstants.LOWER_BOUND_EDGE_STROKE_WIDTH);
				res.put(new Integer[] { minFreq, maxFreq }, GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH);
			} else {
				int cur = minFreq;
				for (int i = 0; i < 7; i++) {
					int lower = cur;
					int upper = lower + numEdge;
					if (i == 6) {
						upper = maxFreq;
					}
					res.put(new Integer[] { lower, upper },
							(float) i / 6 * GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH + 0.25f);
				}
			}

		}
		return res;
	}
}
