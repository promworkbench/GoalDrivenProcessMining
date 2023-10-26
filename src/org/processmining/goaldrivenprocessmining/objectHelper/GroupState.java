package org.processmining.goaldrivenprocessmining.objectHelper;

public class GroupState {
	private GroupSkeleton groupSkeleton;
	private Boolean isDisplay;
	private Boolean isCollapse;

	public GroupState(GroupSkeleton groupSkeleton) {
		this.groupSkeleton = groupSkeleton;
		this.isDisplay = true;
		this.isCollapse = true;
	}

	public GroupSkeleton getGroupSkeleton() {
		return groupSkeleton;
	}

	public void setGroupSkeleton(GroupSkeleton groupSkeleton) {
		this.groupSkeleton = groupSkeleton;
	}

	public Boolean getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public Boolean getIsCollapse() {
		return isCollapse;
	}

	public void setIsCollapse(Boolean isCollapse) {
		this.isCollapse = isCollapse;
	}

}
