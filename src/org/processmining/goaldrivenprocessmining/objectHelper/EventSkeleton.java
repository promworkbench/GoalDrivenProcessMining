package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.HashMap;

public class EventSkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3889100038468173203L;
	private String activity;
	private long time;
	private Boolean isDisplayed;
	private HashMap<String, Object> attributes;
	
	public EventSkeleton(String activity, long time, Boolean isDisplayed) {
		this.activity = activity;
		this.time = time;
		this.isDisplayed = isDisplayed;
	}

	public EventSkeleton(EventSkeleton eventSkeleton) {
		this.activity = eventSkeleton.getActivity();
		this.time = eventSkeleton.getTime();
		this.isDisplayed = eventSkeleton.getIsDisplayed();
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Boolean getIsDisplayed() {
		return isDisplayed;
	}

	public void setIsDisplayed(Boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}
	

	public String toString() {
		return "EventSkeleton [activity=" + activity + ", time=" + time + ", isDisplayed=" + isDisplayed
				+ ", attributes=" + attributes + "]";
	}

}
