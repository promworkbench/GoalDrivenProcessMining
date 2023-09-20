package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class ActivitySkeleton implements Serializable {
	private String originalName;
	private String currentName;

	public ActivitySkeleton(String originalName, String currentName) {
		this.originalName = originalName;
		this.currentName = currentName;
	}
	
	public ActivitySkeleton(String currentName) {
		this.currentName = currentName;
		this.originalName = "";
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

	public int hashCode() {
		return Objects.hash(currentName, originalName);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivitySkeleton other = (ActivitySkeleton) obj;
		return Objects.equals(originalName, other.originalName);
	}

	public String toString() {
		return "ActivitySkeleton [originalName=" + originalName + ", currentName=" + currentName + "]";
	}

}