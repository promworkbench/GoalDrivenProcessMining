package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Config implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9013552664319417538L;
	private List<GroupSkeleton> listGroupSkeletons;
	private String[] highActs;
	private String[] lowActs;
	private Map<String, List<EdgeObject>> mapActEdgeInHighLevel;

	public Config() {
		this.listGroupSkeletons = new ArrayList<GroupSkeleton>();
		this.highActs = new String[] {};
		this.lowActs = new String[] {};
		this.mapActEdgeInHighLevel = new HashMap<String, List<EdgeObject>>();
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

	public String[] getHighActs() {
		return highActs;
	}

	public void setHighActs(String[] selectedActs) {
		this.highActs = selectedActs;
	}

	public String[] getLowActs() {
		return lowActs;
	}

	public void setLowActs(String[] unselectedActs) {
		this.lowActs = unselectedActs;
	}

	public Map<String, List<EdgeObject>> getMapActEdgeInHighLevel() {
		return mapActEdgeInHighLevel;
	}

	public void setMapActEdgeInHighLevel(Map<String, List<EdgeObject>> mapActEdgeInHighLevel) {
		this.mapActEdgeInHighLevel = mapActEdgeInHighLevel;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(highActs);
		result = prime * result + Arrays.hashCode(lowActs);
		result = prime * result + Objects.hash(listGroupSkeletons, mapActEdgeInHighLevel);
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
		return Objects.equals(listGroupSkeletons, other.listGroupSkeletons)
				&& Objects.equals(mapActEdgeInHighLevel, other.mapActEdgeInHighLevel)
				&& Arrays.equals(highActs, other.highActs)
				&& Arrays.equals(lowActs, other.lowActs);
	}

	public String toString() {
		return "Config [listGroupSkeletons=" + listGroupSkeletons + ", selectedActs=" + Arrays.toString(highActs)
				+ ", unselectedActs=" + Arrays.toString(lowActs) + ", mapActEdgeInHighLevel="
				+ mapActEdgeInHighLevel + "]";
	}

}
