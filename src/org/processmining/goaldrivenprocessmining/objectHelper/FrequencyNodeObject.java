package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;

public class FrequencyNodeObject {
	private HashMap<String, Integer> frequencyActivity;

	public FrequencyNodeObject() {
		this.frequencyActivity = new HashMap<>();
	}

	public HashMap<String, Integer> getFrequencyActivity() {
		return frequencyActivity;
	}

	public String toString() {
		return "FrequencyNodeObject [frequencyActivity=" + frequencyActivity + "]";
	}
	
	
}
