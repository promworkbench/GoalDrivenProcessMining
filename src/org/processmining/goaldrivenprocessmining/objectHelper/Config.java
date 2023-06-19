package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
	private List<GroupActObject> listGroupActObjects;
	private MapActivityCategoryObject mapActivityCategoryObject;
	private double highActThreshold;
	private double lowActThreshold;
	private String[] selectedActs;
	private String[] unselectedActs;

	public Config() {
		this.listGroupActObjects = new ArrayList<GroupActObject>();
		this.mapActivityCategoryObject = new MapActivityCategoryObject();
		this.highActThreshold = 1d;
		this.lowActThreshold = 1d;
		this.selectedActs = new String[] {  };
		this.unselectedActs = new String[] {  };

	}
	
	// Selected act
	
	// Group action
	public void removeGroup(GroupActObject groupActObject) {
		if (this.listGroupActObjects.contains(groupActObject)) {
			this.listGroupActObjects.remove(groupActObject);
		}
	}

	public void addGroup(GroupActObject groupActObject) {
		if (!this.listGroupActObjects.contains(groupActObject)) {
			this.listGroupActObjects.add(groupActObject);
		}
	}

	public List<GroupActObject> getListGroupActObjects() {
		return listGroupActObjects;
	}

	public void setListGroupActObjects(List<GroupActObject> listGroupActObjects) {
		this.listGroupActObjects = listGroupActObjects;
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
		return "LogConfig [listGroupActObjects=" + listGroupActObjects + ", mapActivityCategoryObject="
				+ mapActivityCategoryObject + ", highActThreshold=" + highActThreshold + ", lowActThreshold="
				+ lowActThreshold + ", selectedActs=" + Arrays.toString(selectedActs) + ", unselectedActs="
				+ Arrays.toString(unselectedActs) + "]";
	}
}
