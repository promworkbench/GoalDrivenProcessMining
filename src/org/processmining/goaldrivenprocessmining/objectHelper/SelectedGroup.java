package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedGroup {
	private GroupSkeleton groupSkeleton;
	private Boolean isHighLevel;
	private boolean isExpanding;

	public SelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel, Boolean isExpanding) {
		this.groupSkeleton = groupSkeleton;
		this.isHighLevel = isHighLevel;
		this.isExpanding = isExpanding;
	}

	public GroupSkeleton getGroupSkeleton() {
		return groupSkeleton;
	}

	public Boolean getIsHighLevel() {
		return isHighLevel;
	}

	public boolean isExpanding() {
		return isExpanding;
	}

}
