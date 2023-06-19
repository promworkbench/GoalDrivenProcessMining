package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.List;
import java.util.Objects;

public class GroupActObject {
	private String groupName;
	private List<String> listAct;
	
	
	public GroupActObject(String groupName, List<String> listNodeLabel) {
		this.groupName = groupName;
		this.listAct = listNodeLabel;
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
		GroupActObject other = (GroupActObject) obj;
		return Objects.equals(groupName, other.groupName) && Objects.equals(listAct, other.listAct);
	}

}
