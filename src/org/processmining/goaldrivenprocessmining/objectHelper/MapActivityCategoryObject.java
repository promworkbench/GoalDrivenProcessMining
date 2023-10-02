package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

public class MapActivityCategoryObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3353960649338953044L;
	private HashMap<AttributeClassifier, List<ValueCategoryObject>> mapActivityCategory;
	
	public MapActivityCategoryObject() {
		this.mapActivityCategory = new HashMap<>();
	}

	public void put(AttributeClassifier att, List<ValueCategoryObject> group) {
		this.mapActivityCategory.put(att, group);
	}
	
	public HashMap<AttributeClassifier, List<ValueCategoryObject>> getMapActivityCategory() {
		return mapActivityCategory;
	}

	
}
