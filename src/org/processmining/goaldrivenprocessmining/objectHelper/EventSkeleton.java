package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;

public class EventSkeleton implements Serializable {
	private String originalName;
	private String currentName;
	private String time;
	private Boolean isDisplayed;

	public EventSkeleton(String originalName, String currentName, String time, Boolean isDisplayed) {
		this.originalName = originalName;
		this.currentName = currentName;
		this.time = time;
		this.isDisplayed = isDisplayed;
	}
	
	public EventSkeleton(EventSkeleton eventSkeleton) {
		this.originalName = eventSkeleton.originalName;
		this.currentName = eventSkeleton.currentName;
		this.time = eventSkeleton.time;
		this.isDisplayed = eventSkeleton.isDisplayed;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
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
		return "EventSkeleton [originalName=" + originalName + ", currentName=" + currentName + ", time=" + time
				+ ", isDisplayed=" + isDisplayed + "]";
	}

}
