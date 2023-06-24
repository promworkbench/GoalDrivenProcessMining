package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class GDPMEventSkeleton implements Serializable{
	private int position;
	private int activityIndex;

	public GDPMEventSkeleton(int position, int activityIndex) {
		this.position = position;
		this.activityIndex = activityIndex;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getActivityIndex() {
		return activityIndex;
	}

	public void setActivityIndex(int activityIndex) {
		this.activityIndex = activityIndex;
	}

	public boolean isBefore(GDPMEventSkeleton eventSkeleton) {
		if (this.getPosition() <= eventSkeleton.getPosition()) {
			return true;
		} else {
			return false;
		}
	}

	
	public int hashCode() {
		return Objects.hash(activityIndex, position);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GDPMEventSkeleton other = (GDPMEventSkeleton) obj;
		return activityIndex == other.activityIndex && position == other.position;
	}

	public String toString() {
		return "GDPMEventSkeleton [position=" + position + ", activity=" + activityIndex + "]";
	}

}
