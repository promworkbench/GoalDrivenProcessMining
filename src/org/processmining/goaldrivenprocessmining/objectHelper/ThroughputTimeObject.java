package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class ThroughputTimeObject implements Serializable {

	private long min;
	private long max;
	private long median;
	private long mean;

	public ThroughputTimeObject(long min, long max, long median, long mean) {
		this.min = min;
		this.max = max;
		this.median = median;
		this.mean = mean;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getMedian() {
		return median;
	}

	public void setMedian(long median) {
		this.median = median;
	}

	public long getMean() {
		return mean;
	}

	public void setMean(long mean) {
		this.mean = mean;
	}

	public int hashCode() {
		return Objects.hash(max, mean, median, min);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThroughputTimeObject other = (ThroughputTimeObject) obj;
		return max == other.max && mean == other.mean && median == other.median && min == other.min;
	}

	public String toString() {
		return "ThroughputTimeObject [min=" + min + ", max=" + max + ", median=" + median + ", mean=" + mean + "]";
	}

}
