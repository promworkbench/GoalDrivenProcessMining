package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;

public class _GDPMLogSkeleton implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3886005288110230296L;
	private GDPMLogSkeleton logSkeleton;
	// stat object

	public _GDPMLogSkeleton(XLog log) {
		this.logSkeleton = LogSkeletonUtils.getLogSkeleton(log);
		//		this.statObject = StatUtils.getStat(logSkeleton);
	}

	@Override
	public Object clone() {
		return SerializationUtils.clone(this);
	}

	public GDPMLogSkeleton getLogSkeleton() {
		return logSkeleton;
	}

	public void setLogSkeleton(GDPMLogSkeleton logSkeleton) {
		this.logSkeleton = logSkeleton;
	}

	public String toString() {
		return "GDPMLogSkeleton [logSkeleton=" + logSkeleton + "]";
	}

}
