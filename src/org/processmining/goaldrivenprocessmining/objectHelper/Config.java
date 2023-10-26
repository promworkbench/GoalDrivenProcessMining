package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9013552664319417538L;
	private List<GroupSkeleton> listGroupSkeletons;
	private String[] selectedActs;
	private String[] unselectedActs;

	public Config() {
		this.listGroupSkeletons = new ArrayList<GroupSkeleton>();
		this.selectedActs = new String[] {};
		this.unselectedActs = new String[] {};
	}

	public void removeGroup(List<GroupSkeleton> groupActObjects) {
		for (GroupSkeleton groupActObject : groupActObjects) {
			this.removeGroup(groupActObject);
		}
	}

	public void removeGroup(GroupSkeleton groupActObject) {
		if (this.listGroupSkeletons.contains(groupActObject)) {
			this.listGroupSkeletons.remove(groupActObject);
		}
	}

	public void addGroup(GroupSkeleton groupActObject) {
		if (!this.listGroupSkeletons.contains(groupActObject)) {
			this.listGroupSkeletons.add(groupActObject);
		}
	}

	public List<GroupSkeleton> getListGroupSkeletons() {
		return listGroupSkeletons;
	}

	public void setListGroupSkeletons(List<GroupSkeleton> listGroupSkeletons) {
		this.listGroupSkeletons = listGroupSkeletons;
	}

	public String[] getSelectedActs() {
		return selectedActs;
	}

	public void setSelectedActs(String[] selectedActs) {
		this.selectedActs = selectedActs;
	}

	public String[] getUnselectedActs() {
		return unselectedActs;
	}

	public void setUnselectedActs(String[] unselectedActs) {
		this.unselectedActs = unselectedActs;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(unselectedActs);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Config other = (Config) obj;
		return Arrays.equals(unselectedActs, other.unselectedActs);
	}

	public String toString() {
		return "Config [listGroupSkeletons=" + listGroupSkeletons + ", selectedActs=" + Arrays.toString(selectedActs)
				+ ", unselectedActs=" + Arrays.toString(unselectedActs) + "]";
	}

}
