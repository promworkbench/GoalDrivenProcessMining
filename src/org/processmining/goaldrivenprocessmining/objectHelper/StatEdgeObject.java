package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;

public class StatEdgeObject implements Serializable {
	private int totalOccurrences;
	
	public StatEdgeObject(int totalOccurrences) {
		this.totalOccurrences = totalOccurrences;
	}
	public int getTotalOccurrences() {
		return totalOccurrences;
	}
	public void setTotalOccurrences(int totalOccurrences) {
		this.totalOccurrences = totalOccurrences;
	}
	
	
}
