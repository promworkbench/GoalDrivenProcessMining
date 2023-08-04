package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;

public class EventSkeleton implements Serializable {
	private ActivitySkeleton activity;
	private String time;
	private Boolean isDisplayed;

	public EventSkeleton(ActivitySkeleton activity, String time, Boolean isDisplayed) {
		this.activity = activity;
		this.time = time;
		this.isDisplayed = isDisplayed;
	}

	public EventSkeleton(EventSkeleton eventSkeleton) {
		this.activity = eventSkeleton.getActivity();
		this.time = eventSkeleton.getTime();
		this.isDisplayed = eventSkeleton.getIsDisplayed();
	}

	public ActivitySkeleton getActivity() {
		return activity;
	}

	public void setActivity(ActivitySkeleton activity) {
		this.activity = activity;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getIsDisplayed() {
		return isDisplayed;
	}

	public void setIsDisplayed(Boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}

	public String toString() {
		return "EventSkeleton [activity=" + activity + ", time=" + time + ", isDisplayed=" + isDisplayed + "]";
	}

	
}
