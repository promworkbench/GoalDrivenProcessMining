package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;

public class GDPMLogSkeleton implements Serializable {

	private LogSkeleton logSkeleton;
	// stat object
	private MapStatObject statObject;

	public GDPMLogSkeleton() {
		this.logSkeleton = new LogSkeleton();
		this.statObject = new MapStatObject();
	}

	public GDPMLogSkeleton(XLog log) {
		this.logSkeleton = LogSkeletonUtils.getLogSkeleton(log);
		this.statObject = StatUtils.getStat(logSkeleton);
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}

	public LogSkeleton getLogSkeleton() {
		return logSkeleton;
	}

	public void setLogSkeleton(LogSkeleton logSkeleton) {
		this.logSkeleton = logSkeleton;
	}



	public MapStatObject getStatObject() {
		return statObject;
	}

	public void setStatObject(MapStatObject statObject) {
		this.statObject = statObject;
	}

	public String toString() {
		return "GDPMLogSkeleton [logSkeleton=" + logSkeleton + ", statObject="
				+ statObject + "]";
	}

}