package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.HashMap;

import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

public class MapValueGroupObject {
	private HashMap<AttributeClassifier, GroupActObject> mapValueGroup;
	
	public MapValueGroupObject() {
		this.mapValueGroup = new HashMap<>();
	}

	public void put(AttributeClassifier att, GroupActObject group) {
		this.mapValueGroup.put(att, group);
	}
	
	public HashMap<AttributeClassifier, GroupActObject> getMapValueGroup() {
		return mapValueGroup;
	}

	
}
