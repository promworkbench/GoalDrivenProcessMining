package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class GroupSkeleton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3141362801968131142L;
	private String groupName;
	private List<String> listAct;
	private List<GroupSkeleton> listGroup;

	public GroupSkeleton(String groupName, List<String> listAct, List<GroupSkeleton> listGroup) {
		this.groupName = groupName;
		this.listAct = listAct;
		this.listGroup = listGroup;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getListAct() {
		return listAct;
	}

	public void setListAct(List<String> listNodeLabel) {
		this.listAct = listNodeLabel;
	}

	public List<GroupSkeleton> getListGroup() {
		return listGroup;
	}

	public void setListGroup(List<GroupSkeleton> listGroup) {
		this.listGroup = listGroup;
	}

	public int hashCode() {
		return Objects.hash(groupName, listAct);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupSkeleton other = (GroupSkeleton) obj;
		return Objects.equals(groupName, other.groupName) && Objects.equals(listAct, other.listAct)
				&& Objects.equals(listGroup, other.listGroup);
	}

	public String toString() {
		return groupName;
	}

}
