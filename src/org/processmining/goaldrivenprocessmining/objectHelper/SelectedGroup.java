package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedGroup {
	private GroupSkeleton groupSkeleton;
	private Boolean isHighLevel;

	public SelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		this.groupSkeleton = groupSkeleton;
		this.isHighLevel = isHighLevel;
	}

	public GroupSkeleton getGroupSkeleton() {
		return groupSkeleton;
	}

	public Boolean getIsHighLevel() {
		return isHighLevel;
	}

}
