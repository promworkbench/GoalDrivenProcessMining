package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.List;

public class SelectedNodeGroupObject {
	private String groupName;
	private List<String> listNodeLabel;
	
	
	public SelectedNodeGroupObject(String groupName, List<String> listNodeLabel) {
		this.groupName = groupName;
		this.listNodeLabel = listNodeLabel;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<String> getListNodeLabel() {
		return listNodeLabel;
	}
	public void setListNodeLabel(List<String> listNodeLabel) {
		this.listNodeLabel = listNodeLabel;
	}
	
	

}
