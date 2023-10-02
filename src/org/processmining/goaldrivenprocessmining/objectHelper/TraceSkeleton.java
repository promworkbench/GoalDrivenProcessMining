package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TraceSkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2008570674600305009L;
	private List<EventSkeleton> trace;
	

	public TraceSkeleton() {
		this.trace = new ArrayList<>();
	}

	public List<EventSkeleton> getTrace() {
		return trace;
	}

	public void setTrace(List<EventSkeleton> trace) {
		this.trace = trace;
	}

	public String toString() {
		return "TraceSkeleton [trace=" + trace + "]";
	}
	
	
	
	

}
