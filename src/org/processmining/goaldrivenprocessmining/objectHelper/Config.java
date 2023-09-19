package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
	private List<GroupSkeleton> listGroupSkeletons;
	private MapActivityCategoryObject mapActivityCategoryObject;
	private double highActThreshold;
	private double lowActThreshold;
	private String[] selectedActs;
	private String[] unselectedActs;

	public Config() {
		this.listGroupSkeletons = new ArrayList<GroupSkeleton>();
		this.mapActivityCategoryObject = new MapActivityCategoryObject();
		this.highActThreshold = 1d;
		this.lowActThreshold = 1d;
		this.selectedActs = new String[] {  };
		this.unselectedActs = new String[] {  };

	}
	
	// Selected act
	
	// Group action
	
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

	public MapActivityCategoryObject getMapActivityCategoryObject() {
		return mapActivityCategoryObject;
	}

	public void setMapActivityCategoryObject(MapActivityCategoryObject mapActivityCategoryObject) {
		this.mapActivityCategoryObject = mapActivityCategoryObject;
	}

	public double getHighActThreshold() {
		return highActThreshold;
	}

	public void setHighActThreshold(double highActThreshold) {
		this.highActThreshold = highActThreshold;
	}

	public double getLowActThreshold() {
		return lowActThreshold;
	}

	public void setLowActThreshold(double lowActThreshold) {
		this.lowActThreshold = lowActThreshold;
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

	public String toString() {
		return "LogConfig [listGroupActObjects=" + listGroupSkeletons + ", mapActivityCategoryObject="
				+ mapActivityCategoryObject + ", highActThreshold=" + highActThreshold + ", lowActThreshold="
				+ lowActThreshold + ", selectedActs=" + Arrays.toString(selectedActs) + ", unselectedActs="
				+ Arrays.toString(unselectedActs) + "]";
	}
}
