package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class StatNodeObject implements Serializable {

	private String avgThroughputTime;
	private int totalOccurences;
	private float avgOccurences;
	
	

	public StatNodeObject() {
	}

	public StatNodeObject(int totalOccurences, float avgOccurences) {
		this.totalOccurences = totalOccurences;
		this.avgOccurences = avgOccurences;
	}

	public StatNodeObject(String avgThroughputTime, int totalOccurences, float avgOccurences) {
		this.avgThroughputTime = avgThroughputTime;
		this.totalOccurences = totalOccurences;
		this.avgOccurences = avgOccurences;
	}

	public String getAvgThroughputTime() {
		return avgThroughputTime;
	}

	public void setAvgThroughputTime(String avgThroughputTime) {
		this.avgThroughputTime = avgThroughputTime;
	}

	public int getTotalOccurences() {
		return totalOccurences;
	}

	public void setTotalOccurences(int totalOccurences) {
		this.totalOccurences = totalOccurences;
	}

	public float getAvgOccurences() {
		return avgOccurences;
	}

	public void setAvgOccurences(float avgOccurences) {
		this.avgOccurences = avgOccurences;
	}

	public String toString() {
		return "StatNodeObject [avgThroughputTime=" + avgThroughputTime + ", totalOccurences=" + totalOccurences
				+ ", avgOccurences=" + avgOccurences + "]";
	}

	public int hashCode() {
		return Objects.hash(avgOccurences, avgThroughputTime, totalOccurences);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatNodeObject other = (StatNodeObject) obj;
		return avgOccurences == other.avgOccurences && Objects.equals(avgThroughputTime, other.avgThroughputTime)
				&& totalOccurences == other.totalOccurences;
	}

}
