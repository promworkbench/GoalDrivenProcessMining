package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;
import java.util.List;

import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

public class MapActivityCategoryObject {
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
