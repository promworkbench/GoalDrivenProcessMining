package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class _ActivitySkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3078117451196558784L;
	private String originalName;

	public _ActivitySkeleton(String originalName) {
		this.originalName = originalName;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public int hashCode() {
		return Objects.hash(originalName);
	}


	public String toString() {
		return "ActivitySkeleton [originalName=" + originalName + "]";
	}

}
