package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;

public class FrequencyEdgeObject {
	private HashMap<EdgeObject, Integer> frequencyEdge;

	public FrequencyEdgeObject() {
		this.frequencyEdge = new HashMap<>();
	}

	public HashMap<EdgeObject, Integer> getFrequencyEdge() {
		return frequencyEdge;
	}

	public String toString() {
		return "FrequencyEdgeObject [frequencyEdge=" + frequencyEdge + "]";
	}
}
